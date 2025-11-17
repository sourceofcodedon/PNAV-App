package com.pampang.nav.screens.buyer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.pampang.nav.R
import com.pampang.nav.databinding.ActivityBuyerMainBinding
import com.pampang.nav.screens.ChatActivity
import com.pampang.nav.services.MyFirebaseMessagingService
import com.pampang.nav.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyerMainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityBuyerMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var navController: NavController

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val title = intent?.getStringExtra("title")
            val body = intent?.getStringExtra("body")
            if (title != null && body != null) {
                Snackbar.make(mBinding.root, "$title: $body", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConfig()
        MyFirebaseMessagingService.getTokenAndUpload()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("NEW_MESSAGE_RECEIVED"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
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
