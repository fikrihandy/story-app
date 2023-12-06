package academy.bangkit.storyApp.view.addStory

import academy.bangkit.storyApp.data.repository.UserRepository
import academy.bangkit.storyApp.data.response.FileUploadResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.io.File
import android.net.Uri

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private var imageUri: Uri? = null
    private var includeLocation: Boolean = false
    private var userLat: Float? = null
    private var userLon: Float? = null

    fun uploadImage(
        file: File,
        description: String
    ): LiveData<ResultState<FileUploadResponse>> {
        val token: String = repository.getToken()
        return repository.uploadImage(token, file, description, userLat, userLon)
    }

    fun setImageUri(uri: Uri?) {
        imageUri = uri
    }

    fun getIsIncludeLoc(): Boolean = includeLocation

    fun setIsIncludeLoc(isHaveLocation: Boolean) {
        includeLocation = isHaveLocation
    }

    fun getImageUri(): Uri? = imageUri

    fun setUserLatLon(lat: Float?, lon: Float?) {
        userLat = lat
        userLon = lon
    }
}