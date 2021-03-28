package com.example.stonks.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.MainActivity
import com.example.stonks.R
import com.example.stonks.constants.favoriteOffImage
import com.example.stonks.constants.favoriteOnImage
import com.example.stonks.constants.priceFormat

class StockRecyclerViewAdapter(
    private val onFavSelectedListener: OnFavSelectedListener
) : RecyclerView.Adapter<StockRecyclerViewAdapter.ViewHolder>() {

    private var values: Array<Stock> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
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
        private val favBtn: ImageButton = view.findViewById(R.id.favorite_btn)
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

            favBtn.setOnClickListener {
                onFavSelectedListener.onSelect(stock.ticker)
            }
        }
    }

    interface OnFavSelectedListener {
        fun onSelect(position: String)
    }
}