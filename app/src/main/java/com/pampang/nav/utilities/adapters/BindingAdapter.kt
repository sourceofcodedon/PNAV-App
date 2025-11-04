package com.pampang.nav.utilities.adapters

import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pampang.nav.utilities.extension.gone
import com.pampang.nav.utilities.extension.invisible
import com.pampang.nav.utilities.extension.visible
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("list_data")
fun bindListToRecyclerView(recyclerView: RecyclerView, data: List<Any>?) {
    if (data != null) {
        val adapter = recyclerView.adapter as SimpleDiffUtilAdapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter.submitList(data)
    }
}


@BindingAdapter("imageSrc")
fun setImageResource(imageView: ImageView, @DrawableRes resId: Int?) {
    resId?.let {
        imageView.setImageResource(it)
    }
}

@BindingAdapter("set_refresh")
fun setRefreshing(swipeRefreshLayout: SwipeRefreshLayout, isRefreshing: Boolean) {
    swipeRefreshLayout.isRefreshing = isRefreshing
}

@BindingAdapter("set_loading_visibility")
fun setLoadingVisibility(view: View, isLoading: Boolean) {
    if (isLoading) {
        view.invisible()
    } else {
        view.visible()
    }
}

@BindingAdapter("set_progress_loading_visibility")
fun setProgressLoadingVisibility(view: View, isLoading: Boolean) {
    if (isLoading) {
        view.visible()
    } else {
        view.gone()
    }
}

@BindingAdapter("set_error_text")
fun setErrorText(textInputLayout: TextInputLayout, errorText: String?) {
    textInputLayout.error = errorText

    if (textInputLayout.tag == "password") {
        if (errorText == null) {
            textInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            textInputLayout.errorIconDrawable = null
        }
    }
}

@BindingAdapter("set_error_enabled")
fun setErrorEnabled(textInputLayout: TextInputLayout, errorText: String? = null) {
    textInputLayout.error = null
    textInputLayout.isErrorEnabled = false
}

@BindingAdapter("applyStatusBarInsets")
fun View.applyStatusBarInsets(window: Window?) {
    window?.let {
        // Apply padding for status bar
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        // Set status bar text/icons to black
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
    }
}






