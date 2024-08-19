package com.example.newsapp.presentation.news_navigator

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.bookmark.BookmarkScreen
import com.example.newsapp.presentation.bookmark.BookmarkViewmodel
import com.example.newsapp.presentation.details.DetailsEvent
import com.example.newsapp.presentation.details.DetailsScreen
import com.example.newsapp.presentation.details.DetailsViewModel
import com.example.newsapp.presentation.home.HomeScreen
import com.example.newsapp.presentation.home.HomeViewModel
import com.example.newsapp.presentation.navgraph.Route
import com.example.newsapp.presentation.news_navigator.components.BottomNavigationItem
import com.example.newsapp.presentation.news_navigator.components.NewsBottomNavigation
import com.example.newsapp.presentation.search.SearchScreen
import com.example.newsapp.presentation.search.SearchViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsNavigator(modifier: Modifier = Modifier) {
    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(icon = R.drawable.ic_home, text = "Home"),
            BottomNavigationItem(icon = R.drawable.ic_search, text = "Search"),
            BottomNavigationItem(icon = R.drawable.ic_bookmark, text = "Bookmark"),
        )
    }
    val navController = rememberNavController()
    val backstackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }
    selectedItem = remember(key1 = backstackState) {
        when(backstackState?.destination?.route){
            Route.HomeScreen.route ->0
            Route.SearchScreen.route ->1
            Route.BookmarkScreen.route ->2
            else -> 0
        }
    }


    val isBottomBarVisible = remember(key1 = backstackState){
        //visible when we are in the home/search/bookmark screen
        backstackState?.destination?.route == Route.HomeScreen.route ||
                backstackState?.destination?.route == Route.SearchScreen.route ||
                        backstackState?.destination?.route == Route.BookmarkScreen.route
    }

    Scaffold(
        modifier=modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible){
                NewsBottomNavigation(
                    items = bottomNavigationItems,
                    selected = selectedItem
                ) { index ->
                    when(index){
                        0 -> navigateToTab(navController,Route.HomeScreen.route)
                        1 -> navigateToTab(navController,Route.SearchScreen.route)
                        2 -> navigateToTab(navController,Route.BookmarkScreen.route)
                    }
                }
            }

        }
    ) {
        val bottomPadding = it.calculateBottomPadding()
        NavHost(
            modifier = Modifier.padding(bottom=bottomPadding), //padding will be height of news bottom navigation
            navController = navController,
            startDestination = Route.HomeScreen.route,
        ){
            composable(route = Route.HomeScreen.route){
                val viewModel: HomeViewModel = hiltViewModel()
                val articles = viewModel.news.collectAsLazyPagingItems()
                val state by viewModel.state
                HomeScreen(
                    articles = articles,
                    navigateToSearch = {
                        navigateToTab(navController, Route.SearchScreen.route)
                    },
                    navigateToDetails = { article->
                        navigateToDetails(
                            navController = navController,
                            article = article
                        )
                    },
                    event = { event ->
                        viewModel.onEvent(event)
                    },
                    state = state
                )
            }
            composable(route = Route.SearchScreen.route){
                val viewModel: SearchViewModel = hiltViewModel()
                val state = viewModel.state.value

                SearchScreen(
                    state = state,
                    event = viewModel::onEvent,
                    navigateToDetails = { art->
                        navigateToDetails(navController = navController, article = art)
                    }
                )
            }
            composable(route = Route.DetailsScreen.route){
                val viewModel: DetailsViewModel = hiltViewModel()
                if (viewModel.sideEffect !==null){
                    //1. show toast message
                    Toast.makeText(LocalContext.current, viewModel.sideEffect, Toast.LENGTH_SHORT).show() //show toast message
                    //2. remove the side event
                    viewModel.onEvent(DetailsEvent.RemoveSideEvent)
                }
                navController.previousBackStackEntry?.savedStateHandle?.get<Article?>("article")?.let { artc ->
                    DetailsScreen(
                        article = artc,
                        event = viewModel::onEvent,
                        navigateUp = {
                            navController.navigateUp()
                        }
                    )
                }
            }
            composable(route = Route.BookmarkScreen.route){
                val viewModel: BookmarkViewmodel = hiltViewModel()
                val state = viewModel.state.value
                BookmarkScreen(state = state) { art ->
                    navigateToDetails(navController=navController, article = art)
                }
            }
        }
    }
}
private fun navigateToTab(navController: NavController,route: String){
    navController.navigate(route){ //after navigating, pop the backstack
        navController.graph.startDestinationRoute?.let { homeScreen ->
            popUpTo(homeScreen){
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}
private fun navigateToDetails(navController: NavController,article: Article){
    navController.currentBackStackEntry?.savedStateHandle?.set("article",article)
    navController.navigate(
        route = Route.DetailsScreen.route,
    )
}