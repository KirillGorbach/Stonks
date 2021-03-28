package com.example.stonks.util

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stonks.MainActivity
import com.example.stonks.R

class NewsRecyclerViewAdapter: RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>() {

    private var values: Array<News> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(values[position])

    override fun getItemCount(): Int  = values.size

    fun refreshNews(news: Array<News>) {
        this.values = news
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.news_title)

        fun bind(news: News) {
            title.text = news.title
            title.setOnClickListener {
                val url = Intent(Intent.ACTION_VIEW)
                url.data = Uri.parse(news.link)
                MainActivity.applicationContext().startActivity(url)
            }
        }
    }

}