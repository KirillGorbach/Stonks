package com.example.stonks.util

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.R
import com.example.stonks.constants.favoriteOffImage
import com.example.stonks.constants.favoriteOnImage
import com.example.stonks.constants.swipeThreshold
import kotlin.math.abs


// адаптер основного списка акций
class StockRecyclerViewAdapter(
    private val onFavSelectedListener: OnFavSelectedListener,
    private val onSwipedListener: OnSwipedListener,
    private val onStockClickedListener: OnStockClickedListener
) : RecyclerView.Adapter<StockRecyclerViewAdapter.ViewHolder>() {

    private var values: Array<Stock> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(values[position])

    fun refreshStocks(stocks: Array<Stock>) {
        this.values = stocks
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tickerName: TextView = view.findViewById(R.id.ticker)
        private val companyName: TextView = view.findViewById(R.id.co_name)
        private val stockPrice: TextView = view.findViewById(R.id.price)
        private val stockDelta: TextView = view.findViewById(R.id.delta)
        private val favBtn: ImageButton = view.findViewById(R.id.change_favorite_btn)
        private val icon: ImageView = view.findViewById(R.id.stock_icon)

        fun bind(stock: Stock) = with(itemView) {
            tickerName.text = stock.ticker
            companyName.text = stock.companyName
            stockPrice.text = stock.price
            stockDelta.text = stock.delta
            stockDelta.setTextColor(stock.deltaColor)

            stock.image?.let { icon.setImageBitmap(stock.image) }

            if(stock.isFavorite)
                favBtn.setImageResource(favoriteOnImage)
            else
                favBtn.setImageResource(favoriteOffImage)

            // нажатие на кнопку "избранное"
            favBtn.setOnClickListener {
                onFavSelectedListener.onFavStateChanged(stock.ticker)
            }

            setOnTouchListener(getOnSwipeListener())
            setOnClickListener { onStockClickedListener.onStockClicked(stock.ticker) }
        }


        /*
            ловим свайпы на каждом элементе
            т.к. RcyclerView сам отлавливает onTouch,
            приходится делать свайпы только на его элементах
         */
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
                        if (abs(deltaX) > abs(deltaY) && abs(deltaX) > swipeThreshold){
                            if (deltaX > 0)
                                onSwipedListener.onSwipeLeft()
                            else
                                onSwipedListener.onSwipeRight()
                        }
                    }
                }
                return@OnTouchListener true
            }
    }

    interface OnFavSelectedListener {
        fun onFavStateChanged(position: String)
    }

    interface OnSwipedListener {
        fun onSwipeLeft()
        fun onSwipeRight()
    }

    interface OnStockClickedListener {
        fun onStockClicked(ticker: String)
    }

}