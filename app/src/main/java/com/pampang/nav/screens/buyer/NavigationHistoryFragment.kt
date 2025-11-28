package com.pampang.nav.screens.buyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pampang.nav.R
import com.pampang.nav.utilities.adapters.NavHistoryAdapter
import com.pampang.nav.viewmodels.NavigationHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: NavigationHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation_history, container, false)
        recyclerView = view.findViewById(R.id.navigation_history_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.historyItems.observe(viewLifecycleOwner) { historyItems ->
            recyclerView.adapter = NavHistoryAdapter(historyItems)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.loadNavigationHistory()
    }
}
