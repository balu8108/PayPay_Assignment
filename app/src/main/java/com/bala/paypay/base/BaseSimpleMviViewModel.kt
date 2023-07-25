package com.bala.paypay.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("LeakingThis")
abstract class BaseSimpleMviViewModel<ViewIntent, ViewState, ViewSideEffect> :
    ViewModel() {

    protected var initialIntentHandled = false

    private val _initialViewState = initialViewState()

    private val _viewState = MutableStateFlow(_initialViewState)
    private val _sideEffects = Channel<ViewSideEffect>(Channel.BUFFERED)

    val viewState = _viewState.asStateFlow()
    val sideEffects = _sideEffects.receiveAsFlow()

    abstract fun initialViewState(): ViewState

    @CallSuper
    open fun processIntent(intent: ViewIntent) {
        Timber.d("ViewIntent = $intent")
    }

    protected fun emitSideEffect(sideEffect: ViewSideEffect) {
        viewModelScope.launch {
            _sideEffects.send(sideEffect)
        }
    }

    protected fun updateState(block: (currentState: ViewState) -> ViewState) {
        _viewState.update(block)
    }
}
