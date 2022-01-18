package fastcampus.aop.part2.mgr_villa.kakaodata

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class KakaoApi {

    companion object {
        const val BASE_URL = "https://dapi.kakao.com"
        const val API_KEY = "KakaoAK 807e4a4a218d132c6c20e69c53b8af1c"
    }

    object KakaoApiRetrofitclient {
        private val retrofit: Retrofit.Builder by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
        }

        val apiService: KakaoApiService by lazy {
            retrofit
                .build()
                .create(KakaoApiService::class.java)
        }
    }

    interface KakaoApiService {

        @GET("v2/local/search/address.json")
        fun getKakaoAddress(
            @Header("Authorization") key: String,
            @Query("query") address: String
        ): Call<KakaoData>

        @GET("v2/local/search/keyword.json")
        fun getKakaoKeyword(
            @Header("Authorization") key: String,
            @Query("query") keyword: String
        ): Call<KakaoKeywordData>

    }

}