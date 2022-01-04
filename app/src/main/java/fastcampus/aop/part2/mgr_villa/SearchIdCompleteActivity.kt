package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fastcampus.aop.part2.mgr_villa.database.VillaUsersHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivitySearchidcompleteBinding
import fastcampus.aop.part2.mgr_villa.model.VillaUsers
import java.util.*

class SearchIdCompleteActivity: AppCompatActivity() {

    private lateinit var userHelper: VillaUsersHelper

    private val binding:ActivitySearchidcompleteBinding by lazy { ActivitySearchidcompleteBinding.inflate(layoutInflater)}

    private var userPhone: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar = binding.communityToolbar
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)

        val userdb = VillaUsersHelper.getInstance(applicationContext)

        userPhone = intent.getStringExtra("phone").toString()

        val user = userdb?.VillaUserDao()?.getUserId(userPhone)

        binding.findMyId.text = user?.let { stringMasking(it) }

        user?.let { searchIdDoLoginOnClick(it) }
    }

    // 메일주소 일부 마스킹 처리
    private fun stringMasking(email: VillaUsers) : String {

        var arr = email?.mailAddress?.split("@")

        var length = arr?.get(0).toString().length

        val firstSize = arr?.get(0).toString().length / 2
        val first = arr?.get(0).toString().substring(0, firstSize)
        val last = "*".repeat(length - firstSize)

        Log.d("length------------------->", "${first+last}@${arr?.get(1).toString()}")

        return "${first+last}@${arr?.get(1).toString()}"
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

    // 로그인 화면 이동
    private fun searchIdDoLoginOnClick(user: VillaUsers){
        binding.searchIdLogin.setOnClickListener {

            val searchIdDoLoginIntent = Intent(this, LoginActivity::class.java)
            startActivity(searchIdDoLoginIntent)
        }
    }






}