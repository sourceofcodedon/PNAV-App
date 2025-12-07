package com.pampang.nav.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pampang.nav.R
import com.pampang.nav.models.Announcement
import com.pampang.nav.utilities.adapters.AnnouncementsAdapter

class AnnouncementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: AnnouncementsAdapter
    private val announcements = mutableListOf<Announcement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcements)

        recyclerView = findViewById(R.id.announcementsRecyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        adapter = AnnouncementsAdapter(announcements)
        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            fetchAnnouncements()
        }

        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        swipeRefreshLayout.isRefreshing = true
        val db = FirebaseFirestore.getInstance()
        db.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                announcements.clear()
                for (document in result) {
                    val announcement = document.toObject(Announcement::class.java)
                    announcements.add(announcement)
                }
                adapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                // Handle the error
                swipeRefreshLayout.isRefreshing = false
            }
    }
}