package fastcampus.aop.part2.mgr_villa.Object

import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoApiRetrofitClient {

    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(KakaoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiService: KakaoApi.KakaoApiService by lazy {
        retrofit
            .build()
            .create(KakaoApi.KakaoApiService::class.java)
    }

}