package com.pampang.nav.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("timestamp")
fun setTimestamp(textView: TextView, date: Date?) {
    date?.let {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        textView.text = sdf.format(it)
    }
}