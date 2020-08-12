package com.akeedapp.data.main

class MainRepository(private val mainDataProvider: MainNetworkDataProvider) {
    suspend fun getSearchResult(searchKey: String, pageNum: String) =
        mainDataProvider.getSearchResult(searchKey, pageNum)
}