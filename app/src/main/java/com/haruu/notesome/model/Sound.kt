package com.haruu.notesome.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.databinding.BaseObservable
import android.databinding.Bindable

@Entity
class Sound(@PrimaryKey @Bindable val title: String) : BaseObservable()