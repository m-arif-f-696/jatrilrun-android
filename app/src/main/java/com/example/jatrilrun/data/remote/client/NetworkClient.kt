package com.example.jatrilrun.data.remote.client

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

object NetworkClient {
    fun createCookieJar(context: Context): CookieJar {
        return PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
    }

    fun createOkHttpClient(cookieJar: CookieJar, origin: String): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    // Sanctum mengaktifkan session middleware untuk origin frontend ini.
                    .header("Origin", origin)
                    .header("Referer", "$origin/")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor { chain ->
                val request = chain.request()
                if (request.url.encodedPath.startsWith("/api/") &&
                    request.method in setOf("POST", "PUT", "PATCH", "DELETE")
                ) {
                    val token = csrfToken(cookieJar, request) ?: refreshCsrf(chain, cookieJar, request)
                    val response = chain.proceed(request.withCsrf(token))
                    if (response.code == 419 && request.body?.isOneShot() != true &&
                        request.body?.isDuplex() != true
                    ) {
                        response.close()
                        chain.proceed(request.withCsrf(refreshCsrf(chain, cookieJar, request)))
                    } else response
                } else chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun csrfToken(cookieJar: CookieJar, request: Request): String? =
        cookieJar.loadForRequest(request.url)
            .firstOrNull { it.name == "XSRF-TOKEN" }
            ?.let { URLDecoder.decode(it.value, "UTF-8") }
            ?.takeIf { it.isNotBlank() }

    private fun refreshCsrf(chain: Interceptor.Chain, cookieJar: CookieJar, request: Request): String {
        val url = request.url.resolve("/sanctum/csrf-cookie") ?: throw IOException("URL verifikasi sesi tidak valid.")
        val bootstrap = Request.Builder().url(url)
            .header("Accept", "application/json")
            .header("Origin", request.header("Origin").orEmpty())
            .header("Referer", request.header("Referer").orEmpty())
            .build()
        chain.proceed(bootstrap).use { response: Response ->
            if (!response.isSuccessful) throw IOException("Sesi keamanan gagal disiapkan (${response.code}).")
        }
        return csrfToken(cookieJar, request) ?: throw IOException("Server tidak mengirim token keamanan sesi.")
    }

    private fun Request.withCsrf(token: String): Request =
        newBuilder().header("X-XSRF-TOKEN", token).build()
}
