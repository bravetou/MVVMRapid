package com.brave.viewbindingdelegate

import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

object ViewBindingPropertyDelegate {
    /**
     * 启用对[ViewBindingPropertyDelegate]访问方式的严格检查。
     * 如果[ViewBinding]在视图生命周期之外被访问，则抛出异常。
     *
     * 举个例子 =>
     *
     * 如果你尝试访问一个[Fragment],
     * 但该[Fragment]的[Fragment.onDestroyView]被调用
     * 或
     * 该[Fragment]的[Fragment.onViewCreated]未被调用，你会得到崩溃。
     */
    @set:MainThread
    var strictMode = true
        set(value) {
            check(Looper.getMainLooper() == Looper.myLooper())
            field = value
        }
}