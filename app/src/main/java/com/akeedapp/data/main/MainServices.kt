package com.akeedapp.data.main

import com.akeedapp.data.ResultModel
import retrofit2.http.GET
import retrofit2.http.Query

interface MainServices {

    @GET("?apikey=eeefc96f")
    suspend fun getSearchResult(
        @Query("s") s: String,
        @Query("page") page: String
    ): ResultModel

}