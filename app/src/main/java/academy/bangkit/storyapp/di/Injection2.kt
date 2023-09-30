package academy.bangkit.storyapp.di

import academy.bangkit.storyapp.data.pref.UserPreference
import academy.bangkit.storyapp.data.pref.dataStore
import academy.bangkit.storyapp.data.retrofit.ApiConfig
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection2 {
//    fun provideRepository(context: Context): StoryRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val user = runBlocking { pref.getSession().first() }
//        val apiService = ApiConfig.getApiService(user.token)
//        return StoryRepository.getInstance(apiService, pref)
//    }
}