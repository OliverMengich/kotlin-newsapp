package com.example.newsapp.presentation.onboarding

sealed class OnBoardingEvent { // this class contains events sent from UI to the view model

    object SaveAppEntry: OnBoardingEvent()
}