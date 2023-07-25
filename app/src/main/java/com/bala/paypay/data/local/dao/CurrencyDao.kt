package com.bala.paypay.data.local.dao

import androidx.room.*
import com.bala.paypay.data.model.CurrencyEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency")
    fun getAllFlow(): Flow<List<CurrencyEntity>>

    @Insert
    suspend fun insertAll(currencyEntity: List<CurrencyEntity>)

    @Delete
    suspend fun delete(currencyEntity: CurrencyEntity)

    @Query("DELETE FROM currency")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: CurrencyEntity)


}