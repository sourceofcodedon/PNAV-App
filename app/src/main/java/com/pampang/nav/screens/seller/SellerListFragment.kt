package com.pampang.nav.screens.seller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.pampang.nav.R
import com.pampang.nav.databinding.FragmentListBinding
import com.pampang.nav.databinding.FragmentMainBinding
import com.pampang.nav.models.StoreModel
import com.pampang.nav.utilities.adapters.SimpleDiffUtilAdapter
import com.pampang.nav.utilities.extension.RecyclerClick
import com.pampang.nav.utilities.extension.launchActivity
import com.pampang.nav.utilities.extension.setSafeOnClickListener
import com.pampang.nav.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SellerListFragment : Fragment() {

    private lateinit var mBinding: FragmentListBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var mAdapter: SimpleDiffUtilAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.viewModel = mainViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initConfig()
    }

    override fun onResume() {
        super.onResume()
        initRequest()
    }

    private fun initConfig() {
        initExtras()
        initAdapter()
        initEventListener()
        initLiveData()
    }

    private fun initExtras() {
    }

    private fun initAdapter() {
        mAdapter = SimpleDiffUtilAdapter(R.layout.list_item_store, RecyclerClick{ store ->
            store as StoreModel
            val intent = Intent(requireActivity(), EditStoreActivity::class.java).apply {
                putExtra("store_id", store.id)
                putExtra("store_name", store.storeName)
                putExtra("store_category", store.storeCategory)
                putExtra("opening_time", store.openingTime)
                putExtra("closing_time", store.closingTime)
            }
            startActivity(intent)
        })

        mBinding.recyclerViewStores.adapter = mAdapter
    }

    private fun initEventListener() {
        mBinding.fabAddStore.setSafeOnClickListener {
            requireActivity().launchActivity<AddStoreActivity>()
        }
    }

    private fun initRequest() {
        mainViewModel.getStores()
    }

    private fun initLiveData() {
        mainViewModel.storeList.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }
    }

}