package academy.bangkit.storyApp.data.repository

import academy.bangkit.storyApp.view.addStory.ResultState
import academy.bangkit.storyApp.data.pref.UserModel
import academy.bangkit.storyApp.data.pref.UserPreference
import academy.bangkit.storyApp.data.response.DetailStoryResponse
import academy.bangkit.storyApp.data.response.FileUploadResponse
import academy.bangkit.storyApp.data.response.GetAllStoriesResponse
import academy.bangkit.storyApp.data.response.LoginResponse
import academy.bangkit.storyApp.data.retrofit.ApiConfig
import androidx.lifecycle.liveData
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun getToken(): String {
        // You would typically handle blocking or asynchronous calls here
        return runBlocking {
            userPreference.getToken().first()
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }


    fun postLogin(
        email: String,
        password: String,
        onLoginResult: (Boolean, LoginResponse?) -> Unit
    ) {
        val client = ApiConfig.getApiService().postLogin(email, password)
        client.enqueue(
            object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        onLoginResult(true, responseBody)
                    } else {
                        val gson = Gson()
                        val errorResponseToLoginResponse = gson.fromJson(
                            response.errorBody()?.string(),
                            LoginResponse::class.java
                        )
                        onLoginResult(false, errorResponseToLoginResponse)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    onLoginResult(false, null)
                }
            }
        )
    }

    fun getAllStories(
        token: String,
        onFetchResult: (Boolean, GetAllStoriesResponse?) -> Unit
    ) {
        val client = ApiConfig.getApiService(token).getAllStories()
        client.enqueue(
            object : Callback<GetAllStoriesResponse> {
                override fun onResponse(
                    call: Call<GetAllStoriesResponse>,
                    response: Response<GetAllStoriesResponse>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        onFetchResult(true, responseBody)
                    } else {
                        val gson = Gson()
                        val errorResponseToFetchResponse = gson.fromJson(
                            response.errorBody()?.string(),
                            GetAllStoriesResponse::class.java
                        )
                        onFetchResult(false, errorResponseToFetchResponse)
                    }
                }

                override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                    onFetchResult(false, null)
                }
            }
        )
    }

    fun getDetailStory(
        token: String,
        id: String,
        result: (Boolean, DetailStoryResponse?) -> Unit
    ) {
        val client = ApiConfig.getApiService(token).getDetailStory(id)
        client.enqueue(
            object : Callback<DetailStoryResponse> {
                override fun onResponse(
                    call: Call<DetailStoryResponse>,
                    response: Response<DetailStoryResponse>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        result(true, responseBody)
                    } else {
                        val gson = Gson()
                        val errorResponseToDetailStoryResponse = gson.fromJson(
                            response.errorBody()?.string(),
                            DetailStoryResponse::class.java
                        )
                        result(false, errorResponseToDetailStoryResponse)
                    }
                }

                override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                    result(false, null)
                }
            }
        )
    }

    fun uploadImage(token: String, imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse =
                ApiConfig.getApiService(token).uploadImage(multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }

    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}