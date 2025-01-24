---
sidebar_position: 1
---

# ユーザーの検索オプションについて

これは、ユーザーごとに検索を行う際のprofileを個別に設定することができるようにすることにより、ユーザー体験を向上させることを目的としたものである。
なお、形式としては、JSONをサポートする。

```json
{
  "default": "profile-1",
  "profiles": [
    {
      "name": "profile-1",
      "interface": "GUI",
      "sortOption": {
        "buySortType": "DESC_PRICE_PER_ITEM",
        "sellSortType": "ASC_PRICE_PER_ITEM"
      },
      "showNoStockShop": false,
      "textType": "COMPONENT"
    }
  ]
}
```