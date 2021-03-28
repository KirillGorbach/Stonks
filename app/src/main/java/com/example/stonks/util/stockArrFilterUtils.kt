package com.example.stonks.util



// регистронезависимый поиск по тикеру и имени компании
fun filterDataAll(data: Array<Stock>?, searchStr: String): Array<Stock>?  =
    data?.filter {
        val regex = Regex(searchStr, RegexOption.IGNORE_CASE)
        regex.find(it.companyName)!=null ||
                regex.find(it.ticker)!=null
    }?.toTypedArray()

// только избранные
fun filterDataFav(data: Array<Stock>?): Array<Stock>? =
        data?.filter { it.isFavorite }?.toTypedArray()

fun filterNoFilter(data: Array<Stock>?): Array<Stock>? = data



/*fun filterDataFav(searchStr: String) {
    searchData.value = searchData.value?.filter {
        val regex = Regex(searchStr)
        regex.matches(it.companyName) &&
                regex.matches(it.ticker)
    }?.toTypedArray()
}*/
