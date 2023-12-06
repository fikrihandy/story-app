package academy.bangkit.storyApp.view.listStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import academy.bangkit.storyApp.data.repository.UserRepository
import academy.bangkit.storyApp.data.pref.UserModel
import academy.bangkit.storyApp.data.response.Story
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<Story>> =
        repository.getAllStories(token).cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getToken(): String {
        return repository.getToken()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}