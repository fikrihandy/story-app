package academy.bangkit.storyApp.view.authentication.signup

import academy.bangkit.storyApp.data.repository.SignupRepository
import academy.bangkit.storyApp.data.response.RegisterResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignupViewModel(private val repository: SignupRepository) : ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean> = _isSuccessful

    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        repository.postRegister(name, email, password) { isSuccess, response ->
            _isLoading.value = false
            if (isSuccess && response != null) {
                _isSuccessful.value = true
                _registerResponse.value = response
            } else if (!isSuccess) {
                if (response != null) {
                    _isSuccessful.value = false
                    _registerResponse.value = response
                } else {
                    _isSuccessful.value = false
                    _registerResponse.value = RegisterResponse(true, "Check ur internet!")
                }
            }
        }
    }
}