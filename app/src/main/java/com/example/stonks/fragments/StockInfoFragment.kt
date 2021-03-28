package com.example.stonks.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.MainActivity
import com.example.stonks.R
import com.example.stonks.api.getNews
import com.example.stonks.util.NewsRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StockInfoFragment : Fragment() {

    var ticker: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ticker = it.getString("ticker")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_stock_info, container, false)

        val recyclerView = myView.findViewById<RecyclerView>(R.id.news_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(MainActivity.applicationContext())
        val adapter = NewsRecyclerViewAdapter()
        recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.Default) {
            val news = ticker?.let { getNews(it) }
            withContext(Dispatchers.Main) {
                if (news != null) {
                    adapter.refreshNews(news)
                }
            }
        }

        val exitBtn = myView.findViewById<ImageButton>(R.id.back_btn)
        exitBtn.setOnClickListener {
        }


        return myView
    }

    companion object {
        @JvmStatic
        fun newInstance(ticker: String) =
            StockInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("ticker", ticker)
                }
            }
    }

    interface OnFragmentExitListener {
        fun fragmentExit()
    }
}