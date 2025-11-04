package com.pampang.nav.utilities.extension

import androidx.fragment.app.Fragment

fun Fragment.showToast(message: String, length: Int) = context!!.showToast(message, length)
fun Fragment.showToast(message: String) = context!!.showToast(message)
fun Fragment.showToast(message: Int) = context!!.showToast(message)
fun Fragment.showToastLong(message: String) = context!!.showToastLong(message)