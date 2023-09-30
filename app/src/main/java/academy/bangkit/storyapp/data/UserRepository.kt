package academy.bangkit.storyapp.data

import academy.bangkit.storyapp.data.pref.UserModel
import academy.bangkit.storyapp.data.pref.UserPreference
import academy.bangkit.storyapp.data.response.GetAllStoriesResponse
import academy.bangkit.storyapp.data.response.LoginResponse
import academy.bangkit.storyapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val userPreference: UserPreference
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
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