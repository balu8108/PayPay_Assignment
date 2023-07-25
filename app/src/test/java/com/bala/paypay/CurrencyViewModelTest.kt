package com.bala.paypay

import com.bala.paypay.data.CurrencyRepository
import com.bala.paypay.data.model.CurrencyEntity
import com.bala.paypay.ui.currency.CurrencyIntent
import com.bala.paypay.ui.currency.CurrencySideEffect
import com.bala.paypay.ui.currency.CurrencyViewModel
import com.bala.paypay.ui.currency.CurrencyViewState
import com.bala.paypay.utils.ImmediateTestDispatcher
import com.bala.paypay.utils.NetworkHelper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class CurrencyViewModelTest {
    private val testDispatcher = ImmediateTestDispatcher()

    private lateinit var currencyViewModel: CurrencyViewModel
    private val currencyRepository = mockk<CurrencyRepository>()
    private val networkHelper = mockk<NetworkHelper>()

    private val testCurrencyEntity1 = CurrencyEntity(0,"USD", mapOf("USD" to 1.0,"INR" to 82.0),"","",System.currentTimeMillis())
    private val testCurrencyEntity2 = CurrencyEntity(0,"USD", mapOf("USD" to 1.0,"INR" to 82.0),"","",System.currentTimeMillis()-40*60*1000)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        currencyViewModel = CurrencyViewModel(currencyRepository,networkHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `currency screen - starts with default value`() = runTest {
        assertEquals(CurrencyViewState(), currencyViewModel.viewState.value)
    }

    @Test
    fun `initial intent test - first fetch, less than 30 min, more than 30 min`() = runBlocking {

        coEvery {
            currencyRepository.getCurrencyFlow()
        } returnsMany listOf(
            flowOf(CurrencyEntity()),
            flowOf(testCurrencyEntity1),
            flowOf(testCurrencyEntity2)
        )

        coEvery {
            networkHelper.isNetworkConnected()
        } returns false

        val resultStates = mutableListOf<CurrencyViewState>()
        val stateJob = launch(testDispatcher) {
            currencyViewModel.viewState.collect(resultStates::add)
        }

        val resultEffects = mutableListOf<CurrencySideEffect>()
        val effectJob = launch(testDispatcher) {
            currencyViewModel.sideEffects.collect(resultEffects::add)
        }

        currencyViewModel.processIntent(
            CurrencyIntent.InitialIntent
        )
        currencyViewModel.processIntent(
            CurrencyIntent.InitialIntent
        )
        currencyViewModel.processIntent(
            CurrencyIntent.InitialIntent
        )

        val expectedState1 = CurrencyViewState()
        val expectedState2 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 0, currencyValuesShown = testCurrencyEntity1.rates.values.toList())

        stateJob.cancel()
        effectJob.cancel()

        assertEquals(2, resultStates.size)
        assertEquals(0, resultEffects.size)
        assertEquals(expectedState1, resultStates[0])
        assertEquals(expectedState2, resultStates[1])
    }

    @Test
    fun `selected currency changed intent test - entered amount 0`() = runBlocking {

        coEvery {
            currencyRepository.getCurrencyFlow()
        } returns flowOf(testCurrencyEntity1)

        val resultStates = mutableListOf<CurrencyViewState>()
        val stateJob = launch(testDispatcher) {
            currencyViewModel.viewState.collect(resultStates::add)
        }

        val resultEffects = mutableListOf<CurrencySideEffect>()
        val effectJob = launch(testDispatcher) {
            currencyViewModel.sideEffects.collect(resultEffects::add)
        }

        currencyViewModel.processIntent(
            CurrencyIntent.InitialIntent
        )
        currencyViewModel.processIntent(
            CurrencyIntent.SelectedCurrencyChanged(1)
        )

        val expectedState1 = CurrencyViewState()
        val expectedState2 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 0, currencyValuesShown = testCurrencyEntity1.rates.values.toList())
        val expectedState3 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 1, currencyValuesShown = testCurrencyEntity1.rates.values.toList())

        stateJob.cancel()
        effectJob.cancel()

        assertEquals(3, resultStates.size)
        assertEquals(0, resultEffects.size)
        assertEquals(expectedState1, resultStates[0])
        assertEquals(expectedState2, resultStates[1])
        assertEquals(expectedState3, resultStates[2])
    }

    @Test
    fun `selected currency changed intent test - selected currency changed intent,entered amount 82`() = runBlocking {

        coEvery {
            currencyRepository.getCurrencyFlow()
        } returns flowOf(testCurrencyEntity1)

        val resultStates = mutableListOf<CurrencyViewState>()
        val stateJob = launch(testDispatcher) {
            currencyViewModel.viewState.collect(resultStates::add)
        }

        val resultEffects = mutableListOf<CurrencySideEffect>()
        val effectJob = launch(testDispatcher) {
            currencyViewModel.sideEffects.collect(resultEffects::add)
        }

        currencyViewModel.processIntent(
            CurrencyIntent.InitialIntent
        )
        currencyViewModel.processIntent(
            CurrencyIntent.SelectedCurrencyChanged(1)
        )
        currencyViewModel.processIntent(
            CurrencyIntent.EnteredAmount(82.0)
        )

        val expectedState1 = CurrencyViewState()
        val expectedState2 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 0, currencyValuesShown = testCurrencyEntity1.rates.values.toList())
        val expectedState3 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 1, currencyValuesShown = testCurrencyEntity1.rates.values.toList())
        val expectedState4 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 1, currencyValuesShown = listOf(1.0,82.0), enteredAmount = 82.0)

        stateJob.cancel()
        effectJob.cancel()

        assertEquals(4, resultStates.size)
        assertEquals(0, resultEffects.size)
        assertEquals(expectedState1, resultStates[0])
        assertEquals(expectedState2, resultStates[1])
        assertEquals(expectedState3, resultStates[2])
        assertEquals(expectedState4, resultStates[3])
    }

    @Test
    fun `entered amount intent test - entered amount 1`() = runBlocking {

        coEvery {
            currencyRepository.getCurrencyFlow()
        } returns flowOf(testCurrencyEntity1)

        val resultStates = mutableListOf<CurrencyViewState>()
        val stateJob = launch(testDispatcher) {
            currencyViewModel.viewState.collect(resultStates::add)
        }

        val resultEffects = mutableListOf<CurrencySideEffect>()
        val effectJob = launch(testDispatcher) {
            currencyViewModel.sideEffects.collect(resultEffects::add)
        }

        currencyViewModel.processIntent(
            CurrencyIntent.InitialIntent
        )
        currencyViewModel.processIntent(
            CurrencyIntent.EnteredAmount(1.0)
        )

        val expectedState1 = CurrencyViewState()
        val expectedState2 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 0, currencyValuesShown = testCurrencyEntity1.rates.values.toList())
        val expectedState3 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 0, currencyValuesShown = testCurrencyEntity1.rates.values.toList(), enteredAmount = 1.0)

        stateJob.cancel()
        effectJob.cancel()

        assertEquals(3, resultStates.size)
        assertEquals(0, resultEffects.size)
        assertEquals(expectedState1, resultStates[0])
        assertEquals(expectedState2, resultStates[1])
        assertEquals(expectedState3, resultStates[2])
    }

    @Test
    fun `entered amount intent test - entered amount 1,selected currency changed intent`() = runBlocking {

        coEvery {
            currencyRepository.getCurrencyFlow()
        } returns flowOf(testCurrencyEntity1)

        val resultStates = mutableListOf<CurrencyViewState>()
        val stateJob = launch(testDispatcher) {
            currencyViewModel.viewState.collect(resultStates::add)
        }

        val resultEffects = mutableListOf<CurrencySideEffect>()
        val effectJob = launch(testDispatcher) {
            currencyViewModel.sideEffects.collect(resultEffects::add)
        }

        currencyViewModel.processIntent(
            CurrencyIntent.InitialIntent
        )
        currencyViewModel.processIntent(
            CurrencyIntent.EnteredAmount(1.0)
        )
        currencyViewModel.processIntent(
            CurrencyIntent.SelectedCurrencyChanged(1)
        )

        val expectedState1 = CurrencyViewState()
        val expectedState2 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 0, currencyValuesShown = testCurrencyEntity1.rates.values.toList())
        val expectedState3 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 0, currencyValuesShown = testCurrencyEntity1.rates.values.toList(), enteredAmount = 1.0)
        val expectedState4 = CurrencyViewState(testCurrencyEntity1, selectedItemPosition = 1, currencyValuesShown = listOf(1.0/82.0,1.0), enteredAmount = 1.0)

        stateJob.cancel()
        effectJob.cancel()

        assertEquals(4, resultStates.size)
        assertEquals(0, resultEffects.size)
        assertEquals(expectedState1, resultStates[0])
        assertEquals(expectedState2, resultStates[1])
        assertEquals(expectedState3, resultStates[2])
        assertEquals(expectedState4, resultStates[3])
    }
}