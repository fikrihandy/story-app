package academy.bangkit.storyapp.view.liststory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import academy.bangkit.storyapp.data.UserRepository
import academy.bangkit.storyapp.data.pref.UserModel
import academy.bangkit.storyapp.data.response.GetAllStoriesResponse
import academy.bangkit.storyapp.data.response.RegisterResponse
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private lateinit var token: String

    private val _storiesResponse = MutableLiveData<GetAllStoriesResponse>()
    val storiesResponse: LiveData<GetAllStoriesResponse> = _storiesResponse

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getAllStories() {
        viewModelScope.launch {
            val session = repository.getSession()
            session.collect {
                token = it.token
            }
        }

        repository.getAllStories(token) { isSuccess, response ->
            if (isSuccess && response != null) {
                _storiesResponse.value = response
            } else if (!isSuccess) {
                if (response != null) {
                    _storiesResponse.value = response
                } else {
                    _storiesResponse.value = GetAllStoriesResponse(emptyList(), true, "No Internet")
                }
            }
        }
    }

}