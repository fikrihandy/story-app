package academy.bangkit.storyapp.data

import academy.bangkit.storyapp.data.response.RegisterResponse
import academy.bangkit.storyapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupRepository {

    fun postRegister(
        name: String,
        email: String,
        password: String,
        onRegisterResult: (Boolean, RegisterResponse?) -> Unit
    ) {
        val client = ApiConfig.getApiService().postRegister(name, email, password)
        client.enqueue(
            object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        onRegisterResult(true, responseBody)
                    } else {
                        val gson = Gson()
                        val errorResponseToRegisterResponse = gson.fromJson(
                            response.errorBody()?.string(),
                            RegisterResponse::class.java
                        )
                        onRegisterResult(false, errorResponseToRegisterResponse)
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    onRegisterResult(false, null)
                }
            }
        )
    }
}