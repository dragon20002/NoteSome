package com.haruu.notesome.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.haruu.notesome.model.Some

@Database(entities = [Some::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val someDao: SomeDao
}
