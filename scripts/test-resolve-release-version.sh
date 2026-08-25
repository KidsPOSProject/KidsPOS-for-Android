#!/usr/bin/env bash
# resolve-release-version.sh のテスト

set -uo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
target="${script_dir}/resolve-release-version.sh"

fail=0

check() {
  local label="$1" expected="$2" actual="$3"
  if [ "$expected" = "$actual" ]; then
    echo "PASS ${label}: ${actual}"
  else
    echo "FAIL ${label}: expected=${expected} actual=${actual}"
    fail=1
  fi
}

run() {
  local releases_json="$1" requested="${2:-}"
  RELEASES_JSON="$releases_json" "$target" "$requested" 2>&1
}

REAL='[{"tagName":"v1.0.12"},{"tagName":"v1.0.11"},{"tagName":"v1.0.10"},{"tagName":"v1.0.9"},{"tagName":"v1.0.8"}]'

check "既存リリースから patch を +1 する" \
  "1.0.13 10013" "$(run "$REAL")"

check "作成日順が版番順と違っても最大版を選ぶ" \
  "1.0.13 10013" "$(run '[{"tagName":"v1.0.8"},{"tagName":"v1.0.12"},{"tagName":"v1.0.9"}]')"

check "9 と 12 を文字列でなく数値として比較する" \
  "1.0.13 10013" "$(run '[{"tagName":"v1.0.9"},{"tagName":"v1.0.12"}]')"

check "版番形式でないタグを無視する" \
  "1.0.13 10013" "$(run '[{"tagName":"server-v1.0.0.42"},{"tagName":"v1.0.12"},{"tagName":"latest"},{"tagName":"v1.0"}]')"

check "minor が上がった後も最大版から採番する" \
  "1.1.1 10101" "$(run '[{"tagName":"v1.0.12"},{"tagName":"v1.1.0"}]')"

check "major が上がった後も最大版から採番する" \
  "2.0.1 20001" "$(run '[{"tagName":"v1.9.99"},{"tagName":"v2.0.0"}]')"

check "versionName を明示指定できる" \
  "2.0.0 20000" "$(run "$REAL" "2.0.0")"

check "明示指定の先頭 v を取り除く" \
  "2.0.0 20000" "$(run "$REAL" "v2.0.0")"

check "リリースタグが無ければエラーにする" \
  "既存のリリースタグが無いため自動採番できません。versionName を明示してください" "$(run '[]')"

check "版番形式でないタグしか無ければエラーにする" \
  "既存のリリースタグが無いため自動採番できません。versionName を明示してください" "$(run '[{"tagName":"latest"}]')"

check "桁の足りない指定を弾く" \
  "versionName の形式が不正です: 1.0" "$(run "$REAL" "1.0")"

check "数字以外を含む指定を弾く" \
  "versionName の形式が不正です: 1.0.x" "$(run "$REAL" "1.0.x")"

check "コマンド置換を含む指定を弾く" \
  'versionName の形式が不正です: 1.0.1;id' "$(run "$REAL" '1.0.1;id')"

RELEASES_JSON='[]' "$target" "" > /dev/null 2>&1
check "エラー時は非ゼロで終了する" "1" "$?"

# 現在配布済みの versionCode より必ず大きくなること
current_version_code=13
for case in "$REAL:" "$REAL:2.0.0" '[{"tagName":"v1.1.0"}]:'; do
  json="${case%:*}"
  requested="${case##*:}"
  code=$(run "$json" "$requested" | awk '{print $2}')
  if [ -z "$code" ] || [ "$code" -le "$current_version_code" ]; then
    echo "FAIL versionCode が配布済み ${current_version_code} 以下: ${code}"
    fail=1
  fi
done
[ "$fail" -eq 0 ] && echo "PASS versionCode は配布済み ${current_version_code} より大きい"

if [ "$fail" -eq 0 ]; then
  echo "全テスト成功"
else
  echo "テスト失敗あり"
fi
exit "$fail"
