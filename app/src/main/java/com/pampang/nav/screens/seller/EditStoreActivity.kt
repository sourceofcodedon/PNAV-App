package com.pampang.nav.screens.seller

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pampang.nav.R
import com.pampang.nav.databinding.ActivityEditStoreBinding
import com.pampang.nav.models.StoreCategories
import com.pampang.nav.utilities.NetworkUtils
import com.pampang.nav.utilities.extension.setSafeOnClickListener
import com.pampang.nav.utilities.extension.showToast
import com.pampang.nav.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EditStoreActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityEditStoreBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var imageUrl: String? = null
    private var storeId: String? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let {
                handleImageSelection(it)
            }
        }
    }

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
        initIntent()
    }

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_store)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mainViewModel
    }

    private fun initEventListener() {
        mBinding.apply {
            materialToolBar.setNavigationOnClickListener { finish() }

            buttonUploadImage.setOnClickListener {
                openImagePicker()
            }

            buttonSave.setSafeOnClickListener {
                if (!NetworkUtils.isNetworkAvailable(this@EditStoreActivity)) {
                    showToast("The network is unstable. Try again later.")
                    return@setSafeOnClickListener
                }

                val storeName = mBinding.edittextStoreName.text.toString().trim()
                val storeCategory = mBinding.edittextStoreCategory.text.toString().trim()
                val openingTime = mBinding.edittextOpeningTime.text.toString().trim()
                val closingTime = mBinding.edittextClosingTime.text.toString().trim()

                if (storeName.isEmpty() || storeCategory.isEmpty() || openingTime.isEmpty() || closingTime.isEmpty()) {
                    showToast("Please fill all fields.")
                    return@setSafeOnClickListener
                } else {
                    storeId?.let {
                        mainViewModel.updateStore(it, storeName, storeCategory, openingTime, closingTime, imageUrl)
                    }
                }

            }
        }
    }

    private fun initLiveData() {
        mainViewModel.updateStoreResult.observe(this) { result ->
            result?.let {
                it.onSuccess {
                    showResultDialog("Success", "Store has been updated.", true)
                    mainViewModel.clearUpdateStoreResult()
                }
                it.onFailure {
                    showResultDialog("Error", it.message ?: "Unknown error")
                    mainViewModel.clearUpdateStoreResult()
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

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun handleImageSelection(uri: Uri) {
        mBinding.imageViewStore.setImageURI(uri)
        mBinding.imageUploadProgressBar.visibility = View.VISIBLE

        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                // No action needed
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                // No action needed
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                imageUrl = resultData["secure_url"].toString()
                mBinding.imageUploadProgressBar.visibility = View.GONE
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                showToast("Upload error: ${error.description}")
                mBinding.imageUploadProgressBar.visibility = View.GONE
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                // No action needed
            }
        }).dispatch()
    }

    private fun initIntent() {
        storeId = intent.getStringExtra("store_id")
        val storeName = intent.getStringExtra("store_name")
        val storeCategory = intent.getStringExtra("store_category")
        val openingTime = intent.getStringExtra("opening_time")
        val closingTime = intent.getStringExtra("closing_time")
        imageUrl = intent.getStringExtra("image_url")

        if (storeId == null) {
            showToast("Store ID is missing.")
            finish()
            return
        }

        mBinding.edittextStoreName.setText(storeName ?: "")
        mBinding.edittextStoreCategory.setText(StoreCategories.getDisplayName(storeCategory ?: "") ?: "")
        mBinding.edittextOpeningTime.setText(openingTime ?: "")
        mBinding.edittextClosingTime.setText(closingTime ?: "")

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(mBinding.imageViewStore)
        }
    }
}
