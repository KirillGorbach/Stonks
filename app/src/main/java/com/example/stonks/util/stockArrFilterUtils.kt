package com.example.stonks.util

fun filterDataAll(data: Array<Stock>?, searchStr: String): Array<Stock>?  =
    data?.filter {
        val regex = Regex(searchStr, RegexOption.IGNORE_CASE)
        regex.find(it.companyName)!=null ||
                regex.find(it.ticker)!=null
    }?.toTypedArray()


fun filterDataFav(data: Array<Stock>?): Array<Stock>? =
        data?.filter { it.isFavorite }?.toTypedArray()

fun filterNoFilter(data: Array<Stock>?): Array<Stock>? = data

fun filterToggleFav(data: Array<Stock>?, ticker: String): Array<Stock>? =
    data?.map {
        if (it.ticker == ticker){
            it.isFavorite = it.isFavorite!=true
        }
        it
    }?.toTypedArray()



/*fun filterDataFav(searchStr: String) {
    searchData.value = searchData.value?.filter {
        val regex = Regex(searchStr)
        regex.matches(it.companyName) &&
                regex.matches(it.ticker)
    }?.toTypedArray()
}*/
