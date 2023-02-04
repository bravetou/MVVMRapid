@file:JvmName("CommonBindingAdapter")

package com.brave.mvvmrapid.bind

import android.view.View
import android.widget.Checkable
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.ClickUtils

/**
 * [View]的显示隐藏，[isVisible]、[isInvisible]、[isGone]参数，靠后参数覆盖之前
 * @param isVisible 显示
 * @param isInvisible 占位隐藏
 * @param isGone 隐藏
 */
@BindingAdapter(value = ["isVisible", "isInvisible", "isGone"], requireAll = false)
fun setViewVisibility(
    view: View,
    isVisible: Boolean? = null,
    isInvisible: Boolean? = null,
    isGone: Boolean? = null,
) {
    isVisible?.also { view.isVisible = it }
    isInvisible?.also { view.isInvisible = it }
    isGone?.also { view.isGone = it }
}

/**
 * [View.setSelected]
 */
@BindingAdapter(value = ["isSelected"], requireAll = false)
fun setViewSelected(
    view: View,
    isSelected: Boolean? = null,
) {
    isSelected?.also { view.isSelected = it }
}

/**
 * [Checkable.setChecked]
 */
@BindingAdapter(value = ["isChecked"], requireAll = false)
fun setCheckableChecked(
    view: View,
    isChecked: Boolean? = null,
) {
    if (view is Checkable) {
        isChecked?.also { view.isChecked = it }
    }
}

/**
 * [View.setOnClickListener]
 * @param debouncingDuration 防抖动持续时间
 */
@BindingAdapter(value = ["debouncingDuration", "onClickCommand"], requireAll = false)
fun onClickCommand(
    view: View,
    debouncingDuration: Long? = null,
    onClickCommand: ((View) -> Unit)? = null,
) {
    val it = onClickCommand ?: return
    val time = debouncingDuration ?: 0L
    if (time > 0L) {
        ClickUtils.applySingleDebouncing(view, time, it)
    } else {
        view.setOnClickListener(it)
    }
}