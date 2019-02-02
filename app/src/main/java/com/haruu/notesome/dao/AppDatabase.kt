package com.haruu.notesome.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound
import com.haruu.notesome.model.User

@Database(entities = [ShortText::class, Sound::class, User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var instanceCount = 0
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instanceCount++
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "some").build()
            }
            return instance
        }
    }

    abstract fun shortTextDao(): ShortTextDao

    abstract fun soundDao(): SoundDao

    abstract fun userDao(): UserDao
}