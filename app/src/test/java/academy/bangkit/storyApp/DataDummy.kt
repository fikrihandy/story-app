package academy.bangkit.storyApp

import academy.bangkit.storyApp.data.response.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..50) {
            val story = Story(
                i.toString(),
                "name + $i",
                "desc $i",
                "photo $i",
                "createAt $i",
                1.5 + i.toFloat(), 0.5 + i.toFloat()
            )
            items.add(story)
        }
        return items
    }
}