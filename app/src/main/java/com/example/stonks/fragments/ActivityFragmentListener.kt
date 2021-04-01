package com.example.stonks.fragments

interface ActivityFragmentListener {
    fun startNewsFragment(ticker: String)
    fun startWebViewFragment(link: String, parentTicker: String, title:String)
    fun backToMainFragment()
    fun changeTheme()
}