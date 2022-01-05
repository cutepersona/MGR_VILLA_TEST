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

class ChangePwActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangepwBinding

    private var email: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_changepw)

        if (intent.hasExtra("email")) {

            email = intent.getStringExtra("email").toString()

        } else {
            Log.e("null--------------------------->", "email null")
        }


//        setContentView(binding.root)

        initToolBar()

        initPasswordTextCheck()
        initPasswordTextCheck1()
        initChangePw()
    }

    private fun initChangePw() {
        binding.DoChangePw.setOnClickListener {

            if (changPassWord()) {
                showToast("비밀번호가 변경되었습니다.")
                val LoginIntent = Intent(this, LoginActivity::class.java)
                startActivity(LoginIntent)
            } else {
                showToast("비밀번호가 변경되지 않았습니다.")
            }

        }
    }

    private fun changPassWord(): Boolean {
        return if (!email.isEmpty()) {

            val userdb = VillaNoticeHelper.getInstance(applicationContext)

            userdb!!.VillaNoticeDao().updatePW(email, binding.userPasswordEditText1.text.toString())
            true
        } else {
            false
        }
    }

    // 툴바 초기화
    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.DoChangePwToolbar)
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

    // 변경 비밀번호 확인
    private fun initPasswordTextCheck() {
        binding.userPasswordEditText1.addTextChangedListener(object : TextWatcher {
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
        binding.userPasswordEditText2.addTextChangedListener(object : TextWatcher {
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

        var userPw = binding.userPasswordEditText1.text.toString().trim()
//        val p = Pattern.compile(nameValidation)

        if (userPw.isNullOrEmpty()) {
            binding.userPWValid1.setTextColor(-65535)
            binding.userPWValid1.isInvisible = false
            binding.userPWValid1.setText(R.string.must_insert)
        } else {
            if (userPw.length >= 6) {
                binding.userPasswordEditText1.setTextColor(R.color.black.toInt())
                binding.userPWValid1.isInvisible = true
            } else {
                binding.userPWValid1.setTextColor(-65536)
                binding.userPWValid1.setText(R.string.not_enough_num)
                binding.userPWValid1.isInvisible = false
            }
        }
    }

    // 변경 비밀번호 재확인
    private fun checkPWrewind() {

        var userPw = binding.userPasswordEditText2.text.toString().trim()

        if (userPw.isNullOrEmpty()) {
            binding.userPWValid3.setTextColor(-65535)
            binding.userPWValid3.isInvisible = false
            binding.userPWValid3.setText(R.string.must_insert)
        } else {
            if (!binding.userPasswordEditText1.getText().toString()
                    .equals(binding.userPasswordEditText2.getText().toString())
            ) {
                binding.userPWValid3.setTextColor(-65536)
                binding.userPWValid3.setText(R.string.not_match_password)
                binding.userPWValid3.isInvisible = false
            } else if (userPw.length >= 6
                && binding.userPasswordEditText1.getText().toString()
                    .equals(binding.userPasswordEditText2.getText().toString())
            ) {
                binding.userPasswordEditText2.setTextColor(R.color.black.toInt())
                binding.userPWValid3.isInvisible = true
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}