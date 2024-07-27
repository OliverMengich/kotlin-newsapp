package com.example.newsapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.domain.model.Article

class NewsPagingSource(
    private val newsApi: NewsApi,
    private val sources: String,
): PagingSource<Int,Article>() {
    private var totalNewsCount = 0;
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params?.key?: 1
        return  try {
            val newsResponse = newsApi.getNews(sources=sources,page = page)
            totalNewsCount +=newsResponse.articles.size
            val articles = newsResponse.articles.distinctBy { //filters our articles by removing duplicates
                it.title
            }
            LoadResult.Page(
                data = articles,
                nextKey = if(totalNewsCount===newsResponse.totalResults) null else page+1,
                prevKey = null
            )
        }catch (e: Exception){
            e.printStackTrace()
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        //state has some information about our paging.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            // if it's not null we get closest page to that position
            anchorPage?.prevKey?.plus(1)?: anchorPage?.nextKey?.minus(1)
        }
    }
}