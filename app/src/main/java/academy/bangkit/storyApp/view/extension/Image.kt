package academy.bangkit.storyApp.view.extension

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var photo: String
) : Parcelable
