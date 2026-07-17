# KidsPOS for Android

## 概要
キッズビジネスタウンいちかわで使用するAndroid用POSシステムです。

## 機能
- 商品バーコード読み取り
- 会計処理
- 店舗管理
- スタッフ管理
- レシート発行

## アーキテクチャ
- 言語: Kotlin
- UI: Jetpack Compose (Material3) / Single-Activity + Navigation Compose
- DI: Hilt (KSP)
- 状態管理: StateFlow + UiState
- 設定永続化: Preferences DataStore
- 通信: Retrofit + OkHttp (OpenAPI Generator によるクライアント生成)
- ログ: Timber
- ビルド: Gradle Kotlin DSL + Version Catalog (gradle/libs.versions.toml)

## 開発環境
- Android Studio
- JDK 17
- Claude Code

## ビルド方法

### デバッグビルド
```bash
./gradlew assembleProdDebug
./gradlew assembleDemoDebug
```

### リリースビルド
```bash
./gradlew assembleProdRelease
```

リリース署名は keystore.properties があればそれを使用し、無ければリポジトリ同梱の開発用キーストアにフォールバックします（ストア配布なしの開発専用アプリ）。

### テストと Lint
```bash
./gradlew testProdDebugUnitTest
./gradlew lintProdDebug
```

## プロジェクト構成
- **app/** - メインアプリケーションモジュール
  - src/main - アプリ本体（prod / demo の共通部分）
  - src/prod, src/demo - フレーバー別ソース
  - src/test, src/testProd - ユニットテスト
- **gradle/libs.versions.toml** - 依存バージョンの一元管理
- **docs/** - ドキュメント
