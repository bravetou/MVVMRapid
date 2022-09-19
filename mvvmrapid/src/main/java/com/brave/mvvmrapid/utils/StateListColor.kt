package com.brave.mvvmrapid.utils

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.blankj.utilcode.util.Utils

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/19 14:45
 *
 * ***desc***       ：动态颜色
 */
@Suppress(
    "unused",
    "MemberVisibilityCanBePrivate"
)
class StateListColor private constructor() {
    companion object {
        fun newInstance() = StateListColor()
    }

    /**
     * [MutableMap.keys] 状态源
     *
     * [MutableMap.values] 与状态源对应的颜色源
     */
    private val data = mutableMapOf<IntArray, Int>()

    /**
     * 添加一个状态组对应的颜色值
     */
    fun addStateX(states: IntArray, @ColorInt color: Int): StateListColor {
        data[states] = color
        return this
    }

    /**
     * 添加一个状态组对应的颜色资源
     */
    fun addStateXRes(states: IntArray, @ColorRes resId: Int): StateListColor {
        return addStateX(
            states, try {
                ContextCompat.getColor(Utils.getApp(), resId)
            } catch (e: Exception) {
                Color.TRANSPARENT
            }
        )
    }

    /**
     * 添加一个状态组对应的颜色字符串
     */
    fun addStateX(states: IntArray, colorString: String): StateListColor {
        return addStateX(
            states, try {
                colorString.toColorInt()
            } catch (e: Exception) {
                Color.TRANSPARENT
            }
        )
    }

    /**
     * 添加一个单状态对应的颜色值
     */
    fun addStateX(state: Int, @ColorInt color: Int): StateListColor {
        data[intArrayOf(state)] = color
        return this
    }

    /**
     * 添加一个单状态对应的颜色资源
     */
    fun addStateXRes(state: Int, @ColorRes resId: Int): StateListColor {
        return addStateX(
            state, try {
                ContextCompat.getColor(Utils.getApp(), resId)
            } catch (e: Exception) {
                Color.TRANSPARENT
            }
        )
    }

    /**
     * 添加一个单状态对应的颜色字符串
     */
    fun addStateX(state: Int, colorString: String): StateListColor {
        return addStateX(
            state, try {
                colorString.toColorInt()
            } catch (e: Exception) {
                Color.TRANSPARENT
            }
        )
    }


    /**
     * 必须必须调用此方法，最终生成一个[ColorStateList]
     *
     * @return 返回[ColorStateList]
     */
    fun build(): ColorStateList {
        val states = mutableListOf<IntArray>()
        val colors = mutableListOf<Int>()
        data.forEach { (state, color) ->
            states.add(state)
            colors.add(color)
        }
        return ColorStateList(states.toTypedArray(), colors.toIntArray())
    }
}