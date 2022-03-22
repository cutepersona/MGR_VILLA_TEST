package fastcampus.aop.part2.mgr_villa

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityLoginBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import fastcampus.aop.part2.mgr_villa.model.VillaUsers as VillaUsers1

class LoginActivity : AppCompatActivity() {

    //    private lateinit var binding: ActivityLoginBinding
    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    val firestoreDB = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initToolBar()
        initEmailEditTextCheck()
        initDoLoginButton()


    }



    // 로그인 버튼 클릭
    private fun initDoLoginButton() {
        binding.userPasswordEditText1.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if(actionId == EditorInfo.IME_ACTION_DONE){
                doLogin()
            }
            handled
        }


        binding.DoLoginButton.setOnClickListener {

            doLogin()

//            val sharedPreferences = getSharedPreferences("villauser", MODE_PRIVATE)
//            val userEditor = sharedPreferences.edit()
//            userEditor.putString("email", binding.userEmailEditText.text.toString().trim())
//            userEditor.putString("pw", binding.userPasswordEditText1.text.toString().trim())
//            userEditor.apply()



//            val userdb = VillaNoticeHelper.getInstance(applicationContext)
//            val villadb = VillaInfoHelper.getInstance(applicationContext)


        }
    }

    private fun doLogin(){
        // 로그인 회원정보 가져오기
        firestoreDB.collection("VillaUsers")
            // 로그인 하려는 email 계정으로 정보 조회
            .document(binding.userEmailEditText.text.toString().trim())
            .get()
            .addOnCompleteListener { task ->
                // Firestore에 email 계정이 있는 경우
                if (task.isSuccessful){
                    firestoreDB.collection("VillaUsers")
                        .document(binding.userEmailEditText.text.toString().trim())
                        .get()
                        .addOnSuccessListener { task ->
                            // 가입유형에 따른 Toast메시지 구분
                            if (task["signUpType"].toString().equals("N")){
                                showToast("네이버로 가입된 계정입니다.")
                                return@addOnSuccessListener
                            }
                            // 일반 회원 가입인 경우
                            else {
                                if (task["mailAddress"].toString().equals(binding.userEmailEditText.text.toString().trim())
                                    && task["passWord"].toString().equals(binding.userPasswordEditText1.text.toString().trim())) {
                                    // SharedPreference에 Eamil, PW, userType 저장
                                    MyApplication.prefs.setString("email",task["mailAddress"].toString().trim())
                                    MyApplication.prefs.setString("pw",task["passWord"].toString().trim())
                                    MyApplication.prefs.setString("userType",task["userType"].toString().trim())
                                    // 관리자 정보 체크
                                    if (task["userType"].toString().equals("MGR")){
                                        firestoreDB.collection("VillaInfo")
                                            .whereEqualTo("mailAddress", binding.userEmailEditText.text.toString().trim())
                                            .get()
                                            .addOnSuccessListener { result ->
                                                // 관리자 email로 등록된 집이 없음.
                                                if (result.isEmpty) {
                                                    val addrSearchActivity = Intent(this, AddressSearchActivity::class.java)
                                                    addrSearchActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
                                                    startActivity(addrSearchActivity)
                                                }
                                                // 관리자 email로 등록된 집이 있음
                                                if (!result.isEmpty){
                                                    for(i in result!!){
                                                        MyApplication.prefs.setString("villaAddress", i.data["villaAddress"].toString().trim())
                                                        break
                                                    }
                                                        val mgrHomeActivity = Intent(this, VillaHomeActivity::class.java)
                                                        mgrHomeActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
                                                        startActivity(mgrHomeActivity)
                                                }
                                            }
                                            .addOnFailureListener {
                                                showToast("로그인 정보 불러오기 실패")
                                                return@addOnFailureListener
                                            }
                                    }
                                    // 세입자 정보 체크
                                    else {
                                        firestoreDB.collection("VillaTenant")
                                            .whereEqualTo("tenantEmail", binding.userEmailEditText.text.toString().trim())
                                            .get()
                                            .addOnSuccessListener { task ->
                                                if(task.isEmpty){
                                                    // 세입자 주소 등록 안되어 있는 경우 주소 검색
                                                    val addrSearchTenantActivity =
                                                        Intent(this, AddressSearchForTenantActivity::class.java)
                                                    addrSearchTenantActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
                                                    startActivity(addrSearchTenantActivity)
                                                } else {
                                                    // 세입자 주소 검색이후
                                                    firestoreDB.collection("VillaTenant")
                                                        .whereEqualTo("tenantEmail", binding.userEmailEditText.text.toString().trim())
                                                        .whereEqualTo("tenantStatus","IntoDone")
                                                        .get()
                                                        .addOnSuccessListener { task ->
                                                            // 전입 요청 한 상태
                                                            if(task.isEmpty){
                                                                showToast("입주대기 중입니다. 관리자에게 문의바랍니다.")
                                                            // 전입 등록 완료 상태 - > 홈화면 이동
                                                            }else {
                                                                val mgrHomeActivity = Intent(this, VillaHomeActivity::class.java)
                                                                mgrHomeActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
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
                                    showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
                                    return@addOnSuccessListener
                                }
                            }
                        }
                } else {
                    showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener { it ->
                showToast("회원정보를 불러오지 못했습니다.")
                return@addOnFailureListener
            }


//------------------------------------------------------------------------------------------------------
//            Thread(Runnable {
//                val user = userdb?.VillaNoticeDao()?.userLogin(
//                    binding.userEmailEditText.text.toString().trim(),
//                    binding.userPasswordEditText1.text.toString().trim()
//                )
//
//
//                val villaInfo = userdb!!.VillaNoticeDao().isVilla(binding.userEmailEditText.text.toString())
//
//
//                var tenantRequestCheck: Int = 0
//                var tenantStatusCheck: String = ""
//
//                if (user?.userType.equals("TENANT")){
//                    tenantRequestCheck = userdb!!.VillaNoticeDao().isVillaTenantCheck(binding.userEmailEditText.text.toString())
//                    tenantStatusCheck = userdb!!.VillaNoticeDao().tenantStatusCheck(binding.userEmailEditText.text.toString())
//                }
//
//                runOnUiThread {
//                    if (user == null) {
//                        showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
//                    }
//                    else {
////                        MyApplication.prefs.setString("userType",user.userType)
//                        if (villaInfo >= 1) {
//                            // TODO 홈화면으로 이동해야함.
//                            if (user.userType.equals("MGR")){
//                                val mgrHomeActivity =
//                                    Intent(this, VillaHomeActivity::class.java)
//                                mgrHomeActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
//                                startActivity(mgrHomeActivity)
//                            }
//
//                        }else{
//                            // 빌라 정보가 없고 관리자인 경우 빌라정보 등록
//                            if (villaInfo < 1 && user.userType.equals("MGR")) {
//                                val addrSearchActivity =
//                                    Intent(this, AddressSearchActivity::class.java)
//                                addrSearchActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
//                                startActivity(addrSearchActivity)
//                            }
//                            // 입주요청하지 않고 세입자인 경우 입주요청화면 이동
//                            if (tenantRequestCheck < 1 && user.userType.equals("TENANT")){
//                                val addrSearchTenantActivity =
//                                    Intent(this, AddressSearchForTenantActivity::class.java)
//                                addrSearchTenantActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
//                                startActivity(addrSearchTenantActivity)
//                            }
//                            // 입주요청은 했으나 아직 입주완료되지 않은 경우
//                            if (tenantRequestCheck > 0 && tenantStatusCheck.equals("Request")){
//                                showToast("입주대기 중입니다. 관리자에게 문의바랍니다.")
//                            }
//                            // 입주요청이 완료된 세입자
//                            if (tenantRequestCheck > 0 && tenantStatusCheck.equals("IntoDone")){
//                                val mgrHomeActivity =
//                                    Intent(this, VillaHomeActivity::class.java)
//                                mgrHomeActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
//                                startActivity(mgrHomeActivity)
//                            }
//
//                        }
//
//                    }
//                }
//            }).start()
//------------------------------------------------------------------------------------------------------

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)
    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.LoginToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)

        return true

//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
    }

    // email 체크
    private fun initEmailEditTextCheck() {
        binding.userEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // text가 변경된 후 호출
                // s에는 변경 후의 문자열이 담겨 있다.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // text가 변경되기 전 호출
                // s에는 변경 전 문자열이 담겨 있다.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // text가 바뀔 때마다 호출된다.
                // 우린 이 함수를 사용한다.
                checkEmail()
            }
        })
    }

    // 이메일 양식체크
    private fun checkEmail() {
        var email = binding.userEmailEditText.text.toString().trim()
        val pattern = android.util.Patterns.EMAIL_ADDRESS

        if (email.isNullOrEmpty()) {
            binding.userEmailValid1.setTextColor(-65535)
            binding.userEmailValid1.isInvisible = false
            binding.userEmailValid1.setText(R.string.must_insert)

        } else {
            if (pattern.matcher(email).matches()) {
                //이메일 형태가 정상일 경우
                binding.userEmailEditText.setTextColor(R.color.black.toInt())
                binding.userEmailValid1.isInvisible = true
            } else {
                binding.userEmailValid1.setTextColor(-65535)
                binding.userEmailValid1.setText(R.string.wrong_insert)
                binding.userEmailValid1.isInvisible = false
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}