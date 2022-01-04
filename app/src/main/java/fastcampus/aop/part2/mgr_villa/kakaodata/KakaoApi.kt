package fastcampus.aop.part2.mgr_villa.kakaodata

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class KakaoApi {

    companion object {
        const val BASE_URL = "https://dapi.kakao.com"
        const val API_KEY = "KakaoAK 807e4a4a218d132c6c20e69c53b8af1c"
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