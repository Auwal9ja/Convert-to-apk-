package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duas")
data class DuaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val title: String,
    val arabic: String,
    val translation: String,
    val transliteration: String,
    val reference: String,
    val translationHausa: String = "",
    val translationYoruba: String = "",
    val translationIgbo: String = "",
    val isFavorite: Boolean = false
)
