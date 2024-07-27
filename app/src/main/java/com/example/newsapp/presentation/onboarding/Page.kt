package com.example.newsapp.presentation.onboarding

import androidx.annotation.DrawableRes
import com.example.newsapp.R

data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int
)
val pages = listOf(
    Page(
        title = "Introduction",
        description = "Introduction page",
        image = R.drawable.onboarding1
    ),
    Page(
        title = "Introduction2",
        description = "Introduction page",
        image = R.drawable.onboarding2
    ),
    Page(
        title = "Introduction3",
        description = "Introduction page",
        image = R.drawable.onboarding3
    ),
)
