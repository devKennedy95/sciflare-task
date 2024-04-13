package com.task.sciflare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.task.sciflare.R
import com.task.sciflare.databinding.FragmentHomeBinding
import com.task.sciflare.ui.BaseFragment
import com.task.sciflare.ui.adapter.HomeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private var tabLayoutMediator: TabLayoutMediator? = null

    private var currentPage: Int = 0

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewReady(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        getBinding().apply {
            viewPager.isSaveFromParentEnabled = false
            viewPager.adapter = HomeAdapter(requireActivity())

            tabLayoutMediator =
                TabLayoutMediator(binding.tabLayout, binding.viewPager, true) { tab, position ->
                    tab.text = when (position) {
                        0 -> getString(R.string.sender)
                        else -> getString(R.string.receiver)
                    }
                }

            tabLayoutMediator?.attach()
        }
    }

    override fun onResume() {
        super.onResume()
        getBinding().viewPager.setCurrentItem(currentPage, false)
    }

    override fun onPause() {
        super.onPause()
        currentPage = getBinding().viewPager.currentItem
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
    }
}