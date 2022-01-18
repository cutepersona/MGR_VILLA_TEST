package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.databinding.ActivityChoiceCostAccountBinding

class MgrCostDivActivity: AppCompatActivity() {

    private val binding: ActivityChoiceCostAccountBinding by lazy { ActivityChoiceCostAccountBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initButtonSetOnClick()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val CostDivToHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(CostDivToHome)
    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.MgrCostDivToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val CostDivToHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(CostDivToHome)

        return true
//
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

    private fun initButtonSetOnClick() {
        binding.MgrCostButton.setOnClickListener {
            // todo 호수 별로 관리비 등록 해야함.
            val TenantCostListActivity = Intent(this, TenantCostListActivity::class.java)
            startActivity(TenantCostListActivity)
        }

        binding.MgrAccountButton.setOnClickListener {
            val MgrAccountsListActivity = Intent(this, VillaMgrAccountsListActivity::class.java)
            startActivity(MgrAccountsListActivity)
        }

        binding.MgrStandardCostButton.setOnClickListener {
            val StandardCostActivity = Intent(this, MgrStandardCostActivity::class.java)
            startActivity(StandardCostActivity)
        }

    }

}