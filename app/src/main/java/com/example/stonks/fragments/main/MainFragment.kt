package com.example.stonks.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.stonks.MainActivityViewModel
import com.example.stonks.R
import com.example.stonks.fragments.ActivityFragmentListener
import com.example.stonks.fragments.main.util.StockRecyclerViewAdapter
import com.example.stonks.util.MainContentHolder


class MainFragment(
    private val activityFragmentListener: ActivityFragmentListener
): Fragment(),
        StockRecyclerViewAdapter.OnStockClickedListener{

    lateinit var mainContentHolder: MainContentHolder
    lateinit var viewModel: MainActivityViewModel
    lateinit var viewMain: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewMain = inflater.inflate(R.layout.fragment_main, container, false)

        if (activity!=null && context!=null) {
            viewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
            mainContentHolder =
                    MainContentHolder(
                            viewMain,
                            requireContext(),
                            this,
                            viewModel
                    )
        }

        initViewModel()
        return viewMain
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search", mainContentHolder.searchInput.query.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            mainContentHolder.searchInput.setQuery(
                savedInstanceState.getString("search"),
                false
            )
        }
    }

    private fun initViewModel() {
        // когда viewModel.data изменена, обновляем viewModel.searchState
        viewModel.data.observe(viewLifecycleOwner, {
            viewModel.setSearchData(viewModel.searchState)
        })

        // выводим на экран актуальный
        viewModel.searchData.observe(viewLifecycleOwner, {
            it?.let {
                mainContentHolder.adapter.refreshStocks(it)
                if (it.isEmpty())
                    mainContentHolder.labelNoFound.visibility = View.VISIBLE
                else
                    mainContentHolder.labelNoFound.visibility = View.GONE
            }
        })


        // Надпись "Loading" на экране, пока не будет данных
        if (viewModel.dataLoaded.value != true) {
            mainContentHolder.labelLoading.visibility = View.VISIBLE
        }
        viewModel.dataLoaded.observe(viewLifecycleOwner, {
            if (it == true)
                mainContentHolder.labelLoading.visibility = View.GONE
            else
                mainContentHolder.labelLoading.visibility = View.VISIBLE
        })
    }

    override fun onStockClicked(ticker: String) {
        activityFragmentListener.startNewsFragment(ticker)
    }
}