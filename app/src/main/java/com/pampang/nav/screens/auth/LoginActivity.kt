package com.pampang.nav.screens.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.pampang.nav.utilities.extension.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pampang.nav.R
import com.pampang.nav.databinding.ActivityLoginBinding
import com.pampang.nav.screens.buyer.BuyerMainActivity
import com.pampang.nav.screens.seller.SellerMainActivity
import com.pampang.nav.utilities.extension.launchActivity
import com.pampang.nav.viewmodels.AuthViewModel
import com.pampang.nav.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding
    private val mAuthViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var selectedRole: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initConfig()
    }

    private fun initConfig() {
        initBinding()
        initEventListener()
        initLiveData()
    }

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mAuthViewModel
//        hideKeyboardOnOutsideTouch(this, mBinding.constraintLayoutContent)

    }

    private fun initEventListener() {
        mBinding.apply {
            textViewSignUpNow.setOnClickListener {
                launchActivity<SignUpActivity>()
            }

            // --- ROLE SELECTION ---
            buttonBuyer.setOnClickListener {
                selectRole("buyer")
            }

            buttonSeller.setOnClickListener {
                selectRole("seller")
            }

            buttonLogin.setOnClickListener {
                val email = edittextEmail.text.toString().trim()
                val password = edittextPassword.text.toString().trim()
                val role = selectedRole

                if (email.isEmpty() || password.isEmpty()) {
                    showToast("Please fill all fields")
                    return@setOnClickListener
                }

                if (role.isNullOrEmpty()) {
                    showToast("Please select a role")
                    return@setOnClickListener
                }

                mAuthViewModel.login(email, password, role)

            }
        }
    }

    private fun initLiveData() {
        mAuthViewModel.loginResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    mainViewModel.getStores()
                    val role = selectedRole
                    when (role) {
                        "buyer" -> launchActivity<BuyerMainActivity>()
                        "seller" -> launchActivity<SellerMainActivity>() //
                        else -> launchActivity<LoginActivity>() // fallback
                    }
                    finish()
                } else {
                    showResultDialog("Error", it.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role

        val colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary)
        val white = ContextCompat.getColor(this, android.R.color.white)

        mBinding.apply {
            if (role == "buyer") {
                buttonBuyer.setBackgroundColor(colorPrimary)
                buttonBuyer.setTextColor(white)
                buttonSeller.setBackgroundColor(white)
                buttonSeller.setTextColor(colorPrimary)
            } else {
                buttonSeller.setBackgroundColor(colorPrimary)
                buttonSeller.setTextColor(white)
                buttonBuyer.setBackgroundColor(white)
                buttonBuyer.setTextColor(colorPrimary)
            }
        }
    }

    private fun showResultDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }

}