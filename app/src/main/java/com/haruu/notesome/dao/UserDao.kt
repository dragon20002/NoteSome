package com.haruu.notesome.dao

import android.arch.persistence.room.*
import com.haruu.notesome.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

//    @Query("SELECT * FROM some WHERE id IN (:idList)")
//    fun loadAllByIds(idList: List<Long>): List<User>
//
//    @Query("SELECT * FROM some WHERE title LIKE :title")
//    fun findByTitle(title: String): List<User>

    /**
     * @return list of inserted ids
     */
    @Insert
    fun insertAll(vararg shortText: User): List<Long> //ids of inserted items

    /**
     * The implementation of the method will update its parameters in the database
     * if they already exists (checked by primary keys).
     * If they don't already exists, this option will not change the database.
     *
     * @return the number of rows affected
     */
    @Update
    fun update(shortText: User): Int

    /**
     * @return the number of rows affected
     */
    @Delete
    fun delete(shortText: User): Int
}