package com.example.zadanie.ui.widget.friendsList

import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.data.db.model.FriendItem

interface FriendsEvents {
    fun onFriendClick(friend: FriendItem)
}