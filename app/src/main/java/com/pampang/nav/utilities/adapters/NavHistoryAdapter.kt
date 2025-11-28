package com.pampang.nav.utilities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pampang.nav.R
import com.pampang.nav.models.NavigationHistoryItem
import java.text.SimpleDateFormat
import java.util.Locale

class NavHistoryAdapter(private val historyItems: List<NavigationHistoryItem>) :
    RecyclerView.Adapter<NavHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storeNameTextView: TextView = view.findViewById(R.id.store_name_text_view)
        val timestampTextView: TextView = view.findViewById(R.id.timestamp_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_nav_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyItems[position]
        holder.storeNameTextView.text = item.store_name
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
        holder.timestampTextView.text = sdf.format(item.timestamp.toDate())
    }

    override fun getItemCount() = historyItems.size
}
