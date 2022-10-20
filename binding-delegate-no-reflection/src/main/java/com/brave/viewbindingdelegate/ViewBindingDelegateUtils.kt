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
 * 找到具有给定ID的第一个子视图，
 * 如果ID匹配[View.getId()]则查找视图本身，
 * 如果ID无效或在层次结构中没有匹配的视图则抛出IllegalArgumentException异常
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
inline fun <V : View> View.requireViewByIdCompat(@IdRes id: Int): V {
    return ViewCompat.requireViewById(this, id)
}

/**
 * 在[Activity]中找到具有给定ID的第一个子视图，
 * 如果ID匹配[View.getId()]则查找视图本身，
 * 如果ID无效或在层次结构中没有匹配的视图则抛出IllegalArgumentException异常
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
inline fun <V : View> Activity.requireViewByIdCompat(@IdRes id: Int): V {
    return ActivityCompat.requireViewById(this, id)
}

/**
 * 在[Activity]中为[ViewBinding]查找根视图
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun findRootView(activity: Activity): View {
    val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
    // 没有在[Activity]中找到内容视图
    checkNotNull(contentView) { "Activity has no content view" }
    // ViewGroup中的Children数量
    return when (contentView.childCount) {
        1 -> contentView.getChildAt(0)
        // 没有找到子View
        0 -> error("Content view has no children. Provide a root view explicitly")
        // 找到了多个子View
        else -> error("More than one child view found in the Activity content view")
    }
}

/**
 * 在[DialogFragment]中为[ViewBinding]查找根视图
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun DialogFragment.getRootView(viewBindingRootId: Int): View {
    // DialogFragment没有对话框
    val dialog = checkNotNull(dialog) {
        "DialogFragment doesn't have a dialog. Use viewBinding delegate after onCreate Dialog"
    }
    // Fragment的对话框没有窗口
    val window = checkNotNull(dialog.window) {
        "Fragment's Dialog has no window"
    }
    // 在顶级视图中查找根视图，如果ID为0则返回根视图
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
fun <Binding : ViewBinding> emptyVbCallback(): (Binding) -> Unit {
    return EMPTY_VB_CALLBACK
}

/**
 * 检查主线程
 */
internal inline fun checkMainThread() {
    // 该方法必须在主线程上调用
    check(Looper.getMainLooper() === Looper.myLooper()) {
        "The method must be called on the main thread"
    }
}