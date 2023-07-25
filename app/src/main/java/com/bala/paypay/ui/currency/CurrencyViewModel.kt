package com.bala.paypay.ui.currency

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.bala.paypay.BuildConfig
import com.bala.paypay.base.BaseSimpleMviViewModel
import com.bala.paypay.data.CurrencyRepository
import com.bala.paypay.data.model.CurrencyEntity
import com.bala.paypay.utils.NetworkHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyViewModel @ViewModelInject constructor(
    private val currencyRepository: CurrencyRepository,
    private val networkHelper: NetworkHelper
) : BaseSimpleMviViewModel<CurrencyIntent, CurrencyViewState, CurrencySideEffect>() {

    override fun processIntent(intent: CurrencyIntent) {
        super.processIntent(intent)
        when (intent) {
            CurrencyIntent.InitialIntent -> handleInitialIntent()
            is CurrencyIntent.EnteredAmount -> handleEnteredAmount(intent)
            is CurrencyIntent.SelectedCurrencyChanged -> handleSelectedCurrencyChanged(intent)
        }
    }

    private fun handleInitialIntent() {
        viewModelScope.launch {
            currencyRepository.getCurrencyFlow().collectLatest { currencyEntity ->
                if ((System.currentTimeMillis() - currencyEntity.timestamp > 30 * 60 * 1000) || (currencyEntity == CurrencyEntity())) {
                    fetchCurrency()
                } else {
                    updateState {
                        it.copy(
                            currencyOriginal = currencyEntity,
                            selectedItemPosition = 0,
                            currencyValuesShown = currencyEntity.rates.values.toList()
                        )
                    }
                }
            }
        }
    }

    private fun handleSelectedCurrencyChanged(intent: CurrencyIntent.SelectedCurrencyChanged) {
        viewModelScope.launch {
            val originalRates = viewState.value.currencyOriginal.rates
            val base = viewState.value.currencyOriginal.base
            val selectedCurrency = originalRates.keys.toList()[intent.selectedItemPosition]
            val enteredAmount = viewState.value.enteredAmount
            val ratesValuesToShow = originalRates.values.map {
                if (enteredAmount != 0.0) {
                    (enteredAmount * it) * ((originalRates[base]
                        ?: 0.0) / (originalRates[selectedCurrency]
                        ?: 1.0))
                } else it
            }.toList()

            updateState {
                it.copy(
                    currencyValuesShown = ratesValuesToShow,
                    selectedItemPosition = intent.selectedItemPosition
                )
            }
        }
    }

    private fun handleEnteredAmount(intent: CurrencyIntent.EnteredAmount) {
        viewModelScope.launch {
            val originalRates = viewState.value.currencyOriginal.rates
            val base = viewState.value.currencyOriginal.base
            val enteredAmount = intent.amount
            val selectedCurrency = originalRates.keys.toList()[viewState.value.selectedItemPosition]
            val ratesValuesToShow = originalRates.values.map {
                if (enteredAmount != 0.0) {
                    (enteredAmount * it) * ((originalRates[base]
                        ?: 0.0) / (originalRates[selectedCurrency]
                        ?: 1.0))
                } else it
            }.toList()

            updateState {
                it.copy(currencyValuesShown = ratesValuesToShow, enteredAmount = enteredAmount)
            }
        }
    }

    private suspend fun fetchCurrency() {
        if (networkHelper.isNetworkConnected()) {
            currencyRepository.getAndInsertCurrencyies(
                BuildConfig.APIKEY
            )
        }
    }

    override fun initialViewState(): CurrencyViewState {
        return CurrencyViewState()
    }


}