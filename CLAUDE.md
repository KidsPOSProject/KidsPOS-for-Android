# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト情報
- **名称**: KidsPOS for Android - キッズビジネスタウンいちかわ用POSシステム
- **パッケージ**: info.nukoneko.cuc.android.kidspos
- **Android SDK**: minSdk 23, targetSdk 36 (gradle/libs.versions.toml と app/build.gradle.kts で管理)
- **Kotlin JVM Target**: 17

## 開発コマンド

### ビルド
```bash
# Prodビルド（本番用）
./gradlew assembleProdDebug    # デバッグビルド
./gradlew assembleProdRelease  # リリースビルド

# Demoビルド（デモ用）
./gradlew assembleDemoDebug
./gradlew assembleDemoRelease

# クリーンビルド
./gradlew clean assembleProdDebug
```

### テストとLint
```bash
# ユニットテスト実行
./gradlew testProdDebugUnitTest

# Lint実行（必須）
./gradlew lintProdDebug

# Lint自動修正
./gradlew lintFix
```

### デバイスへのインストール
```bash
./gradlew installProdDebug
./gradlew uninstallProdDebug
```

## アーキテクチャ構造

### Single-Activity + Compose
- **UI層**: Jetpack Compose (Material3)。MainActivity 1つ + Navigation Compose（main / settings ルート）
- **ViewModel層**: @HiltViewModel + StateFlow の UiState を公開。画面は collectAsStateWithLifecycle で観測
- **Model層**: data/repository/ の Repository、api/ の APIService、entity/ のデータモデル

### 依存性注入
- **Hilt (KSP)**: 唯一のDIフレームワーク
- モジュールは `di/hilt/` 配下（NetworkModule / DataModule、フレーバー別 ApiModule は src/prod・src/demo）
- ViewModel はコンストラクタインジェクション、Compose からは hiltViewModel()

### 状態と画面間通信
- 設定値（サーバーアドレス / 動作モード / 店舗 / スタッフ）は SettingsRepository（Preferences DataStore）の Flow を観測
- バーコード読取イベントは BarcodeEventBus（MutableSharedFlow、@Singleton）で配信
- greenrobot EventBus・LiveData・DataBinding は使用しない

### 非同期処理
- **Kotlin Coroutines**: `viewModelScope` を使用。Repository は dispatcher 注入で `withContext` する
- **Retrofit + OkHttp**: API通信（ServerSelectionInterceptor が DataStore のサーバーアドレスに追従）

## コーディング規則

### ログ出力（重要）
```kotlin
// ❌ 禁止
print("message")
println("message")

// ✅ 必須：Timber使用
Timber.d("Debug message")
Timber.e(throwable, "Error message")
Timber.i("Info message")
```

### ファイル命名
- 画面: `*Screen.kt`（Composable）
- ViewModel: `*ViewModel.kt`
- Repository: `*Repository.kt`

### バーコード機能
- KeyEvent の解析は `BarcodeKeyEventDecoder`（純Kotlin）が担当
- MainActivity の dispatchKeyEvent → BarcodeEventBus → 各 ViewModel が collect

### リソース管理
- 全ての文字列は `strings.xml` に定義（日本語のみ）
- Composable 内の文字列解決は `stringResource()` を使用（context.getString は Lint エラーになる）
- 既存実装パターンを必ず参照・踏襲する

## テスト

- 配置: `app/src/test`（フレーバー共通）と `app/src/testProd`（BuildConfig.DEMO_MODE 依存のテスト）
- モックライブラリは使わず、Fake（FakeAPIService / FakePreferencesDataStore）+ MainDispatcherRule + runTest + turbine を使用
- 共通テストユーティリティは `app/src/test/.../testutil/` に配置

## タスク完了時の確認事項

1. **ビルド成功確認**: `./gradlew assembleProdDebug`
2. **Lintエラーゼロ**: `./gradlew lintProdDebug`
3. **テスト合格**: `./gradlew testProdDebugUnitTest`
4. **Timber使用確認**: print文・println文が含まれていないこと
5. **既存パターン準拠**: 類似機能の実装を参照したこと
