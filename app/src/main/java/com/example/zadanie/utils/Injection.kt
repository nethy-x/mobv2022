package com.example.zadanie.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.api.RestApi
import com.example.zadanie.data.db.AppRoomDatabase
import com.example.zadanie.data.db.dao.BarsDao
import com.example.zadanie.data.db.dao.FriendsDao

object Injection {
    private fun provideBarsCache(context: Context): BarsDao {
        val database = AppRoomDatabase.getInstance(context)
        return database.barsDao()
    }

    private fun provideFriendsCache(context: Context): FriendsDao {
        val database = AppRoomDatabase.getInstance(context)
        return database.friendsDao()
    }

    fun provideDataRepository(context: Context): DataRepository {
        return DataRepository.getInstance(RestApi.create(context), provideBarsCache(context), provideFriendsCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(
                context
            )
        )
    }
}