package academy.bangkit.storyApp.view.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import academy.bangkit.storyApp.data.repository.UserRepository
import academy.bangkit.storyApp.data.pref.UserModel
import academy.bangkit.storyApp.data.response.LoginResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResponseLiveData = MutableLiveData<LoginResponse>()
    val loginResponseLiveData: LiveData<LoginResponse> = _loginResponseLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        repository.postLogin(email, password) { isSuccess, loginResponse ->
            _isLoading.value = false
            if (isSuccess && loginResponse != null) {
                _loginResponseLiveData.value = loginResponse
            } else if (!isSuccess) {
                if (loginResponse != null) {
                    _loginResponseLiveData.value = loginResponse
                } else {
                    _loginResponseLiveData.value =
                        LoginResponse(null, true, "Check ur internet connection")
                }
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}