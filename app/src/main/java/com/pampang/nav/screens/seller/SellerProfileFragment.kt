package com.pampang.nav.screens.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pampang.nav.R
import com.pampang.nav.databinding.FragmentProfileBinding
import com.pampang.nav.models.ProfileMenuModel
import com.pampang.nav.screens.auth.LoginActivity
import com.pampang.nav.utilities.adapters.SimpleDiffUtilAdapter
import com.pampang.nav.utilities.extension.RecyclerClick
import com.pampang.nav.utilities.extension.launchActivity
import com.pampang.nav.utilities.extension.showToast
import com.pampang.nav.viewmodels.AuthViewModel
import com.pampang.nav.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerProfileFragment : Fragment() {

    private lateinit var mBinding: FragmentProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var mAdapter: SimpleDiffUtilAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        mBinding.lifecycleOwner = this

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
        initEventListener()
        initAdapter()
        initLiveData()
    }

    private fun initAdapter() {
        mAdapter = SimpleDiffUtilAdapter(R.layout.list_item_profile_menu, RecyclerClick {
            it as ProfileMenuModel
            when (it.title) {
                "Personal Detail" -> {
                    showToast(it.title)
                }
                "Contact Us" -> {
                    showToast(it.title)
                }
                "Privacy and Security" -> {
                    showToast(it.title)
                }
                "Preferences" -> {
                    showToast(it.title)
                }

                "Logout" -> {
                    showLogoutConfirmationDialog()
                }
            }
        })

        mBinding.recyclerViewProfileMenu.adapter = mAdapter

    }

    private fun initExtras() {
    }

    private fun initEventListener() {
        mBinding.apply {

        }
    }

    private fun initRequest() {
        authViewModel.loadUserData()
    }

    private fun initLiveData() {
        mainViewModel.profileMenuItems.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }

        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let { mBinding.textViewTitle.text = it.displayName }
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ -> logout() }
            .setNegativeButton("No", null)
            .setCancelable(false)
            .show()
    }

    private fun logout() {
        authViewModel.logout()
        requireActivity().launchActivity<LoginActivity>()
        requireActivity().finish()
    }

}