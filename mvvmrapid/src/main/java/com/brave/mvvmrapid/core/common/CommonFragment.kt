package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
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
abstract class CommonFragment<Binding : ViewBinding, VM : CommonViewModel> : Fragment(),
    ICommonView {
    private val _tag by lazy { this::class.java.simpleName }

    private val _helper by lazy { GenericsHelper(javaClass) }

    protected open val binding: Binding by fragmentViewBinding(onViewDestroyed = {
        if (it is ViewDataBinding) it.unbind()
    }, viewBinder = { _ ->
        initViewBinding() ?: _helper.find<ViewBinding, Binding>() //
            ?.inflate(layoutInflater) //
        ?: error("Generic <Binding> not found")
    }, viewNeedsInitialization = false)

    protected open fun initViewBinding(): Binding? = null

    protected open val viewModel: VM by lazy {
        initViewModel() ?: _helper.find<CommonViewModel, VM>() //
            ?.let { ViewModelProvider(this)[viewModelKey, it] } //
        ?: error("Generic <VM> not found")
    }

    protected open fun initViewModel(): VM? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = getRootView(inflater, container, savedInstanceState)

    /**
     * 获取根布局
     * @return 根布局，默认返回[Binding]的[root][ViewBinding.getRoot]
     */
    protected open fun getRootView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun getContext(): Context =
        super.getContext() ?: error("Fragment $this not attached to a context.")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStart()
        initBinding()
        initSystemBar()
        updateDefaultUI()
        initGlobalBus()
        initView(savedInstanceState)
        initData()
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initObserver()
        initEnd()
    }

    protected open fun initBinding() {
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
    protected abstract val variableId: Int

    /**
     * ViewModel密匙
     */
    protected open val viewModelKey: String = "taskId_${_tag}_$tag"

    /**
     * 刷新布局
     */
    protected open fun refreshLayout() {
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

    protected open fun onStart(text: String) {}
    protected open fun onComplete() {}
    protected open fun onError(e: Throwable) {}

    /**
     * 跳转页面，
     * 只有Activity继承至[CommonActivity]才可正常使用
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
     * 跳转页面，
     * 只有Activity继承至[CommonActivity]才可正常使用
     * @param launcher 可用于启动活动或处理已准备调用的启动程序
     * @param bundle 参数
     */
    @JvmOverloads
    inline fun <reified AC : Activity> startActivityForResult(
        launcher: ActivityResultLauncher<Intent>,
        bundle: Bundle = bundleOf(),
    ) {
        val activity = activity ?: return
        if (activity is CommonActivity<*, *>) {
            activity.startActivityForResult<AC>(launcher, bundle)
        }
    }

    override fun onDestroyView() {
        // 解绑ViewDataBinding
        binding.also { binding -> if (binding is ViewDataBinding) binding.unbind() }
        super.onDestroyView()
    }

    /**
     * 该[方法][startActivityForResult]需与[finishForResult]联用，
     * 只有Activity继承至[CommonActivity]才可正常使用
     */
    @JvmOverloads
    fun <AC : Activity> startActivityForResult(
        cls: Class<AC>,
        bundle: Bundle = bundleOf(),
        requestCode: Int? = null,
        callback: (Bundle) -> Unit = {}
    ) {
        val activity = activity ?: return
        if (activity is CommonActivity<*, *>) {
            val code = requestCode ?: cls.hashCode()
            activity.startActivityForResult(cls, bundle, code, callback)
        }
    }

    /**
     * 该[方法][finishForResult]需与[startActivityForResult]联用，
     * 只有Activity继承至[CommonActivity]才可正常使用
     */
    @JvmOverloads
    fun finishForResult(resultCode: Int = Activity.RESULT_OK, data: Bundle = bundleOf()) {
        val activity = activity ?: return
        if (activity is CommonActivity<*, *>) {
            activity.finishForResult(resultCode, data)
        }
    }
}