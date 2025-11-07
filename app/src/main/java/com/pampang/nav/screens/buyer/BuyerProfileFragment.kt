package com.pampang.nav.screens.buyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
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
class BuyerProfileFragment : Fragment() {

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
            when (it.titleResId) {
                R.string.profile_personal_detail -> {
                    showEditUsernameDialog()
                }
                R.string.profile_contact_us -> {
                    showToast(getString(it.titleResId))
                }
                R.string.profile_privacy_security -> {
                    showChangePasswordDialog()
                }
                R.string.profile_preferences -> {
                    showLanguageSelectionDialog()
                }

                R.string.profile_logout -> {
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

        authViewModel.updateUsernameResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    showToast("Username updated successfully")
                    authViewModel.loadUserData()
                } else {
                    showToast(it.exceptionOrNull()?.message ?: "Unknown error")
                }
                authViewModel.clearUpdateUsernameResult()
            }
        }
        
        authViewModel.updatePasswordResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    showToast("Password updated successfully")
                    logout()
                } else {
                    showToast(it.exceptionOrNull()?.message ?: "Unknown error")
                }
                authViewModel.clearUpdatePasswordResult()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.profile_logout))
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

    private fun showEditUsernameDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_username, null)
        val usernameEditText = dialogView.findViewById<TextInputEditText>(R.id.edittext_username)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Username")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newUsername = usernameEditText.text.toString().trim()
                if (newUsername.isNotEmpty()) {
                    authViewModel.updateUsername(newUsername)
                } else {
                    showToast("Username cannot be empty")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Japanese", "Korean")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val selectedLanguage = when (which) {
                    0 -> "en"
                    1 -> "ja"
                    2 -> "ko"
                    else -> "en"
                }
                setLocale(selectedLanguage)
            }
            .show()
    }

    private fun setLocale(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPasswordEditText = dialogView.findViewById<TextInputEditText>(R.id.edittext_current_password)
        val newPasswordEditText = dialogView.findViewById<TextInputEditText>(R.id.edittext_new_password)
        val confirmPasswordEditText = dialogView.findViewById<TextInputEditText>(R.id.edittext_confirm_password)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val currentPassword = currentPasswordEditText.text.toString().trim()
                val newPassword = newPasswordEditText.text.toString().trim()
                val confirmPassword = confirmPasswordEditText.text.toString().trim()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showToast("All fields are required")
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    showToast("Passwords do not match")
                    return@setPositiveButton
                }

                authViewModel.updatePassword(currentPassword, newPassword)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}