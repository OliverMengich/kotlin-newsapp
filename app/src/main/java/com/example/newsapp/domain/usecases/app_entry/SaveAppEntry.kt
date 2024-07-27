package com.example.newsapp.domain.usecases.app_entry

import com.example.newsapp.domain.manager.LocalUserManager

class SaveAppEntry(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke(){
        localUserManager.saveAppEntry()
    }
}
//Dependency injection is a way to send dependencies or objects to a class