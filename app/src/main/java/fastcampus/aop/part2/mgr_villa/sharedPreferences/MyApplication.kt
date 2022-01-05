package fastcampus.aop.part2.mgr_villa.sharedPreferences

import android.app.Application
import com.google.android.gms.common.util.SharedPreferencesUtils

class MyApplication: Application() {

    companion object{
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }

}