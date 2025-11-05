package com.pampang.nav.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.pampang.nav.adapters.GroupChatAdapter
import com.pampang.nav.databinding.FragmentGroupChatBinding
import com.pampang.nav.viewmodels.GroupChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatFragment : Fragment() {

    private lateinit var binding: FragmentGroupChatBinding
    private val viewModel: GroupChatViewModel by viewModels()
    private val groupChatAdapter = GroupChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewChat.adapter = groupChatAdapter

        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }

        lifecycleScope.launch {
            viewModel.messages.collect {
                groupChatAdapter.submitList(it)
                if (it.isNotEmpty()) {
                    binding.recyclerViewChat.scrollToPosition(it.size - 1)
                }
            }
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
    }
}