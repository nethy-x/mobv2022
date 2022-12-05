package com.example.zadanie.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zadanie.data.db.dao.BarsDao
import com.example.zadanie.data.db.dao.FriendsDao
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.data.db.model.FriendItem

@Database(entities = [BarItem::class, FriendItem::class], version = 2, exportSchema = false)
abstract class AppRoomDatabase: RoomDatabase() {
    abstract fun barsDao(): BarsDao
    abstract fun friendsDao(): FriendsDao

    companion object{
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {  INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppRoomDatabase::class.java, "localDatabase"
            ).fallbackToDestructiveMigration()
                .build()
    }
}