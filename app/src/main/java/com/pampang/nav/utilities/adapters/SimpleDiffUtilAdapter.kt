package com.pampang.nav.utilities.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.auth.FirebaseAuth
import com.pampang.nav.R
import com.pampang.nav.databinding.ListItemProfileMenuBinding
import com.pampang.nav.databinding.ListItemStoreBinding
import com.pampang.nav.models.ProfileMenuModel
import com.pampang.nav.models.StoreModel
import com.pampang.nav.utilities.extension.RecyclerClick
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class SimpleDiffUtilAdapter @Inject constructor(
    @LayoutRes private val layoutRes: Int,
    private val onClickCallBack: Any? = null,
    private val onDeleteCallBack: Any? = null
) : SimpleListAdapter<Any>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }


    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutRes,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: Any, position: Int) {
        when (binding) {
            is ListItemProfileMenuBinding -> {
                binding.model = item as ProfileMenuModel
                binding.onClickCallBack = onClickCallBack as? RecyclerClick
            }

            is ListItemStoreBinding -> {
                val store = item as StoreModel
                val currentUser = FirebaseAuth.getInstance().currentUser
                binding.model = store
                binding.onClickCallBack = onClickCallBack as? RecyclerClick
                binding.onDeleteCallBack = onDeleteCallBack as? RecyclerClick
                binding.isOwner = currentUser != null && store.ownerId == currentUser.uid
                updateStoreStatus(binding, store)
            }

        }
    }

    private fun updateStoreStatus(binding: ListItemStoreBinding, store: StoreModel) {
        val openingTime = store.openingTime
        val closingTime = store.closingTime

        if (openingTime.isNullOrEmpty() || closingTime.isNullOrEmpty()) {
            binding.storeStatusIndicator.text = ""
            return
        }

        try {
            val sdf = SimpleDateFormat("h:mm a", Locale.US)

            val openTimeParsed = sdf.parse(openingTime)
            val closeTimeParsed = sdf.parse(closingTime)

            val cal = Calendar.getInstance()
            cal.time = openTimeParsed
            val openMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

            cal.time = closeTimeParsed
            val closeMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

            val now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
            val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

            val isOpen = if (openMinutes < closeMinutes) {
                // Same day opening (e.g. 9am to 5pm)
                nowMinutes >= openMinutes && nowMinutes < closeMinutes
            } else if (openMinutes > closeMinutes) {
                // Overnight opening (e.g. 10pm to 6am)
                nowMinutes >= openMinutes || nowMinutes < closeMinutes
            } else {
                // open 24 hours
                true
            }
            val context = binding.root.context
            if (isOpen) {
                binding.storeStatusIndicator.text = context.getString(R.string.store_status_open)
                binding.storeStatusIndicator.setTextColor(Color.GREEN)
            } else {
                binding.storeStatusIndicator.text = context.getString(R.string.store_status_closed)
                binding.storeStatusIndicator.setTextColor(Color.RED)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            binding.storeStatusIndicator.text = "" // Clear on error
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}