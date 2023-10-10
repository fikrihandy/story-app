package academy.bangkit.storyApp.view.listStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import academy.bangkit.storyApp.data.repository.UserRepository
import academy.bangkit.storyApp.data.pref.UserModel
import academy.bangkit.storyApp.data.response.GetAllStoriesResponse
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storiesResponse = MutableLiveData<GetAllStoriesResponse>()
    val storiesResponse: LiveData<GetAllStoriesResponse> = _storiesResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getAllStories() {
        _isLoading.value = true
        val token: String = repository.getToken()
        repository.getAllStories(token) { isSuccess, response ->
            _isLoading.value = false
            if (isSuccess && response != null) {
                _storiesResponse.value = response
            } else if (!isSuccess) {
                if (response != null) {
                    _storiesResponse.value = response
                } else {
                    _storiesResponse.value =
                        GetAllStoriesResponse(emptyList(), true, "No Internet")
                }
            }
        }
    }
}