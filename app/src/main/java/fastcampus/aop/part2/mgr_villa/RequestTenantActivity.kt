package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.databinding.ActivityRequesttenantlistBinding

class RequestTenantActivity : AppCompatActivity() {

    private val binding : ActivityRequesttenantlistBinding by lazy {ActivityRequesttenantlistBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val ToTenantInOut = Intent(this, TenantInOutVillaActivity::class.java)
        startActivity(ToTenantInOut)
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.RequestTenantListToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val ToTenantInOut = Intent(this, TenantInOutVillaActivity::class.java)
        startActivity(ToTenantInOut)
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