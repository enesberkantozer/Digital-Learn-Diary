package com.example.digitallearndiary.Cloud

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.digitallearndiary.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// API Veri Modelleri
public data class VisionRequest(val requests: List<AnnotateRequest>)
public data class AnnotateRequest(val image: VisionImage, val features: List<Feature>)
public data class VisionImage(val content: String)
public data class Feature(val type: String = "TEXT_DETECTION")
public data class VisionResponse(val responses: List<AnnotateResponse>)
public data class AnnotateResponse(val fullTextAnnotation: FullText?)
public data class FullText(val text: String)

public interface VisionApiService {
    @POST("v1/images:annotate")
    suspend fun analyzeImage(@Query("key") apiKey: String, @Body request: VisionRequest): VisionResponse
}

public object VisionService {
    public val API_KEY: String = BuildConfig.VISION_API_KEY
    private const val BASE_URL = "https://vision.googleapis.com/"

    private val service: VisionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }

    public suspend fun resmiAnalizEt(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return@withContext "Hata: Dosya okunamadı."
            val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)

            val request = VisionRequest(listOf(AnnotateRequest(VisionImage(base64Image), listOf(Feature()))))
            val response = service.analyzeImage(API_KEY, request)

            response.responses.firstOrNull()?.fullTextAnnotation?.text ?: "Okunabilir metin bulunamadı."
        } catch (e: Exception) {
            "Hata: ${e.localizedMessage}"
        }
    }
}