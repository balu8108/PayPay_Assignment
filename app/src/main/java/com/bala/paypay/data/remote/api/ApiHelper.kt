package com.bala.paypay.data.remote.api

import com.bala.paypay.data.model.CurrencyEntity
import retrofit2.Response

interface ApiHelper {
    suspend fun getCurrencyies(apiKey: String): Response<CurrencyEntity>
}


