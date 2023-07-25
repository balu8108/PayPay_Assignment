package com.bala.paypay.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bala.paypay.data.local.dao.CurrencyDao
import com.bala.paypay.data.model.CurrencyEntity

@Database(entities = [CurrencyEntity::class], version = 2, exportSchema = false)
@TypeConverters(MapConverter::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}