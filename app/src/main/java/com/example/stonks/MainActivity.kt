package com.example.stonks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.fragments.ActivityFragmentListener
import com.example.stonks.fragments.main.MainFragment
import com.example.stonks.fragments.news.NewsFragment
import com.example.stonks.fragments.webview.WebViewFragment

class MainActivity : AppCompatActivity(),
ActivityFragmentListener{

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
    private val webViewFragmentTag = "WebViewFragment"
    private val nightModeTag = "NightMode"
    private val sharedPreferencesTag = "MyPrefs"
    private val themeTag = "MainTheme"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTheme()

        if (savedInstanceState==null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment(), mainFragmentTag)
                .commit()
        }
    }

    private fun initTheme() {
        val prefs = getPreferences(MODE_PRIVATE)
        if (prefs.contains(themeTag)) {
            AppCompatDelegate.setDefaultNightMode(
                prefs.getInt(themeTag, AppCompatDelegate.MODE_NIGHT_NO)
            )
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // если мы в новостях, возвращаемся на главный
    override fun onBackPressed() {
        val manager = supportFragmentManager

        if (manager.findFragmentByTag(newsFragmentTag)!=null) {
            backToMainFragment()
        } else {
            val webViewFragment = manager.findFragmentByTag(webViewFragmentTag) as WebViewFragment?
            if (webViewFragment != null) {
                startNewsFragment(webViewFragment.parentTicker)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun startNewsFragment(ticker: String) {
        if(supportFragmentManager.findFragmentByTag(newsFragmentTag) == null) {
            val manager = supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.container, NewsFragment(ticker), newsFragmentTag)
                .commit()
        }
    }

    override fun startWebViewFragment(link: String, parentTicker: String, title: String) {
        if(supportFragmentManager.findFragmentByTag(webViewFragmentTag) == null) {
            val manager = supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.container, WebViewFragment(link, parentTicker, title), webViewFragmentTag)
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

    override fun changeTheme() {
        val prefs = getPreferences(MODE_PRIVATE)
        var theme: Int = AppCompatDelegate.MODE_NIGHT_NO
        if (prefs.contains(themeTag)){
            theme = prefs.getInt(themeTag, AppCompatDelegate.MODE_NIGHT_NO)
        }
        val editor = prefs.edit()
        when(theme) {
            AppCompatDelegate.MODE_NIGHT_NO -> editor.putInt(themeTag, AppCompatDelegate.MODE_NIGHT_YES)
            else -> editor.putInt(themeTag, AppCompatDelegate.MODE_NIGHT_NO)
        }
        editor.apply()
        recreate()

        //другой способ
//        finish()
//        startActivity(intent)
//        overridePendingTransition(0, 0)
    }



}
