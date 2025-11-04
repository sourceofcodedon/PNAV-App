package com.pampang.nav.screens.seller

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pampang.nav.R
import com.pampang.nav.databinding.ActivityAddStoreBinding
import com.pampang.nav.databinding.ActivitySellerMainBinding
import com.pampang.nav.utilities.extension.launchActivity
import com.pampang.nav.utilities.extension.setSafeOnClickListener
import com.pampang.nav.utilities.extension.showToast
import com.pampang.nav.viewmodels.AuthViewModel
import com.pampang.nav.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddStoreActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAddStoreBinding
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConfig()
    }

    private fun initConfig() {
        initBinding()
        initEventListener()
        setupTimePickerListeners()
        initLiveData()
        setupStoreDropdown()
    }

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_store)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mainViewModel
    }

    private fun initEventListener() {
        mBinding.apply {
            materialToolBar.setNavigationOnClickListener { finish() }

            buttonCreate.setSafeOnClickListener {
                val storeName = mBinding.edittextStoreName.text.toString().trim()
                val storeCategory = mBinding.edittextStoreCategory.text.toString().trim()
                val openingTime = mBinding.edittextOpeningTime.text.toString().trim()
                val closingTime = mBinding.edittextClosingTime.text.toString().trim()

                if (storeName.isEmpty() || storeCategory.isEmpty() || openingTime.isEmpty() || closingTime.isEmpty()) {
                    showToast("Please Fill all Fields")
                    return@setSafeOnClickListener
                } else {
                    mainViewModel.addStore(storeName, storeCategory, openingTime, closingTime)
                }

            }
        }
    }

    private fun initLiveData() {
        mainViewModel.addStoreResult.observe(this) { result ->
            result?.let {
                it.onSuccess {
                    showResultDialog("Success", "Store has been added", true)
                    mainViewModel.clearAddStoreResult()
                }
                it.onFailure {
                    showResultDialog("Error", it.message ?: "Unknown error")
                    mainViewModel.clearAddStoreResult()
                }
            }
        }

    }

    private fun setupTimePickerListeners() {
        // Opening Time Picker
        mBinding.edittextOpeningTime.setOnClickListener {
            showTimePicker { time ->
                mBinding.edittextOpeningTime.setText(time)
            }
        }

        // Closing Time Picker
        mBinding.edittextClosingTime.setOnClickListener {
            showTimePicker { time ->
                mBinding.edittextClosingTime.setText(time)
            }
        }
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val formattedTime = formatTime(selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            },
            hour,
            minute,
            false // false for 12-hour format with AM/PM
        )

        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

    private fun showResultDialog(title: String, message: String, isSuccess: Boolean = false) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                if (isSuccess) finish()
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }

    private fun setupStoreDropdown() {
        val stores = arrayOf("FirstFishStore", "SecondFishStore", "FirstGulayStore", "SecondGulayStore", "FirstMeatStore", "SecondMeatStore")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, stores)
        mBinding.edittextStoreCategory.setAdapter(adapter)
    }
}
