package com.haruu.notesome.dao

import android.arch.persistence.room.*
import com.haruu.notesome.model.ShortText

@Dao
interface ShortTextDao {

    @Query("SELECT * FROM shorttext")
    fun getAll(): List<ShortText>

//    @Query("SELECT * FROM some WHERE id IN (:idList)")
//    fun loadAllByIds(idList: List<Long>): List<ShortText>
//
//    @Query("SELECT * FROM some WHERE title LIKE :title")
//    fun findByTitle(title: String): List<ShortText>

    /**
     * @return list of inserted ids
     */
    @Insert
    fun insertAll(vararg shortText: ShortText): List<Long> //ids of inserted items

    /**
     * The implementation of the method will update its parameters in the database
     * if they already exists (checked by primary keys).
     * If they don't already exists, this option will not change the database.
     *
     * @return the number of rows affected
     */
    @Update
    fun update(shortText: ShortText): Int

    /**
     * @return the number of rows affected
     */
    @Delete
    fun delete(shortText: ShortText): Int

}