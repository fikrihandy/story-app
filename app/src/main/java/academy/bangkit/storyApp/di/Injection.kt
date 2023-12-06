package academy.bangkit.storyApp.di

import academy.bangkit.storyApp.data.repository.UserRepository
import academy.bangkit.storyApp.data.pref.UserPreference
import academy.bangkit.storyApp.data.pref.dataStore
import android.content.Context

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}