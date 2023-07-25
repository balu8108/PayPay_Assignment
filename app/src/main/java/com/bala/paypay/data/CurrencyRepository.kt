package com.bala.paypay.data

import com.bala.paypay.data.local.db.CurrencyDatabase
import com.bala.paypay.data.remote.api.ApiHelper
import com.bala.paypay.data.model.CurrencyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CurrencyRepository @Inject constructor(
    private val currencyDatabse: CurrencyDatabase,
    private val apiHelper: ApiHelper
) {

    suspend fun getAndInsertCurrencyies(lastKey: String) {
        val response = apiHelper.getCurrencyies(lastKey)
        if (response.isSuccessful) {
            deleteAll()
            response.body()?.let {
                it.timestamp = System.currentTimeMillis()
                insertCurrencies(it)
            }
        }
    }

    private suspend fun insertCurrencies(currencyEntity: CurrencyEntity) {
        currencyDatabse.currencyDao().insert(currencyEntity)
    }

    private suspend fun deleteAll() {
        currencyDatabse.currencyDao().deleteAll()
    }

    fun getCurrencyFlow(): Flow<CurrencyEntity> {
        return currencyDatabse.currencyDao().getAllFlow().map {
            if (it.isEmpty()) CurrencyEntity()
            else it.get(0)
        }
    }
}