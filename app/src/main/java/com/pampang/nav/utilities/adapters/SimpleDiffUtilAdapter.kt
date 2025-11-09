package com.pampang.nav.utilities.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.auth.FirebaseAuth
import com.pampang.nav.databinding.ListItemProfileMenuBinding
import com.pampang.nav.databinding.ListItemStoreBinding
import com.pampang.nav.models.ProfileMenuModel
import com.pampang.nav.models.StoreModel
import com.pampang.nav.utilities.extension.RecyclerClick
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
            }

        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}