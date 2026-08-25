#!/usr/bin/env bash
# 次にリリースする versionName と versionCode を決めて "<versionName> <versionCode>" 形式で出力する。
#
# 第1引数に versionName を渡すとそれを採用し、空なら既存リリースタグのうち最大のものの
# patch を +1 する。versionCode は major*10000 + minor*100 + patch で導出する。
# 既存リリース一覧は RELEASES_JSON（gh release list --json tagName と同じ形）があればそれを使い、
# 無ければ gh コマンドで取得する。

set -euo pipefail

requested="${1:-}"
version_name="${requested#v}"

if [ -z "$version_name" ]; then
  releases_json="${RELEASES_JSON:-}"
  if [ -z "$releases_json" ]; then
    releases_json=$(gh release list --limit 200 --json tagName)
  fi
  latest=$(printf '%s' "$releases_json" | jq -r '[.[].tagName | select(test("^v[0-9]+\\.[0-9]+\\.[0-9]+$")) | ltrimstr("v")] | sort_by(split(".") | map(tonumber)) | last // ""')
  if [ -z "$latest" ]; then
    echo "既存のリリースタグが無いため自動採番できません。versionName を明示してください" >&2
    exit 1
  fi
  IFS=. read -r major minor patch <<< "$latest"
  version_name="${major}.${minor}.$((patch + 1))"
fi

if ! printf '%s' "$version_name" | grep -qE '^[0-9]+\.[0-9]+\.[0-9]+$'; then
  echo "versionName の形式が不正です: ${version_name}" >&2
  exit 1
fi

IFS=. read -r major minor patch <<< "$version_name"
echo "${version_name} $((major * 10000 + minor * 100 + patch))"
