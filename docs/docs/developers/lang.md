---
sidebar_position: 10
---
# 言語を更新するには

このページでは、言語を更新する方法について説明します。

## GradleのTaskを実行する

言語を更新するには、GradleのTaskを実行します。

```bash
./gradlew generateTranslate
```

このTaskを実行すると、`src/main/resources/minecraft`ディレクトリに言語ファイルが生成されます。

なお、コマンドでの解析の際、空白によって文字列が分割されるため、`_`で文字列が結合されています。