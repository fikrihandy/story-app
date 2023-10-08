package academy.bangkit.storyapp.view.addstory

import academy.bangkit.storyapp.data.repository.UserRepository
import academy.bangkit.storyapp.data.response.FileUploadResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.io.File
import android.net.Uri
import androidx.lifecycle.MutableLiveData

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    fun uploadImage(file: File, description: String): LiveData<ResultState<FileUploadResponse>> {
        val token: String = repository.getToken()
        return repository.uploadImage(token, file, description)
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }
}