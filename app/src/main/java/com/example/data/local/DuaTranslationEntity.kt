package com.example.data.local

import androidx.room.Entity

@Entity(tableName = "dua_translations", primaryKeys = ["duaId", "language"])
data class DuaTranslationEntity(
    val duaId: Int,
    val language: String,
    val translation: String,
    val reference: String,
    val title: String = ""
)
