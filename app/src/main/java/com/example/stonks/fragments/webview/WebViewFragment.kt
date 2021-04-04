package com.example.stonks.fragments.webview

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.stonks.MainActivity
import com.example.stonks.R


class WebViewFragment(
        private val link: String,
        val parentTicker: String,
        private val title: String
): Fragment() {


    private lateinit var myView: View
    private lateinit var webView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        retainInstance = true

        myView = inflater.inflate(R.layout.fragment_web_view, container, false)

        webView = myView.findViewById(R.id.webview)
        webView.webViewClient = AppWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(link)


        myView.findViewById<TextView>(R.id.webview_fragment_name)
                .text = when(title.length) {
                    in 0..20 -> title
                    else -> title.substring(0,20).plus("...")
                }
        // при возвращении
        myView.findViewById<ImageButton>(R.id.webview_back_btn)
                .setOnClickListener {
                    MainActivity.getFragmentListener()
                            .startNewsFragment(parentTicker)
                }

        return myView
    }

    private inner class AppWebViewClient: WebViewClient() {
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            webView.visibility = View.GONE
            myView.findViewById<ConstraintLayout>(R.id.cant_load_label_layout)
                    .visibility = View.VISIBLE
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

}