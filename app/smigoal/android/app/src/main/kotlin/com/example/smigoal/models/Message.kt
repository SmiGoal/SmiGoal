package com.example.smigoal.models

import com.google.gson.annotations.SerializedName
import java.util.regex.Pattern

data class Message(
    @SerializedName("message") val message: String,
    @SerializedName("url") val url: String?
)

fun extractUrls(text: String): List<String> {
    val urls = mutableListOf<String>()
    val pattern = Pattern.compile(
        "(https?://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?)",
        Pattern.CASE_INSENSITIVE
    )
    val matcher = pattern.matcher(text)

    while (matcher.find()) {
        urls.add(matcher.group())
    }

    return urls
}