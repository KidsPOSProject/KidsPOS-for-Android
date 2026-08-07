# KidsPOS for Android

キッズビジネスタウンいちかわのレジ端末として使う Android アプリです。バーコードで商品を読み取って会計し、売上を [KidsPOS-Server](https://github.com/KidsPOSProject/KidsPOS-Server) に送ります。

サーバーが無くても試せるように demo フレーバーを用意してあります。こちらは通信せずダミーデータで動くので、動作確認やデモにはこちらをどうぞ。本番は prod フレーバーです。

## ビルド

JDK 17 があればビルドできます（minSdk 23 / targetSdk 36）。

```bash
./gradlew assembleProdDebug   # 本番用
./gradlew assembleDemoDebug   # デモ用
```

リリースビルドの署名は keystore.properties があればそれを使い、無ければリポジトリ同梱の開発用キーストアにフォールバックします。ストア配布はしていない、会場運用専用のアプリです。

## つくり

コードは Kotlin、UI は Jetpack Compose（Material3）です。Activity は MainActivity ひとつだけで、画面遷移は Navigation Compose に任せています。ViewModel は Hilt で注入し、状態は StateFlow の UiState として画面へ流します。

サーバーとの通信は Retrofit。API クライアントはサーバー側の api.yaml から OpenAPI Generator で生成しているので、手書きの通信コードはほとんどありません。接続先サーバーや店舗の選択といった設定は Preferences DataStore に保存され、バーコードの読取イベントは BarcodeEventBus 経由で各画面に届きます。

## 開発するとき

コミット前にひととおり回しておくと安心です。

```bash
./gradlew testProdDebugUnitTest
./gradlew lintProdDebug
```

テストはモックライブラリを使わず、Fake 実装 + turbine で書いています。ログ出力は Timber を使ってください（print / println は使いません）。依存ライブラリのバージョンは gradle/libs.versions.toml にまとめてあります。
