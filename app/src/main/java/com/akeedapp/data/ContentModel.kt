package com.akeedapp.data

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
data class ContentModel(
    @JsonProperty("Title")
    var Title: String? = "",
    @JsonProperty("Year")
    var Year: String? = "",
    @JsonProperty("imdbID")
    var imdbID: String? = "",
    @JsonProperty("Type")
    var Type: String? = "",
    @JsonProperty("Poster")
    var Poster: String? = ""
) : Parcelable