package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.utils.Evento
import kotlinx.coroutines.launch

class AddFriendsViewModel(private val repository: DataRepository) : ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message


    val loading = MutableLiveData(false)

    fun addFriend(contact: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiAddFriend(
                contact,
                { _message.postValue(Evento(it)) },
                { _message.postValue(Evento(it)) }
            )
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}