package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
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
import fastcampus.aop.part2.mgr_villa.databinding.ActivitySearchidBinding
import java.util.concurrent.TimeUnit

class SearchIdActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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


    private val binding:ActivitySearchidBinding by lazy { ActivitySearchidBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        auth = Firebase.auth
        auth.setLanguageCode("kr")

        initPhoneNumberTextCheck()

        binding.searchIdDone.setOnClickListener {

            // todo 임시 주석 처리  test후 해제
            var authNumber = binding.userAuthCompleteEditText.text.toString()
            val phoneCredential =
                PhoneAuthProvider.getCredential(
                    storedVerificationId,
                    authNumber
                )

            signInWithPhoneAuthCredential(phoneCredential)

        }

        phoneSnsAuthCheck()


    }

    private fun initToolBar(){
        val toolbar = binding.searchIdToolbar
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
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

    // 전화번호 양식 체크
    private fun checkPhoneNumber() {

        var user_Phone = binding.userPhoneNumberEditText.text.toString().trim()

        if (user_Phone.isNullOrEmpty()) {
            binding.userPhoneNumberValid1.setTextColor(-65535)
            binding.userPhoneNumberValid1.isInvisible = false
            binding.userPhoneNumberValid1.setText(R.string.must_insert)
        } else {
            binding.userPhoneNumberEditText.setTextColor(R.color.black.toInt())
            binding.userPhoneNumberValid1.isInvisible = true
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    showToast("인증완료 되었습니다.")
//                    val user = task.result?.user
                    this.authCheckFlag = true

                        val SearchCompleteIntent = Intent(this, SearchIdCompleteActivity::class.java)
//                        Log.d("phone", binding.userPhoneNumberEditText.text.toString().trim())
                        SearchCompleteIntent.putExtra("phone",
                            binding.userPhoneNumberEditText.text.toString().trim()
                        )
                        startActivity(SearchCompleteIntent)

//                    val SearchCompleteIntent = Intent(this, SearchIdCompleteActivity::class.java)
//                    startActivity(SearchCompleteIntent)
//                    Log.d(ContentValues.TAG, "${user?.phoneNumber.toString()}")

                } else {
                    // Sign in failed, display a message and update the UI
                    this.authCheckFlag = false
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun phoneSnsAuthCheck() {
        binding.phoneSnsAuth.setOnClickListener {
            var userPhone = binding.userPhoneNumberEditText.text.trim().toString()

            if (userPhone.isNullOrEmpty()) {
                showToast("핸드폰 번호를 입력해 주세요.")
                return@setOnClickListener
            }

//            firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(userPhone, "123456")

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+82" + userPhone)       // Phone number to verify
                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this@SearchIdActivity)                 // Activity (for callback binding)
                .setCallbacks(collbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}