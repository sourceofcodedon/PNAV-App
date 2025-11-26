package com.pampang.nav.screens.seller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pampang.nav.R
import com.pampang.nav.MapNav.FirstFishStore
import com.pampang.nav.MapNav.FirstGulayStore
import com.pampang.nav.MapNav.FirstMeatStore
import com.pampang.nav.MapNav.SecondFishStore
import com.pampang.nav.MapNav.SecondGulayStore
import com.pampang.nav.MapNav.SecondMeatStore
import com.pampang.nav.databinding.FragmentListBinding
import com.pampang.nav.models.StoreModel
import com.pampang.nav.utilities.adapters.SimpleDiffUtilAdapter
import com.pampang.nav.utilities.extension.RecyclerClick
import com.pampang.nav.utilities.extension.launchActivity
import com.pampang.nav.utilities.extension.setSafeOnClickListener
import com.pampang.nav.utilities.extension.showToast
import com.pampang.nav.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerListFragment : Fragment() {

    private lateinit var mBinding: FragmentListBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var mAdapter: SimpleDiffUtilAdapter
    private var userType: String? = null
    private var showFavorites = false


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
        userType = arguments?.getString("user_type")
        if (userType == "buyer") {
            mBinding.fabAddStore.visibility = View.GONE
        } else {
            mBinding.buttonFavorite.visibility = View.GONE
        }
    }

    private fun initAdapter() {
        val onClick: RecyclerClick? = if (userType == "buyer") {
            RecyclerClick { store ->
                store as StoreModel
                val intent = when (store.storeCategory) {
                    "FirstMeatStore" -> Intent(requireActivity(), FirstMeatStore::class.java)
                    "SecondMeatStore" -> Intent(requireActivity(), SecondMeatStore::class.java)
                    "FirstGulayStore" -> Intent(requireActivity(), FirstGulayStore::class.java)
                    "SecondGulayStore" -> Intent(requireActivity(), SecondGulayStore::class.java)
                    "FirstFishStore" -> Intent(requireActivity(), FirstFishStore::class.java)
                    "SecondFishStore" -> Intent(requireActivity(), SecondFishStore::class.java)

                    else -> null
                }
                if (intent != null) {
                    intent.putExtra("storeName", store.storeName)
                    startActivity(intent)
                } else {
                    showToast("Unable to open store")
                }
            }
        } else {
            RecyclerClick { store ->
                store as StoreModel
                val intent = Intent(requireActivity(), EditStoreActivity::class.java).apply {
                    putExtra("store_id", store.id)
                    putExtra("store_name", store.storeName)
                    putExtra("store_category", store.storeCategory)
                    putExtra("opening_time", store.openingTime)
                    putExtra("closing_time", store.closingTime)
                    putExtra("image_url", store.image)
                }
                startActivity(intent)
            }
        }

        val onDelete: RecyclerClick? = if (userType == "buyer") {
            null
        } else {
            RecyclerClick { store ->
                store as StoreModel
                showDeleteConfirmationDialog(store)
            }
        }

        val onBookmark: RecyclerClick? = if (userType == "buyer") {
            RecyclerClick { store ->
                store as StoreModel
                if (store.isBookmarked) {
                    mainViewModel.deleteBookmark(store.id)
                } else {
                    mainViewModel.addBookmark(store.id)
                }
            }
        } else {
            null
        }

        mAdapter = SimpleDiffUtilAdapter(
            layoutRes = R.layout.list_item_store,
            onClickCallBack = onClick,
            onDeleteCallBack = onDelete,
            onBookmarkCallBack = onBookmark
        )

        mBinding.recyclerViewStores.adapter = mAdapter
    }

    private fun initEventListener() {
        mBinding.fabAddStore.setSafeOnClickListener {
            requireActivity().launchActivity<AddStoreActivity>()
        }

        mBinding.buttonFavorite.setSafeOnClickListener {
            showFavorites = !showFavorites
            updateStoreList(mainViewModel.storeList.value ?: emptyList())
            if (showFavorites) {
                mBinding.toolbar.title = getString(R.string.favorite)
                mBinding.buttonFavorite.text = getString(R.string.store_list_title)
            } else {
                mBinding.toolbar.title = getString(R.string.store_list_title)
                mBinding.buttonFavorite.text = getString(R.string.favorite)
            }
        }
    }

    private fun initRequest() {
        mainViewModel.getStores()
        if (userType == "buyer") {
            mainViewModel.getBookmarks()
        }
    }

    private fun initLiveData() {
        mainViewModel.storeList.observe(viewLifecycleOwner) { stores ->
            updateStoreList(stores)
        }

        mainViewModel.deleteStoreResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                it.onSuccess {
                    showToast("Store deleted successfully")
                    mainViewModel.clearDeleteStoreResult()
                }
                it.onFailure {
                    showToast("Failed to delete store: ${it.message}")
                    mainViewModel.clearDeleteStoreResult()
                }
            }
        }

        mainViewModel.addBookmarkResult.observe(viewLifecycleOwner) { result: Result<Unit>? ->
            result?.let {
                it.onSuccess {
                    showToast("Store bookmarked successfully")
                    mainViewModel.clearAddBookmarkResult()
                }
                it.onFailure {
                    showToast("Failed to bookmark store: ${it.message}")
                    mainViewModel.clearAddBookmarkResult()
                }
            }
        }

        mainViewModel.deleteBookmarkResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                it.onSuccess {
                    showToast("Bookmark removed successfully")
                    mainViewModel.clearDeleteBookmarkResult()
                }
                it.onFailure {
                    showToast("Failed to remove bookmark: ${it.message}")
                    mainViewModel.clearDeleteBookmarkResult()
                }
            }
        }
    }

    private fun updateStoreList(stores: List<StoreModel> = emptyList()) {
        if (showFavorites) {
            mAdapter.submitList(stores.filter { it.isBookmarked })
        } else {
            mAdapter.submitList(stores)
        }
    }

    private fun showDeleteConfirmationDialog(store: StoreModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Store")
            .setMessage("Are you sure you want to delete ${store.storeName}?")
            .setPositiveButton("Delete") { _, _ ->
                mainViewModel.deleteStore(store.id!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}