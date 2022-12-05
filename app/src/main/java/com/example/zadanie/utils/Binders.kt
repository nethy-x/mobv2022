package com.example.zadanie.utils

import android.view.View
import android.widget.Toast
import androidx.databinding.BindingAdapter

@BindingAdapter(
    "showTextToast"
)
fun applyShowTextToast(
    view: View,
    message: Evento<String>?
) {
    message?.getContentIfNotHandled()?.let {
        Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
    }
}