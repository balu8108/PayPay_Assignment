package com.bala.paypay.ui.currency


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.SpinnerAdapter
import androidx.fragment.app.viewModels
import com.bala.paypay.R
import com.bala.paypay.base.BaseSimpleMviFragment
import com.bala.paypay.databinding.FragmentCurrencyBinding
import com.bala.paypay.ui.adapter.CurrencyListRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CurrencyFragment :
    BaseSimpleMviFragment<FragmentCurrencyBinding, CurrencyIntent, CurrencyViewState, CurrencySideEffect, CurrencyViewModel>() {

    private val adapter by lazy { CurrencyListRecyclerViewAdapter(emptyList(), emptyList()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override val viewModel: CurrencyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.processIntent(CurrencyIntent.InitialIntent)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentCurrencyBinding {
        return FragmentCurrencyBinding.inflate(inflater)
    }

    override fun handleSideEffects(sideEffect: CurrencySideEffect) {
    }

    override fun render(viewState: CurrencyViewState) {
        initSpinner(viewState.currencyOriginal.rates.keys.toList(),viewState.selectedItemPosition)
        renderList(viewState.currencyOriginal.rates.keys.toList(), viewState.currencyValuesShown)
    }

    override fun setupStaticViewsAndButtonClickListeners() {
        super.setupStaticViewsAndButtonClickListeners()
        viewBinding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                viewModel.processIntent(CurrencyIntent.EnteredAmount(p0.toDouble()))
                return true
            }

            override fun onQueryTextChange(p0: String): Boolean {
                viewModel.processIntent(CurrencyIntent.EnteredAmount(p0.toDouble()))
                return true
            }
        })
        viewBinding?.gridView?.adapter = adapter
    }

    private fun renderList(currencyList: List<String>, currencyValues: List<Double>) {
        adapter.currencyValues = currencyValues
        adapter.currencyNames = currencyList
        adapter.notifyDataSetChanged()
    }

    private fun initSpinner(currencyList: List<String>, selectedItemPosition: Int) {

        viewBinding?.spinner?.adapter = activity?.let {
            ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, currencyList)
        } as SpinnerAdapter
        viewBinding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.processIntent(CurrencyIntent.SelectedCurrencyChanged(position))
            }
        }

        viewBinding?.spinner?.setSelection(selectedItemPosition)
    }

}