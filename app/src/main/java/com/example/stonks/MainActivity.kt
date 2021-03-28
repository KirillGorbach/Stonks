package com.example.stonks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stonks.fragments.StockInfoFragment
import com.example.stonks.util.StockRecyclerViewAdapter

class MainActivity : AppCompatActivity(),
StockRecyclerViewAdapter.OnStockClickedListener,
StockInfoFragment.OnFragmentExitListener{

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
        fun getInstance(): MainActivity? {
            return instance
        }
//        fun getOnFragmentExitListener(): StockInfoFragment.OnFragmentExitListener {
//            return instance as StockInfoFragment.OnFragmentExitListener
//        }
    }


    lateinit var mainContentHolder: MainContentHolder
    var newsFragment: StockInfoFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mainContentHolder = MainContentHolder(
            findViewById<View>(android.R.id.content).rootView,
            this, viewModel
        )

        initViewModel()
    }

    private fun initViewModel() {

        // когда viewModel.data изменена, обновляем viewModel.searchState
        viewModel.data.observe(this, {
            viewModel.setSearchData(viewModel.searchState)
        })

        // выводим на экран актуальный
        viewModel.searchData.observe(this, {it?.let {
            mainContentHolder.adapter?.refreshStocks(it)
            if (it.isEmpty())
                mainContentHolder.labelNoFound.visibility = View.VISIBLE
            else
                mainContentHolder.labelNoFound.visibility = View.GONE
        }})


        // Надпись "Loading" на экране, пока не будет данных
        if (viewModel.dataLoaded.value!=true) {
            mainContentHolder.labelLoading.visibility = View.VISIBLE
        }
        viewModel.dataLoaded.observe(this, {
            if (it==true)
                mainContentHolder.labelLoading.visibility = View.GONE
            else
                mainContentHolder.labelLoading.visibility = View.VISIBLE
        })
    }

    /*
        ViewModel не следит за жизнью приложения.
        Закрывать БД приходится так
     */
    override fun onStop() {
        super.onStop()
        viewModel.deleteDB()
    }
    override fun onRestart() {
        super.onRestart()
        viewModel.reinitDB()
    }


    override fun onStockClicked(ticker: String) {
//        if (newsFragment == null) {
//            newsFragment = StockInfoFragment.newInstance(ticker)
//            supportFragmentManager.beginTransaction()
//                .add(R.id.info_fragment, newsFragment!!)
//                .commit()
//        }
    }

    override fun fragmentExit() {
//        newsFragment?.let {
//            supportFragmentManager.beginTransaction()
//                .remove(it)
//                .commit()
//        }
//        newsFragment = null

    }

}
