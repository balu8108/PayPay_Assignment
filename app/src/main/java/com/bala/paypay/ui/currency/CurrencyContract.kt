package com.bala.paypay.ui.currency

import com.bala.paypay.data.model.CurrencyEntity

data class CurrencyViewState(
    val currencyOriginal: CurrencyEntity = CurrencyEntity(),
    val enteredAmount: Double = 0.0,
    val currencyValuesShown: List<Double> = emptyList(),
    val selectedItemPosition: Int = 0
)

sealed class CurrencyIntent {
    object InitialIntent : CurrencyIntent()
    data class SelectedCurrencyChanged(val selectedItemPosition: Int) : CurrencyIntent()
    data class EnteredAmount(val amount: Double) : CurrencyIntent()
}

sealed class CurrencySideEffect