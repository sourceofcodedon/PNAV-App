package com.pampang.nav.screens.buyer

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.pampang.nav.R
import com.pampang.nav.databinding.ActivityBuyerMainBinding
import com.pampang.nav.screens.ChatActivity
import com.pampang.nav.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyerMainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityBuyerMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConfig()
    }

    private fun initConfig() {
        initBinding()
        initBottomNavigation()
    }

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_buyer_main)
        mBinding.lifecycleOwner = this
    }

    private fun initBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frame_layout) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(mBinding.bottomNavigation, navController)

        mBinding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.messages -> {
                    startActivity(Intent(this, ChatActivity::class.java))
                    false // Do not select the item
                }
                else -> NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }
}
