package com.pampang.nav.screens

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import com.pampang.nav.R
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.pampang.nav.databinding.ActivitySplashScreenBinding
import com.pampang.nav.screens.buyer.BuyerMainActivity // <-- Add this line
import com.pampang.nav.screens.seller.SellerMainActivity
import com.pampang.nav.screens.auth.LoginActivity
import com.pampang.nav.utilities.extension.launchActivity
import com.pampang.nav.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySplashScreenBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConfig()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun initConfig() {
        initBinding()
        initData()
    }

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        mBinding.lifecycleOwner = this
        initLoading()
    }

    private fun initData() {
        authViewModel.loadUserData()
    }


    private fun initLoading() {
        mBinding.progressBarSplash.isIndeterminate = false
        mBinding.progressBarSplash.progress = 0

        val animator = ObjectAnimator.ofInt(mBinding.progressBarSplash, "progress", 0, 100)
        animator.duration = 800 // 0.8 seconds
        animator.interpolator = FastOutSlowInInterpolator()
        animator.start()

        animator.doOnEnd {
            val currentUser = authViewModel.currentUser.value
            val role = authViewModel.loggedInRole.value

            if (currentUser != null) {
                when (role) {
                    "buyer" -> launchActivity<BuyerMainActivity>()
                    "seller" -> launchActivity<SellerMainActivity>()
                    else -> launchActivity<LoginActivity>()
                }
            } else {
                launchActivity<LoginActivity>()
            }
            finish()
        }

    }


}