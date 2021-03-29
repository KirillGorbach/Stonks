package com.example.stonks.fragments

interface ActivityFragmentListener {
    fun startNewsFragment(ticker: String)
    fun backToMainFragment()
}