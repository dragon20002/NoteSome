package com.haruu.notesome.dao

import android.arch.persistence.room.*
import com.haruu.notesome.model.Some

@Dao
interface SomeDao {

    @Query("SELECT * FROM some")
    fun getAll()

    @Insert
    fun insert(some: Some): Long

    @Update
    fun update(some: Some): Int

    @Delete
    fun delete(some: Some)

}