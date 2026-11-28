package com.example.jatrilrun.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("id_card") val idCard: String,
    @SerializedName("type_id_card") val typeIdCard: String,
    @SerializedName("blood_type") val bloodType: String,
    @SerializedName("emergency_contacts") val emergencyContacts: List<EmergencyContactRequest>
)

data class EmergencyContactRequest(
    @SerializedName("contact_name") val name: String,
    @SerializedName("relationship") val relationship: String,
    @SerializedName("phone_number") val phoneNumber: String
)

data class RegistrationRequest(
    @SerializedName("event_id") val eventId: String,
    @SerializedName("category_id") val categoryId: String
)

data class RegistrationResponse(@SerializedName("data") val data: RegistrationDto?)
data class RegistrationDto(
    @SerializedName("id") val id: String,
    @SerializedName("event_id") val eventId: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("slot_reserved_until") val slotReservedUntil: String? = null,
    @SerializedName("bib_number") val bibNumber: String? = null,
    @SerializedName("event") val event: EventDto? = null,
    @SerializedName("category") val category: CategoryDto? = null
)

data class RegistrationListResponse(@SerializedName("data") val data: List<RegistrationDto> = emptyList())
data class TicketResponse(@SerializedName("data") val data: TicketDto?)
data class TicketDto(
    @SerializedName("registration_id") val registrationId: String,
    @SerializedName("bib_number") val bibNumber: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("runner_name") val runnerName: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("event_name") val eventName: String? = null
)
