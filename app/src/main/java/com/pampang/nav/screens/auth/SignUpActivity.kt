package com.pampang.nav.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.pampang.nav.utilities.extension.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pampang.nav.R
import com.pampang.nav.databinding.ActivitySignupBinding
import com.pampang.nav.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySignupBinding
    private val mAuthViewModel: AuthViewModel by viewModels()

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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        mBinding.viewModel = mAuthViewModel
        mBinding.lifecycleOwner = this
    }

    private fun initEventListener() {
        mBinding.apply {
            textViewLogin.setOnClickListener {
                finish()
            }

            textViewTerms.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, TermsAndConditionsActivity::class.java))
            }

            // --- ROLE SELECTION ---
            buttonBuyer.setOnClickListener {
                selectRole("buyer")
            }

            buttonSeller.setOnClickListener {
                selectRole("seller")
            }

            // --- REGISTER BUTTON ---
            buttonRegister.setOnClickListener {
                val email = edittextEmail.text.toString().trim()
                val password = edittextPassword.text.toString().trim()
                val confirmPassword = edittextConfirmPassword.text.toString().trim()
                val username = edittextUsername.text.toString().trim()
                val role = selectedRole

                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    showToast("Please fill all fields")
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    showToast("Passwords do not match")
                    return@setOnClickListener
                }

                if (!isValidPassword(password)) {
                    showToast("Password must be at least 8 characters long, contain one uppercase letter, and one special character.")
                    return@setOnClickListener
                }

                if (role.isNullOrEmpty()) {
                    showToast("Please select a role")
                    return@setOnClickListener
                }

                if (!checkboxTerms.isChecked) {
                    showToast("Please accept the Terms and Conditions")
                    return@setOnClickListener
                }

                mAuthViewModel.register(email, password, username, role)
            }
        }
    }

    private fun initLiveData() {
        mAuthViewModel.registerResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    showResultDialog("Success", "Registration successful!", true)
                } else {
                    showResultDialog("Error", it.exceptionOrNull()?.message ?: "Unknown error")
                }
                mAuthViewModel.clearRegisterResult()
            }
        }

    }

    // --- ROLE SELECTION HANDLER ---
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

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) {
            return false
        }
        if (!password.matches(Regex(".*[A-Z].*"))) {
            return false
        }
        if (!password.matches(Regex(".*[!@#\$%^&*()_+-=,./?;':\"`~\\\\[\\\\]{}|<>].*"))) {
            return false
        }
        return true
    }

    private fun showResultDialog(title: String, message: String, isSuccess: Boolean = false) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(if (isSuccess) "Back to Login" else "OK") { dialog, _ ->
                if (isSuccess) finish()
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }


}