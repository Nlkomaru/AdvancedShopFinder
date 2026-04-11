---
sidebar_position: 1
---

# ユーザーの検索オプションについて

これは、ユーザーごとに検索を行う際のprofileを個別に設定することができるようにすることにより、ユーザー体験を向上させることを目的としたものである。
なお、形式としては、JSONをサポートする。

```json
{
  "setting": "default",
  "findOptions": {
    "default": {
      "version": 1,
      "limitAmountOption": {
        "buyFindLimit": -1,
        "sellFindLimit": -1
      },
      "sortOption": {
        "buySortType": "DESC_PRICE_PER_ITEM",
        "sellSortType": "ASC_PRICE_PER_ITEM"
      },
      "showNoStockShop": false,
      "showOwnerNotHasEnoughMoneyShop": false
    }
  }
}
```