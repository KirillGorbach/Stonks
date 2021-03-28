package com.example.stonks.api

import org.json.JSONObject

class StockResponse(js:String): JSONObject(js) {
    val ticker: String = this.optString("symbol")
    val companyName: String = this.optString("shortName")
    val price = this.optDouble("regularMarketPrice")
    val regularMarketPreviousClose =
        this.optDouble("regularMarketPreviousClose")
}

class Resp(js: String): JSONObject(js) {
    val stockArray = this.getJSONArray("quotes")
        .let {
            0.until(it.length()).map { i -> StockResponse(it[i].toString())
            }.toTypedArray()
        }
}

class ProfileResp(js: String): JSONObject(js) {
    val data: String = this.optString("logo")
}
