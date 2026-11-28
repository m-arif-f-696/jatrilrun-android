package com.example.jatrilrun.data.remote

import com.example.jatrilrun.data.remote.dto.AuthResponse
import com.example.jatrilrun.data.remote.dto.EventListResponse
import com.example.jatrilrun.data.remote.dto.EmergencyContactRequest
import com.example.jatrilrun.data.remote.dto.ProfileRequest
import com.example.jatrilrun.data.remote.dto.RegistrationRequest
import com.example.jatrilrun.data.remote.dto.TransactionListResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendContractTest {
    private val gson = Gson()

    @Test
    fun `auth response reads Laravel data and UUID`() {
        val body = """{"data":{"id":"b07108e2-d583-4cb1-9d8b-d3435426c622","name":"Runner","email":"runner@example.test"},"message":"Login berhasil"}"""
        val response = gson.fromJson(body, AuthResponse::class.java)
        assertEquals("b07108e2-d583-4cb1-9d8b-d3435426c622", response.data?.id)
        assertEquals("Runner", response.data?.name)
    }

    @Test
    fun `empty event list from deployed API is represented as empty`() {
        val body = """{"data":[],"links":{"next":null},"meta":{"total":0},"message":"List event berhasil diambil"}"""
        val response = gson.fromJson(body, EventListResponse::class.java)
        assertTrue(response.data.isEmpty())
    }

    @Test
    fun `profile and registration requests use Laravel field names`() {
        val profile = ProfileRequest(
            "Runner", "081234567890", "male", "2000-01-01", "1234567890123456", "ktp", "O",
            listOf(EmergencyContactRequest("Kontak", "Saudara", "081299999999"))
        )
        val json = gson.toJson(profile)
        assertTrue(json.contains("\"date_of_birth\":\"2000-01-01\""))
        assertTrue(json.contains("\"emergency_contacts\""))
        assertTrue(json.contains("\"type_id_card\":\"ktp\""))

        val registration = gson.toJson(RegistrationRequest("event-uuid", "category-uuid"))
        assertTrue(registration.contains("\"event_id\":\"event-uuid\""))
        assertTrue(registration.contains("\"category_id\":\"category-uuid\""))
    }

    @Test
    fun `transaction list retains pending status and registration id`() {
        val body = """{"data":[{"id":"tx-uuid","registration_id":"reg-uuid","amount":455000,"payment_status":"PENDING"}]}"""
        val transaction = gson.fromJson(body, TransactionListResponse::class.java).data.single()
        assertEquals("reg-uuid", transaction.registrationId)
        assertEquals("PENDING", transaction.status)
    }
}
