package com.bala.paypay.data.remote.api

import com.bala.paypay.data.model.CurrencyEntity
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override suspend fun getCurrencyies(apiKey: String): Response<CurrencyEntity> =
        apiService.getCurrencies(apiKey)

}