package com.pampang.nav.screens.seller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pampang.nav.R
import com.pampang.nav.models.DropdownItem

class GroupedArrayAdapter(
    context: Context,
    private val resource: Int,
    private val items: List<DropdownItem>
) : ArrayAdapter<DropdownItem>(context, resource, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(resource, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        val item = items[position]

        when (item) {
            is DropdownItem.Header -> {
                textView.text = item.title
                textView.isEnabled = false
                textView.setBackgroundColor(context.getColor(R.color.colorPrimary))
                textView.setTextColor(context.getColor(R.color.white))
            }
            is DropdownItem.StoreItem -> {
                textView.text = item.displayName
                textView.isEnabled = true
                textView.setBackgroundColor(context.getColor(android.R.color.transparent))
                textView.setTextColor(context.getColor(android.R.color.black))
            }
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        val item = items[position]

        when (item) {
            is DropdownItem.Header -> {
                textView.text = item.title
                textView.isEnabled = false
                textView.setBackgroundColor(context.getColor(R.color.colorPrimary))
                textView.setTextColor(context.getColor(R.color.white))
            }
            is DropdownItem.StoreItem -> {
                textView.text = "    ${item.displayName}"
                textView.isEnabled = true
                textView.setBackgroundColor(context.getColor(android.R.color.transparent))
                textView.setTextColor(context.getColor(android.R.color.black))
            }
        }
        return view
    }

    override fun isEnabled(position: Int): Boolean {
        return items[position] is DropdownItem.StoreItem
    }
}