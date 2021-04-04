package com.example.stonks.fragments.main.util

import android.graphics.Bitmap
import android.graphics.Color
import com.example.stonks.constants.priceFormat

class Stock(val ticker: String,
            val companyName: String,
            var price: String,
            var delta: String,
            var deltaColor: Int,
            var isFavorite: Boolean,
            var image: Bitmap?){


    override fun toString(): String {
        return ticker
    }

    companion object {
        fun getStock(ticker: String,
                     companyName: String,
                     price: Float,
                     previousClosePrice: Float,
                     isFavorite: Boolean,
                     image: Bitmap?
        ): Stock {

            val priceNew = priceFormat.format(price)

            val companyNameNew = when (companyName.length) {
                in 0..25 -> companyName
                else -> companyName.substring(0,22).plus("...")
            }
            val deltaNew: String
            val deltaColorNew: Int
            if (price-previousClosePrice<=0) {
                deltaNew = priceFormat.format(price - previousClosePrice)
                deltaColorNew = Color.RED
            } else {
                deltaNew = "+$priceFormat".format(price - previousClosePrice)
                deltaColorNew = Color.GREEN
            }

            return Stock(ticker,
                companyNameNew,
                priceNew,
                deltaNew,
                deltaColorNew,
                isFavorite,
                image)
        }
    }
}