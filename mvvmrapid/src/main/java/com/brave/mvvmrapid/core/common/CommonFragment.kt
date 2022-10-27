package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.core.CommonConfig
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
@Suppress("UNCHECKED_CAST", "REDUNDANT_MODIFIER", "SortModifiers", "PrivatePropertyName")
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
    open protected abstract val variableId: Int

    /**
     * ViewModel密匙
     */
    open protected val viewModelKey: String = "taskId_${TAG}_$tag"

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
        val intent = Intent(activity, AC::class.java)
        bundle?.let { data -> intent.putExtras(data) }
        startActivity(intent)
    }

    /**
     * 获取页面传递参数，使用[CommonActivity.startActivity]方法进入的
     * @param T 泛型参数类型
     * @param key 对应key
     * @param default 默认值
     * @return T 参数类型
     */
    @JvmOverloads
    fun <T : Any> getParam(key: String, default: T? = null): T? {
        val bundle = activity?.intent?.extras ?: return null
        return (bundle.get(key) as? T?) ?: default
    }

    /**
     * 获取片段传递参数
     * @param T 泛型参数类型
     * @param key 对应key
     * @param default 默认值
     * @return T 参数类型
     */
    @JvmOverloads
    fun <T : Any> getArgumentsParam(key: String, default: T? = null): T? {
        val bundle = arguments ?: return null
        return (bundle.get(key) as? T?) ?: default
    }

    override fun onDestroyView() {
        // 销毁时取消所有回调函数的持有
        callbacks.clear()
        super.onDestroyView()
    }

    private val callbacks = linkedMapOf<Int, (ActivityResult) -> Unit>()
    private val mRequestCode by lazy {
        getParam(CommonConfig.REQUEST_CODE, -1) ?: -1
    }
    private val register = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callbacks.filter { (requestCode, _) ->
            requestCode == result.data?.extras?.getInt(CommonConfig.REQUEST_CODE)
        }.forEach {
            it.value(result)
        }
    }

    /**
     * 该[方法][startActivityForResult]需与[finishForResult]联用
     */
    @JvmOverloads
    fun <AC : Activity> startActivityForResult(
        cls: Class<AC>,
        bundle: Bundle = bundleOf(),
        callback: (ActivityResult) -> Unit = {}
    ) {
        activity?.apply {
            val requestCode = cls.hashCode()
            callbacks[requestCode] = callback
            register.launch(Intent(this, cls).let { intent ->
                bundle.putInt(CommonConfig.REQUEST_CODE, requestCode)
                intent.putExtras(bundle)
            })
        }
    }

    /**
     * 该[方法][finishForResult]需与[startActivityForResult]联用
     */
    @JvmOverloads
    fun finishForResult(resultCode: Int, data: Bundle = bundleOf()) {
        activity?.apply {
            val intent = Intent()
            data.putInt(CommonConfig.REQUEST_CODE, mRequestCode)
            intent.putExtras(data)
            setResult(resultCode, intent)
            finish()
        }
    }
}