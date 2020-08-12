package com.akeedapp.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultModel(
    @JsonProperty("Search")
    var Search: ArrayList<ContentModel>? = ArrayList<ContentModel>(),
    @JsonProperty("totalResults")
    var totalResults: String? = "",
    @JsonProperty("Response")
    var Response: String? = "",
    @JsonProperty("Error")
    var Error: String? = ""
)