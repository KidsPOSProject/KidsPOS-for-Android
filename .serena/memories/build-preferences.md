# ビルド設定の優先事項

## デフォルトビルド設定

### ビルドバリアント
- **常にProdDebugビルドを使用する**
- リリースビルドは明示的に指示された場合のみ実行

### ビルドコマンド
- ビルド: `./gradlew assembleProdDebug`
- インストール: `./gradlew installProdDebug`
- クリーンビルド: `./gradlew clean assembleProdDebug`

### 理由
- デバッグビルドは開発とテストに適している
- より高速なビルド時間
- デバッグ情報が含まれる
- 実機テストに最適

### 例外
- ユーザーが明示的にリリースビルドを要求した場合のみ `assembleProdRelease` を使用
