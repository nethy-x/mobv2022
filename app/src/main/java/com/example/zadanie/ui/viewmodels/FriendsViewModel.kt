package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.db.model.FriendItem
import com.example.zadanie.utils.Evento
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    private val _friends = MutableLiveData<List<FriendItem>?>()
    val friends: LiveData<List<FriendItem>?> get() = _friends

    init {
        viewModelScope.launch {
            loading.postValue(true)
            val friendsFromDb = repository.dbFriends()
            if (friendsFromDb == null || friendsFromDb.isEmpty()) {
                refreshData()
            } else {
                _friends.postValue(friendsFromDb)
            }
            loading.postValue(false)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiFriendsList { _message.postValue(Evento(it))  }
            val friendsFromDb = repository.dbFriends()
            _friends.postValue(friendsFromDb)
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}

}