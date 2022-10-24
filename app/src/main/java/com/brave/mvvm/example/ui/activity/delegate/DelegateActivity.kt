package com.brave.mvvm.example.ui.activity.delegate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.StringUtils
import com.brave.mvvm.example.BR
import com.brave.mvvm.example.R
import com.brave.mvvm.example.bean.FunctionBean
import com.brave.mvvm.example.databinding.ActivityDelegateBinding
import com.brave.mvvm.example.databinding.ActivityDelegateTemplateBinding
import com.brave.mvvm.example.ui.adapter.FunctionAdapter
import com.brave.mvvm.example.ui.fragment.delegate.DelegateTemplateNoReflectionFragment
import com.brave.mvvm.example.ui.fragment.delegate.DelegateTemplateReflectBindFragment
import com.brave.mvvm.example.ui.fragment.delegate.DelegateTemplateReflectInflateFragment
import com.brave.mvvmrapid.core.common.CommonActivity
import com.brave.viewbindingdelegate.CreateMethod
import com.brave.viewbindingdelegate.viewBinding

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/10/24 14:39
 *
 * ***desc***       ：Delegate View
 */
class DelegateActivity : CommonActivity<ActivityDelegateBinding, DelegateViewModel>() {
    override val variableId: Int
        get() = BR.viewModel

    private val adapter by lazy {
        FunctionAdapter()
    }

    private val data by lazy {
        mutableListOf(
            FunctionBean(0, R.mipmap.icon_dot, "no reflection"),
            FunctionBean(1, R.mipmap.icon_dot, "reflect `inflate`"),
            FunctionBean(2, R.mipmap.icon_dot, "reflect `bind`")
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setNewInstance(data)

        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            when (item.id) {
                0 -> {
                    // 无反射
                    startActivity<DelegateTemplateNoReflectionActivity>()
                }
                1 -> {
                    // 反射`inflate`
                    startActivity<DelegateTemplateReflectInflateActivity>()
                }
                2 -> {
                    // 反射`bind`
                    startActivity<DelegateTemplateReflectBindActivity>()
                }
            }
        }
    }
}

class DelegateTemplateNoReflectionActivity :
    AppCompatActivity(R.layout.activity_delegate_template) {
    private val binding by viewBinding(ActivityDelegateTemplateBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvContent.text = "activity => 无反射"
        initRecycler(binding)
    }
}

class DelegateTemplateReflectInflateActivity :
    AppCompatActivity() {
    private val binding by viewBinding<ActivityDelegateTemplateBinding>(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tvContent.text = "activity => 反射 inflate"
        initRecycler(binding)
    }
}

class DelegateTemplateReflectBindActivity :
    AppCompatActivity(R.layout.activity_delegate_template) {
    private val binding: ActivityDelegateTemplateBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvContent.text = "activity => 反射 bind"
        initRecycler(binding)
    }
}

private fun AppCompatActivity.initRecycler(binding: ActivityDelegateTemplateBinding) {
    binding.rvListShow.layoutManager = LinearLayoutManager(this)
    binding.rvListShow.adapter = showAdapter
    showAdapter.setNewInstance(showData)
    showAdapter.setOnItemClickListener { _, _, position ->
        val item = showAdapter.getItem(position)
        when (item.id) {
            0 -> {
                // 无反射
                supportFragmentManager.findFragmentByTag(
                    DelegateTemplateNoReflectionFragment.TAG
                ) ?: DelegateTemplateNoReflectionFragment()
            }
            1 -> {
                // 反射`inflate`
                supportFragmentManager.findFragmentByTag(
                    DelegateTemplateReflectInflateFragment.TAG
                ) ?: DelegateTemplateReflectInflateFragment()
            }
            2 -> {
                // 反射`bind`
                supportFragmentManager.findFragmentByTag(
                    DelegateTemplateReflectBindFragment.TAG
                ) ?: DelegateTemplateReflectBindFragment()
            }
            else -> null
        }?.let {
            if (!it.tag.isNullOrEmpty()) return@setOnItemClickListener
            supportFragmentManager.beginTransaction()
                .add(
                    binding.fragment.id, it, when (item.id) {
                        0 -> DelegateTemplateNoReflectionFragment.TAG
                        1 -> DelegateTemplateReflectInflateFragment.TAG
                        2 -> DelegateTemplateReflectBindFragment.TAG
                        else -> null
                    }
                ).commitNow()
        }
    }

    binding.rvListRemove.layoutManager = LinearLayoutManager(this)
    binding.rvListRemove.adapter = removeAdapter
    removeAdapter.setNewInstance(removeData)
    removeAdapter.setOnItemClickListener { _, _, position ->
        val item = removeAdapter.getItem(position)
        when (item.id) {
            0 -> {
                // 无反射
                supportFragmentManager.findFragmentByTag(
                    DelegateTemplateNoReflectionFragment.TAG
                )
            }
            1 -> {
                // 反射`inflate`
                supportFragmentManager.findFragmentByTag(
                    DelegateTemplateReflectInflateFragment.TAG
                )
            }
            2 -> {
                // 反射`bind`
                supportFragmentManager.findFragmentByTag(
                    DelegateTemplateReflectBindFragment.TAG
                )
            }
            else -> null
        }?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commitNow()
        }
    }
}

private val showAdapter by lazy {
    FunctionAdapter()
}
private val showData by lazy {
    mutableListOf(
        FunctionBean(
            0,
            -1,
            StringUtils.getString(R.string.str_show_no_reflection_fragments)
        ),
        FunctionBean(
            1,
            -1,
            StringUtils.getString(R.string.str_show_reflection_inflate_method_fragment)
        ),
        FunctionBean(
            2,
            -1,
            StringUtils.getString(R.string.str_show_reflection_bind_method_fragment)
        ),
    )
}
private val removeAdapter by lazy {
    FunctionAdapter()
}
private val removeData by lazy {
    mutableListOf(
        FunctionBean(
            0,
            -1,
            StringUtils.getString(R.string.str_remove_no_reflection_fragments)
        ),
        FunctionBean(
            1,
            -1,
            StringUtils.getString(R.string.str_remove_reflection_inflate_method_fragment)
        ),
        FunctionBean(
            2,
            -1,
            StringUtils.getString(R.string.str_remove_reflection_bind_method_fragment)
        ),
    )
}