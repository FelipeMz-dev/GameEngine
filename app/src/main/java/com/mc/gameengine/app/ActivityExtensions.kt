package com.mc.gameengine.app

import android.R
import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val view = currentFocus ?: findViewById(R.id.content)
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showKeyboard() {
    val view = currentFocus ?: findViewById(R.id.content)
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

fun Activity.isKeyboardOpen(): Boolean {
    val view = currentFocus ?: findViewById(R.id.content)
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm.isActive && view.hasFocus()
}