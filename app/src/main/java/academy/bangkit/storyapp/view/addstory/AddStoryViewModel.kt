package academy.bangkit.storyapp.view.addstory

import academy.bangkit.storyapp.data.repository.UserRepository
import academy.bangkit.storyapp.data.response.FileUploadResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    fun uploadImage(file: File, description: String): LiveData<ResultState<FileUploadResponse>> {
        val token: String = repository.getToken()
        return repository.uploadImage(token, file, description)
    }
}