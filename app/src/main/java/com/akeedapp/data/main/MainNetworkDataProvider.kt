package com.akeedapp.data.main

class MainNetworkDataProvider(private val mainServices: MainServices) {
    suspend fun getSearchResult(searchKey: String, pageNum: String) =
        mainServices.getSearchResult(searchKey, pageNum)
}