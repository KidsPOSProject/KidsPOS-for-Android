# KidsPOS for Android

## 概要
キッズビジネスタウンいちかわで使用するAndroid用POSシステムです。

## 機能
- 商品バーコード読み取り
- 会計処理
- 店舗管理
- スタッフ管理
- レシート発行

## 開発環境
- Android Studio Hedgehog | 2023.1.1以降
- Kotlin 1.8.20以降
- Gradle 8.13
- minSdk 23
- targetSdk 33

## ビルド方法

### デバッグビルド
```bash
./gradlew assembleProdDebug
```

### リリースビルド
```bash
./gradlew assembleProdRelease
```

### テスト実行
```bash
./gradlew test
./gradlew lint
```

## プロジェクト構成
- **app/** - メインアプリケーションモジュール
- **buildSrc/** - ビルド設定の共通化
- **docs/** - ドキュメント

## ライセンス
(C) KidsPOS Project
