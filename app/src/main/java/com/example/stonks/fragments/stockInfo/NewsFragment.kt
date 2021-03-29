package com.example.stonks.fragments.stockInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.R
import com.example.stonks.api.getNews
import com.example.stonks.fragments.ActivityFragmentListener
import com.example.stonks.fragments.stockInfo.util.NewsRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NewsFragment(
    private val activityFragmentListener: ActivityFragmentListener,
    private val ticker: String
): Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_news, container, false)

        myView.findViewById<TextView>(R.id.fragment_name).text = ticker

        val recyclerView = myView.findViewById<RecyclerView>(R.id.news_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = NewsRecyclerViewAdapter()
        recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.Default) {
            val news = getNews(ticker)
            withContext(Dispatchers.Main) {
                if (news != null) {
                    adapter.refreshNews(news)
                    view?.findViewById<TextView>(R.id.no_news_found)?.visibility =
                        View.GONE
                }
            }
        }

        val exitBtn = myView.findViewById<ImageButton>(R.id.back_btn)
        exitBtn.setOnClickListener {
            activityFragmentListener.backToMainFragment()
        }


        return myView
    }
}