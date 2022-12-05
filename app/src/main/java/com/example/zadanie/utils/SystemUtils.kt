package com.example.zadanie.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class SystemUtils {
    companion object {
        fun closeKeyboard(view: View) {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}