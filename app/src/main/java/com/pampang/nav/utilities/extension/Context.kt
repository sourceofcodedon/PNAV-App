package com.pampang.nav.utilities.extension

import android.content.Context
import android.widget.Toast

/**
 * Toast an error that response from the safe api call
 */
//fun Context.showToastError(message: NetworkErrorModel, showDebugToastError: Boolean = false) {
//    if (showDebugToastError) {
////        if (BuildConfig.DEBUG) {
//        if (message.code != null) {
//            showToastLong(
//                "Error ${message.code}: ${message.message}${
//                    if (!message.endPoint.isNullOrBlank()) "\n${message.getEndPointOnly()}" else ""
//                }"
//            )
//        } else {
//            showToastLong(message.message)
////            }
//        }
//    }
//}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToastUnderDevelopment() {
    Toast.makeText(this, "Under development", Toast.LENGTH_SHORT).show()
}

fun Context.showToast(message: String, length: Int) {
    Toast.makeText(this, message, length).show()
}

fun Context.showToastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//fun Context.hasInternet(): Boolean {
//    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    val nc = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//    return if (nc == null) {
//        false
//    } else {
//        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
//                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
//    }
//}
//
//fun Context.hasInternet(notifyNoInternet: Boolean = true, trueFunc: (internet: Boolean) -> Unit) {
//    if (hasInternet()) {
//        trueFunc(true)
//    } else if (notifyNoInternet) {
//        showToast(getString(R.string.check_internet_connection))
//    }
//}
//
//fun Context.hasInternet(
//    trueFunc: (internet: Boolean) -> Unit,
//    falseFunc: (internet: Boolean) -> Unit
//) {
//    if (hasInternet()) {
//        trueFunc(true)
//    } else {
//        falseFunc(true)
//    }
//}