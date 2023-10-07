package academy.bangkit.storyapp.data.retrofit

import academy.bangkit.storyapp.data.response.DetailStoryResponse
import academy.bangkit.storyapp.data.response.FileUploadResponse
import academy.bangkit.storyapp.data.response.LoginResponse
import academy.bangkit.storyapp.data.response.RegisterResponse
import academy.bangkit.storyapp.data.response.GetAllStoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getAllStories(): Call<GetAllStoriesResponse>

    @GET("stories/{id}")
    fun getDetailStory(@Path("id") id: String): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): FileUploadResponse
}