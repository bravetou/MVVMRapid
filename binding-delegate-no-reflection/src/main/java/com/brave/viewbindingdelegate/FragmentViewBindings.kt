@file:JvmName("FragmentViewBindings")
@file:Suppress("UNCHECKED_CAST")

package com.brave.viewbindingdelegate

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

private class DialogFragmentViewBindingProperty<in F : DialogFragment, out Binding : ViewBinding>(
    private val viewNeedsInitialization: Boolean,
    viewBinder: (F) -> Binding,
    onViewDestroyed: (Binding) -> Unit,
) : LifecycleViewBindingProperty<F, Binding>(viewBinder, onViewDestroyed) {
    override fun getLifecycleOwner(thisRef: F): LifecycleOwner = if (thisRef.view == null) {
        thisRef
    } else {
        try {
            thisRef.viewLifecycleOwner
        } catch (ignored: IllegalStateException) {
            error("Fragment doesn't have a view associated with it or the view has been destroyed")
        }
    }

    override fun isViewInitialized(thisRef: F): Boolean {
        if (!viewNeedsInitialization) {
            return true
        }
        return if (thisRef.showsDialog) {
            thisRef.dialog != null
        } else {
            thisRef.view != null
        }
    }
}

private class FragmentViewBindingProperty<in F : Fragment, out Binding : ViewBinding>(
    private val viewNeedsInitialization: Boolean,
    viewBinder: (F) -> Binding,
    onViewDestroyed: (Binding) -> Unit,
) : LifecycleViewBindingProperty<F, Binding>(viewBinder, onViewDestroyed) {
    private var fragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks? = null

    private var fragmentManager: Reference<FragmentManager>? = null

    override fun getValue(thisRef: F, property: KProperty<*>): Binding {
        val viewBinding = super.getValue(thisRef, property)
        registerFragmentLifecycleCallbacksIfNeeded(thisRef)
        return viewBinding
    }

    private fun registerFragmentLifecycleCallbacksIfNeeded(fragment: Fragment) {
        if (fragmentLifecycleCallbacks != null) return
        val fragmentManager = fragment.parentFragmentManager.also { fm ->
            this.fragmentManager = WeakReference(fm)
        }
        fragmentLifecycleCallbacks = ClearOnDestroy(fragment).also { callbacks ->
            fragmentManager.registerFragmentLifecycleCallbacks(callbacks, false)
        }
    }

    override fun isViewInitialized(thisRef: F): Boolean {
        if (!viewNeedsInitialization) return true
        return if (thisRef !is DialogFragment) {
            thisRef.view != null
        } else {
            super.isViewInitialized(thisRef)
        }
    }

    override fun clear() {
        super.clear()
        fragmentManager?.get()?.let { fragmentManager ->
            fragmentLifecycleCallbacks?.let(fragmentManager::unregisterFragmentLifecycleCallbacks)
        }
        fragmentManager = null
        fragmentLifecycleCallbacks = null
    }

    override fun getLifecycleOwner(thisRef: F): LifecycleOwner = try {
        thisRef.viewLifecycleOwner
    } catch (ignored: IllegalStateException) {
        error("Fragment doesn't have a view associated with it or the view has been destroyed")
    }

    private inner class ClearOnDestroy(
        fragment: Fragment
    ) : FragmentManager.FragmentLifecycleCallbacks() {
        private var fragment: Reference<Fragment> = WeakReference(fragment)

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            // Fix for the view destruction in the case with a navigation issue
            if (fragment.get() === f) {
                postClear()
            }
        }
    }
}

/**
 * ???????????????[Fragment]?????????[ViewBinding]
 * @param viewBinder ??????????????????
 */
@JvmName("viewBindingFragment")
fun <F : Fragment, Binding : ViewBinding> Fragment.viewBinding(
    viewBinder: (F) -> Binding,
): ViewBindingProperty<F, Binding> = viewBinding(
    viewBinder,
    emptyVbCallback()
)

/**
 * ???????????????[Fragment]?????????[ViewBinding]
 * @param viewBinder ??????????????????
 * @param onViewDestroyed ????????????????????????
 */
@JvmName("viewBindingFragmentWithCallbacks")
fun <F : Fragment, Binding : ViewBinding> Fragment.viewBinding(
    viewBinder: (F) -> Binding,
    onViewDestroyed: (Binding) -> Unit = {},
): ViewBindingProperty<F, Binding> = when (this) {
    is DialogFragment -> dialogFragmentViewBinding(
        onViewDestroyed,
        viewBinder
    )
    else -> fragmentViewBinding(
        onViewDestroyed,
        viewBinder
    )
}

/**
 * ???????????????[Fragment]?????????[ViewBinding]
 * @param vbFactory ??????[ViewBinding]??????????????????????????????*MyViewBinding::bind*
 * @param viewProvider [View]??????????????????????????????[Fragment][Fragment]?????????[View][Fragment.getView]
 */
@JvmName("viewBindingFragment")
inline fun <F : Fragment, Binding : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (F) -> View = Fragment::requireView,
): ViewBindingProperty<F, Binding> = viewBinding(
    vbFactory,
    viewProvider,
    emptyVbCallback()
)

/**
 * ???????????????[Fragment]?????????[ViewBinding]
 * @param vbFactory ??????[ViewBinding]??????????????????????????????*MyViewBinding::bind*
 * @param viewProvider [View]??????????????????????????????[Fragment][Fragment]?????????[View][Fragment.getView]
 * @param onViewDestroyed ????????????????????????
 */
@JvmName("viewBindingFragmentWithCallbacks")
inline fun <F : Fragment, Binding : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (F) -> View = Fragment::requireView,
    noinline onViewDestroyed: (Binding) -> Unit = {},
): ViewBindingProperty<F, Binding> = viewBinding(
    { fragment: F ->
        vbFactory(viewProvider(fragment))
    },
    onViewDestroyed
)

/**
 * ???????????????[Fragment]?????????[ViewBinding]
 * @param vbFactory ??????[ViewBinding]??????????????????????????????*MyViewBinding::bind*
 * @param viewBindingRootId [View] ID
 */
@JvmName("viewBindingFragment")
inline fun <F : Fragment, Binding : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int,
): ViewBindingProperty<F, Binding> = viewBinding(
    vbFactory,
    viewBindingRootId,
    emptyVbCallback()
)

/**
 * ???????????????[Fragment]?????????[ViewBinding]
 * @param vbFactory ??????[ViewBinding]??????????????????????????????*MyViewBinding::bind*
 * @param viewBindingRootId [View] ID
 * @param onViewDestroyed ????????????????????????
 */
@JvmName("viewBindingFragmentWithCallbacks")
inline fun <F : Fragment, Binding : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int,
    noinline onViewDestroyed: (Binding) -> Unit,
): ViewBindingProperty<F, Binding> = when (this) {
    is DialogFragment -> {
        viewBinding<DialogFragment, Binding>(
            vbFactory,
            { fragment ->
                fragment.getRootView(viewBindingRootId)
            },
            onViewDestroyed
        ) as ViewBindingProperty<F, Binding>
    }
    else -> {
        viewBinding(
            vbFactory,
            { fragment: F ->
                fragment.requireView().requireViewByIdCompat(viewBindingRootId)
            },
            onViewDestroyed
        )
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <F : Fragment, Binding : ViewBinding> fragmentViewBinding(
    onViewDestroyed: (Binding) -> Unit,
    viewBinder: (F) -> Binding,
    viewNeedsInitialization: Boolean = true
): ViewBindingProperty<F, Binding> = FragmentViewBindingProperty(
    viewNeedsInitialization,
    viewBinder,
    onViewDestroyed
)

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <F : Fragment, Binding : ViewBinding> dialogFragmentViewBinding(
    onViewDestroyed: (Binding) -> Unit,
    viewBinder: (F) -> Binding,
    viewNeedsInitialization: Boolean = true
): ViewBindingProperty<F, Binding> = DialogFragmentViewBindingProperty(
    viewNeedsInitialization,
    viewBinder,
    onViewDestroyed
) as ViewBindingProperty<F, Binding>