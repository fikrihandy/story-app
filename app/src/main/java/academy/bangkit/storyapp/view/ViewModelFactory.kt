package academy.bangkit.storyapp.view

import academy.bangkit.storyapp.data.repository.SignupRepository
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import academy.bangkit.storyapp.data.repository.UserRepository
import academy.bangkit.storyapp.di.Injection
import academy.bangkit.storyapp.view.addstory.AddStoryViewModel
import academy.bangkit.storyapp.view.authenticatiton.login.LoginViewModel
import academy.bangkit.storyapp.view.liststory.MainViewModel
import academy.bangkit.storyapp.view.authenticatiton.signup.SignupViewModel
import academy.bangkit.storyapp.view.storydetail.DetailStoryViewModel

class ViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository = SignupRepository()) as T
            }

            modelClass.isAssignableFrom(DetailStoryViewModel::class.java) -> {
                DetailStoryViewModel(repository) as T
            }
            
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}