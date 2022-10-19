package com.brave.mvvm.example.ui.adapter

import com.brave.mvvm.example.bean.FunctionBean
import com.brave.mvvm.example.databinding.ItemFunctionLayoutBinding
import com.brave.mvvm.mvvmrapid.rv.BaseBindingQuickAdapter
import com.brave.mvvmrapid.utils.getDrawableX

class FunctionAdapter : BaseBindingQuickAdapter<FunctionBean, ItemFunctionLayoutBinding>() {
    override fun convert(binding: ItemFunctionLayoutBinding, item: FunctionBean) {
        binding.tvFunction.text = item.name
        binding.tvFunction.setCompoundDrawablesWithIntrinsicBounds(
            if (-1 != item.imgRes) {
                context.getDrawableX(item.imgRes)
            } else null,
            null,
            null,
            null
        )
    }
}