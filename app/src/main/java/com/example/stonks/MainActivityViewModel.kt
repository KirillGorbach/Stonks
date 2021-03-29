package com.example.stonks

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.example.stonks.api.getImage
import com.example.stonks.api.getImageFromResourses
import com.example.stonks.api.getStocks
import com.example.stonks.database.AppDatabase
import com.example.stonks.database.icons.IconEntity
import com.example.stonks.database.tickers.FavoriteTickerEntity
import com.example.stonks.fragments.main.util.Stock
import com.example.stonks.util.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.Exception

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {


    // флаг загрузки данных
    var dataLoaded = MutableLiveData<Boolean>()
    // буфер из тех акций, которые должны быть на экране в данный момент
    var searchData = MutableLiveData<Array<Stock>>()
    // все акции, что имеет приложение
    var data = MutableLiveData<Array<Stock>>()

    var database = AppDatabase.getAppDatabase(getApplication())

    // чтобы не вводить строку много раз, viewModel хранит её
    var currentSearchString = ""
    // набор фильтров, по которым формируется searchData
    var searchState = SearchState.ALL

    init {
        dataLoaded.value = false
        fetchData()
    }


    // асинхронная загрузка списка акций
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
                setSearchData(searchState)
                // остальное можно загрузить потом
                dataLoaded.value = true
                initFavorites()
                initImages()
            }
        }
    }

    /*
        делаем снимок основных данных, затем для каждого тикера
        пробуем найти иконку в хранилище, если не нашли - скачиваем
        и сохраняем
        в БД хранятся пути к файлам с иконками
     */
    private fun initImages() {
        GlobalScope.launch(Dispatchers.Default) {
            val oldData = data.value?.map { it->it.ticker }?.toTypedArray()
            val icons = database?.iconDao()?.getIcons()
            oldData?.forEach { ticker->
                var image: Bitmap? = null
                var loadedFromStorage = false

                icons?.forEach {
                    if (it.ticker==ticker) {
                        image = getImageFromResourses(getApplication() as Context, it.source)
                        image?.let { loadedFromStorage = true }
                    }
                }
                if(!loadedFromStorage) {
                    image = getImage(ticker)
                    cacheImage(ticker, image)
                }

                withContext(Dispatchers.Main) {
                    data.value = data.value?.map {
                        if(it.ticker==ticker) {
                            it.image = image
                        }
                        it
                    }?.toTypedArray()
                }
            }
        }
    }


    /*
        пишем битмап в файл, затем сохраняем uri файла
        в базу данных
     */
    private fun cacheImage(ticker: String, image: Bitmap?) {
        image?.let {
            GlobalScope.launch(Dispatchers.Default) {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    try {
                        val file =
                            File((getApplication()as Context).filesDir, "$ticker.bitmap")
                        file.createNewFile()
                        if (file.exists()) {
                            val byteOutputStream = ByteArrayOutputStream()
                            image.compress(Bitmap.CompressFormat.PNG, 0, byteOutputStream)
                            val bitmapData = byteOutputStream.toByteArray()
                            with(FileOutputStream(file)) {
                                write(bitmapData)
                                flush()
                                close()
                            }
                            val iconsDao = database?.iconDao()
                            iconsDao?.insertIcon(
                                IconEntity(
                                    ticker = ticker,
                                    source = file.toURI().toString()
                                )
                            )
                        }
                    } catch (ex: Exception) {
                        ex.message?.let { it1 -> Log.e("IMAGELOAD", it1) }
                    }
                }
            }
        }
    }


    /*
        ищем тикеры избранных в БД
     */
    private fun initFavorites() {
        GlobalScope.launch(Dispatchers.Default) {
            val favDao = database?.favoriteTickerDao()
            val dbTickers = favDao?.getFavorites()
            withContext(Dispatchers.Main) {
                data.value = data.value?.map { stock ->
                    if(dbTickers?.find { it.ticker==stock.ticker }!=null)
                        stock.isFavorite=true
                    stock
                }?.toTypedArray()
            }
        }
    }

    // добавляем или удаляем тикер из таблицы избранных
    private fun saveFavTickerState(ticker: String, state: Boolean) {
        GlobalScope.launch(Dispatchers.Default) {
            val favDao = database?.favoriteTickerDao()
            if(state) {
                favDao?.insertTicker(FavoriteTickerEntity(ticker = ticker))
            } else {
                favDao?.deleteTicker(ticker)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        AppDatabase.destroyDatabase()
    }

    /*
        обработка нажатия на кнопку акции "избранное"
        добавляет или удаляет акцию из избранного
     */
    fun toggleFav(ticker: String) {
        searchData.value = data.value?.map {
            if (it.ticker == ticker){
                it.isFavorite = it.isFavorite!=true
                saveFavTickerState(ticker, it.isFavorite)
            }
            it
        }?.toTypedArray()
    }

    // устанавливаем фильтрацию данных
    fun setSearchData(state: SearchState) {
        when(state) {
            SearchState.ALL -> searchData.value = filterNoFilter(data.value)
            SearchState.FAVORITE -> searchData.value = filterDataFav(data.value)
            SearchState.SEARCH -> searchData.value = filterDataAll(data.value, currentSearchString)
            SearchState.SEARCH_FAV -> searchData.value = filterDataAll(filterDataFav(data.value), currentSearchString)
        }
        searchState = state
    }

    enum class SearchState {
        ALL,        // все акции
        FAVORITE,   // только избранные
        SEARCH,     // совпадают со строкой поиска
        SEARCH_FAV  // только избранные + совпадают со строкой поиска
    }

}
