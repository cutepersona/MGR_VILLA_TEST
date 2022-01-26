package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import fastcampus.aop.part2.mgr_villa.customdialog.LogOutDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMyinfoBinding
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_myinfo.*

class MyInfoActivity : AppCompatActivity() {

    private val binding : ActivityMyinfoBinding by lazy { ActivityMyinfoBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Glide.with(this).load(R.raw.chunsic).into(binding.MyInfoImage)

        initToolBar()
        initMyInfo()
        initHomeBottomNavigationBar()
        initPrivacyPolicy()

        binding.MyInfoArea.setOnClickListener {
            val toMyPage = Intent(this, MyPageActivity::class.java)
            startActivity(toMyPage)
        }

    }

    // 개인정보 보호정책
    private fun initPrivacyPolicy() {
        binding.MyInfoPrivacy.setOnClickListener {
            val toPirvacyPolicy = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(toPirvacyPolicy)
        }
    }

    // 내 기본정보 가져오기
    private fun initMyInfo() {
        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
        Thread(Runnable {
            val userInfo = villaNoticedb!!.VillaNoticeDao().getUser(
                    MyApplication.prefs.getString("email","").trim()
            )

            runOnUiThread {
                binding.InfoUserName.setText(userInfo.userName)
            }
        }).start()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val toHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(toHome)
    }

    // 하단 네비게이션 바
    private fun initHomeBottomNavigationBar() {
        val toHomeActivity = Intent(this, VillaHomeActivity::class.java)

        bnv_MyInfo.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nv_Home ->
                    startActivity(toHomeActivity)
//                R.id.nv_All ->
//                    startActivity(toHomeActivity)
            }

            true
        }


    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.MyInfoToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val toHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(toHome)


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

}