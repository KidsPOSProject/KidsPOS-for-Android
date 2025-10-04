# ビルド＆デプロイ設定

## デフォルト動作

### ビルド時の完全な手順
1. **アプリを停止**: `adb shell am force-stop info.nukoneko.cuc.android.kidspos`
2. **ビルド＋インストール**: `./gradlew installProdDebug`
3. **アプリを起動**: `adb shell am start -n info.nukoneko.cuc.android.kidspos/.ui.launch.LaunchActivity`

### ビルドバリアント
- 常にProdDebugビルドを使用
- リリースビルドは明示的に指示された場合のみ

### 完全なワークフロー
```bash
adb shell am force-stop info.nukoneko.cuc.android.kidspos && \
./gradlew installProdDebug && \
adb shell am start -n info.nukoneko.cuc.android.kidspos/.ui.launch.LaunchActivity
```

### 理由
- アプリが実行中の場合、停止してから再インストールすることでクリーンな状態で動作確認できる
- キャッシュや状態の問題を回避
- より確実なテスト環境

## 重要
- 「ビルド」「ビルドして」などの指示 = アプリ停止→ビルド→インストール→起動をすべて実行
- 実行中でも必ず停止してから再インストール
