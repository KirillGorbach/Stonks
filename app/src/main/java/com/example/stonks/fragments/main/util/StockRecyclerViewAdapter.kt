package com.example.stonks.fragments.main.util

import android.util.Log
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
import com.example.stonks.constants.iconImageNotFound
import com.example.stonks.constants.swipeThreshold
import kotlin.math.abs


// адаптер основного списка акций
class StockRecyclerViewAdapter(
    private val onFavSelectedListener: OnFavSelectedListener,
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

            /*
                recyclerView имеет баг - при изменении содержимого
                иногда дефолтная картинка из stock_list_item заменяется
                на одну из загруженных
                например - иногда CSCO (без картинки) получает логотип
                акции F и т.п.
                поэтому устанавливаем дефолтную картинку программно
             */
            if (stock.image!=null) {
                icon.setImageBitmap(stock.image)
            } else {
                icon.setImageResource(iconImageNotFound)
            }

            if(stock.isFavorite)
                favBtn.setImageResource(favoriteOnImage)
            else
                favBtn.setImageResource(favoriteOffImage)

            // нажатие на кнопку "избранное"
            favBtn.setOnClickListener {
                onFavSelectedListener.onFavStateChanged(stock.ticker)
            }

            setOnClickListener { onStockClickedListener.onStockClicked(stock.ticker) }
        }


    }

    interface OnFavSelectedListener {
        fun onFavStateChanged(position: String)
    }


    interface OnStockClickedListener {
        fun onStockClicked(ticker: String)
    }

}