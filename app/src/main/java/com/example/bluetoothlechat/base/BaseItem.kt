package com.example.bluetoothlechat.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import com.xwray.groupie.databinding.BindableItem

/**
 * Property of TicketShala, Inc @ 2021 All Rights Reserved.
 */

class BaseItem<M, V : ViewDataBinding>(
    private val layoutResId: Int,
    var model: M,
    private val callback: OnItemClickListener<M>
) : BindableItem<V>() {

    override fun getLayout(): Int = layoutResId

    override fun bind(viewBinding: V, position: Int) {
        viewBinding.setVariable(BR.model, model)
        viewBinding.setVariable(BR.callback, callback)
        viewBinding.setVariable(BR.pos, position)
    }

    interface OnItemClickListener<M> {
        fun onItemClick(
            view: View,
            model: M,
            position: Int
        )
    }

}