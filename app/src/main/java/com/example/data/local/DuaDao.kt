package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaDao {
    @Query("SELECT * FROM duas ORDER BY id ASC")
    fun getAllDuas(): Flow<List<DuaEntity>>

    @Query("""
        SELECT * FROM duas 
        WHERE title LIKE '%' || :query || '%' 
        OR translation LIKE '%' || :query || '%' 
        OR transliteration LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY id ASC
    """)
    fun searchDuas(query: String): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoriteDuas(): Flow<List<DuaEntity>>

    @Query("SELECT DISTINCT category FROM duas ORDER BY id ASC")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM duas WHERE category = :category ORDER BY id ASC")
    fun getDuasByCategory(category: String): Flow<List<DuaEntity>>

    @Query("UPDATE duas SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuas(duas: List<DuaEntity>)

    @Query("SELECT COUNT(*) FROM duas")
    suspend fun getDuaCount(): Int

    @Query("SELECT * FROM dua_translations WHERE duaId = :duaId AND language = :language LIMIT 1")
    suspend fun getTranslation(duaId: Int, language: String): DuaTranslationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: DuaTranslationEntity)
}
