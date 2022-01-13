package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import fastcampus.aop.part2.mgr_villa.adapter.AccountsAdapter
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.adapter.TenantAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityAccountlistBinding
import fastcampus.aop.part2.mgr_villa.model.AccountLayout
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.recycleview_accounts.view.*
import kotlinx.android.synthetic.main.recycleview_tenants.view.*

class VillaMgrAccountsListActivity : AppCompatActivity() {


    private val binding: ActivityAccountlistBinding by lazy {
        ActivityAccountlistBinding.inflate(
            layoutInflater
        )
    }

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


        // Room 리스트 수정,삭제 클릭
        AccountListAdapter.setSlideButtonClickListener(object :
            AccountsAdapter.OnSlideButtonClickListener {
            override fun onSlideButtonClick(v: View, imageView: ImageView, position: Int) {

                val mgrCheck = MyApplication.prefs.getString("userType", "")
                val villadb = VillaNoticeHelper.getInstance(applicationContext)

                if (mgrCheck.equals("MGR")
                    && !mgrCheck.equals("")
                ) {
                    when (imageView) {
                        imageView.AccountUpdate -> {
                            Thread(Runnable {
                                runOnUiThread {
                                    val AccountActivity = Intent(v.context, AddAccountActivity::class.java)
                                    AccountActivity.putExtra("accountId",AccountListItems[position].accountId.toString().toLong())
                                    startActivity(AccountActivity)
                                }
                            }).start()
                        }
                        imageView.AccountDelete -> {
                            Thread(Runnable {
                                villadb!!.VillaNoticeDao().deleteAccount(
                                    MyApplication.prefs.getString("villaAddress", "").trim(),
                                    AccountListItems[position].accountId.toString().toLong()
                                )
                                runOnUiThread {
                                    initAccountsItems()
                                }
                            }).start()
//                            showToast(TenantRoomListItems[position].tenantRoomId.toString())
                        }

                    }
                }

            }

        })


    }

    private fun initAccountsItems() {

        AccountListItems.clear()

        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {

            val listAccounts = villaNoticedb!!.VillaNoticeDao()
                .getAllVillaAccounts(MyApplication.prefs.getString("villaAddress", ""))

            val AccountCount = villaNoticedb!!.VillaNoticeDao()
                .isAccount(MyApplication.prefs.getString("villaAddress", ""))

            for (Account in listAccounts) {
                // 결과를 리싸이클러 뷰에 추가
                val item = AccountLayout(
                    Account.accountId,
                    Account.bankName,
                    Account.accountHolder,
                    Account.accountNumber
                )
                AccountListItems.add(item)


            }

            runOnUiThread {
                AccountListAdapter.notifyDataSetChanged()

                binding.mgrAccountsNull.isVisible = AccountCount <= 0
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}