package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.utils.Evento
import com.example.zadanie.ui.viewmodels.data.NearbyBar
import com.example.zadanie.ui.widget.detailList.BarDetailItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DataRepository) : ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)
    val user = MutableLiveData<Int>(null)
    val bar = MutableLiveData<NearbyBar>(null)
    val type = bar.map { it?.tags?.getOrDefault("amenity", "") ?: "" }
    val details: LiveData<List<BarDetailItem>> = bar.switchMap {
        liveData {
            it?.let {
                emit(it.tags.map {
                    BarDetailItem(it.key, it.value)
                })
            } ?: emit(emptyList<BarDetailItem>())
        }
    }

    fun getUsers(id: String){
        if (id.isBlank())
            return
        viewModelScope.launch {
            loading.postValue(true)
            user.postValue(repository.dbUsersByBarId(id))
            loading.postValue(false)
        }
    }

    fun loadBar(id: String) {
        if (id.isBlank())
            return
        viewModelScope.launch {
            loading.postValue(true)
            bar.postValue(repository.apiBarDetail(id) { _message.postValue(Evento(it)) })
            loading.postValue(false)
        }
    }
}