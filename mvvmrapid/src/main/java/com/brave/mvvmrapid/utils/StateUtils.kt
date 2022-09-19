package com.brave.mvvmrapid.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.Utils

/**
 * 为[StateListDrawable]添加状态
 *
 * @param stateSet 状态集
 * @param drawable [Drawable]
 */
fun StateListDrawable.addStateX(stateSet: IntArray, drawable: Drawable): StateListDrawable {
    this.addState(stateSet, drawable)
    return this
}

/**
 * 为[StateListDrawable]添加状态
 *
 * @param stateSet 状态集
 * @param resId [Drawable]资源ID
 */
fun StateListDrawable.addStateX(stateSet: IntArray, @DrawableRes resId: Int): StateListDrawable {
    this.addState(
        stateSet, try {
            ContextCompat.getDrawable(Utils.getApp(), resId)
        } catch (e: Exception) {
            ColorDrawable(Color.TRANSPARENT)
        }
    )
    return this
}

/**
 * 为[StateListDrawable]添加状态
 *
 * @param state 单状态
 * @param drawable [Drawable]
 */
fun StateListDrawable.addStateX(state: Int, drawable: Drawable): StateListDrawable {
    this.addState(intArrayOf(state), drawable)
    return this
}

/**
 * 为[StateListDrawable]添加状态
 *
 * @param state 单状态
 * @param resId [Drawable]资源ID
 */
fun StateListDrawable.addStateX(state: Int, @DrawableRes resId: Int): StateListDrawable {
    this.addState(
        intArrayOf(state), try {
            ContextCompat.getDrawable(Utils.getApp(), resId)
        } catch (e: Exception) {
            ColorDrawable(Color.TRANSPARENT)
        }
    )
    return this
}