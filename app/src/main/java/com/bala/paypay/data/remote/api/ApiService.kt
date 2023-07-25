package com.bala.paypay.data.remote.api

import com.bala.paypay.data.model.CurrencyEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/latest.json")
    suspend fun getCurrencies(@Query("app_id") apiKey: String): Response<CurrencyEntity>
}