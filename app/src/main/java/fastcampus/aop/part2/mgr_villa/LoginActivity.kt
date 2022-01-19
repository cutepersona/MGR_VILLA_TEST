package fastcampus.aop.part2.mgr_villa

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
import androidx.databinding.DataBindingUtil
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
        binding.DoLoginButton.setOnClickListener {

//            val sharedPreferences = getSharedPreferences("villauser", MODE_PRIVATE)
//            val userEditor = sharedPreferences.edit()
//            userEditor.putString("email", binding.userEmailEditText.text.toString().trim())
//            userEditor.putString("pw", binding.userPasswordEditText1.text.toString().trim())
//            userEditor.apply()

            MyApplication.prefs.setString("email", binding.userEmailEditText.text.toString().trim())
            MyApplication.prefs.setString("pw", binding.userPasswordEditText1.text.toString().trim())

            val userdb = VillaNoticeHelper.getInstance(applicationContext)
//            val villadb = VillaInfoHelper.getInstance(applicationContext)

            Thread(Runnable {
                val user = userdb?.VillaNoticeDao()?.userLogin(
                    binding.userEmailEditText.text.toString().trim(),
                    binding.userPasswordEditText1.text.toString().trim()
                )

//                villadb!!.VillaInfoDao().deleteAll()

                val villaInfo = userdb!!.VillaNoticeDao().isVilla(binding.userEmailEditText.text.toString())

                runOnUiThread {
                    if (user == null) {
                        showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
                    }
                    else {
                        MyApplication.prefs.setString("userType",user.userType)

                        if (villaInfo >= 1) {
                            // TODO 홈화면으로 이동해야함.
                            if (user.userType.equals("MGR")){
                                val mgrHomeActivity =
                                    Intent(this, VillaHomeActivity::class.java)
                                mgrHomeActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
                                startActivity(mgrHomeActivity)
                            }


                        }else{
                            if (villaInfo < 1 && user.userType.equals("MGR")) {
                                val addrSearchActivity =
                                    Intent(this, AddressSearchActivity::class.java)
                                addrSearchActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
                                startActivity(addrSearchActivity)
                            }
                            if (user.userType.equals("TENANT")){


                                val addrSearchTenantActivity =
                                    Intent(this, AddressSearchForTenantActivity::class.java)
                                addrSearchTenantActivity.putExtra("email", binding.userEmailEditText.text.toString().trim())
                                startActivity(addrSearchTenantActivity)
                            }

                        }

                    }
                }
            }).start()


        }
    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.LoginToolbar)
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