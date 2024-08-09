package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

@Dao // data access object
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // on conflict you update
    suspend fun upsert(article: Article)

    @Delete
    suspend fun delete(article: Article)

    //databases only saves primitive data types and not objects

    @Query("SELECT * FROM Article")
    fun getArticles(): Flow<List<Article>>

    @Query("SELECT * FROM Article WHERE url=:url")
    suspend fun getArticle(url: String): Article?

}