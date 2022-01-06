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



    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.MgrCostDivToolbar)
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

    private fun initButtonSetOnClick() {
        binding.MgrCostButton.setOnClickListener {

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