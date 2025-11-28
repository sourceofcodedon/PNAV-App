package com.pampang.nav.screens.seller

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pampang.nav.R
import com.pampang.nav.databinding.ActivityAddStoreBinding
import com.pampang.nav.models.DropdownItem
import com.pampang.nav.models.StoreCategories
import com.pampang.nav.models.StoreModel
import com.pampang.nav.utilities.NetworkUtils
import com.pampang.nav.utilities.extension.setSafeOnClickListener
import com.pampang.nav.utilities.extension.showToast
import com.pampang.nav.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddStoreActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAddStoreBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var imageUrl: String? = null
    private var selectedStore: DropdownItem.StoreItem? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
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
    }

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_store)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mainViewModel
    }

    private fun initEventListener() {
        mBinding.apply {
            materialToolBar.setNavigationOnClickListener { finish() }

            buttonUploadImage.setOnClickListener {
                openImagePicker()
            }

            buttonCreate.setSafeOnClickListener {
                if (!NetworkUtils.isNetworkAvailable(this@AddStoreActivity)) {
                    showToast("The network is unstable. Try again later.")
                    return@setSafeOnClickListener
                }

                val storeName = mBinding.edittextStoreName.text.toString().trim()
                val storeNumber = mBinding.edittextStoreNumber.text.toString().trim()
                val openingTime = mBinding.edittextOpeningTime.text.toString().trim()
                val closingTime = mBinding.edittextClosingTime.text.toString().trim()

                if (storeName.isEmpty() || storeNumber.isEmpty() || selectedStore == null || openingTime.isEmpty() || closingTime.isEmpty()) {
                    showToast("Please fill all fields.")
                    return@setSafeOnClickListener
                } else {
                    mainViewModel.addStore(storeName, storeNumber, selectedStore!!.id, openingTime, closingTime, imageUrl)
                }

            }
        }
    }

    private fun initLiveData() {
        mainViewModel.addStoreResult.observe(this) { result ->
            result?.let { res ->
                res.onSuccess {
                    showResultDialog("Success", "Store has been created, wait for the admin approval.", true)
                    mainViewModel.clearAddStoreResult()
                }
                res.onFailure { throwable ->
                    showResultDialog("Error", throwable.message ?: "Unknown error")
                    mainViewModel.clearAddStoreResult()
                }
            }
        }

        mainViewModel.storeList.observe(this) { stores ->
            stores?.let {
                setupStoreDropdown(it)
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

    private fun setupStoreDropdown(stores: List<StoreModel>) {
        val existingStoreIds = stores.map { it.storeCategory }.toSet()
        val allItems = StoreCategories.getGroupedStores()

        val availableItems = allItems.filter { item ->
            item !is DropdownItem.StoreItem || item.id !in existingStoreIds
        }

        val adapter = GroupedArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, availableItems)
        mBinding.edittextStoreCategory.setAdapter(adapter)

        mBinding.edittextStoreCategory.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = availableItems[position]
            if (item is DropdownItem.StoreItem) {
                selectedStore = item
                mBinding.edittextStoreCategory.setText(item.displayName, false)
            }
        }
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
}