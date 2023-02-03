@file:JvmName("StateHelper")

package com.brave.mvvmrapid.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.DrawableRes

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
    this.addStateX(stateSet, globalContext.getDrawableX(resId))
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
    this.addStateX(state, globalContext.getDrawableX(resId))
    return this
}