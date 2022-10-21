package com.brave.viewbindingdelegate

import androidx.viewbinding.ViewBinding

/**
 * [ViewBinding]的创建方式
 */
enum class CreateMethod {
    /**
     * 使用`ViewBinding.bind(View)`
     */
    BIND,

    /**
     * 使用`ViewBinding.inflate(LayoutInflater, ViewGroup, boolean)`
     */
    INFLATE
}