package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantinoutBinding

class TenantInOutVillaActivity : AppCompatActivity() {

    val binding: ActivityTenantinoutBinding by lazy { ActivityTenantinoutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val ToTenantList = Intent(this, TenantListActivity::class.java)
        startActivity(ToTenantList)
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.TenantInOutToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val ToTenantList = Intent(this, TenantListActivity::class.java)
        startActivity(ToTenantList)
//
//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
        return true

//        return super.onOptionsItemSelected(item)
    }

}