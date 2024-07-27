package com.example.newsapp.domain.manager

import kotlinx.coroutines.flow.Flow
import kotlin.Boolean

interface LocalUserManager {
    suspend fun saveAppEntry()
    fun readAppEntry(): Flow<Boolean>
}