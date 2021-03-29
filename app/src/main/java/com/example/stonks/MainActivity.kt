package com.example.stonks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.fragments.ActivityFragmentListener
import com.example.stonks.fragments.main.MainFragment
import com.example.stonks.fragments.news.NewsFragment

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
        fun getFragmentListener(): ActivityFragmentListener =
            instance as ActivityFragmentListener
    }

    private val mainFragmentTag = "MainFragment"
    private val newsFragmentTag = "NewsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState==null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment(), mainFragmentTag)
                .commit()
        }
    }

    // если мы в новостях, возвращаемся на главный
    override fun onBackPressed() {
        val manager = supportFragmentManager
        if (manager.findFragmentByTag(newsFragmentTag)!=null)
            backToMainFragment()
        else
            super.onBackPressed()
    }

    override fun startNewsFragment(ticker: String) {
        if(supportFragmentManager.findFragmentByTag(newsFragmentTag) == null) {
            val manager = supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.container, NewsFragment(ticker), newsFragmentTag)
                .commit()
        }
    }

    override fun backToMainFragment() {
        if(supportFragmentManager.findFragmentByTag(mainFragmentTag) == null) {
            val manager = supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.container, MainFragment(), mainFragmentTag)
                .commit()
        }
    }

}
