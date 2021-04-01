package com.example.stonks.fragments.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.MainActivity
import com.example.stonks.R
import com.example.stonks.api.getNews
import com.example.stonks.fragments.news.util.News
import com.example.stonks.fragments.news.util.NewsRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



const val TAG_TITLES = "titles"

class NewsFragment(
    private val ticker: String
): Fragment() {

    private var newsArray: MutableList<News> = mutableListOf()
    private lateinit var adapter: NewsRecyclerViewAdapter
    lateinit var noNewsLabel: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true

        val myView = inflater.inflate(R.layout.fragment_news, container, false)

        val label = "$ticker NEWS"
        myView.findViewById<TextView>(R.id.fragment_name).text = label

        noNewsLabel = myView.findViewById(R.id.no_news_found)

        val recyclerView = myView.findViewById<RecyclerView>(R.id.news_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = context?.let { NewsRecyclerViewAdapter(this.ticker) }!!
        recyclerView.adapter = adapter

        if (newsArray.isEmpty()) fetchNews()

        val exitBtn = myView.findViewById<ImageButton>(R.id.back_btn)
        exitBtn.setOnClickListener {
            MainActivity.getFragmentListener().backToMainFragment()
        }

        return myView
    }

    private fun fetchNews() {
        GlobalScope.launch(Dispatchers.Default) {
            val news = getNews(ticker)
            withContext(Dispatchers.Main) {
                if (news != null) {
                    adapter.refreshNews(news)
                    newsArray = news.toMutableList()
                    noNewsLabel.visibility = View.GONE
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(TAG_TITLES, newsArray.map { it.title }.toTypedArray())
        for (i in newsArray) {
            outState.putString(i.title, i.link)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        val titles = savedInstanceState?.getStringArray(TAG_TITLES)
        titles?.let {
            newsArray = mutableListOf()
            for (i in titles) {
                savedInstanceState.getString(i)?.let {
                    it1 -> News(i, it1)
                }?.let { it2 -> newsArray.add(it2) }
            }
            adapter.refreshNews(newsArray.toTypedArray())
            noNewsLabel.visibility = View.GONE
        }
        super.onViewStateRestored(savedInstanceState)
    }

}