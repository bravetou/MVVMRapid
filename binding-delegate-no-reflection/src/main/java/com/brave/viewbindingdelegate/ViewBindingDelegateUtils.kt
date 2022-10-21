@file:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)

package com.brave.viewbindingdelegate

import android.app.Activity
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

/**
 * 在[View]中查找对应[ID][id]的第一个子[View]
 * @param id ID
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
fun <V : View> View.requireViewByIdCompat(@IdRes id: Int): V =
    ViewCompat.requireViewById(this, id)

/**
 * 在[Activity]中查找对应[ID][id]的第一个子[View]
 * @param id ID
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
fun <V : View> Activity.requireViewByIdCompat(@IdRes id: Int): V =
    ActivityCompat.requireViewById(this, id)

/**
 * 在[Activity]中查找根[View]
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun findRootView(activity: Activity): View {
    val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
    // 空检测[Activity]中[content][View]
    checkNotNull(contentView) { "Activity has no content view" }
    // [ViewGroup]中的子[View]数量
    return when (contentView.childCount) {
        1 -> contentView.getChildAt(0)
        // 没有找到子[View]
        0 -> error("Content view has no children. Provide a root view explicitly")
        // 找到了多个子[View]
        else -> error("More than one child view found in the Activity content view")
    }
}

/**
 * 在[DialogFragment]中查找对应[ID][viewBindingRootId]视图
 * @param viewBindingRootId ID
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun DialogFragment.getRootView(viewBindingRootId: Int): View {
    // 空检测[Dialog]
    val dialog = checkNotNull(dialog) {
        "DialogFragment doesn't have a dialog. Use viewBinding delegate after onCreate Dialog"
    }
    // 空检测[Window]
    val window = checkNotNull(dialog.window) {
        "Fragment's Dialog has no window"
    }
    // 在[Window]的顶级视图中查找[ID][viewBindingRootId]视图
    return with(window.decorView) {
        if (viewBindingRootId != 0) requireViewByIdCompat(
            viewBindingRootId
        ) else this
    }
}

/**
 * 空的[ViewBinding]回调
 */
internal val EMPTY_VB_CALLBACK: (ViewBinding) -> Unit = { _ -> }

/**
 * 空的[ViewBinding]回调
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <Binding : ViewBinding> emptyVbCallback(): (Binding) -> Unit =
    EMPTY_VB_CALLBACK

/**
 * 检查主线程
 */
internal fun checkMainThread() {
    // 该方法必须在主线程上调用
    check(Looper.getMainLooper() === Looper.myLooper()) {
        "The method must be called on the main thread"
    }
}