package com.haruu.notesome.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Sound(@PrimaryKey val title: String)