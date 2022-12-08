package com.example.zadanie.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zadanie.data.db.model.BarItem

@Dao
interface BarsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBars(bars: List<BarItem>)

    @Query("DELETE FROM bars")
    suspend fun deleteBars()

    @Query("SELECT * FROM bars ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN id END ASC," +
            "CASE WHEN :isAsc = 0 THEN id END DESC" )
    suspend fun getBars(isAsc: Boolean): List<BarItem>?

    @Query("SELECT * FROM bars ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN  name END DESC," +
            "CASE WHEN :isAsc = 0 THEN name END ASC")
    suspend fun getBarsName(isAsc: Boolean): List<BarItem>?

    @Query("SELECT * FROM bars ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN  users END ASC," +
            "CASE WHEN :isAsc = 0 THEN users END DESC")
    suspend fun getBarsUsers(isAsc: Boolean): List<BarItem>?

    @Query("SELECT users FROM bars WHERE id= :id")
    suspend fun getUsersByBarId(id: String): Int


}