# KidsPOS Android プロジェクト概要

## プロジェクト基本情報
- **名称**: KidsPOS for Android
- **パッケージ名**: info.nukoneko.cuc.android.kidspos
- **用途**: キッズビジネスタウンいちかわで使用するAndroid用POSシステム
- **プラットフォーム**: Android (minSdk 23 / targetSdk 36)

## 技術スタック

### 言語とフレームワーク
- **言語**: Kotlin
- **SDKバージョン**: gradle/libs.versions.toml と app/build.gradle.kts で管理
- **JVMターゲット**: 17

### アーキテクチャパターン
- **Single-Activity + Jetpack Compose (Material3)**: MainActivity 1つ + Navigation Compose
- **MVVM**: @HiltViewModel + StateFlow の UiState、collectAsStateWithLifecycle で観測
- **DI (依存性注入)**: Hilt (KSP)
- **画面間通信**: SettingsRepository (Preferences DataStore) の Flow 観測 + BarcodeEventBus (MutableSharedFlow)

### 主要ライブラリ
- **UI**: Jetpack Compose、Material3、Navigation Compose、SplashScreen API
- **非同期処理**: Kotlin Coroutines
- **ネットワーク**: Retrofit + OkHttp（OpenAPI Generator によるクライアント生成）
- **シリアライゼーション**: Kotlinx Serialization
- **設定永続化**: Preferences DataStore
- **バーコード読取**: ZXing Android Embedded
- **ログ**: Timber
- **クラッシュレポート**: Firebase Crashlytics
- **テスト**: JUnit4 + kotlinx-coroutines-test + turbine（モックライブラリ不使用、Fake を使用）

## プロジェクト構造

### ビルドバリアント
- **buildTypes**: debug / release（release は minify / shrinkResources 有効）
- **productFlavors**: prod / demo

### 主要ディレクトリ構成
```
app/src/main/kotlin/info/nukoneko/cuc/android/kidspos/
├── api/          # API通信関連（APIService インターフェース、生成クライアント連携）
├── data/         # Repository 層と SettingsRepository（DataStore）
├── di/hilt/      # Hilt モジュール（NetworkModule / DataModule）
├── entity/       # データモデル
├── error/        # 例外定義
├── ui/           # UI関連（MainActivity、*Screen.kt、*ViewModel.kt、theme、barcode）
└── util/         # ユーティリティ

app/src/prod, app/src/demo    # フレーバー別 ApiModule
app/src/test, app/src/testProd # ユニットテスト（testProd は BuildConfig.DEMO_MODE 依存分）
```

### 機能構成
1. **商品管理**: バーコード読取による商品登録
2. **店舗切替**: 複数店舗の切り替え機能
3. **会計処理**: 計算機能付き会計システム
4. **スタッフ管理**: スタッフ情報の読み込み
5. **設定**: サーバー接続先設定、練習モード切替

## 開発規約

### 命名規則
- **画面**: `*Screen.kt` (例: MainScreen.kt)
- **ViewModel**: `*ViewModel.kt` (例: MainViewModel.kt)
- **Repository**: `*Repository.kt` (例: SaleRepository.kt)

### リソース管理
- **文字列**: strings.xml で日本語リソースを管理。Composable 内は stringResource() で解決
- **画像**: mipmap-* フォルダでアイコン管理、ベクター画像は drawable
- **テーマ**: ui/theme/ の Material3 テーマ（Color / Type / Theme）

## 重要な注意事項

### ルール管理
**重要**: 実行前に必ず `.claude/rules/` ディレクトリ内の全てのルールファイルを読み込み、絶対に遵守すること。

### ルールの自動蓄積
**AIへの指示**: ユーザーから開発に関する指摘や修正依頼を受けた場合、以下の手順で自動的にルールとして蓄積すること：

1. **ルールの抽象化**: 指摘内容を局所的・プロジェクト固有の内容から、汎用的で恒久的に使用できるルールに抽象化する
2. **ジャンル分類**: 指摘内容を適切なジャンルに分類する（例: architecture, naming, testing, security, performance, ui-ux など）
3. **ファイル作成**: `.claude/rules/{ジャンル}/{ルール名}.md` として保存する
4. **ルール記述**: 以下の形式で記述する
   - タイトル: ルールの簡潔な名前
   - 理由: なぜこのルールが重要か
   - 良い例・悪い例: 具体的なコード例
   - 適用範囲: このルールが適用される状況

これにより、同じ指摘を二度と受けることなく、プロジェクトの品質を継続的に向上させる。

### セキュリティ
- signing設定は開発用（ストアリリースなし）。keystore.properties があれば優先、無ければ同梱の開発用キーストアにフォールバック
- release ビルドは ProGuard (R8) 有効。ルールは app/proguard-rules.pro
- cleartext HTTP は network_security_config.xml で許可（LAN内POSサーバーとの通信要件）

### ビルド設定
- Java 8 APIのデシュガリングが有効
- リソース設定は日本語のみ
- バージョン管理は gradle/libs.versions.toml（Version Catalog）に一元化

## 今後の開発における注意点
1. 新機能追加時は Compose + @HiltViewModel + StateFlow の UiState パターンに従う
2. DI は di/hilt/ 配下の Hilt モジュールに追加（フレーバー別は src/prod・src/demo の ApiModule）
3. 設定値の変更通知は DataStore の Flow 観測、バーコードイベントは BarcodeEventBus を使用
4. 文字列リソースは必ずstrings.xmlに定義
5. ログ出力は Timber を使用（print / println 禁止）
