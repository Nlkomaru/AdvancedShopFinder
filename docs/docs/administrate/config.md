# コンフィグについて


```jsonc
{
   "configVersion": 2, //Don't touch this
   "placeData":[
     //このデータから、最も近い都市の名前が取得される
      {
        "world": "world",
         "x":-532,
         "z":-85,
         "placeName":"<dark_green>もりもと</dark_green>"
      },
   ],
   "profileLimit" :  3,
   // あいまい検索では、多くのデータが取得されるためLimitが指定される。
   "fuzzySearchLimit": 50,
   "messageFormat":"<shop-type>: オーナー :<green><player-name></green> 値段: <green><price>/<shop-stacking-amount></green>個 在庫: <green><count></green> <br>座標: <yellow><world></yellow> x:<blue><x></blue> y:<blue><y></blue> z:<blue><z></blue> 距離: <green><distance></green>ブロック 最寄り: <near-town>から<green><near-town-distance></green>ブロック"
}
```

## `messageFormat`について

[MiniMessage](https://docs.advntr.dev/minimessage/format)形式がサポートされており、以下の値がinjectされる.

params
- `<shop-type>` : `<color:red>買取` | `<color:green>販売`
- `<player-name>` : ショップのオーナー名
- `<price>`: 販売金額
- `<shop-stacking-amount>`: ショップの販売単位: 例) `32`(個)
- `<count>` : 在庫数 : 1st販売のものが5stある場合は、`5 * 64`で表される
- `<distance>`: 直線距離
- `<near-town>`: 最寄りの町
- `<near-town-distance>`: 最寄りの町の中心からの直線距離

座標のパラメータについては、省略