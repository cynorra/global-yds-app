package com.vocabmaster.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApi {
    @GET("get")
    suspend fun translateWord(
        @Query("q") word: String,
        @Query("langpair") langPair: String = "en|tr"
    ): TranslationResponse
}

data class TranslationResponse(
    val responseData: ResponseData?
)

data class ResponseData(
    val translatedText: String?
)
