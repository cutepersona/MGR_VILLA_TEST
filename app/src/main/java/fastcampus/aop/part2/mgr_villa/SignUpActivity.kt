package fastcampus.aop.part2.mgr_villa

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.*
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.view.isInvisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nhn.android.naverlogin.OAuthLogin
import fastcampus.aop.part2.mgr_villa.customdialog.WelcomeDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivitySignupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    //    private val firebaseAuthSettings = auth.firebaseAuthSettings
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId = ""
    private var authCheckFlag: Boolean = false


    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(
            layoutInflater
        )
    }

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

//                AuthComplete.isInvisible = false

                showToast("90초 이내에 인증을 완료해 주세요.")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }


        }
    }

    var second = 0
    var minute = 0

    val countDown = object : CountDownTimer(1000 * 90, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(p0: Long) {
            second = ((p0 / 1000) % 60).toInt()
            minute = ((p0 / 1000) / 60).toInt()

            // countDownInterval 마다 호출 (여기선 1000ms)
            runOnUiThread {
                binding.SignUpAuthCredentialTimer.text = "$minute:${String.format("%02d", second)}"
            }

        }

        override fun onFinish() {
            // 타이머가 종료되면 호출
            binding.SignUpAuthCredentialTimer.text = "1:30"
        }
    }

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
//
//    private val AuthComplete: Button by lazy {
//        findViewById(R.id.AuthComplete)
//    }

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

    // 소셜 로그인 정보
    var Nemail: String = ""
    var Nname: String = ""
    var Nmobile: String = ""


    // 네이버 아이디 로그인
    lateinit var mOAuthLoginInstance: OAuthLogin

    // firestore Database 이용
    val firestoreDB = Firebase.firestore

    var scrollY: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_signup)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.communityToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)

//        firestore = FirebaseFirestore.getInstance()

        if (intent.hasExtra("tenant")) {
            binding.emptyButtomUp.text = intent.getStringExtra("tenant")
        } else if (intent.hasExtra("mgr")) {
            binding.emptyButtomUp.text = intent.getStringExtra("mgr")
        }

        if (intent.hasExtra("N")) {
//            showToast(intent.getStringExtra("N").toString())
            binding.userEmailEditText.setText(intent.getStringExtra("Nemail").toString())
            binding.userNameEditText.setText(intent.getStringExtra("Nname").toString())
            binding.userPhoneNumberEditText.setText(intent.getStringExtra("Nmobile").toString())
        }

        mOAuthLoginInstance = OAuthLogin.getInstance()

        auth = Firebase.auth
        auth.setLanguageCode("kr")

        initToolBar()

        countDown.cancel()

        binding.SignUpDone.setTextColor(Color.BLACK)

        initFocusEditText()


        initEmailEditTextCheck()
        initNameTextCheck()
        initPasswordTextCheck()
        initPasswordTextCheck1()
        initPhoneNumberTextCheck()

        initValidInvisible()
        signUpComplete()
        phoneSnsAuthCheck()
//        authCompleteCheck()
        allUseTermsCheck()

        privPolicyCheck()

        binding.necessaryTerms.setOnClickListener {
            val toTerms = Intent(this, TermsActivity::class.java)
            startActivity(toTerms)
        }

        binding.privacyPolicy.setOnClickListener {
            val toPrivacyPolicy = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(toPrivacyPolicy)
        }


    }

    private fun initFocusEditText() {

        binding.userEmailEditText.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    scrollY = 0
                    binding.signUpScrollView.smoothScrollToView(binding.userEmailText)
                }
            }
        })

        binding.userNameEditText.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    scrollY = 0
                    binding.signUpScrollView.smoothScrollToView(binding.userName)
                }
            }
        })

        binding.userPasswordEditText1.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    scrollY = 0
                    binding.signUpScrollView.smoothScrollToView(binding.userPassword)
                }
            }
        })

        binding.userPasswordEditText2.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    scrollY = 0
                    binding.signUpScrollView.smoothScrollToView(binding.userPasswordEditText1)
                }
            }
        })

        binding.userPhoneNumberEditText.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    scrollY = 0
                    binding.signUpScrollView.smoothScrollToView(binding.userPhoneNumber)
                }
            }
        })

        binding.userAuthCompleteEditText.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    scrollY = 0
                    binding.signUpScrollView.smoothScrollToView(binding.userPhoneNumberEditText)
                }
            }
        })


    }

    fun computeDistanceToView(view: View): Int {
        return abs(calculateRectOnScreen(binding.signUpScrollView).top - (this.scrollY + calculateRectOnScreen(view).top))
    }

    fun calculateRectOnScreen(view: View): Rect {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Rect(
            location[0],
            location[1],
            location[0] + view.measuredWidth,
            location[1] + view.measuredHeight
        )
    }

//    private fun smoothScorllToView(view: View){
//
//        val y = computeDistanceToView(view)
//        ObjectAnimator.ofInt(this, "scrollY", y).apply {
//            duration = 1000L // 스크롤이 지속되는 시간을 설정한다. (1000 밀리초 == 1초)
//        }.start()
//
//    }


    fun ScrollView.smoothScrollToView(
        view: View,
        marginTop: Int = 0,
        maxDuration: Long = 500L,
        onEnd: () -> Unit = {}
    ) {
        if (this.getChildAt(0).height <= this.height) { // 스크롤의 의미가 없다.
            onEnd()
            return
        }
        val y = computeDistanceToView(view) - marginTop
        val ratio = abs(y - this.scrollY) / (this.getChildAt(0).height - this.height).toFloat()
        ObjectAnimator.ofInt(this, "scrollY", y).apply {
            duration = (maxDuration * ratio).toLong()
            interpolator = AccelerateDecelerateInterpolator()
            doOnEnd {
                onEnd()
            }
            start()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        mOAuthLoginInstance.logout(applicationContext)
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)

    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.communityToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        mOAuthLoginInstance.logout(applicationContext)
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
                && !privacyPolicyCheck.isChecked
            ) {
                privacyPolicyCheck.isChecked = true
            } else if (!necessaryTermsCheck.isChecked
                && privacyPolicyCheck.isChecked
            ) {
                necessaryTermsCheck.isChecked = true
            } else {
                necessaryTermsCheck.isChecked = !necessaryTermsCheck.isChecked
                privacyPolicyCheck.isChecked = !privacyPolicyCheck.isChecked
            }
            if (checkButtonActivate()){
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_background)
            } else {
                binding.SignUpDone.setBackgroundResource(R.drawable.button_wrong_background)
            }
        }
    }
//
//    private fun authCompleteCheck() {
//        AuthComplete.setOnClickListener {
////            Log.d("AuthComplete.setOnClickListener","${storedVerificationId.toString()}")
//            var authNumber = userAuthCompleteEditText.text.toString()
//            val phoneCredential =
//                PhoneAuthProvider.getCredential(
//                    storedVerificationId,
//                    authNumber
//                )
//            signInWithPhoneAuthCredential(phoneCredential)
//
//        }
//    }

    private fun phoneSnsAuthCheck() {
        phoneSnsAuth.setOnClickListener {
            var userPhone = userPhoneNumberEditText.text.trim().toString()

            if (userPhone.isNullOrEmpty()) {
                showToast("핸드폰 번호를 입력해 주세요.")
                return@setOnClickListener
            }

            countDown.start()

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

    private fun checkButtonActivate(): Boolean{
        return (emailflag
                && nameflag
                && pwflag
                && repwflag
                && termsflag
                && privacyflag)
    }


    private fun checkForm(): Boolean {
        checkEmail()
        checkName()
        checkPW()
        checkPWrewind()
        checkPhoneNumber()

        if (!necessaryTermsCheck.isChecked || !privacyPolicyCheck.isChecked) {
            privacyPolicyValid.setTextColor(-65535)
            privacyPolicyValid.isInvisible = false
            privacyPolicyValid.setText(R.string.must_insert)
            privacyflag = false
        } else {
            privacyflag = true
        }


//  necessaryTermCheck()
//        privPolicyCheck()

        return (emailflag
                && nameflag
                && pwflag
                && repwflag
                && termsflag
                && privacyflag)

    }

    // 가입완료 버튼 클릭
    private fun signUpComplete() {
        SignUpDone.setOnClickListener {



            if (!checkForm()) {

//                Log.d("emailflag", "${emailflag}")
//                Log.d("nameflag", "${nameflag}")
//                Log.d("pwflag", "${pwflag}")
//                Log.d("repwflag", "${repwflag}")
//                Log.d("termsflag", "${termsflag}")
//                Log.d("privacyflag", "${privacyflag}")

                return@setOnClickListener
            } else {

                binding.SignUpDone.setBackgroundResource(R.drawable.button_background)
                binding.SignUpDone.setTextColor(Color.WHITE)

                val userPhone = binding.userPhoneNumberEditText.text.trim().toString()
                val authNum = binding.userAuthCompleteEditText.text.trim().toString()

                if (authNum.isEmpty()) {
                    showToast("인증 번호를 입력해 주세요.")
                    return@setOnClickListener
                }
                if (userPhone.isEmpty()) {
                    showToast("휴대폰 번호를 입력해 주세요.")
                    return@setOnClickListener
                }

                val authNumber = userAuthCompleteEditText.text.toString()
                if (authNumber.isNotEmpty()) {

                    val phoneCredential =
                        PhoneAuthProvider.getCredential(
                            storedVerificationId,
                            authNumber
                        )
                    signInWithPhoneAuthCredential(phoneCredential)
                } else {
                    showToast("아직 인증되지 않았습니다.")
                    return@setOnClickListener
                }

                // 회원정보 저장
//                    Log.d("checkForm", "${checkForm().toString()}")
                val userdb = VillaNoticeHelper.getInstance(applicationContext)

//                Log.d("userEmail","${userEmailEditText.text}")
//                Log.d("userName","${userNameEditText.text}")
//                Log.d("userPassword","${userPasswordEditText1.text}")
//                Log.d("userPhoneNumber","${userPhoneNumberEditText.text}")


//                val VillaUsers = VillaUsers(
//                    userEmailEditText.text.toString().trim(),
//                    "1",
//                    userNameEditText.text.toString().trim(),
//                    userPasswordEditText1.text.toString().trim(),
//                    userPhoneNumberEditText.text.toString().trim(),
//                    binding.emptyButtomUp.text.toString().trim()
//                )

                val users = firestoreDB.collection("VillaUsers")

                val VillaUsers = hashMapOf(
                    "mailAddress" to userEmailEditText.text.toString().trim(),
                    "roomNumber" to "1",
                    "userName" to userNameEditText.text.toString().trim(),
                    "passWord" to userPasswordEditText1.text.toString().trim(),
                    "phoneNumber" to userPhoneNumberEditText.text.toString().trim(),
                    "userType" to binding.emptyButtomUp.text.toString().trim(),
                    "signUpType" to ""
                )
//
//                firestoreDB.collection("VillaUsers")
//                    .get()
//                    .addOnSuccessListener { result ->
//                        val user = result.find { it["mailAddress"] ==  userEmailEditText.text.toString().trim()}
//                        if (user != null) {
//                            showToast(user.id.toString())
//                            return@addOnSuccessListener
//                        } else {
//                            showToast("mail null")
//                        }
//                    }

                // 회원정보 체크하기
                firestoreDB.collection("VillaUsers")
                    .whereEqualTo("mailAddress", binding.userEmailEditText.text.toString().trim())
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty){
                            showToast("이미 가입된 계정입니다.")
                            return@addOnSuccessListener
                        }else {
                            users.document(userEmailEditText.text.toString().trim())
                                .set(VillaUsers)
                                .addOnSuccessListener { documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                                    CoroutineScope(Dispatchers.IO).launch {
//                                        userdb!!.VillaNoticeDao().insert(
//                                            VillaUsers(
//                                                userEmailEditText.text.toString().trim(),
//                                                "1",
//                                                userNameEditText.text.toString().trim(),
//                                                userPasswordEditText1.text.toString()
//                                                    .trim(),
//                                                userPhoneNumberEditText.text.toString()
//                                                    .trim(),
//                                                binding.emptyButtomUp.text.toString().trim()
//                                            )
//                                        )
//                                    }

                                    showToast("회원가입을 환영합니다.")

                                    val toLogin = Intent(this, LoginActivity::class.java)
                                    startActivity(toLogin)
                                }
                                .addOnFailureListener { e ->
                                    showToast("회원가입에 실패하였습니다.")
                                    Log.w(TAG, "Error adding document", e)
                                    return@addOnFailureListener
                                }
                        }
                    }
//
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            for (i in task.result!!) {
//                                if (i.id == binding.userEmailEditText.text.toString().trim()) {
//                                    showToast("이미 가입된 계정입니다.")
//                                    break
//                                } else {
//                                    users.document(userEmailEditText.text.toString().trim())
//                                        .set(VillaUsers)
//                                        .addOnSuccessListener { documentReference ->
////                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                                            CoroutineScope(Dispatchers.IO).launch {
//                                                userdb!!.VillaNoticeDao().insert(
//                                                    VillaUsers(
//                                                        userEmailEditText.text.toString().trim(),
//                                                        "1",
//                                                        userNameEditText.text.toString().trim(),
//                                                        userPasswordEditText1.text.toString()
//                                                            .trim(),
//                                                        userPhoneNumberEditText.text.toString()
//                                                            .trim(),
//                                                        binding.emptyButtomUp.text.toString().trim()
//                                                    )
//                                                )
//                                            }
//
//                                            showToast("회원가입을 환영합니다.")
//
//                                            val toLogin = Intent(this, LoginActivity::class.java)
//                                            startActivity(toLogin)
//                                        }
//                                        .addOnFailureListener { e ->
//                                            showToast("회원가입에 실패하였습니다.")
//                                            Log.w(TAG, "Error adding document", e)
//                                            return@addOnFailureListener
//                                        }
//                                    break
//                                }
//                            }
//                        }
//                    }
                    .addOnFailureListener {
                        showToast("회원가입에 실패 하였습니다.")
                        return@addOnFailureListener
                    }


                Log.d("userdb!!.VillaUserDao().insert", "Villauser")

                // 회원가입 완료 팝업
//                showSignInCompletePopup()
                countDown.cancel()
//                showToast("가입을 환영합니다.")
//
//                val toLogin = Intent(this, LoginActivity::class.java)
//                startActivity(toLogin)

//            }
            }

//            val HomeIntent = Intent(this, VillaHomeActivity::class.java)

////            signUpIntent.putExtra("mailAddress",userEmailEditText.text.toString().trim())
//            startActivity(HomeIntent)

        }
    }

    //회원가입완료 팝업 dialog 호출
    private fun showSignInCompletePopup() {

        val welcomeDialog = WelcomeDialog(this)
        welcomeDialog.showDialog()

    }

    // 하단 안내문 initVisible
    private fun initValidInvisible() {
        userEmailValid1.isInvisible = true
        userNameValid1.isInvisible = true
        userPWValid1.isInvisible = true
        userPWValid3.isInvisible = true
//        AuthComplete.isInvisible = true

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
//                    showToast("인증완료 되었습니다.")
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
                if (checkButtonActivate()){
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_background)
                } else {
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_wrong_background)
                }
            }
        })
    }

    private fun initNameTextCheck() {
        userNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkName()
                if (checkButtonActivate()){
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_background)
                } else {
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_wrong_background)
                }
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
                if (checkButtonActivate()){
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_background)
                } else {
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_wrong_background)
                }
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
                if (checkButtonActivate())
                {
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_background)
                }
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
                if (checkButtonActivate()){
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_background)
                } else {
                    binding.SignUpDone.setBackgroundResource(R.drawable.button_wrong_background)
                }
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
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }


}