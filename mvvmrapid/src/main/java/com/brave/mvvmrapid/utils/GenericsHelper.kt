package com.brave.mvvmrapid.utils

import java.lang.reflect.ParameterizedType


/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/10/21 14:26
 *
 * ***desc***       ：泛型帮助类
 */
class GenericsHelper(cls: Class<*>? = null) {
    /**
     * 该类下所有的泛型Class<*>
     */
    val classes = mutableListOf<Class<*>>()

    init {
        cls?.let { findGenerics(it) }
    }

    /**
     * 查找泛型并载入集合
     */
    private fun findGenerics(clazz: Class<*>) {
        when (val type = clazz.genericSuperclass) {
            is ParameterizedType -> type
            else -> null
        }?.actualTypeArguments
            ?.filterNotNull()
            ?.filterIsInstance<Class<*>>()
            ?.also {
                if (it.isNotEmpty()) {
                    classes.addAll(it)
                }
            }
        clazz.superclass?.let {
            findGenerics(it)
        }
    }
}