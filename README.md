# KidsPOS for Android

キッズビジネスタウンいちかわで使用するレジ端末用 Android アプリです。バーコードで商品を読み取り、会計処理を行って売上を [KidsPOS-Server](https://github.com/KidsPOSProject/KidsPOS-Server) に送信します。

## 機能

- 商品バーコード読み取り
- 会計処理
- 店舗切替
- スタッフ管理
- レシート発行

## 技術スタック

- Kotlin / Jetpack Compose (Material3)
- Single-Activity + Navigation Compose
- Hilt (KSP) / StateFlow / Preferences DataStore
- Retrofit + OkHttp（OpenAPI Generator によるクライアント生成）
- Timber

## 必要環境

- JDK 17
- Android Studio
- minSdk 23 / targetSdk 36

## ビルド

```bash
./gradlew assembleProdDebug   # 本番用
./gradlew assembleDemoDebug   # デモ用
```

demo フレーバーはサーバーと通信せず、ダミーデータで動作します。

リリースビルドの署名は keystore.properties を優先し、無い場合はリポジトリ同梱の開発用キーストアを使用します（ストア配布なしの開発専用アプリ）。

## テストと Lint

```bash
./gradlew testProdDebugUnitTest
./gradlew lintProdDebug
```

## 開発規約

- テストはモックライブラリを使用せず、Fake 実装と turbine で記述します
- ログ出力は Timber を使用します（print / println は禁止）
- 依存ライブラリのバージョンは gradle/libs.versions.toml で管理します
