package com.pampang.nav.utilities.adapters

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView

class SimpleViewHolder(val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root), LifecycleOwner {

    private val mLifecycleRegistry = LifecycleRegistry(this)

    init {
        mLifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry

    fun markAttach() {
        mLifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    fun markDetach() {
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}