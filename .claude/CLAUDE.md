# KidsPOS Android プロジェクト概要

## プロジェクト基本情報
- **名称**: KidsPOS for Android
- **パッケージ名**: info.nukoneko.cuc.android.kidspos
- **用途**: キッズビジネスタウンいちかわで使用するAndroid用POSシステム
- **プラットフォーム**: Android (minSdk: 設定による、targetSdk: 設定による)

## 技術スタック

### 言語とフレームワーク
- **言語**: Kotlin
- **最小SDK**: buildSrcで管理 (Versions.minSdk)
- **ターゲットSDK**: buildSrcで管理 (Versions.targetSdk)
- **JVMターゲット**: 17

### アーキテクチャパターン
- **MVVM (Model-View-ViewModel)**: ViewModelを使用した実装
- **DI (依存性注入)**: Koin + Dagger Hilt
- **イベントバス**: EventBus (org.greenrobot)

### 主要ライブラリ
- **UI**: Android Material Design、DataBinding、ViewBinding
- **非同期処理**: Kotlin Coroutines
- **ネットワーク**: Retrofit + OkHttp
- **シリアライゼーション**: Kotlinx Serialization
- **バーコード読取**: ZXing Android
- **ログ**: Logger (orhanobut)
- **クラッシュレポート**: Firebase Crashlytics

## プロジェクト構造

### ビルドバリアント
- **buildTypes**: debug / release
- **productFlavors**: prod / demo

### 主要ディレクトリ構成
```
app/src/main/kotlin/info/nukoneko/cuc/android/kidspos/
├── api/          # API通信関連
├── di/           # 依存性注入
├── entity/       # データモデル
├── error/        # 例外定義
├── event/        # イベントバス関連
├── extensions/   # 拡張関数
├── ui/           # UI関連（Activity、Fragment、ViewModel）
└── util/         # ユーティリティ
```

### 機能構成
1. **商品管理**: バーコード読取による商品登録
2. **店舗切替**: 複数店舗の切り替え機能
3. **会計処理**: 計算機能付き会計システム
4. **スタッフ管理**: スタッフ情報の読み込み
5. **設定**: サーバー接続先設定、練習モード切替

## 開発規約

### 命名規則
- **Activity**: `*Activity.kt` (例: MainActivity.kt)
- **Fragment**: `*Fragment.kt` (例: ItemListFragment.kt)
- **ViewModel**: `*ViewModel.kt` (例: MainViewModel.kt)
- **レイアウト**: 
  - Activity: `activity_*.xml`
  - Fragment: `fragment_*.xml`
  - Item: `item_*.xml`

### リソース管理
- **文字列**: strings.xml で日本語リソースを管理
- **画像**: mipmap-* フォルダでアイコン管理
- **スタイル**: styles.xml でテーマ管理

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
- signing設定は開発用（ストアリリースなし）
- ProGuardは無効化されている

### ビルド設定
- Java 8 APIのデシュガリングが有効
- リソース設定は日本語のみ

## 今後の開発における注意点
1. 新機能追加時はMVVMパターンに従う
2. DIはKoinモジュールに追加
3. イベント通信はEventBusを使用
4. 文字列リソースは必ずstrings.xmlに定義
5. バーコード読取機能はBarcodeReadDelegateを継承