package fastcampus.aop.part2.mgr_villa

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainLogo: TextView by lazy{
        findViewById(R.id.MainText)
    }


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
//        setContentView(R.layout.activity_main)





//        this.supportActionBar?.hide()

//        Log.d("onCreate", "-----> animation start")
        var shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        mainLogo.startAnimation(shake)


        binding.signUp.setOnClickListener {
            val choiceUserTypeIntent = Intent(this, ChoiceMgrTenantActivity::class.java)
            startActivity(choiceUserTypeIntent)
        }

        binding.Login.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.searchId.setOnClickListener {
            val searchIdIntent = Intent(this, SearchIdActivity::class.java)
            startActivity(searchIdIntent)
        }

        binding.searchPw.setOnClickListener {
            val searchPwIntent = Intent(this, SearchPwActivity::class.java)
            startActivity(searchPwIntent)
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}