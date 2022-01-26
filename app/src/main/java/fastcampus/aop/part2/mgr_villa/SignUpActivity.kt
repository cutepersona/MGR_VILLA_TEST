package fastcampus.aop.part2.mgr_villa

import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.MimeTypeFilter.matches
import androidx.core.view.isInvisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.customdialog.welcomedialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivitySignupBinding
import fastcampus.aop.part2.mgr_villa.fragment.OkFragment
import fastcampus.aop.part2.mgr_villa.model.VillaUsers
import fastcampus.aop.part2.mgr_villa.model.enumUserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    //    private val firebaseAuthSettings = auth.firebaseAuthSettings
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId = ""
    private var authCheckFlag: Boolean = false


    private val collbacks by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                showToast("90초 이내에 인증을 완료해 주세요.")
//                UserInfo.phoneAuthNum = credential.smsCode.toString()
//                binding.phoneAuthEtAuthNum.setText(credential.smsCode.toString())
//                binding.phoneAuthEtAuthNum.isEnabled = false
//                Handler(Looper.getMainLooper()).postDelayed({
//                    verifyPhoneNumberWithCode(credential)
//                }, 1000)

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                AuthComplete.isInvisible = false

                showToast("90초 이내에 인증을 완료해 주세요.")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }


        }
    }

    private val binding: ActivitySignupBinding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    private val userEmailEditText: EditText by lazy {
        findViewById(R.id.userEmailEditText)
    }

    private val userEmailValid1: TextView by lazy {
        findViewById(R.id.userEmailValid1)
    }

    private val userNameEditText: EditText by lazy {
        findViewById(R.id.userNameEditText)
    }

    private val userNameValid1: TextView by lazy {
        findViewById(R.id.userNameValid1)
    }


    private val userPasswordEditText1: EditText by lazy {
        findViewById(R.id.userPasswordEditText1)
    }

    private val userPasswordEditText2: EditText by lazy {
        findViewById(R.id.userPasswordEditText2)
    }

    private val userPWValid1: TextView by lazy {
        findViewById(R.id.userPWValid1)
    }

    private val userPWValid3: TextView by lazy {
        findViewById(R.id.userPWValid3)
    }

    private val userPhoneNumberValid1: TextView by lazy {
        findViewById(R.id.userPhoneNumberValid1)
    }

    private val privacyPolicyValid: TextView by lazy {
        findViewById(R.id.privacyPolicyValid)
    }

    private val phoneSnsAuth: Button by lazy {
        findViewById(R.id.phoneSnsAuth)
    }

    private val userPhoneNumberEditText: EditText by lazy {
        findViewById(R.id.userPhoneNumberEditText)
    }

    private val userAuthCompleteEditText: EditText by lazy {
        findViewById(R.id.userAuthCompleteEditText)
    }

    private val AuthComplete: Button by lazy {
        findViewById(R.id.AuthComplete)
    }

    private val SignUpDone: Button by lazy {
        findViewById(R.id.SignUpDone)
    }

    private val AllUseTerms: Button by lazy {
        findViewById(R.id.AllUseTerms)
    }
    private val necessaryTermsCheck: CheckBox by lazy {
        findViewById(R.id.necessaryTermsCheck)
    }

    private val privacyPolicyCheck: CheckBox by lazy {
        findViewById(R.id.privacyPolicyCheck)
    }
//
//    private val communityToolbar: Toolbar by lazy{
//        findViewById(R.id.communityToolbar)
//    }

    private var emailflag = false
    private var nameflag = false
    private var pwflag = false
    private var repwflag = false
    private var phoneflag = false
    private var termsflag = false
    private var privacyflag = false

    // 이메일 체크 정규식
//    private val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private val emailValidation = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\$"
    private val nameValidation = "^[a-zA-Zㄱ-ㅣ가-힣]*$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_signup)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.communityToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("tenant")) {
            binding.emptyButtomUp.text = intent.getStringExtra("tenant")
        } else if (intent.hasExtra("mgr")) {
            binding.emptyButtomUp.text = intent.getStringExtra("mgr")
        }

        auth = Firebase.auth
        auth.setLanguageCode("kr")


        initEmailEditTextCheck()
        initNameTextCheck()
        initPasswordTextCheck()
        initPasswordTextCheck1()
        initPhoneNumberTextCheck()

        initValidInvisible()
        signUpComplete()
        phoneSnsAuthCheck()
        authCompleteCheck()
        allUseTermsCheck()

        privPolicyCheck()
        binding.privacyPolicy.setOnClickListener {
            val toPrivacyPolicy = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(toPrivacyPolicy)
        }


    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun privPolicyCheck() {
        necessaryTermsCheck.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                privacyPolicyValid.setTextColor(-65535)
                privacyPolicyValid.isInvisible = false
                privacyPolicyValid.setText(R.string.must_insert)
                termsflag = false
            } else {
                privacyPolicyValid.isInvisible = true
                termsflag = true
            }
        }

        privacyPolicyCheck.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                privacyPolicyValid.setTextColor(-65535)
                privacyPolicyValid.isInvisible = false
                privacyPolicyValid.setText(R.string.must_insert)
                privacyflag = false
            } else {
                privacyPolicyValid.isInvisible = true
                privacyflag = true
            }
        }
    }

    // 전체동의
    private fun allUseTermsCheck() {
        AllUseTerms.setOnClickListener {
//            Log.d("AllUseTerms.setOnClickListener", "necessaryTermsCheck.isChecked")
            if (necessaryTermsCheck.isChecked
                && !privacyPolicyCheck.isChecked){
                privacyPolicyCheck.isChecked = true
            } else if (!necessaryTermsCheck.isChecked
                && privacyPolicyCheck.isChecked){
                necessaryTermsCheck.isChecked = true
            }else{
                necessaryTermsCheck.isChecked = !necessaryTermsCheck.isChecked
                privacyPolicyCheck.isChecked = !privacyPolicyCheck.isChecked
            }

        }
    }

    private fun authCompleteCheck() {
        AuthComplete.setOnClickListener {
//            Log.d("AuthComplete.setOnClickListener","${storedVerificationId.toString()}")
            var authNumber = userAuthCompleteEditText.text.toString()
            val phoneCredential =
                PhoneAuthProvider.getCredential(
                    storedVerificationId,
                    authNumber
                )
            signInWithPhoneAuthCredential(phoneCredential)

        }
    }

    private fun phoneSnsAuthCheck() {
        phoneSnsAuth.setOnClickListener {
            var userPhone = userPhoneNumberEditText.text.trim().toString()

            if (userPhone.isNullOrEmpty()) {
                showToast("핸드폰 번호를 입력해 주세요.")
                return@setOnClickListener
            }

//            firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(userPhone, "123456")

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+82" + userPhone)       // Phone number to verify
                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this@SignUpActivity)                 // Activity (for callback binding)
                .setCallbacks(collbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }


    private fun checkForm(): Boolean {


        checkEmail()
        checkName()
        checkPW()
        checkPWrewind()
        checkPhoneNumber()

        if (!necessaryTermsCheck.isChecked || !privacyPolicyCheck.isChecked){
             privacyPolicyValid.setTextColor(-65535)
                privacyPolicyValid.isInvisible = false
                privacyPolicyValid.setText(R.string.must_insert)
                privacyflag = false
        }else{
            privacyflag = true
        }


//  necessaryTermCheck()
//        privPolicyCheck()

        return (emailflag
                &&nameflag
                &&pwflag
                &&repwflag
                &&termsflag
                &&privacyflag)

    }

    // 가입완료 버튼 클릭
    private fun signUpComplete() {
        SignUpDone.setOnClickListener {



                //todo 임시 주석처리 모달 팝업 처리후 주석해제해야 함.
            if (!checkForm()) {

//                Log.d("emailflag", "${emailflag}")
//                Log.d("nameflag", "${nameflag}")
//                Log.d("pwflag", "${pwflag}")
//                Log.d("repwflag", "${repwflag}")
//                Log.d("termsflag", "${termsflag}")
//                Log.d("privacyflag", "${privacyflag}")

                return@setOnClickListener
            } else {
//                    Log.d("checkForm", "${checkForm().toString()}")
                val userdb = VillaNoticeHelper.getInstance(applicationContext)

//                Log.d("userEmail","${userEmailEditText.text}")
//                Log.d("userName","${userNameEditText.text}")
//                Log.d("userPassword","${userPasswordEditText1.text}")
//                Log.d("userPhoneNumber","${userPhoneNumberEditText.text}")

                CoroutineScope(Dispatchers.IO).launch {
                    userdb!!.VillaNoticeDao().insert(
                        VillaUsers(
                            userEmailEditText.text.toString().trim(),
                            "1",
                            userNameEditText.text.toString().trim(),
                            userPasswordEditText1.text.toString().trim(),
                            userPhoneNumberEditText.text.toString().trim(),
                            binding.emptyButtomUp.text.toString().trim()
                        )
                    )
                }

                Log.d("userdb!!.VillaUserDao().insert","Villauser")

                // 회원가입 완료 팝업
                showSignInCompletePopup()
//            }
            }

//            val HomeIntent = Intent(this, VillaHomeActivity::class.java)

////            signUpIntent.putExtra("mailAddress",userEmailEditText.text.toString().trim())
//            startActivity(HomeIntent)

        }
    }

    //회원가입완료 팝업 dialog 호출
    private fun showSignInCompletePopup() {

        val okDialog = OkFragment()
        okDialog.show(supportFragmentManager, "OkFragment")

//
//        var okPopup = AlertDialog.Builder(this)
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.ok_popup, null)
//        okPopup.setView(view)
//
//        var listener = DialogInterface.OnClickListener { popup, _ ->
//            var alert = popup as AlertDialog
//        }

//        okPopup.show()

    }

    // 하단 안내문 initVisible
    private fun initValidInvisible() {
        userEmailValid1.isInvisible = true
        userNameValid1.isInvisible = true
        userPWValid1.isInvisible = true
        userPWValid3.isInvisible = true
        AuthComplete.isInvisible = true

        privacyPolicyValid.isInvisible = true
    }
//
//    // 전화번호 인증코드 요청
//    private fun startPhoneNumberVerification(v:View?, phoneNumber: String) {
//
//        when(v?.id)
//        {
//            R.id.phoneSnsAuth -> {
//                val options = PhoneAuthOptions.newBuilder(auth)
//                    .setPhoneNumber(phoneNumber)       // Phone number to verify
//                    .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
//                    .setActivity(this)                 // Activity (for callback binding)
//                    .setCallbacks(collbacks)          // OnVerificationStateChangedCallbacks
//                    .build()
//                PhoneAuthProvider.verifyPhoneNumber(options)
//            }
//        }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
                    showToast("인증완료 되었습니다.")
                    val user = task.result?.user
                    authCheckFlag = true
//                    Log.d(TAG, "${user?.phoneNumber.toString()}")

                } else {
                    // Sign in failed, display a message and update the UI
                    authCheckFlag = false
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }


    private fun initEmailEditTextCheck() {
        userEmailEditText.addTextChangedListener(object : TextWatcher {
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

    private fun initNameTextCheck() {
        userNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkName()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun initPasswordTextCheck() {
        userPasswordEditText1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPW()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun initPasswordTextCheck1() {
        userPasswordEditText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPWrewind()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun checkEmail() {
        var email = userEmailEditText.text.toString().trim()
        val pattern = android.util.Patterns.EMAIL_ADDRESS

        if (email.isNullOrEmpty()) {
            userEmailValid1.setTextColor(-65535)
            userEmailValid1.isInvisible = false
            userEmailValid1.setText(R.string.must_insert)
            emailflag = false
        } else {
            if (pattern.matcher(email).matches()) {
                //이메일 형태가 정상일 경우
                userEmailEditText.setTextColor(R.color.black.toInt())
                userEmailValid1.isInvisible = true
                emailflag = true
            } else {
                userEmailValid1.setTextColor(-65535)
                userEmailValid1.setText(R.string.wrong_insert)
                userEmailValid1.isInvisible = false
                emailflag = false
            }
        }
    }


    private fun checkName() {

        var user_name = userNameEditText.text.toString().trim()
        val p = Pattern.compile(nameValidation)

        if (user_name.isNullOrEmpty()) {
            userNameValid1.setTextColor(-65535)
            userNameValid1.isInvisible = false
            userNameValid1.setText(R.string.must_insert)
            nameflag = false
        } else {
            if (p.matcher(user_name).matches()) {
                userNameEditText.setTextColor(R.color.black.toInt())
                userNameValid1.isInvisible = true
                nameflag = true
            } else {
                userNameValid1.setTextColor(-65536)
                userNameValid1.setText(R.string.not_insert_num)
                userNameValid1.isInvisible = false
                nameflag = false
            }
        }
    }

    private fun checkPW() {

        var userPw = userPasswordEditText1.text.toString().trim()
//        val p = Pattern.compile(nameValidation)

        if (userPw.isNullOrEmpty()) {
            userPWValid1.setTextColor(-65535)
            userPWValid1.isInvisible = false
            userPWValid1.setText(R.string.must_insert)
            pwflag = false
        } else {
            if (userPw.length >= 6) {
                userPasswordEditText1.setTextColor(R.color.black.toInt())
                userPWValid1.isInvisible = true
                pwflag = true
            } else {
                userPWValid1.setTextColor(-65536)
                userPWValid1.setText(R.string.not_enough_num)
                userPWValid1.isInvisible = false
                pwflag = false
            }
        }
    }

    private fun checkPWrewind() {

        var userPw = userPasswordEditText2.text.toString().trim()

        if (userPw.isNullOrEmpty()) {
            userPWValid3.setTextColor(-65535)
            userPWValid3.isInvisible = false
            userPWValid3.setText(R.string.must_insert)
            repwflag = false
        } else {
            if (!userPasswordEditText1.getText().toString()
                    .equals(userPasswordEditText2.getText().toString())
            ) {
                userPWValid3.setTextColor(-65536)
                userPWValid3.setText(R.string.not_match_password)
                userPWValid3.isInvisible = false
                repwflag = false
            } else if (userPw.length >= 6
                && userPasswordEditText1.getText().toString()
                    .equals(userPasswordEditText2.getText().toString())
            ) {
                userPasswordEditText2.setTextColor(R.color.black.toInt())
                userPWValid3.isInvisible = true
                repwflag = true
            }
        }
    }

    private fun initPhoneNumberTextCheck() {
        userPhoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPhoneNumber()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun checkPhoneNumber() {

        var user_Phone = userPhoneNumberEditText.text.toString().trim()

        if (user_Phone.isNullOrEmpty()) {
            userPhoneNumberValid1.setTextColor(-65535)
            userPhoneNumberValid1.isInvisible = false
            userPhoneNumberValid1.setText(R.string.must_insert)
        } else {
            userPhoneNumberEditText.setTextColor(R.color.black.toInt())
            userPhoneNumberValid1.isInvisible = true
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}