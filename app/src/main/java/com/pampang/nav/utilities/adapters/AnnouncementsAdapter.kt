package com.pampang.nav.utilities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pampang.nav.R
import com.pampang.nav.models.Announcement
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementsAdapter(private var announcements: MutableList<Announcement>) :
    RecyclerView.Adapter<AnnouncementsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.announcementTitleTextView)
        val messageTextView: TextView = view.findViewById(R.id.announcementMessageTextView)
        val timestampTextView: TextView = view.findViewById(R.id.announcementTimestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.titleTextView.text = announcement.title
        holder.messageTextView.text = announcement.message

        val sdf = SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault())
        holder.timestampTextView.text = sdf.format(Date(announcement.timestamp))
    }

    override fun getItemCount() = announcements.size

    fun updateAnnouncements(newAnnouncements: List<Announcement>) {
        announcements.clear()
        announcements.addAll(newAnnouncements)
        notifyDataSetChanged()
    }
}
