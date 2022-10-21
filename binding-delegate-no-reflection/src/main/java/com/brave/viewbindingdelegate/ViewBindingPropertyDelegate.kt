package com.brave.viewbindingdelegate

import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

object ViewBindingPropertyDelegate {
    /**
     * 启用[ViewBindingPropertyDelegate]的严格检查访问方式。
     * 如果[ViewBinding]在生命周期之外被访问，则抛出异常。
     *
     * 举个例子 =>
     *
     * 如果你尝试访问一个[Fragment],
     * 但
     * 该[Fragment]的[onDestroyView][Fragment.onDestroyView]已经被调用
     * 或
     * 该[Fragment]的[onViewCreated][Fragment.onViewCreated]还没有调用，
     * 将会抛出异常。
     */
    @set:MainThread
    var strictMode = true
        set(value) {
            check(Looper.getMainLooper() == Looper.myLooper())
            field = value
        }
}