package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityChoiceCostAccountBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

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

//---------------------------------------------------------------------------------------
//            val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//            Thread(Runnable {
//                val ConstCost = villaNoticedb!!.VillaNoticeDao().isConstCost(
//                    MyApplication.prefs.getString("villaAddress","").trim()
//                )
//
//                val accountCheck = villaNoticedb!!.VillaNoticeDao().isAccount(
//                    MyApplication.prefs.getString("villaAddress","").trim()
//                )
//
//                val favoriteAccountCheck = villaNoticedb!!.VillaNoticeDao().isFavoriteAccount()
//
//                runOnUiThread {
//                    if (ConstCost <= 0) {
//                        showToast("기준관리비가 등록되어 있지 않습니다.")
//                    } else if (accountCheck <= 0) {
//                        showToast("계좌가 등록되어 있지 않습니다.")
//                    } else if (favoriteAccountCheck <= 0) {
//                        showToast("주 계좌가 선택되어 있지 않습니다.")
//                    } else {
//                        val TenantCostListActivity = Intent(this, TenantCostListActivity::class.java)
//                        startActivity(TenantCostListActivity)
//                    }
//
//                }
//            }).start()
//---------------------------------------------------------------------------------------

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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}