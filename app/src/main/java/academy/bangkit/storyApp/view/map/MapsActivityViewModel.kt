package academy.bangkit.storyApp.view.map

import academy.bangkit.storyApp.data.repository.UserRepository
import academy.bangkit.storyApp.data.response.GetAllStoriesResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MapsActivityViewModel(private val repository: UserRepository) : ViewModel() {

    val idsMap: MutableMap<String, String> = mutableMapOf()

    private val _storiesResponse = MutableLiveData<GetAllStoriesResponse>()
    val storiesResponse: LiveData<GetAllStoriesResponse> = _storiesResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStories() {
        _isLoading.value = true
        val token: String = repository.getToken()
        repository.getAllStoriesWithLocation(token) { isSuccess, response ->
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

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}