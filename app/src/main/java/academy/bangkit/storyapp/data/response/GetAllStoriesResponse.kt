package academy.bangkit.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class GetAllStoriesResponse(

    @field:SerializedName("listStory")
    val listStory: List<Story> = emptyList(),

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
