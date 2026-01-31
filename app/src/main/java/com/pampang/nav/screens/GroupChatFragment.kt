package com.pampang.nav.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.pampang.nav.adapters.GroupChatAdapter
import com.pampang.nav.databinding.FragmentGroupChatBinding
import com.pampang.nav.viewmodels.GroupChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.max
import kotlin.math.min

private const val MAX_TRANSLATION_X = -250f

@AndroidEntryPoint
class GroupChatFragment : Fragment() {

    private lateinit var binding: FragmentGroupChatBinding
    private val viewModel: GroupChatViewModel by viewModels()
    private val groupChatAdapter = GroupChatAdapter {
        viewModel.setReplyToMessage(it)
    }
    private var initialX = 0f
    private var isSwiping = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewChat.adapter = groupChatAdapter

        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }

        lifecycleScope.launch {
            viewModel.messages.collect {
                groupChatAdapter.submitList(it) {
                    lifecycleScope.launch {
                        val lastReadTimestamp = viewModel.getLastReadTimestamp()
                        val firstUnreadMessageIndex = it.indexOfFirst { message -> (message.timestamp?.after(lastReadTimestamp ?: Date(0)) ?: false) }.takeIf { it != -1 } ?: (it.size - 1)
                        if (firstUnreadMessageIndex >= 0) {
                            binding.recyclerViewChat.scrollToPosition(firstUnreadMessageIndex)
                        }
                        markVisibleMessagesAsSeen()
                    }
                }
            }
        }
        
        binding.recyclerViewChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                markVisibleMessagesAsSeen()
            }
        })

        lifecycleScope.launch {
            viewModel.replyToMessage.collect { message ->
                if (message != null) {
                    binding.replyingToLayout.visibility = View.VISIBLE
                    binding.replyingToSender.text = message.senderName
                    binding.replyingToText.text = message.text
                } else {
                    binding.replyingToLayout.visibility = View.GONE
                }
            }
        }

        binding.cancelReplyButton.setOnClickListener {
            viewModel.setReplyToMessage(null)
        }

        lifecycleScope.launch {
            viewModel.networkError.collectLatest {
                Toast.makeText(requireContext(), "Network is Unstable", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSend.setOnClickListener {
            val message = binding.edittextChatbox.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.edittextChatbox.text.clear()
            }
        }

        binding.recyclerViewChat.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = event.x
                    isSwiping = false
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    val translationX = event.x - initialX
                    if (translationX < -50) {
                        isSwiping = true
                    }
                    if (isSwiping) {
                        val constrainedTranslationX = max(MAX_TRANSLATION_X, min(0f, translationX))
                        forEachVisibleHolder { it.revealTimestamp(constrainedTranslationX) }
                    }
                    isSwiping
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isSwiping) {
                        forEachVisibleHolder { it.resetTranslation() }
                        isSwiping = false
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
    }
    
    private fun markVisibleMessagesAsSeen() {
        val layoutManager = binding.recyclerViewChat.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        for (i in firstVisiblePosition..lastVisiblePosition) {
            if (i >= 0 && i < groupChatAdapter.currentList.size) {
                val message = groupChatAdapter.currentList[i]
                if (message.senderId != currentUserId && !message.seenBy.contains(currentUserId)) {
                    viewModel.markMessageAsSeen(message.id)
                }
            }
        }
    }

    private fun forEachVisibleHolder(action: (GroupChatAdapter.MessageViewHolder) -> Unit) {
        val layoutManager = binding.recyclerViewChat.layoutManager as LinearLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        for (i in firstVisible..lastVisible) {
            val holder = binding.recyclerViewChat.findViewHolderForAdapterPosition(i) as? GroupChatAdapter.MessageViewHolder
            holder?.let(action)
        }
    }

    override fun onPause() {
        super.onPause()
        val lastMessage = groupChatAdapter.currentList.lastOrNull()
        if (lastMessage != null) {
            lastMessage.timestamp?.let { viewModel.updateLastReadTimestamp(it) }
        }
    }
}
