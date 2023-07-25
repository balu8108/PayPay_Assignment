package com.bala.paypay.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseSimpleMviFragment<VB : ViewBinding, ViewIntent, ViewSate, ViewSideEffect,
        ViewModel : BaseSimpleMviViewModel<ViewIntent, ViewSate, ViewSideEffect>> : Fragment() {

    var viewBinding: VB? = null
    abstract val viewModel: ViewModel

    abstract fun inflateViewBinding(inflater: LayoutInflater): VB

    open fun saveViewStateToSavedStateHandle(viewState: ViewSate) {}

    abstract fun render(viewState: ViewSate)
    abstract fun handleSideEffects(sideEffect: ViewSideEffect)
    open fun setupStaticViewsAndButtonClickListeners() {
        // no-op
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = inflateViewBinding(inflater)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.viewState.collect {
                        Timber.d("state = $it")
                        saveViewStateToSavedStateHandle(it)
                        render(it)
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.sideEffects.collect {
                        handleSideEffects(it)
                    }
                }
            }
        }
        setupStaticViewsAndButtonClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}
