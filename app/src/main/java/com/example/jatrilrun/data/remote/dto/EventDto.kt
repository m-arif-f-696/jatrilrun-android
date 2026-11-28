package com.example.jatrilrun.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EventListResponse(@SerializedName("data") val data: List<EventDto> = emptyList())
data class EventDetailResponse(@SerializedName("data") val data: EventDto?)

data class EventDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("start_location") val startLocation: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("categories") val categories: List<CategoryDto>? = null
)

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("quota") val quota: Int? = null,
    @SerializedName("max_participant") val maxParticipant: Int? = null,
    @SerializedName("category_type") val type: CategoryTypeDto? = null
)

data class CategoryTypeDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("distance_km") val distanceKm: Double? = null
)
