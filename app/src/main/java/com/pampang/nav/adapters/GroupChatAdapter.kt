package com.pampang.nav.adapters

import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.pampang.nav.databinding.ItemGroupChatReceivedBinding
import com.pampang.nav.databinding.ItemGroupChatSentBinding
import com.pampang.nav.models.GroupChatMessage

const val VIEW_TYPE_SENT = 1
const val VIEW_TYPE_RECEIVED = 2
private const val MAX_TRANSLATION_X = -250f

class GroupChatAdapter(
    private val onMessageLongClickListener: (GroupChatMessage) -> Unit
) : ListAdapter<GroupChatMessage, GroupChatAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = if (viewType == VIEW_TYPE_SENT) {
            ItemGroupChatSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            ItemGroupChatReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return MessageViewHolder(binding, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
        holder.itemView.setOnLongClickListener {
            onMessageLongClickListener(message)
            true
        }
    }

    class MessageViewHolder(private val binding: ViewBinding, private val viewType: Int) : RecyclerView.ViewHolder(binding.root) {

        private val messageContainer: View
        private val timestampView: View

        init {
            when (binding) {
                is ItemGroupChatSentBinding -> {
                    messageContainer = binding.messageContainer
                    timestampView = binding.textViewTimestamp
                }
                is ItemGroupChatReceivedBinding -> {
                    messageContainer = binding.messageContainer
                    timestampView = binding.textViewTimestamp
                }
                else -> throw IllegalStateException("Unknown view binding type")
            }
        }

        fun bind(chatMessage: GroupChatMessage) {
            when (binding) {
                is ItemGroupChatSentBinding -> {
                    binding.chatMessage = chatMessage
                    if (chatMessage.repliedToMessageId != null) {
                        binding.replyLayout.visibility = View.VISIBLE
                        binding.repliedToSender.text = chatMessage.repliedToMessageSender
                        binding.repliedToText.text = chatMessage.repliedToMessageText
                    } else {
                        binding.replyLayout.visibility = View.GONE
                    }
                    
                    val shouldShowSeen = chatMessage.seenBy.size > 1
                    Log.d("GroupChatAdapter", "Binding sent message ${chatMessage.id}. SeenBy size: ${chatMessage.seenBy.size}. Should show seen: $shouldShowSeen.")
                    binding.textViewSeen.visibility = if (shouldShowSeen) View.VISIBLE else View.GONE
                }
                is ItemGroupChatReceivedBinding -> {
                    binding.chatMessage = chatMessage
                    if (chatMessage.repliedToMessageId != null) {
                        binding.replyLayout.visibility = View.VISIBLE
                        binding.repliedToSender.text = chatMessage.repliedToMessageSender
                        binding.repliedToText.text = chatMessage.repliedToMessageText
                    } else {
                        binding.replyLayout.visibility = View.GONE
                    }
                }
            }
        }

        fun revealTimestamp(translationX: Float) {
            // Only translate the message container for sent messages
            if (viewType == VIEW_TYPE_SENT) {
                messageContainer.translationX = translationX
            }
            // Fade in the timestamp for all messages
            timestampView.alpha = translationX / MAX_TRANSLATION_X
        }

        fun resetTranslation() {
            if (viewType == VIEW_TYPE_SENT) {
                ObjectAnimator.ofFloat(messageContainer, "translationX", 0f).apply {
                    duration = 200
                    start()
                }
            }
            ObjectAnimator.ofFloat(timestampView, "alpha", 0f).apply {
                duration = 200
                start()
            }
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<GroupChatMessage>() {
        override fun areItemsTheSame(oldItem: GroupChatMessage, newItem: GroupChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupChatMessage, newItem: GroupChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
