package academy.bangkit.storyapp.view.storydetail

import academy.bangkit.storyapp.data.repository.UserRepository
import academy.bangkit.storyapp.data.response.DetailStoryResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailStoryViewModel(private val repository: UserRepository) : ViewModel() {


    private val _detailStoryResponse = MutableLiveData<DetailStoryResponse>()
    val detailStoryResponse: LiveData<DetailStoryResponse> = _detailStoryResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetailStory(id: String) {
        _isLoading.value = true
        val token: String = repository.getToken()
        repository.getDetailStory(token, id) { isSuccess, response ->
            _isLoading.value = false
            if (isSuccess && response != null) {
                _detailStoryResponse.value = response
            } else if (!isSuccess) {
                if (response != null) {
                    _detailStoryResponse.value = response
                } else {
                    _detailStoryResponse.value =
                        DetailStoryResponse(true, "No Internet", null)
                }
            }
        }
    }
}