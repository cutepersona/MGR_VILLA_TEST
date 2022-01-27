package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMainBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainLogo: TextView by lazy{
        findViewById(R.id.MainText)
    }

    var backKeyPressedTime: Long = 0

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash------------->", keyHash)



        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        mainLogo.startAnimation(shake)

        autoLogin()

        initSignUp()
        initLogin()
        initSearchId()
        initSearchPW()






    }




    // 회원가입버튼 클릭
    private fun initSignUp() {
        binding.signUp.setOnClickListener {
            val choiceUserTypeIntent = Intent(this, ChoiceMgrTenantActivity::class.java)
            startActivity(choiceUserTypeIntent)
        }
    }

    // 로그인 버튼 클릭
    private fun initLogin() {
        binding.Login.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    // 아이디 찾기 버튼 클릭
    private fun initSearchId() {
        binding.searchId.setOnClickListener {
            val searchIdIntent = Intent(this, SearchIdActivity::class.java)
            startActivity(searchIdIntent)
        }
    }

    // 비밀번호 찾기 버튼 클릭
    private fun initSearchPW() {
        binding.searchPw.setOnClickListener {
            val searchPwIntent = Intent(this, SearchPwActivity::class.java)
            startActivity(searchPwIntent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        ActivityCompat.finishAffinity(this)
        System.exit(0)

//        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
//            backKeyPressedTime = System.currentTimeMillis()
//
//            return
//        }
//
//        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
//            finishAffinity()
//        }
    }

    private fun autoLogin(){
        if (MyApplication.prefs.getString("email","") != ""
            && MyApplication.prefs.getString("pw","") != ""
            && MyApplication.prefs.getString("villaAddress","") != ""
        ){
            val mgrHomeActivity =
                Intent(this, VillaHomeActivity::class.java)
            mgrHomeActivity.putExtra("email", MyApplication.prefs.getString("email","").trim())
            startActivity(mgrHomeActivity)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}