package com.example.stonks.api


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.stonks.constants.mostActivesList
import com.example.stonks.constants.myAPIKey
import com.example.stonks.util.Stock
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL



fun getImage(ticker: String): Bitmap? {
    val url = "https://finnhub.io/api/v1/stock/profile2?" +
            "symbol=$ticker&token=c191shn48v6rd7oudbjg"


    return try {
//        val data = URL(url).readText()
//        Log.i("IMAGELOAD", data)
//        val imageURL = ProfileResp(data).data


        val connection = URL("https://finnhub.io/api/logo?symbol=$ticker")
            .openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        Log.w("IMAGELOAD", "Loaded $ticker icon successfully")
        BitmapFactory.decodeStream(input)
    } catch (ex: Exception) {
        Log.w("IMAGELOAD", "Unable to load $ticker icon")
        Log.e("IMAGELOAD", ex.toString())
        null
    }
}


/*
    Возвращает массив акций.
    Функция запрашивает у mboum выборки:
    самых активных, самых растущих и самых упавших акций.
    Каждый раз эти выборки могут отличаться, следовательно,
    акции из "избранного" могут и не загрузиться. Именно поэтому
    функция getStocks() запрашивает аж три списка.
 */
fun getStocks(): Array<Stock>? {

    val urlActives = "https://mboum.com/api/v1/co/collections/?list=$mostActivesList" +
            "&start=1&apikey=$myAPIKey"

    val resActives: String

    try {
        resActives = URL(urlActives).readText()
    } catch (ex: Exception) {
        Log.w("ERROR", "Error getting data from https://mboum.com")
        return null
    }

    val activeStocks: Array<StockResponse> = resActives.let { Resp(it).stockArray }

    return activeStocks.map {
        Stock.getStock(it.ticker,
            it.companyName,
            it.price,
            it.regularMarketPreviousClose,
            false,
            null)
    }.toTypedArray()
}

/*
val urlGainers = "https://mboum.com/api/v1/co/collections/?list=$dayGainersList" +
        "&start=1&apikey=$myAPIKey"
val resGainers = URL(urlGainers).readText()

val urlLosers = "https://mboum.com/api/v1/co/collections/?list=$dayLosersList" +
        "&start=1&apikey=$myAPIKey"
val resLosers = URL(urlLosers).readText()

    val stocks = (activeStocks+gainersStocks+losersStocks).distinctBy { it.ticker }
*/