# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト情報
- **名称**: KidsPOS for Android - キッズビジネスタウンいちかわ用POSシステム
- **パッケージ**: info.nukoneko.cuc.android.kidspos
- **Android SDK**: minSdk 23, targetSdk 33 (buildSrc/Versions.ktで管理)
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
./gradlew test
./gradlew testProdDebugUnitTest

# Lint実行（必須）
./gradlew lint
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

### MVVMパターン
- **View層**: Activity/Fragment (DataBinding/ViewBinding使用)
- **ViewModel層**: AndroidX ViewModelを継承
- **Model層**: Repository、API、Entity

### 依存性注入
- **Koin**: メインのDIフレームワーク（modules: coreModule, apiModule, viewModelModule）
- **Hilt**: Dagger Hiltも併用可能
- 新規依存は `di/module/` 配下の適切なモジュールに追加

### イベント駆動
- **EventBus** (org.greenrobot): アプリ全体のイベント通信
- イベントクラスは `event/` パッケージに配置
- `@Subscribe(threadMode = ThreadMode.MAIN)` でハンドラー定義

### 非同期処理
- **Kotlin Coroutines**: `viewModelScope`、`lifecycleScope` を使用
- **Retrofit + OkHttp**: API通信（ServerSelectionInterceptorで動的サーバー切替）

## コーディング規則

### ログ出力（重要）
```kotlin
// ❌ 禁止
print("message")
println("message")
debugPrint("message")

// ✅ 必須：Logger使用
Logger.d("Debug message")
Logger.e("Error message")
Logger.i("Info message")
```

### ファイル命名
- Activity: `*Activity.kt`
- Fragment: `*Fragment.kt`
- ViewModel: `*ViewModel.kt`
- レイアウト: `activity_*.xml`, `fragment_*.xml`, `item_*.xml`

### バーコード機能
- `BarcodeReadDelegate` インターフェースを実装
- `BaseBarcodeReadableActivity` を継承してActivity作成

### リソース管理
- 全ての文字列は `strings.xml` に定義（日本語のみ）
- 既存実装パターンを必ず参照・踏襲する

## タスク完了時の確認事項

1. **ビルド成功確認**: `./gradlew assembleProdDebug`
2. **Lintエラーゼロ**: `./gradlew lint`
3. **テスト合格**: `./gradlew test`
4. **Logger使用確認**: print文が含まれていないこと
5. **既存パターン準拠**: 類似機能の実装を参照したこと
