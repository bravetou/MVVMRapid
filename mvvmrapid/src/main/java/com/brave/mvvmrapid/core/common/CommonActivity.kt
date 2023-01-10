package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.core.CommonConfig
import com.brave.mvvmrapid.utils.GenericsHelper
import com.brave.mvvmrapid.utils.inflate
import com.brave.viewbindingdelegate.activityViewBinding

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/8 18:11
 *
 * ***desc***       ：Activity常用类
 */
@Suppress("PrivatePropertyName")
abstract class CommonActivity<Binding : ViewBinding, VM : CommonViewModel>
    : AppCompatActivity(), ICommonView {
    private val TAG by lazy { this::class.java.simpleName }

    private val allGenerics by lazy { GenericsHelper(javaClass).classes }

    protected open val binding: Binding by activityViewBinding(onViewDestroyed = {
        if (it is ViewDataBinding) it.unbind()
    }, viewBinder = { _ ->
        initViewBinding() ?: allGenerics.filterIsInstance<Class<Binding>>()
            .find { ViewBinding::class.java.isAssignableFrom(it) }
            ?.inflate(layoutInflater)
        ?: error("Generic <Binding> not found")
    }, viewNeedsInitialization = false)

    protected open fun initViewBinding(): Binding? = null

    protected open val viewModel: VM by lazy {
        initViewModel() ?: allGenerics
            .filterIsInstance<Class<VM>>()
            .find { CommonViewModel::class.java.isAssignableFrom(it) }
            ?.let { ViewModelProvider(this)[viewModelKey, it] }
        ?: error("Generic <VM> not found")
    }

    protected open fun initViewModel(): VM? = null

    protected val context: Context by lazy { this }

    protected val activity: FragmentActivity by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStart()
        initBinding()
        // 此处考虑到与immersionBar一起使用时需要使用布局文件中的View，所以与initBinding交换位置
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
        // 设置布局
        setContentView(getRootView())
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
     * 获取根布局
     * @return 根布局，默认返回[Binding]的[root][ViewBinding.getRoot]
     */
    protected open fun getRootView(): View = binding.root

    /**
     * 初始化[viewModel]的id
     */
    protected abstract val variableId: Int

    /**
     * ViewModel密匙
     */
    protected open val viewModelKey: String = "taskId_${TAG}_$taskId"

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
        viewModel.defUI.onStart.observe(this) { text -> onStart(text ?: "") }
        viewModel.defUI.onComplete.observe(this) { onComplete() }
        viewModel.defUI.onError.observe(this) { e -> onError(e) }
    }

    protected open fun onStart(text: String) {}
    protected open fun onComplete() {}
    protected open fun onError(e: Throwable) {}

    /**
     * 跳转页面
     * @param bundle 参数
     */
    @JvmOverloads
    inline fun <reified AC : Activity> startActivity(bundle: Bundle? = null) {
        val intent = Intent(this, AC::class.java)
        bundle?.let { data -> intent.putExtras(data) }
        startActivity(intent)
    }

    override fun onDestroy() {
        // 销毁时取消所有回调函数的持有
        callbacks.clear()
        register.unregister()
        super.onDestroy()
    }

    private val mRequestCode by lazy {
        intent?.extras?.getInt(CommonConfig.REQUEST_CODE, -1) ?: -1
    }
    private val callbacks = linkedMapOf<Int, (ActivityResult) -> Unit>()
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
        requestCode: Int? = null,
        callback: (ActivityResult) -> Unit = {}
    ) {
        val code = requestCode ?: cls.hashCode()
        callbacks[code] = callback
        register.launch(Intent(this, cls).let { intent ->
            bundle.putInt(CommonConfig.REQUEST_CODE, code)
            intent.putExtras(bundle)
        })
    }

    /**
     * 该[方法][finishForResult]需与[startActivityForResult]联用
     */
    @JvmOverloads
    fun finishForResult(resultCode: Int = RESULT_OK, data: Bundle = bundleOf()) {
        val intent = Intent()
        data.putInt(CommonConfig.REQUEST_CODE, mRequestCode)
        intent.putExtras(data)
        setResult(resultCode, intent)
        finish()
    }
}