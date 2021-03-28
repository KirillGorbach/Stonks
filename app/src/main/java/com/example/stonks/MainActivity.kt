package com.example.stonks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.stonks.constants.*
import com.example.stonks.util.StockRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(),
        StockRecyclerViewAdapter.OnFavSelectedListener {

    private val viewModel by lazy {
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    init {
        instance = this
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MainActivity? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

    }

    var adapter = StockRecyclerViewAdapter(this)

    lateinit var stockRecyclerView: RecyclerView

    private lateinit var searchInput: SearchView
    private lateinit var searchBtn: ImageButton
    private lateinit var labelFavStocks: TextView
    private lateinit var labelAllStocks: TextView
    private lateinit var labelLoading: TextView
    private lateinit var labelNoFound: TextView
    private lateinit var favList: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchData()
            swipeRefreshLayout.isRefreshing = false
        }


        searchInput = findViewById(R.id.search_input)
        searchInput.setOnQueryTextListener(getQueryTextListener())


        labelLoading = findViewById(R.id.loading_label)
        labelNoFound = findViewById(R.id.no_stocks_found)

        labelFavStocks = findViewById(R.id.favorite_label)
        labelAllStocks = findViewById(R.id.all_stocks_label)
        initLabels()


        stockRecyclerView = findViewById(R.id.stock_list)
        stockRecyclerView.layoutManager = LinearLayoutManager(this)
        stockRecyclerView.adapter = adapter

        initViewModel()

        loadFav()

    }


    private fun initViewModel() {

        viewModel.data.observe(this, {

            viewModel.setSearchData(viewModel.searchState)

            if (this::favList.isInitialized) {
                if (favList.isNotEmpty()) {
                    viewModel.defineFav(favList)
                    favList = mutableListOf()
                }
            }
        })

        viewModel.searchData.observe(this, {
            adapter.refreshStocks(it)
            if (it.isEmpty())
                labelNoFound.visibility = View.VISIBLE
            else
                labelNoFound.visibility = View.GONE
        })


        if (viewModel.dataLoaded.value!=true) {
            labelLoading.visibility = View.VISIBLE
        }
        viewModel.dataLoaded.observe(this, {
            if (it==true)
                labelLoading.visibility = View.GONE
            else
                labelLoading.visibility = View.VISIBLE
        })
    }
    private fun initLabels() {
        setLabelChosen(labelAllStocks)
        setLabelNotChosen(labelFavStocks)

        labelFavStocks.setOnClickListener {
            favStocksInputListen()
        }
        labelAllStocks.setOnClickListener {
            allStocksInputListen()
        }
    }

    private fun allStocksInputListen() {
        setLabelChosen(labelAllStocks)
        setLabelNotChosen(labelFavStocks)
        viewModel.setSearchData(MainActivityViewModel.SearchState.ALL)
    }
    private fun favStocksInputListen() {
        setLabelChosen(labelFavStocks)
        setLabelNotChosen(labelAllStocks)
        viewModel.setSearchData(MainActivityViewModel.SearchState.FAVORITE)
    }

    private fun setLabelChosen(label: TextView) {
        label.setTextColor(ContextCompat.getColor(this, colorLabelChosen))
        label.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            sizeLabelChosen
        )
    }
    private fun setLabelNotChosen(label: TextView) {
        label.setTextColor(ContextCompat.getColor(this, colorLabelNotChosen))
        label.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            sizeLabelNotChosen
        )
    }

    private fun search(text: String) {
        viewModel.currentSearchString = text
        if (viewModel.currentSearchString.isNotEmpty()) {
            viewModel.setSearchData(MainActivityViewModel.SearchState.SEARCH)
        } else {
            viewModel.setSearchData(MainActivityViewModel.SearchState.ALL)
        }
    }
    private fun getQueryTextListener() =
        object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    search(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    search(query)
                }
                return true
            }
        }

    override fun onStop() {
        super.onStop()
        saveFav()
    }

    private fun saveFav() {
        GlobalScope.launch(Dispatchers.Default) {
            val favList = viewModel.data.value?.filter {
                it.isFavorite
            }?.map { it.ticker }?.toTypedArray()

            try {
                this@MainActivity.openFileOutput(fileName, Context.MODE_PRIVATE)
                        .use { stream ->
                            stream.bufferedWriter().use { out->
                                favList?.forEach { out.write("$it\n") }
                            }
                        }
            } catch (ex: Exception) {
                Log.w("FILEWRITE", "Could not save file $fileName")
            }
        }
    }
    private fun loadFav() {
        GlobalScope.launch(Dispatchers.Default) {
            val tickersList = mutableListOf<String>()
            try {
                this@MainActivity.openFileInput(fileName)
                        .use { stream ->
                            stream.bufferedReader().forEachLine {
                                tickersList.add(it)
                            }
                        }
            } catch (ex: Exception) {
                Log.w("FILEREAD", "Could not load file $fileName")
            }
            withContext(Dispatchers.Main) {
                favList = tickersList
            }
        }
    }

    override fun onSelect(position: String) {
        viewModel.toggleFav(position)
    }

}


/*private fun initData() {
       CoroutineScope(Dispatchers.Default).launch {
           var itIsFirstFailure = true
           var loaded = false
           var loadedStocks: Array<Stock>? = null

           while (!loaded) {

               loadedStocks = getStocks()
               if(loadedStocks!=null) {
                   loaded = true
               } else {
                   if (itIsFirstFailure) {
                       withContext(Dispatchers.Main) { warnToast() }
                       itIsFirstFailure = false
                   }
                   delay(500)
               }
           }

           stockList = loadedStocks!!
           withContext(Dispatchers.Main) {
               stockRecyclerView.adapter = StockRecyclerViewAdapter(stockList)
           }
       }
   }*/