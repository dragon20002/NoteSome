package com.haruu.notesome.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class User(@PrimaryKey val name: String)