# KidsPOS for Android フルモダン化リファクタリング計画

## Context

キッズビジネスタウン用POSアプリ（Kotlin、約40ファイル）を「1番モダンなAndroid構成」へ全面リファクタリングし、PRを作成する（/long-task として自走実行）。ユーザー指示: Compose 移行を含む最もモダンな構成にし、**調査で発見した課題はすべて解決する**（スコープ外を作らない）。

採用するモダンスタック（Google 推奨アーキテクチャ / Now in Android 準拠）:
- **UI**: Jetpack Compose + Material3、Single-Activity + Navigation Compose、SplashScreen API、enableEdgeToEdge
- **DI**: Hilt + KSP（kapt 全廃。現状未使用宣言だけある Hilt を正式採用し、Koin を削除）
- **データ**: Repository 層 + Preferences DataStore（SharedPreferences 廃止）、設定は Flow で観測
- **状態**: StateFlow + UiState data class、collectAsStateWithLifecycle。LiveData / DataBinding / EventBus(greenrobot) 全廃
- **ビルド**: Kotlin DSL（*.gradle.kts）、Version Catalog 一本化（buildSrc 削除）、compileSdk/targetSdk 35
- **テスト**: JUnit + kotlinx-coroutines-test + turbine でユニットテスト新設

現状の主な課題（調査済み）:
- Koin 3.1.2 実使用 / Hilt は宣言のみ完全未使用、kapt は Hilt と DataBinding のためだけに存在
- greenrobot EventBus（@Subscribe 8箇所 / post 11箇所）、ViewModel が自前 CoroutineScope（Jobなし）
- XMLレイアウト11枚に DataBinding / ViewBinding / findViewById / BaseObservable 混在
- 非推奨API: Handler.postDelayed、onActivityResult + IntentIntegrator、BroadcastChannel
- Activity 3つ（Launch/Main/Setting）+ Fragment/Dialog 6つ、すべて landscape 固定
- GlobalConfig が SharedPreferences + setter 内 EventBus.post
- compileSdk/targetSdk 33、Groovy DSL、buildSrc と libs.versions.toml の二重管理
- テストゼロ、CI が JDK 11（AGP 8.13 は JDK 17 必須なので壊れている）
- 潜在バグ: demo の DemoAPIService がコンストラクタ引数 `throw NotImplementedError` で必ずクラッシュ（app/src/demo/kotlin/.../di/module/apiModule.kt:15-21）
- リリースビルドが minify/shrinkResources 無効（ProGuard ルールが死蔵）
- 署名情報（storePassword 等）が app/build.gradle にハードコード
- orhanobut/logger 2.2.0 がサポートライブラリ依存の疑い → enableJetifier を強制
- AndroidManifest: usesCleartextTraffic=true がアプリ全体に適用、allowBackup が旧形式（dataExtractionRules なし）

スコープ外: Room（ローカルDBを使う要件が存在しないため導入しない。理由をPRに明記）。それ以外の発見済み課題はすべて本PRで解決する。

## 実行体制

- ブランチ: `claude/android-modernization-refactor-h1ksy6`（チェックアウト済み・clean）
- 実装はSonnetサブエージェントに委譲し、メイン（Fable 5）が設計・差分監査
- 各ステップ完了ごとに両フレーバービルド検証→コミット（git add は対象ファイル個別指定。-A / . 禁止）
- 新規依存（Compose BOM / Hilt / KSP / DataStore / Navigation / turbine 等）はすべて実装時にレジストリで実在バージョンを確認して完全固定
- 環境に Android SDK がないため Step 0 でセットアップ。取得不能ならビルド検証は CI に委ね、PR に明記

## 実装ステップ

### Step 0: 検証環境の準備（コード変更なし）
- Android cmdline-tools + `sdkmanager` で最新安定 platform（36 → 不可なら 35 → 34 の順でフォールバック）+ build-tools + platform-tools 導入、local.properties 設定。採用した SDK 値に以降のステップを連動
- ベースライン確認: `./gradlew :app:assembleProdDebug :app:assembleDemoDebug`

### Step 1: CI 修正
- `.github/workflows/buildApk.yaml`: JDK 11 → 17 (temurin)、actions を v4系 commit SHA pinning（SHA は gh api で実取得）、APK出力パスを `app/build/outputs/apk/prod/release/` に修正

### Step 2: ビルド基盤の全面モダン化（Kotlin DSL / Version Catalog / SDK35 / Compose / KSP）
- `settings.gradle` / `build.gradle`(root) / `app/build.gradle` → **`.gradle.kts` に変換**
- `gradle/libs.versions.toml` を唯一のバージョン管理に:
  - 追加: Compose BOM + material3 / ui / ui-tooling-preview / activity-compose / navigation-compose / lifecycle-runtime-compose / lifecycle-viewmodel-compose、`org.jetbrains.kotlin.plugin.compose`(2.2.20)、KSP（Kotlin 2.2.20 対応版）、Hilt 最新安定 + hilt-navigation-compose、datastore-preferences、core-splashscreen、zxing-android-embedded 4.3.0、junit / kotlinx-coroutines-test / turbine
  - 削除: koin（Step 4 で参照除去後）、eventbus（Step 5 後）、preference-ktx / recyclerview / constraintlayout（Step 6 後）— 依存削除は各ステップに連動
  - 修正: `kotlin-serialization` プラグインの version.ref を kotlin に、誤登録の `kotlinx-serialization`（Gradleプラグイン本体の implementation 混入）削除
- `app/build.gradle.kts`: compileSdk/targetSdk を Step 0 で導入した最新安定SDKへ、`buildFeatures { compose = true }`、kapt → **KSP**（Hilt用）、OpenAPI Generator 設定を KTS 移植
- **署名設定の外部化**: ハードコードされた storePassword 等を keystore.properties（gitignore対象）/ 環境変数から読み込む方式に変更。CI・ローカルが壊れないよう既存開発用キーストアへのフォールバックを用意し、開発専用である旨を文書化
- `buildSrc/` 全削除、`gradle.properties` の無効な `-XX:MaxPermSize` 削除
- dataBinding/viewBinding と kapt(@Bindable) は旧UIが残る間必要なので **Step 6 完了時に削除**
- このステップでは旧コード（Koin/EventBus/XML UI）はそのままビルド可能に保つ

### Step 3: データ層モダン化（Repository + DataStore + demoクラッシュ修正）
- `api/APIService.kt` を interface 化、現実装は `api/OpenApiAPIService.kt`（main）へ移設。demo の DemoAPIService を interface 直接実装に変更（NotImplementedError 引数全削除 → **クラッシュ修正**）
- `data/repository/` 新設: ItemRepository / StaffRepository / StoreRepository / SaleRepository / ServerRepository。`withContext(ioDispatcher)`（dispatcher 注入）で ViewModel から Dispatchers.IO を排除。itemIds 連結ロジックは SaleRepository へ
- **`data/settings/SettingsRepository` 新設（Preferences DataStore）**: serverAddress / runningMode / currentStore / currentStaff を Flow で公開、kotlinx.serialization で Store/Staff を JSON 永続化。既存 SharedPreferences からの**マイグレーション**（SharedPreferencesMigration）を設定し既存端末の設定を引き継ぐ
- GlobalConfig は SettingsRepository へ置換（setter 内 EventBus.post は廃止し、変更は DataStore の Flow 観測に置換。ServerSelectionInterceptor は App スコープで serverAddress Flow を collect して追従）

### Step 4: DI を Hilt (KSP) に統一（Koin 削除）
- `App.kt` に `@HiltAndroidApp`、startKoin 削除
- `di/` を Hilt モジュールに再編: NetworkModule（Json/OkHttp/Retrofit/Interceptor）、DataModule（DataStore/Repository バインド）、フレーバー別 ApiModule（prod: OpenApiAPIService + 生成API群 / demo: DemoAPIService。ソースセット分割を維持）
- ViewModel は `@HiltViewModel` + constructor injection、Activity は `@AndroidEntryPoint`、Compose からは hiltViewModel()
- koin 依存・モジュールファイル削除

### Step 5: EventBus 脱却（greenrobot 削除）
- 設定変更系イベント（ServerAddressChanged / RunningModeChanged / SelectShop）は Step 3 の DataStore Flow 観測で置換済み → イベント定義ごと削除
- バーコード系（BarcodeEvent）と会計完了（SentSaleSuccess）は、Single-Activity 化（Step 6）後は MainActivity の dispatchKeyEvent → MainViewModel 直接呼び出し + ViewModel 間は共有 `BarcodeEventBus`（MutableSharedFlow ラッパー、@Singleton で Hilt 提供）で配信
- `di/EventBusImpl.kt` / greenrobot 依存 / @Subscribe / onStart/onStop 登録解除コードを全削除

### Step 6: UI を Single-Activity + Compose (Material3) に全面移行
- `ui/theme/` に Material3 テーマ（Color/Type/Theme）新設。AndroidManifest は MainActivity 1つに（Launch/Setting Activity 削除）、`Theme.SplashScreen` ベースのテーマ + **SplashScreen API**（2秒スプラッシュは installSplashScreen + keepOnScreenCondition で再現）、landscape 固定維持、`enableEdgeToEdge()`
- **Navigation Compose**: NavHost に `main` / `settings` ルート。画面構成:
  - MainScreen: ModalNavigationDrawer + Scaffold + TopAppBar（店舗名・スタッフ名表示）+ 商品 LazyColumn + 合計金額 + 会計ボタン（旧 MainActivity + ItemListFragment + Adapter を統合）
  - ダイアログ: 電卓（旧 CalculatorDialogFragment + CalculatorView）、店舗選択（旧 StoreListDialogFragment + Adapter）、会計結果、エラー → Compose Dialog/AlertDialog。表示状態は UiState でホイスト、BroadcastChannel の suspend パターンは状態+コールバックに置換
  - SettingsScreen: preference-ktx 廃止。サーバーアドレス入力、QR読取（`rememberLauncherForActivityResult(ScanContract())`）、practiceモード切替を Compose で実装
- バーコード入力: BaseBarcodeReadableActivity + BarcodeReadDelegate のロジックを MainActivity の dispatchKeyEvent に集約（Compose と独立、純Kotlin部分は維持しテスト対象に）
- **削除**: res/layout 全11ファイル、全 Fragment/Adapter/カスタムView（SquareLayout/CalculatorView）、ItemStoreListContentViewModel、LaunchActivity/SettingActivity、`dataBinding`/`viewBinding`/kapt、不要依存（preference-ktx / recyclerview / constraintlayout 等）
- 残骸確認: `grep -r "DataBindingUtil\|androidx.databinding\|BR\.\|findViewById\|R.layout\|Fragment"`

### Step 7: 非推奨 API 残存ゼロ確認
- Handler.postDelayed / onActivityResult / IntentIntegrator / BroadcastChannel は Step 3〜6 で消滅している前提。grep で確認し漏れがあれば置換

### Step 8: テスト導入（ユニット + Compose UI）
- ユニット: BarcodeKindTest / BarcodeReadDelegate(純Kotlin化した onKey)Test / BarcodeEventBusTest / SaleRepositoryTest / StoreRepositoryTest / SettingsRepositoryTest（DataStore は一時ファイル + TestScope）/ 主要 ViewModel の UiState 遷移テスト（Fake Repository + StandardTestDispatcher + turbine）
- **Compose UIテスト**: Robolectric + createComposeRule で JVM 上で実行可能なスモークテスト（MainScreen の表示・会計ボタン活性、SettingsScreen の入力反映等）を追加
- `./gradlew testProdDebugUnitTest testDemoDebugUnitTest`

### Step 9: ロギング・jetifier・リリースビルド堅牢化・マニフェスト
- **orhanobut/logger → Timber へ移行**（Logger.d 等の呼び出しを置換、App で Timber.plant。debugビルドのみ出力）→ `checkJetifier` で他に要 jetifier 依存がないことを確認し **enableJetifier を削除**。CLAUDE.md のログ規約も Timber に更新
- **release ビルドの minify/shrinkResources 有効化**: `isMinifyEnabled = true` + `isShrinkResources = true`、proguard-rules.pro を新構成（Compose/Hilt/Retrofit/kotlinx.serialization/OpenAPI生成コード）に合わせて更新し、`assembleProdRelease` の成功と主要クラス保持を確認
- **AndroidManifest 堅牢化**: usesCleartextTraffic を network_security_config.xml へ移行（LAN内POSサーバーとのHTTP通信要件のため cleartext 自体は許可を維持し、理由をコメュメント化）、allowBackup に dataExtractionRules / fullBackupContent を追加

### Step 10: ドキュメント更新・仕上げ・検証・PR
- README.md / CLAUDE.md / .claude/CLAUDE.md を新アーキテクチャ（Compose / Hilt / DataStore / Single-Activity / Timber / Kotlin DSL）に合わせて更新
- 全体検証: `./gradlew clean :app:assembleProdDebug :app:assembleDemoDebug :app:assembleProdRelease lintProdDebug testProdDebugUnitTest`
- 残骸 grep: greenrobot / @Subscribe / koin / DataBindingUtil / BroadcastChannel / IntentIntegrator / SharedPreferences / dependencies.Dep / orhanobut
- /simplify → reviewer エージェントレビュー → 指摘の妥当性検証・修正
- push（`git push -u origin claude/android-modernization-refactor-h1ksy6`）→ base=main で PR 作成（GitHub MCP 使用）→ 完了レポート

## 主要リスクと対策

| リスク | 対策 |
|---|---|
| 環境に Android SDK なし / ネットワーク制限 | Step 0 で導入。不可なら CI を検証手段とし PR に明記 |
| 大規模リライトによる挙動退行（特にバーコード KeyEvent・会計フロー） | KeyEvent 解析ロジックは純Kotlinのまま維持しテストで固定。各ステップでビルドが通る分割、画面単位コミット |
| Hilt + KSP + Kotlin 2.2.20 の互換性 | 実装時に Hilt/KSP の Kotlin 2.2 対応バージョンをレジストリ・公式リリースノートで確認して固定。問題があれば Koin 4.x 維持にフォールバック（PR に判断理由を記載） |
| DataStore 移行で既存端末の設定消失 | SharedPreferencesMigration を設定（キー: setting_server_info / setting_running_mode / store / staff） |
| フレーバー別ソースセット（prod/demo） | ApiModule を両ソースセットで同名提供、毎ステップ両フレーバービルド |
| targetSdk 35+ + edge-to-edge | Compose Scaffold の insets で対応（オプトアウトせず正式対応） |
| minify 有効化による実行時クラッシュ（リフレクション/serialization） | ProGuard ルールを依存ごとに整備し release ビルド成功 + mapping 確認。実機検証不可のためリスクを PR に明記 |
| 署名外部化で CI の release ビルドが壊れる | 既存開発用キーストアへのフォールバックを残す |
| 巨大差分 | ステップごと（Step 6 は画面ごと）にコミット分割し、PR 本文に構成図と挙動差異を明記 |

## 検証方法

1. 各ステップ後: `./gradlew :app:assembleProdDebug :app:assembleDemoDebug`
2. 最終: 上記 + `lintProdDebug` + `testProdDebugUnitTest` + 残骸 grep
3. SDK導入不可の場合: CI（Step 1 で修復済みの workflow）でのビルド成否を確認
