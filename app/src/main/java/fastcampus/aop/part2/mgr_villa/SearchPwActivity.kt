package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivitySearchpwBinding
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class SearchPwActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId = ""
    private var authCheckFlag: Boolean = false

    private var emailflag = false
    private var phoneflag = false

    private val binding: ActivitySearchpwBinding by lazy {
        ActivitySearchpwBinding.inflate(
            layoutInflater
        )
    }

    var second = 0
    var minute = 0

    val countDown  = object : CountDownTimer(1000 * 90, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(p0: Long) {
            second = ((p0 / 1000) % 60).toInt()
            minute = ((p0 / 1000) / 60).toInt()

            // countDownInterval 마다 호출 (여기선 1000ms)
            runOnUiThread {
                binding.SearchPwAuthCredentialTimer.text = "$minute:${String.format("%02d",second)}"
            }

        }

        override fun onFinish() {
            // 타이머가 종료되면 호출
            binding.SearchPwAuthCredentialTimer.text = "1:30"
        }
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

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(ContentValues.TAG, "onVerificationFailed", e)

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
                Log.d(ContentValues.TAG, "onCodeSent:$verificationId")


                showToast("90초 이내에 인증을 완료해 주세요.")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }


        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        countDown.cancel()

        auth = Firebase.auth
        auth.setLanguageCode("kr")

        initEmailEditTextCheck()
        initPhoneNumberTextCheck()
        phoneSnsAuthCheck()
        initSearchPwOnClick()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        countDown.cancel()
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)
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

    // 휴대폰 체크
    private fun initPhoneNumberTextCheck() {
        binding.userPhoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPhoneNumber()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun phoneSnsAuthCheck() {
        binding.phoneSnsAuth.setOnClickListener {
            val userPhone = binding.userPhoneNumberEditText.text.trim().toString()

            if (userPhone.isNullOrEmpty()) {
                showToast("핸드폰 번호를 입력해 주세요.")
                return@setOnClickListener
            }

            countDown.start()

//            showToast("phoneSnsAuthCheck")

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+82" + userPhone)       // Phone number to verify
                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this@SearchPwActivity)                 // Activity (for callback binding)
                .setCallbacks(collbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }


    private fun initSearchPwOnClick() {
        binding.searchPwDone.setOnClickListener {

            val userPhone = binding.userPhoneNumberEditText.text.trim().toString()
            val authNum = binding.userAuthCompleteEditText.text.trim().toString()

            if (authNum.isEmpty()) {
                showToast("인증 번호를 입력해 주세요.")
                return@setOnClickListener
            }
            if (userPhone.isEmpty()){
                showToast("휴대폰 번호를 입력해 주세요.")
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val userdb = VillaNoticeHelper.getInstance(applicationContext)
                val checkId = userdb!!.VillaNoticeDao().isUserId(
                    binding.userEmailEditText.text.toString().trim()
                    ,binding.userPhoneNumberEditText.text.toString().trim()
                )

                CoroutineScope(Dispatchers.Main).launch {
                    if (checkId < 1){
                        showToast("회원정보가 없습니다.")
                        return@launch
                    }else{
                        val authNumber = binding.userAuthCompleteEditText.text.toString()
                        if (authNumber.isNotEmpty()){
                            val phoneCredential =
                                PhoneAuthProvider.getCredential(
                                    storedVerificationId,
                                    authNumber
                                )

                            signInWithPhoneAuthCredential(phoneCredential)
                        } else {
                            showToast("아직 인증되지 않았습니다.")
                            return@launch
                        }
                    }
                }
            }



        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    showToast("인증완료 되었습니다.")
//                    val user = task.result?.user
//                    authCheckFlag = true
//                    if (authCheckFlag){


//                        startActivity(Intent(this, ChangePwActivity::class.java))
                    countDown.cancel()

                    val changePwIntent = Intent(this@SearchPwActivity, ChangePwActivity::class.java)

                    changePwIntent.putExtra(
                        "email",
                        binding.userEmailEditText.text.toString()
                    )

                    startActivity(changePwIntent)
//                    }

                } else {
                    // Sign in failed, display a message and update the UI
//                    authCheckFlag = false
                    showToast("인증번호를 확인해 주세요.")
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        return@addOnCompleteListener
                    }
                    // Update UI
                }
            }
    }

    private fun checkEmail() {
        var email = binding.userEmailEditText.text.toString().trim()
        val pattern = android.util.Patterns.EMAIL_ADDRESS

        if (email.isNullOrEmpty()) {
            binding.userEmailValid1.setTextColor(-65535)
            binding.userEmailValid1.isInvisible = false
            binding.userEmailValid1.setText(R.string.must_insert)
            emailflag = false

        } else {
            if (pattern.matcher(email).matches()) {
                //이메일 형태가 정상일 경우
                binding.userEmailEditText.setTextColor(R.color.black.toInt())
                binding.userEmailValid1.isInvisible = true
                emailflag = true
            } else {
                binding.userEmailValid1.setTextColor(-65535)
                binding.userEmailValid1.setText(R.string.wrong_insert)
                binding.userEmailValid1.isInvisible = false
                emailflag = false
            }
        }
    }

    private fun checkPhoneNumber() {

        var user_Phone = binding.userPhoneNumberEditText.text.toString().trim()

        if (user_Phone.isNullOrEmpty()) {
            binding.userPhoneNumberValid1.setTextColor(-65535)
            binding.userPhoneNumberValid1.isInvisible = false
            binding.userPhoneNumberValid1.setText(R.string.must_insert)
            phoneflag = false
        } else {
            binding.userPhoneNumberEditText.setTextColor(R.color.black.toInt())
            binding.userPhoneNumberValid1.isInvisible = true
            phoneflag = true
        }

    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.searchPwToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }


    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        countDown.cancel()
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}