package com.example.stonks.api


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.stonks.MainActivity
import com.example.stonks.constants.deltaInterval
import com.example.stonks.constants.mostActivesList
import com.example.stonks.constants.myFINNHUBAPIKey
import com.example.stonks.constants.myMBOUMAPIKey
import com.example.stonks.fragments.news.util.News
import com.example.stonks.fragments.main.util.Stock
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

fun getStockMainInfo(): Array<Stock>? {
    val urlActives = "https://mboum.com/api/v1/co/collections/?list=$mostActivesList" +
            "&start=1&apikey=$myMBOUMAPIKey"

    return try {
        val resActives = URL(urlActives).readText()
        val activeStocks: Array<StockResponse> = resActives.let { Resp(it).stockArray }
        var result = activeStocks.map { Stock.getStock(
                it.ticker,
                it.companyName,
                0f,
                0f,
                false,
                null
        ) }.toList()

        if (result.size>10)
            result = result.subList(0, 10)

         result.toTypedArray()
    } catch (e: Exception) {
        Log.w("LOAD", "Could not load stocks info")
        null
    }
}


fun getStockWithPrice(stock: Stock): Stock? {
    val url = "https://finnhub.io/api/v1/quote?symbol=${stock.ticker}" +
            "&token=$myFINNHUBAPIKey"
    return try {
        val resp = FinnhubQuote(URL(url).readText())
        Stock.getStock(
                stock.ticker,
                stock.companyName,
                resp.currentPrice,
                resp.previousPrice,
                stock.isFavorite,
                stock.image
        )
    } catch (e: Exception) {
        //Это сообщение появляется слишком часто
//        Log.w("LOAD", "Could not load stock price: ${stock.ticker}")
        null
    }
}

// Загружаем битмап-изображение с финхаба
fun getImage(ticker: String): Bitmap? {

    return try {
        val connection = URL("https://finnhub.io/api/logo?symbol=$ticker")
            .openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (ex: Exception) {
        Log.w("IMAGELOAD", "Unable to load $ticker icon")
        null
    }
}

// достаём картинку из файла
fun getImageFromResourses(appContext: Context, fileUri: String): Bitmap? {
    return try {
        val uri: Uri = Uri.parse(fileUri)
        val stream = appContext.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(stream)
        stream?.close()
        bitmap
    } catch (ex:Exception) {
        null
    }
}




fun getNews(ticker: String): Array<News>? {
    val url = "https://mboum.com/api/v1/ne/news/?symbol=$ticker&apikey=$myMBOUMAPIKey"

    return try {
        val response = URL(url).readText()
        NewsListResp(response).data
    } catch (ex: Exception) {
        Log.w("NEWS", "Could not load news list for $ticker")
        null
    }
}

/*
    запрашиваем историю изменений акции за последнее время
    при deltaInterval = "1d" mboum выдаёт данные за 5 лет каждый день

    из полученного json мы выбираем только строки "close" - цена при
    закрытии торгов в этот день
 */
fun getGraphicData(ticker: String): MutableList<Float>? {
    return try {
        val url = "https://mboum.com/api/v1/hi/history/?symbol=$ticker&interval=$deltaInterval" +
                "&diffandsplits=true&apikey=$myMBOUMAPIKey"
        val resp: MutableList<Float> = mutableListOf()
        for (i in Regex("""close\": [0-9.]*""").findAll(URL(url).readText())) {
            resp.addAll( i.groupValues
                    .map { it.substring(8).toFloat() }
                    .toTypedArray())
        }
        resp
    } catch (ex: Exception) {
        ex.printStackTrace()
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

fun getStocks(): Array<Stock>? {

    val urlActives = "https://mboum.com/api/v1/co/collections/?list=$mostActivesList" +
            "&start=1&apikey=$myMBOUMAPIKey"

    val resActives: String

    try {
        resActives = URL(urlActives).readText()
    } catch (ex: Exception) {
        Log.w("GETTINGSTOCKS", "Error getting data from https://mboum.com")
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

val urlGainers = "https://mboum.com/api/v1/co/collections/?list=$dayGainersList" +
        "&start=1&apikey=$myAPIKey"
val resGainers = URL(urlGainers).readText()

val urlLosers = "https://mboum.com/api/v1/co/collections/?list=$dayLosersList" +
        "&start=1&apikey=$myAPIKey"
val resLosers = URL(urlLosers).readText()

    val stocks = (activeStocks+gainersStocks+losersStocks).distinctBy { it.ticker }
*/
