package com.example.stonks

import android.util.Log
import androidx.lifecycle.*
import com.example.stonks.api.getImage
import com.example.stonks.api.getStocks
import com.example.stonks.util.*
import kotlinx.coroutines.*
import java.lang.Exception

class MainActivityViewModel : ViewModel() {



    var dataLoaded = MutableLiveData<Boolean>()
    var searchData = MutableLiveData<Array<Stock>>()
    var data = MutableLiveData<Array<Stock>>()



    var favTickersList = mutableListOf<String>()
    var currentSearchString = ""
    var searchState = SearchState.ALL

    init {
        dataLoaded.value = false
        fetchData()
        initImages()
        //defineFav(favTickersList)
        dataLoaded.value = true
    }



    fun fetchData() {
        GlobalScope.launch(Dispatchers.Default) {
            var stocks: Array<Stock>? = null
            while (stocks == null) {
                stocks = try {
                    getStocks()
                } catch (ex: Exception) {
                    delay(1000)
                    null
                }
            }
            withContext(Dispatchers.Main) {
                data.value = stocks
                setSearchData(SearchState.ALL)
            }
        }
    }

    private fun initImages() {
        GlobalScope.launch(Dispatchers.Default) {
            val oldData = data.value
            Log.i("IMAGELOAD", "Loading start")
            oldData?.forEach {
                Log.i("IMAGELOAD", "Loading ${it.ticker}")
                val image = getImage(it.ticker)

                withContext(Dispatchers.Main) {
                    data.value = data.value?.map { stock ->
                        if(stock.ticker==it.ticker)
                            stock.image = image
                        stock
                    }?.toTypedArray()
                    Log.i("IMAGELOAD", it.ticker)
                }
            }

        }
    }

    /*fun defineFav(tickersList: MutableList<String>) {

//        if (tickersList.isNotEmpty()) {
//            favTickersList = tickersList
//            data.value = data.value?.map { stock ->
//                tickersList.find { it == stock.ticker }
//                    ?.let { stock.isFavorite = true }
//                stock
//            }?.toTypedArray()
//        }
    }*/

    fun toggleFav(ticker: String) {
        searchData.value = filterToggleFav(searchData.value, ticker)
    }

    fun setSearchData(state: SearchState) {
        searchState = state
        when(state) {
            SearchState.ALL -> searchData.value = filterNoFilter(data.value)
            SearchState.FAVORITE -> searchData.value = filterDataFav(data.value)
            SearchState.SEARCH -> searchData.value = filterDataAll(data.value, currentSearchString)
        }
    }

    enum class SearchState {
        ALL,
        FAVORITE,
        SEARCH
    }

}


/*
    fun saveFav() {
        GlobalScope.launch(Dispatchers.Default) {
            val favList = data.value?.filter {
                it.isFavorite
            }?.map { it.ticker }?.toTypedArray()

            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val file = File(Environment.getExternalStorageDirectory(), fileName)
                favList?.joinToString(" ")?.let { Log.i("WRITE", it) }

                if (file.exists()){
                    favList?.joinToString(" ")?.let { Log.i("WRITE", it) }
                    file.printWriter().use { out->
                        favList?.forEach { out.println(it) }
                    }
                } else {
                    try {
                        file.createNewFile()

                        file.printWriter().use { out->
                            favList?.forEach { out.println(it) }
                        }
                    } catch (ex: Exception) {
                        Log.i("FILE", "Unable to create file $fileName")
                    }
                }

            }
        }
    }

    private fun getFavFromFile() {

        GlobalScope.launch(Dispatchers.Default) {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val tickersList = mutableListOf<String>()

                val file = File(Environment.getExternalStorageDirectory(), fileName)
                Log.i("READFILE", tickersList.toString())
                if (file.exists()){
                    file.inputStream().bufferedReader()
                            .forEachLine { tickersList.add(it) }
                    Log.i("READFILE", tickersList.joinToString(" "))

                    withContext(Dispatchers.Main) {
                        data.value = data.value?.map { stock ->
                            tickersList.find { it==stock.ticker }
                                    ?.let { stock.isFavorite = true }
                            stock
                        }?.toTypedArray()
                    }
                }
            }
        }
    }
*/