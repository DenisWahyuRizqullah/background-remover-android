package com.denis.backgroundremover.data

import android.content.Context
import android.net.Uri
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.InputStream

class BackgroundRemoverClient(private val context: Context) {

    // Inisialisasi Ktor Client dengan Engine CIO
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    // GANTI URL INI dengan URL deployment API backend Anda (misal Railway)
    private val BASE_URL = "https://background-remover-production-8296.up.railway.app"

    suspend fun removeBackground(imageUri: Uri): ByteArray? {
        return try {
            // Membuka input stream dari URI gambar galeri
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val imageBytes = inputStream?.readBytes() ?: return null
            inputStream.close()

            // Menembak endpoint backend menggunakan Multipart form data
            val response: HttpResponse = client.post("$BASE_URL/remove-bg") {
                setBody(MultiPartFormDataContent(
                    formData {
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/png") // Sesuaikan tipe gambar
                            append(HttpHeaders.ContentDisposition, "filename=\"upload.png\"")
                        })
                    }
                ))
            }

            if (response.status == HttpStatusCode.OK) {
                // Mengambil response body berupa byte array (gambar hasil proses)
                response.readBytes()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}