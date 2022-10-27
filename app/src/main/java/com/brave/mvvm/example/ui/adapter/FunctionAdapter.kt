package com.brave.mvvm.example.ui.adapter

import com.blankj.utilcode.util.SizeUtils
import com.brave.mvvm.example.bean.FunctionBean
import com.brave.mvvm.example.databinding.ItemFunctionLayoutBinding
import com.brave.mvvm.mvvmrapid.rv.BaseBindingQuickAdapter
import com.brave.mvvmrapid.utils.getDrawableX

class FunctionAdapter : BaseBindingQuickAdapter<FunctionBean, ItemFunctionLayoutBinding>() {
    override fun convert(binding: ItemFunctionLayoutBinding, item: FunctionBean) {
        binding.tvFunction.text = item.name
        val drawable = if (-1 != item.imgRes) {
            context.getDrawableX(item.imgRes)
        } else null
        binding.tvFunction.compoundDrawablePadding = if (null == drawable) {
            0
        } else SizeUtils.dp2px(6f)
        binding.tvFunction.setCompoundDrawablesWithIntrinsicBounds(
            drawable,
            null,
            null,
            null
        )
    }

    override fun convert(
        binding: ItemFunctionLayoutBinding,
        item: FunctionBean,
        payloads: List<Any>
    ) {
        binding.tvFunction.text = item.name
    }
}