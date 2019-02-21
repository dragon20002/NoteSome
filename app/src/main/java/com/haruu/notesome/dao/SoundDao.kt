package com.haruu.notesome.dao

import android.arch.persistence.room.*
import com.haruu.notesome.model.Sound

@Dao
interface SoundDao {

    @Query("SELECT * FROM sound")
    fun get(): List<Sound>

//    @Query("SELECT * FROM some WHERE id IN (:idList)")
//    fun loadAllByIds(idList: List<Long>): List<Sound>
//
//    @Query("SELECT * FROM some WHERE title LIKE :title")
//    fun findByTitle(title: String): List<Sound>

    /**
     * @return list of inserted ids
     */
    @Insert
    fun insert(vararg shortText: Sound): List<Long> //ids of inserted items

//    /**
//     * The implementation of the method will update its parameters in the database
//     * if they already exists (checked by primary keys).
//     * If they don't already exists, this option will not change the database.
//     *
//     * @return the number of rows affected
//     */
//    @Update
//    fun update(shortText: Sound): Int

    /**
     * @return the number of rows affected
     */
    @Delete
    fun delete(vararg shortText: Sound): Int
}