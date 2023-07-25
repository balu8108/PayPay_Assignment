package com.bala.paypay.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bala.paypay.databinding.FragmentCurrencyListBinding


class CurrencyListRecyclerViewAdapter(
    var currencyNames: List<String>,
    var currencyValues: List<Double>
) : RecyclerView.Adapter<CurrencyListRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val viewBinding: FragmentCurrencyListBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(position: Int) {
            viewBinding.currencyValue.text = currencyValues.get(position).toString()
            viewBinding.currencyName.text = currencyNames.get(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            FragmentCurrencyListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return currencyValues.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }


}


