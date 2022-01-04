package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import fastcampus.aop.part2.mgr_villa.adapter.PagerHomeAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaInfoHelper
import fastcampus.aop.part2.mgr_villa.database.VillaUsersHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityHomeBinding
import fastcampus.aop.part2.mgr_villa.fragment.AddFragment
import fastcampus.aop.part2.mgr_villa.fragment.MgrHomeFragment
import fastcampus.aop.part2.mgr_villa.fragment.OkFragment
import fastcampus.aop.part2.mgr_villa.fragment.VillaTenantFragment

class VillaHomeActivity : AppCompatActivity() {

//    private lateinit var userHelper: VillaUsersHelper

//    private var userList = mutableListOf<VillaUsers>()

    private val binding: ActivityHomeBinding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    private var userEmail: String = ""
    private var detailAddress: String =""
    private var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        if (intent.hasExtra("email")) {
            userEmail = intent.getStringExtra("email").toString()
            initLoginData(userEmail)
        }

        initHomeFragment()

//        setBindingFragment()

    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.HomeToolbar)
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

    // initFragment
    private fun initHomeFragment(){
//
//        val bundle = Bundle()
//        bundle.putString("detailAddress",detailAddress)
//        bundle.putString("address",address)
//
//        val mgrFrag = MgrHomeFragment()
//        mgrFrag.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.recycleViewConstraint, MgrHomeFragment())
        transaction.commit()
    }

    // 로그인 정보 가져오기
    private fun initLoginData(email: String) {

        val userdb = VillaUsersHelper.getInstance(applicationContext)
        val villadb = VillaInfoHelper.getInstance(applicationContext)

        Thread(Runnable {
            val user = userdb?.VillaUserDao()?.getUser(
                userEmail
            )

            val villaInfo = villadb!!.VillaInfoDao().getVillaInfo(userEmail)
            detailAddress = villaInfo.roomNumber
            address = villaInfo.villaAddress

            runOnUiThread {
                if (user == null || villaInfo.villaAddress.isNullOrEmpty()) {
                    showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
                    return@runOnUiThread
                } else {
                    binding.hUserName.setText(user.userName)

                }
            }
        }).start()


    }

    fun getAddress():String{
        return address
    }

    fun getDetailAddress():String{
        return detailAddress
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}