package com.haruu.notesome.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Some(
        @PrimaryKey(autoGenerate = false) //id 는 서버에서 만들어준 것을 사용한다.
        var id: Long? = null,
        @ColumnInfo
        val title: String,
        @ColumnInfo
        var content: String? = null
)