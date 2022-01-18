package fastcampus.aop.part2.mgr_villa.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.room.PrimaryKey

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("villaInfo", 0)

    fun getString(key: String, defValue: String): String{
        return prefs.getString(key,defValue).toString()
    }

    fun setString(key: String, setVal: String){
        prefs.edit().putString(key, setVal).apply()
    }

    fun clear(){
        prefs.edit().clear().commit()
    }


}