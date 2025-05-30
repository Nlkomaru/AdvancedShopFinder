# ✨ Advanced Shop Finder ✨

## 🏪 概要 🔍

このプロジェクトは、QuickShopプラグインのためのシンプルなショップ検索ツールです。
近くのお店を素早く見つけるお手伝いをします！ 🗺️💨

## 🚀 使い方 🎮

Advanced Shop Finderを使うには、以下のコマンドを利用できます。
コマンドのエイリアスとして `asf`, `shopFinder`, `sf` も使用可能です。

### アイテム検索 🛒
- `/sf search <アイテム名>`: 指定したアイテムを販売しているショップを検索します。
  - 例: `/sf search diamond`
- オプション: `-p [プロファイル名]` で検索設定プロファイルを指定できます。

### エンチャント本検索 📚
- `/sf search-book <エンチャント名>`: 指定したエンチャントが付与された本を販売しているショップを検索します。
  - 例: `/sf search-book mending`
- オプション: `-p [プロファイル名]` で検索設定プロファイルを指定できます。

### あいまい検索 🤔
- `/sf fuzzy-search <検索語>`: アイテム名の一部など、あいまいなキーワードでショップを検索します。
  - 例: `/sf fuzzy-search dia`
- オプション: `-p [プロファイル名]` で検索設定プロファイルを指定できます。

### その他コマンド ⚙️
- `/sf help`: ヘルプメッセージを表示します。
- `/sf reload`: プラグインの設定を再読み込みします (管理者向け)。
- `/sf setting <プロファイル名>`: 検索設定プロファイルを作成・編集します (現在開発中)。
