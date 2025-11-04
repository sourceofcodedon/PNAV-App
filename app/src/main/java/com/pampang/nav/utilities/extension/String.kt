package com.pampang.nav.utilities.extension


fun String.isEmailValid(): Boolean {
    return "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex().matches(this)
}

//fun isNotEmpty(textInputEditText: TextInputEditText, showError: Boolean = true): Boolean {
//    if (textInputEditText.text.toString().matches("".toRegex())) {
//        if (showError) {
//            textInputEditText.error = "Required field"
//            shakeView(textInputEditText, 20, 5)
//            textInputEditText.requestFocus()
//        }
//        return false
//    }
//    return true
//}