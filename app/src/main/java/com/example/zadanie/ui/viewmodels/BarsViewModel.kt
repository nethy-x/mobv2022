package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.utils.Evento
import kotlinx.coroutines.launch

enum class SortBy {
    DEFAULT, NAME, USERS
}

class BarsViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    private val _sortBy = MutableLiveData(SortBy.DEFAULT)
    val sortBy: LiveData<SortBy> get() = _sortBy

    var isAsc = false

    val loading = MutableLiveData(false)

    private val _bars = MutableLiveData<List<BarItem>?>()
    val bars: LiveData<List<BarItem>?> get() = _bars

    init {
        viewModelScope.launch {
            val barsFromDb = repository.dbBars(isAsc, SortBy.DEFAULT)
            if (barsFromDb == null || barsFromDb.isEmpty()) {
                refreshData()
            } else {
                _bars.postValue(barsFromDb)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiBarList { _message.postValue(Evento(it))  }
            val pubsFromDb = repository.dbBars(isAsc, sortBy.value)
            _bars.postValue(pubsFromDb)
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}

    fun setSortBy(newSortBy: SortBy) {
        isAsc = if (_sortBy.value == newSortBy) {
            !isAsc
        } else {
            false
        }
        _sortBy.postValue(newSortBy)
        viewModelScope.launch {
            loading.postValue(true)
            val barsFromDb = repository.dbBars(isAsc, newSortBy)
            _bars.postValue(barsFromDb)
            loading.postValue(false)
        }
    }
}