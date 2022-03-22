package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import fastcampus.aop.part2.mgr_villa.R.style
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMainBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth

    val firestoreDB = Firebase.firestore

    private val mainLogo: ImageView by lazy{
        findViewById(R.id.MainText)
    }

    var backKeyPressedTime: Long = 0

    lateinit var mOAuthLoginInstance : OAuthLogin
    lateinit var mContext: Context

    val naver_client_id = "NJrt6Ht1CE5xqoouwaIq"
    val naver_client_secret = "Jc4B24yJvN"
    val naver_client_name = "MGR_VILLA"

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//         키 해쉬값 확인 for Kakako
//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash------------->", keyHash)

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        mainLogo.startAnimation(shake)

        auth = Firebase.auth
        auth.setLanguageCode("kr")

        autoLogin()

        mContext = this

        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)

        binding.buttonOAuthLoginImg.setOAuthLoginHandler(mOAuthLoginHandler)


        initSignUp()
        initLogin()
        initSearchId()
        initSearchPW()

        // 카카오 로그인 초기화
        initMainKakaoButton()


    }

    // 네이버 아이디 로그인 API 연동
    val mOAuthLoginHandler: OAuthLoginHandler = @SuppressLint("HandlerLeak")
    object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
                val accessToken: String = mOAuthLoginInstance.getAccessToken(baseContext)

                val header = "Bearer " + accessToken
                val url = "https://openapi.naver.com/v1/nid/me"

                val requestHeaders: MutableMap<String, String> = HashMap()
                requestHeaders["Authorization"] = header

                Thread(Runnable {
                    val responseBody = get(url, requestHeaders)
                    val loginResult = JSONObject(responseBody)
                    val response: JSONObject = loginResult.getJSONObject("response")

                    val email = response.getString("email")
                    val mobile = response.getString("mobile").replace("-","")
                    val name = response.getString("name")

                    //
                    firestoreDB.collection("VillaUsers")
                        .document(email)
                        .get()
                        .addOnSuccessListener { task ->
                            if (task.exists()){
                                MyApplication.prefs.setString("email",email.trim())
                                MyApplication.prefs.setString("userType",task["userType"].toString().trim())

                                // 로그인 한 유저가 관리자 인 경우
                                if (MyApplication.prefs.getString("userType","").equals("MGR")){
                                    firestoreDB.collection("VillaInfo")
                                        .whereEqualTo("mailAddress", email.trim())
                                        .get()
                                        .addOnSuccessListener { result ->
                                            // 관리자 email로 등록된 건물이 없음.
                                            if (result.isEmpty) {
                                                val addrSearchActivity = Intent(this@MainActivity, AddressSearchActivity::class.java)
                                                addrSearchActivity.putExtra("email", email.trim())
                                                startActivity(addrSearchActivity)
                                            }
                                            // 관리자 email로 등록된 집이 있음
                                            if (!result.isEmpty){
                                                for(i in result!!){
                                                    // 관리자 email로 등록된 집주소를 SharedPreference에 저장
                                                    MyApplication.prefs.setString("villaAddress", i.data["villaAddress"].toString().trim())
                                                    break
                                                }
                                                // 홈 화면으로 이동
                                                val mgrHomeActivity = Intent(this@MainActivity, VillaHomeActivity::class.java)
                                                mgrHomeActivity.putExtra("email", email.trim())
//
                                                startActivity(mgrHomeActivity)
                                            }
                                        }
                                        .addOnFailureListener {
                                            showToast("로그인 정보 불러오기 실패")
                                            return@addOnFailureListener
                                        }


                                } else {
                                    firestoreDB.collection("VillaTenant")
                                        .whereEqualTo("tenantEmail", email.trim())
                                        .get()
                                        .addOnSuccessListener { task ->
                                            if(task.isEmpty){
                                                val addrSearchTenantActivity = Intent(this@MainActivity, AddressSearchForTenantActivity::class.java)
                                                addrSearchTenantActivity.putExtra("N", "N")
                                                addrSearchTenantActivity.putExtra("email", email.trim())
                                                startActivity(addrSearchTenantActivity)
                                            } else {
                                                firestoreDB.collection("VillaTenant")
                                                    .whereEqualTo("tenantEmail", email.trim())
                                                    .whereEqualTo("tenantStatus","IntoDone")
                                                    .get()
                                                    .addOnSuccessListener { task ->
                                                        if(task.isEmpty){
                                                            showToast("입주대기 중입니다. 관리자에게 문의바랍니다.")
                                                        }else {
                                                            val mgrHomeActivity = Intent(this@MainActivity, VillaHomeActivity::class.java)
                                                            mgrHomeActivity.putExtra("email", email.trim())
                                                            startActivity(mgrHomeActivity)
                                                        }
                                                    }.addOnFailureListener {
                                                        showToast("로그인하지 못하였습니다. 관리자에게 문의 바랍니다.")
                                                        return@addOnFailureListener
                                                    }
                                            }
                                        }
                                }


                            } else {
                                // 회원가입 안되어 있을때
                                val toTenantDiv = Intent(this@MainActivity, ChoiceMgrTenantActivity::class.java)
                                toTenantDiv.putExtra("N","NAVER")
                                toTenantDiv.putExtra("Nemail",email)
                                toTenantDiv.putExtra("Nname",name)
                                toTenantDiv.putExtra("Nmobile",mobile)
                                startActivity(toTenantDiv)
                            }
                        }.addOnFailureListener { it ->
                            showToast(it.message.toString())
                        }

                }).start()


            } else {
                val errorCode: String = mOAuthLoginInstance.getLastErrorCode(mContext).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)

            }
        }
    }

    private operator fun get(apiUrl: String, requestHeaders: Map<String, String>): String? {
            val con = connect(apiUrl)
            return try {
                con!!.requestMethod = "GET"
                for ((key, value) in requestHeaders) {
                    con.setRequestProperty(key, value)
                }
                val responseCode = con.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                    readBody(con.inputStream)
                } else { // 에러 발생
                    readBody(con.errorStream)
                }
            } catch (e: IOException) {
                throw RuntimeException("API 요청과 응답 실패", e)
            } finally {
                con!!.disconnect()
            }


    }


    private fun connect(apiUrl: String): HttpURLConnection? {
        return try {
            val url = URL(apiUrl)
            url.openConnection() as HttpURLConnection
        } catch (e: MalformedURLException) {
            throw RuntimeException("API URL이 잘못되었습니다. : $apiUrl", e)
        } catch (e: IOException) {
            throw RuntimeException("연결이 실패했습니다. : $apiUrl", e)
        }
    }

    private fun readBody(body: InputStream): String? {
        val streamReader = InputStreamReader(body)
        try {
            BufferedReader(streamReader).use { lineReader ->
                val responseBody = StringBuilder()
                var line: String?
                while (lineReader.readLine().also { line = it } != null) {
                    responseBody.append(line)
                }
                return responseBody.toString()
            }
        } catch (e: IOException) {
            throw RuntimeException("API 응답을 읽는데 실패했습니다.", e)
        }
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

//        binding.MainKakaoButton.setOnClickListener {
//            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
//                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
//            }else{
//                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
//            }
//        }
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
//            && MyApplication.prefs.getString("pw","") != ""
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