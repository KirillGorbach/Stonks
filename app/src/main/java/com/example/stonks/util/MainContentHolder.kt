package com.example.stonks.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.stonks.MainActivity
import com.example.stonks.MainActivityViewModel
import com.example.stonks.R
import com.example.stonks.constants.*
import com.example.stonks.fragments.main.util.StockRecyclerViewAdapter
import kotlin.math.abs



/*
    Класс, отвечаюший за набор элементов на экране
    Создан для того, чтобы действия с элементами могли не зависеть
    от одного фрагмента
    Основная задача - корректно отображать viewModel.searchState
 */
@SuppressLint("ClickableViewAccessibility")
class MainContentHolder(
    view: View,
    private val context: Context,
    private val onStockClickedListener: StockRecyclerViewAdapter.OnStockClickedListener,
    private val viewModel: MainActivityViewModel
) : StockRecyclerViewAdapter.OnFavSelectedListener {

    // нужно применять сортировку среди всех или только среди избранных
    var showFavList = false

    // из-за кликабельности элементов списка адаптер требует интерфейсы
    var adapter =
        StockRecyclerViewAdapter(this, onStockClickedListener)

    var stockRecyclerView: RecyclerView = view.findViewById(R.id.stock_list)

    val searchInput: SearchView = view.findViewById(R.id.search_input)
    private val labelFavStocks: Button = view.findViewById(R.id.favorite_btn)
    private val labelAllStocks: Button = view.findViewById(R.id.all_stocks_btn)
    val labelLoading: TextView = view.findViewById(R.id.loading_label)
    val labelNoFound: TextView = view.findViewById(R.id.no_stocks_found)

    private val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)

    // обновление данных по всайпу вверх
    private val swipeRefreshLayout: SwipeRefreshLayout =
        view.findViewById(R.id.refresh)


    init {
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshOnRefresh())
        searchInput.setOnQueryTextListener(getQueryTextListener())

        labelFavStocks.setOnClickListener{setFavoriteChosen()}
        labelAllStocks.setOnClickListener{setFavoriteNotChosen()}
        setFavoriteNotChosen()


        stockRecyclerView.layoutManager = LinearLayoutManager(context)
        stockRecyclerView.adapter = adapter

        toolbar.setOnTouchListener(getOnSwipeListener())
    }

    // ставим фильтр на избранные
    private fun setFavoriteChosen() {
        showFavList = true
        setLabelChosen(labelFavStocks)
        setLabelNotChosen(labelAllStocks)
        when (viewModel.searchState) {
            MainActivityViewModel.SearchState.SEARCH ->
                viewModel.setSearchData(MainActivityViewModel.SearchState.SEARCH_FAV)
            MainActivityViewModel.SearchState.ALL ->
                viewModel.setSearchData(MainActivityViewModel.SearchState.FAVORITE)
            else -> viewModel.setSearchData(viewModel.searchState)
        }
    }
    //снимаем фильтр на избранные
    private fun setFavoriteNotChosen() {
        showFavList = false
        setLabelChosen(labelAllStocks)
        setLabelNotChosen(labelFavStocks)
        when (viewModel.searchState) {
            MainActivityViewModel.SearchState.SEARCH_FAV ->
                viewModel.setSearchData(MainActivityViewModel.SearchState.SEARCH)
            MainActivityViewModel.SearchState.FAVORITE ->
                viewModel.setSearchData(MainActivityViewModel.SearchState.ALL)
            else -> viewModel.setSearchData(viewModel.searchState)
        }
    }


    private fun setLabelChosen(label: Button) {
        label.setTextColor(ContextCompat.getColor(context, colorLabelChosen))
        label.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            sizeLabelChosen
        )
    }
    private fun setLabelNotChosen(label: Button) {
        label.setTextColor(ContextCompat.getColor(context, colorLabelNotChosen))
        label.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            sizeLabelNotChosen
        )
    }

    private fun swipeRefreshOnRefresh(): ()->Unit = {
        viewModel.fetchData()
        swipeRefreshLayout.isRefreshing = false
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

    private fun search(text: String) {
        viewModel.currentSearchString = text
        if (viewModel.currentSearchString.isNotEmpty())
            if (showFavList)
                // искать среди избранных
                viewModel.setSearchData(MainActivityViewModel.SearchState.SEARCH_FAV)
            else
                // искать среди всех акций
                viewModel.setSearchData(MainActivityViewModel.SearchState.SEARCH)
        else
            // обновить уже стоящий фильтр
            viewModel.setSearchData(viewModel.searchState)

    }

    // при нажатии на кнопку "избранное" какой-то акции изменяем её состояние
    override fun onFavStateChanged(position: String) {
        viewModel.toggleFav(position)
        viewModel.setSearchData(viewModel.searchState)
    }

    // ловим свайпы на тулбаре
    private var gesturePrevX: Float? = null
    private var gesturePrevY: Float? = null

    private fun getOnSwipeListener(): View.OnTouchListener =
        View.OnTouchListener { v, event ->
            v.performClick()
            if (event.action == MotionEvent.ACTION_DOWN) {
                gesturePrevX = event?.x
                gesturePrevY = event?.y
            } else if (event.action == MotionEvent.ACTION_UP) {
                val deltaX = event?.x?.minus(gesturePrevX!!)
                val deltaY = event?.y?.minus(gesturePrevY!!)
                if (deltaX!=null && deltaY!=null){
                    if (abs(deltaX) > abs(deltaY) && abs(deltaX) > swipeThresholdShort){
                        if (deltaX > 0)
                            this.setFavoriteNotChosen()
                        else
                            this.setFavoriteChosen()
                    }
                }
            }
            return@OnTouchListener true
        }
}