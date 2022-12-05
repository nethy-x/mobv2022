package com.example.zadanie.utils

import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.zadanie.R
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.ui.viewmodels.data.NearbyBar
import java.util.*

fun <T> RecyclerView.Adapter<*>.autoNotify(
    oldList: List<T>,
    newList: List<T>,
    compare: (T, T) -> Boolean
) {
    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

            return compare(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return Objects.equals(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
    })
    diff.dispatchUpdatesTo(this)
}
object UIBehavior {
    fun iconByType(item: NearbyBar, icon: ImageView) {
        if (item.type == "fast_food") {
            icon.setImageResource(R.drawable.fast_food)
        } else if (item.type == "restaurant") {
            icon.setImageResource(R.drawable.restaurant)
        } else if (item.type == "cafe") {
            icon.setImageResource(R.drawable.cafe)
        } else if (item.type == "pub") {
            icon.setImageResource(R.drawable.bar)
        } else {
            icon.setImageResource(R.drawable.default_type)
        }
    }

    fun iconByType(item: BarItem, icon: ImageView) {
        if (item.type == "fast_food") {
            icon.setImageResource(R.drawable.fast_food)
        } else if (item.type == "restaurant") {
            icon.setImageResource(R.drawable.restaurant)
        } else if (item.type == "cafe") {
            icon.setImageResource(R.drawable.cafe)
        } else if (item.type == "pub") {
            icon.setImageResource(R.drawable.bar)
        } else {
            icon.setImageResource(R.drawable.default_type)
        }
    }
}