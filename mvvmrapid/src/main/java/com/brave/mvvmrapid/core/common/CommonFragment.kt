package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.utils.GenericsHelper
import com.brave.mvvmrapid.utils.inflate
import com.brave.viewbindingdelegate.fragmentViewBinding

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/9 14:27
 *
 * ***desc***       ：Fragment常用类
 */
@Suppress("PrivatePropertyName")
abstract class CommonFragment<Binding : ViewBinding, VM : CommonViewModel>
    : Fragment(), ICommonView {
    private val TAG by lazy { this::class.java.simpleName }

    private val allGenerics by lazy { GenericsHelper(javaClass).classes }

    open val binding: Binding by fragmentViewBinding(onViewDestroyed = {
        if (it is ViewDataBinding) it.unbind()
    }, viewBinder = { _ ->
        initViewBinding() ?: allGenerics.filterIsInstance<Class<Binding>>()
            .find { ViewBinding::class.java.isAssignableFrom(it) }
            ?.inflate(layoutInflater)
        ?: error("Generic <Binding> not found")
    }, viewNeedsInitialization = false)

    open fun initViewBinding(): Binding? = null

    open val viewModel: VM by lazy {
        initViewModel() ?: allGenerics
            .filterIsInstance<Class<VM>>()
            .find { CommonViewModel::class.java.isAssignableFrom(it) }
            ?.let { ViewModelProvider(this)[viewModelKey, it] }
        ?: error("Generic <VM> not found")
    }

    open fun initViewModel(): VM? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    protected val context by lazy {
        requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStart()
        initSystemBar()
        initBinding()
        updateDefaultUI()
        initGlobalBus()
        initView(savedInstanceState)
        initData()
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initObserver()
        initEnd()
    }

    private fun initBinding() {
        binding.let { binding ->
            // [ViewDataBinding]
            if (binding is ViewDataBinding) {
                // 关联ViewModel
                binding.setVariable(variableId, viewModel)
                // 支持LiveData绑定xml，数据改变，UI自动会更新
                binding.lifecycleOwner = this
                // 让ViewModel拥有View的生命周期感应
                lifecycle.addObserver(viewModel)
            }
        }
    }

    /**
     * 初始化[viewModel]的id
     */
    abstract val variableId: Int

    /**
     * ViewModel密匙
     */
    open val viewModelKey: String = "taskId_${TAG}_$tag"

    /**
     * 刷新布局
     */
    open fun refreshLayout() {
        binding.let { binding ->
            if (binding is ViewDataBinding) {
                binding.setVariable(variableId, viewModel)
            }
        }
    }

    override fun initStart() {}

    override fun initSystemBar() {}

    override fun initGlobalBus() {}

    override fun initData() {}

    override fun initObserver() {}

    override fun initEnd() {}

    /**
     * 默认UI更新
     */
    private fun updateDefaultUI() {
        viewModel.defUI.onStart.observe(viewLifecycleOwner) { text -> onStart(text ?: "") }
        viewModel.defUI.onComplete.observe(viewLifecycleOwner) { onComplete() }
        viewModel.defUI.onError.observe(viewLifecycleOwner) { e -> onError(e) }
    }

    open fun onStart(text: String) {}
    open fun onComplete() {}
    open fun onError(e: Throwable) {}

    /**
     * 跳转页面
     * @param bundle 参数
     */
    @JvmOverloads
    inline fun <reified AC : Activity> startActivity(bundle: Bundle? = null) {
        val activity = activity ?: return
        if (activity is CommonActivity<*, *>) {
            activity.startActivity<AC>(bundle)
        }
    }

    /**
     * 该[方法][startActivityForResult]需与[finishForResult]联用
     */
    @JvmOverloads
    fun <AC : Activity> startActivityForResult(
        cls: Class<AC>,
        bundle: Bundle = bundleOf(),
        requestCode: Int? = null,
        callback: (ActivityResult) -> Unit = {}
    ) {
        val activity = activity ?: return
        if (activity is CommonActivity<*, *>) {
            val code = requestCode ?: cls.hashCode()
            activity.startActivityForResult(cls, bundle, code, callback)
        }
    }

    /**
     * 该[方法][finishForResult]需与[startActivityForResult]联用
     */
    @JvmOverloads
    fun finishForResult(resultCode: Int, data: Bundle = bundleOf()) {
        val activity = activity ?: return
        if (activity is CommonActivity<*, *>) {
            activity.finishForResult(resultCode, data)
        }
    }
}