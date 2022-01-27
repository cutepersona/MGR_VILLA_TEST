package fastcampus.aop.part2.mgr_villa.kakaodata

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.KakaoSdk
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class GlobalApplication : MyApplication() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "5f6a3eda149fe33d168c5e8a86c6e64c")

    }


}