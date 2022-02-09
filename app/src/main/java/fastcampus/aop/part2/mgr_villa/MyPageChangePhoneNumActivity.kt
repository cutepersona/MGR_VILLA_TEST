package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityChangepwBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityLoginBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMypagechangephonenumBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMypagechangepwBinding
import io.reactivex.Completable.timer
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class MyPageChangePhoneNumActivity : AppCompatActivity() {

    private val binding: ActivityMypagechangephonenumBinding by lazy { ActivityMypagechangephonenumBinding.inflate(layoutInflater) }

    val firestoreDB = Firebase.firestore

    private lateinit var auth: FirebaseAuth


    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId = ""
    private var authCheckFlag: Boolean = false

    var second = 0
    var minute = 0
    var timeCheck = 0

    private var email: String = ""

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

    val countDown  = object : CountDownTimer(1000 * 90, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(p0: Long) {
            second = ((p0 / 1000) % 60).toInt()
            minute = ((p0 / 1000) / 60).toInt()

            // countDownInterval 마다 호출 (여기선 1000ms)
            runOnUiThread {
                binding.MyPageAuthCredentialTimer.text = "$minute:${String.format("%02d",second)}"
            }

        }

        override fun onFinish() {
            // 타이머가 종료되면 호출
            binding.MyPageAuthCredentialTimer.text = "1:30"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        auth.setLanguageCode("kr")
//        auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)

        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email").toString()
        }

        countDown.cancel()
        binding.MyPageAuthCredentialTimer.isEnabled = false
        initToolBar()
        phoneSnsAuthCheck()
        initPhoneNumTextCheck()
        initChangePhoneNum()
    }

    private fun phoneSnsAuthCheck() {
        binding.MyPagePhoneSnsAuth.setOnClickListener {
            var userPhone = binding.MyPageUserPhoneNumberEditText.text.trim().toString()

            if (userPhone.isNullOrEmpty()) {
                showToast("핸드폰 번호를 입력해 주세요.")
                return@setOnClickListener
            }

            countDown.start()

//            showToast("phoneSnsAuthCheck")

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+82" + userPhone)       // Phone number to verify
                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this@MyPageChangePhoneNumActivity)                 // Activity (for callback binding)
                .setCallbacks(collbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    showToast("인증완료 되었습니다.")


                } else {
                    // Sign in failed, display a message and update the UI
//                    authCheckFlag = false
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        return@addOnCompleteListener
                    }
                    // Update UI
                }
            }
    }

    private fun initChangePhoneNum() {
        binding.DoChangePhoneNum.setOnClickListener {
            val userPhone = binding.MyPageUserPhoneNumberEditText.text.trim().toString()

            if (userPhone.isEmpty()) {
                showToast("핸드폰 번호를 입력해 주세요.")
                return@setOnClickListener
            }


            val authNumber = binding.MyPagePhoneNumSnsAuthEditText.text.toString()
            if (authNumber.isNotEmpty()){
                val phoneCredential =
                    PhoneAuthProvider.getCredential(
                        storedVerificationId,
                        authNumber
                    )

                signInWithPhoneAuthCredential(phoneCredential)

                if (changePhoneNum()) {
                    showToast("전화번호가 변경되었습니다.")
                    val toMyPage = Intent(this, MyPageActivity::class.java)
                    startActivity(toMyPage)
                } else {
                    showToast("전화번호가 변경되지 않았습니다.")
                }

            } else {
                showToast("아직 인증되지 않았습니다.")
                return@setOnClickListener
            }

        }
    }

    private fun changePhoneNum(): Boolean {
        return if (!email.isEmpty()) {

            firestoreDB.collection("VillaUsers")
                .document(email)
                .update(mapOf(
                    "phoneNumber" to binding.MyPageUserPhoneNumberEditText.text.toString().trim()
                ))
//------------------------------------------------------------------------------------------
//            val userdb = VillaNoticeHelper.getInstance(applicationContext)
//
//            Thread(Runnable {
//                userdb!!.VillaNoticeDao().updatePhoneNum(binding.MyPageUserPhoneNumberEditText.text.toString(), email)
//            }).start()
//------------------------------------------------------------------------------------------
            true
        } else {
            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        countDown.cancel()
        val toMyPage = Intent(this, MyPageActivity::class.java)
        startActivity(toMyPage)

    }

    // 툴바 초기화
    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.MyPageChangePhoneNumToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        countDown.cancel()
        val toMyPage = Intent(this, MyPageActivity::class.java)
        startActivity(toMyPage)
        return true
    }

    // 변경 비밀번호 확인
    private fun initPhoneNumTextCheck() {
        binding.MyPageUserPhoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPhoneNum()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun checkPhoneNum() {

        var userPw = binding.MyPageUserPhoneNumberEditText.text.toString().trim()
//        val p = Pattern.compile(nameValidation)

        if (userPw.isNullOrEmpty()) {
            binding.MyPageUserPhoneValid.setTextColor(-65535)
            binding.MyPageUserPhoneValid.isInvisible = false
            binding.MyPageUserPhoneValid.setText(R.string.must_insert)
        } else {
            if (userPw.length >= 6) {
                binding.MyPageUserPhoneNumberEditText.setTextColor(R.color.black.toInt())
                binding.MyPageUserPhoneValid.isInvisible = true
            } else {
                binding.MyPageUserPhoneValid.setTextColor(-65536)
                binding.MyPageUserPhoneValid.setText(R.string.not_enough_num)
                binding.MyPageUserPhoneValid.isInvisible = false
            }
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}