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
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMainBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

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

//         키 해쉬값 확인 for Kakako
//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash------------->", keyHash)

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        mainLogo.startAnimation(shake)

        autoLogin()

        initSignUp()
        initLogin()
        initSearchId()
        initSearchPW()

        // 카카오 로그인 초기화
        initMainKakaoButton()


    }

    // Main 카카오 버튼 초기화
    private fun initMainKakaoButton() {
        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
//                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            }
            else if (tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, SecondActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                finish()
            }
        }


        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else if (token != null) {

                showToast("카카오 로그인 성공 toast")

//            Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, SecondActivity::class.java)
//            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//            finish()
            }
        }

        binding.MainKakaoButton.setOnClickListener {
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
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