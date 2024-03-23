package com.example.smigoal.models


import com.google.gson.annotations.SerializedName

data class APIResponseDTO(
    @SerializedName("matches")
    val matches: List<Matche>
) {
    data class Matche(
        @SerializedName("cacheDuration")
        val cacheDuration: String, // 300.000s
        @SerializedName("platformType")
        val platformType: String, // WINDOWS
        @SerializedName("threat")
        val threat: Threat,
        @SerializedName("threatEntryMetadata")
        val threatEntryMetadata: ThreatEntryMetadata,
        @SerializedName("threatEntryType")
        val threatEntryType: String, // URL
        @SerializedName("threatType")
        val threatType: String // MALWARE
    ) {
        data class Threat(
            @SerializedName("url")
            val url: String // http://www.urltocheck1.org/
        )

        data class ThreatEntryMetadata(
            @SerializedName("entries")
            val entries: List<Entry>
        ) {
            data class Entry(
                @SerializedName("key")
                val key: String, // malware_threat_type
                @SerializedName("value")
                val value: String // landing
            )
        }
    }
}