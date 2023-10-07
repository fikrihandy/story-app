package academy.bangkit.storyapp.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(token: String? = null): ApiService {
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = if (token != null) {
                    req.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    req.newBuilder().build()
                }
                chain.proceed(requestHeaders)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}