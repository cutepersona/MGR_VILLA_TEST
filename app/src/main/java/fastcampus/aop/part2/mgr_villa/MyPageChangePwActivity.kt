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
import fastcampus.aop.part2.mgr_villa.databinding.ActivityChangepwBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityLoginBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMypagechangepwBinding

class MyPageChangePwActivity : AppCompatActivity() {

    private val binding: ActivityMypagechangepwBinding by lazy { ActivityMypagechangepwBinding.inflate(layoutInflater) }
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email").toString()
        } 


        initToolBar()

        initPasswordTextCheck()
        initPasswordTextCheck1()
        initChangePw()
    }

    private fun initChangePw() {
        binding.DoChangePw.setOnClickListener {
            if (changPassWord()) {
                showToast("비밀번호가 변경되었습니다.")
                val toMyPage = Intent(this, MyPageActivity::class.java)
                startActivity(toMyPage)
            } else {
                showToast("비밀번호가 변경되지 않았습니다.")
            }

        }
    }

    private fun changPassWord(): Boolean {
        return if (!email.isEmpty()) {
            val userdb = VillaNoticeHelper.getInstance(applicationContext)

            Thread(Runnable {
                userdb!!.VillaNoticeDao().updatePW(email, binding.MyPageUserPasswordEditText1.text.toString())
            }).start()
            true
        } else {
            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toMyPage = Intent(this, MyPageActivity::class.java)
        startActivity(toMyPage)

    }

    // 툴바 초기화
    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.MyPageChangePwToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val toMyPage = Intent(this, MyPageActivity::class.java)
        startActivity(toMyPage)

        return true
    }

    // 변경 비밀번호 확인
    private fun initPasswordTextCheck() {
        binding.MyPageUserPasswordEditText1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPW()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    // 변경 비밀번호 확인
    private fun initPasswordTextCheck1() {
        binding.MyPageUserPasswordEditText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPWrewind()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun checkPW() {

        var userPw = binding.MyPageUserPasswordEditText1.text.toString().trim()
//        val p = Pattern.compile(nameValidation)

        if (userPw.isNullOrEmpty()) {
            binding.MyPageUserPWValid1.setTextColor(-65535)
            binding.MyPageUserPWValid1.isInvisible = false
            binding.MyPageUserPWValid1.setText(R.string.must_insert)
        } else {
            if (userPw.length >= 6) {
                binding.MyPageUserPasswordEditText1.setTextColor(R.color.black.toInt())
                binding.MyPageUserPWValid1.isInvisible = true
            } else {
                binding.MyPageUserPWValid1.setTextColor(-65536)
                binding.MyPageUserPWValid1.setText(R.string.not_enough_num)
                binding.MyPageUserPWValid1.isInvisible = false
            }
        }
    }

    // 변경 비밀번호 재확인
    private fun checkPWrewind() {

        var userPw = binding.MyPageUserPasswordEditText2.text.toString().trim()

        if (userPw.isNullOrEmpty()) {
            binding.MyPageUserPWValid3.setTextColor(-65535)
            binding.MyPageUserPWValid3.isInvisible = false
            binding.MyPageUserPWValid3.setText(R.string.must_insert)
        } else {
            if (!binding.MyPageUserPasswordEditText1.getText().toString()
                    .equals(binding.MyPageUserPasswordEditText2.getText().toString())
            ) {
                binding.MyPageUserPWValid3.setTextColor(-65536)
                binding.MyPageUserPWValid3.setText(R.string.not_match_password)
                binding.MyPageUserPWValid3.isInvisible = false
            } else if (userPw.length >= 6
                && binding.MyPageUserPasswordEditText1.getText().toString()
                    .equals(binding.MyPageUserPasswordEditText2.getText().toString())
            ) {
                binding.MyPageUserPasswordEditText2.setTextColor(R.color.black.toInt())
                binding.MyPageUserPWValid3.isInvisible = true
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}