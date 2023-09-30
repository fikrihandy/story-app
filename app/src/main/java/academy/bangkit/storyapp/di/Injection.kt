package academy.bangkit.storyapp.di

import academy.bangkit.storyapp.data.UserRepository
import academy.bangkit.storyapp.data.pref.UserPreference
import academy.bangkit.storyapp.data.pref.dataStore
import android.content.Context

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }


}