package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.customdialog.LogOutDialog
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMyinfoBinding
import kotlinx.android.synthetic.main.activity_myinfo.*

class MyInfoActivity : AppCompatActivity() {

    private val binding : ActivityMyinfoBinding by lazy { ActivityMyinfoBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        initHomeBottomNavigationBar()

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