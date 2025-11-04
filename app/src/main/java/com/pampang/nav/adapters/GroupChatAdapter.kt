package com.pampang.nav.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.pampang.nav.databinding.ItemGroupChatReceivedBinding
import com.pampang.nav.databinding.ItemGroupChatSentBinding
import com.pampang.nav.models.GroupChatMessage

private const val VIEW_TYPE_SENT = 1
private const val VIEW_TYPE_RECEIVED = 2

class GroupChatAdapter : ListAdapter<GroupChatMessage, GroupChatAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = if (viewType == VIEW_TYPE_SENT) {
            ItemGroupChatSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            ItemGroupChatReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: GroupChatMessage) {
            when (binding) {
                is ItemGroupChatSentBinding -> binding.chatMessage = chatMessage
                is ItemGroupChatReceivedBinding -> binding.chatMessage = chatMessage
            }
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<GroupChatMessage>() {
        override fun areItemsTheSame(oldItem: GroupChatMessage, newItem: GroupChatMessage): Boolean {
            return oldItem.timestamp == newItem.timestamp && oldItem.senderId == newItem.senderId
        }

        override fun areContentsTheSame(oldItem: GroupChatMessage, newItem: GroupChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}