package com.example.stonks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.fragments.ActivityFragmentListener
import com.example.stonks.fragments.main.MainFragment
import com.example.stonks.fragments.stockInfo.NewsFragment

class MainActivity : AppCompatActivity(),
ActivityFragmentListener{

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

    var mainFragment: MainFragment? = null
    val mainFragmentTag = "MainFragment"
    var newsFragment: NewsFragment? = null
    val newsFragmentTag = "NewsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = MainFragment(this as ActivityFragmentListener)

        if (savedInstanceState==null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, mainFragment!!, mainFragmentTag)
                .commit()
        }
    }

    override fun startNewsFragment(ticker: String) {
        newsFragment = supportFragmentManager.findFragmentByTag(newsFragmentTag)?.let {
            supportFragmentManager.findFragmentByTag(newsFragmentTag) as NewsFragment
        }
        if(newsFragment == null) {
            newsFragment = NewsFragment(this as ActivityFragmentListener, ticker)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, newsFragment!!, newsFragmentTag)
                .commit()
        }
    }

    override fun backToMainFragment() {
        mainFragment = supportFragmentManager.findFragmentByTag(mainFragmentTag)?.let {
            supportFragmentManager.findFragmentByTag(mainFragmentTag) as MainFragment
        }
        if(mainFragment == null) {
            mainFragment = MainFragment(this as ActivityFragmentListener)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment!!, mainFragmentTag)
                .commit()
        }
    }

}
