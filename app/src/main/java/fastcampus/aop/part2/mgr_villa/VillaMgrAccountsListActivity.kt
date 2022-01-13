package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import fastcampus.aop.part2.mgr_villa.adapter.AccountsAdapter
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityAccountlistBinding
import fastcampus.aop.part2.mgr_villa.model.AccountLayout
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class VillaMgrAccountsListActivity: AppCompatActivity() {


    private val binding: ActivityAccountlistBinding by lazy { ActivityAccountlistBinding.inflate(layoutInflater)}

    private var AccountListItems = arrayListOf<AccountLayout>()                   // 리싸이클러 뷰 아이템
    private val AccountListAdapter = AccountsAdapter(AccountListItems)            // 리싸이클러 뷰 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        binding.rvAccounts.adapter = AccountListAdapter

        initAccountsItems()

        binding.AddAccountButton.setOnClickListener {
            val AddAccountActivity = Intent(this, AddAccountActivity::class.java)
            startActivity(AddAccountActivity)
        }

    }

    private fun initAccountsItems(){

        AccountListItems.clear()

        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {

            val listAccounts = villaNoticedb!!.VillaNoticeDao().getAllVillaAccounts(MyApplication.prefs.getString("villaAddress",""))

            val AccountCount = villaNoticedb!!.VillaNoticeDao().isAccount(MyApplication.prefs.getString("villaAddress", ""))

            for(Account in listAccounts){
                // 결과를 리싸이클러 뷰에 추가
                val item = AccountLayout(
                    Account.accountId
                    ,Account.bankName
                    ,Account.accountHolder
                    ,Account.accountNumber
                )
                AccountListItems.add(item)



            }

            runOnUiThread {
                AccountListAdapter.notifyDataSetChanged()

                if (AccountCount > 0) {
                    binding.mgrAccountsNull.isVisible = false
                } else {
                    binding.mgrAccountsNull.isVisible = true
                }
            }
        }).start()

    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.AccountsListToolbar)
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

}