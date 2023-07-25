package com.bala.paypay.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "currency")
data class CurrencyEntity(

    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "base") var base: String = "",
    @ColumnInfo(name = "rates") var rates: Map<String, Double> = emptyMap(),
    @ColumnInfo(name = "disclaimer") var disclaimer: String = "",
    @ColumnInfo(name = "license") var license: String = "",
    @ColumnInfo(name = "timestamp") var timestamp: Long = 0

)