package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.adapter.AccountsAdapter
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.adapter.TenantAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityAccountlistBinding
import fastcampus.aop.part2.mgr_villa.model.AccountLayout
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.model.VillaAccount
import fastcampus.aop.part2.mgr_villa.model.VillaTenant
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_accountlist.*
import kotlinx.android.synthetic.main.recycleview_accounts.view.*
import kotlinx.android.synthetic.main.recycleview_tenants.view.*

class VillaMgrAccountsListActivity : AppCompatActivity() {


    private val binding: ActivityAccountlistBinding by lazy {
        ActivityAccountlistBinding.inflate(
            layoutInflater
        )
    }

    val firestoreDB = Firebase.firestore

    private var AccountListItems = arrayListOf<VillaAccount>()                   // 리싸이클러 뷰 아이템
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

//                val villadb = VillaNoticeHelper.getInstance(applicationContext)
//                val mgrCheck = MyApplication.prefs.getString("userType", "")

//                if (mgrCheck.equals("MGR")
//                    && !mgrCheck.equals("")
//                ) {
                    when (imageView) {
                        imageView.AccountUpdate -> {


                            val AccountActivity = Intent(v.context, AddAccountActivity::class.java)
                            AccountActivity.putExtra("accountId",AccountListItems[position].accountId)
                            startActivity(AccountActivity)

                            //-----------------------------------------------------------------------------
//                            Thread(Runnable {
//                                runOnUiThread {
//                                    val AccountActivity = Intent(v.context, AddAccountActivity::class.java)
//                                    AccountActivity.putExtra("accountId",AccountListItems[position].accountId.toString().toLong())
//                                    startActivity(AccountActivity)
//                                }
//                            }).start()
                            //-----------------------------------------------------------------------------
                        }
                        imageView.AccountDelete -> {

                            firestoreDB.collection("VillaAccount")
                                .document(AccountListItems[position].accountId)
                                .get()
                                .addOnSuccessListener { task ->
                                    if(task["favorite"].toString().equals("favorite")){
                                        showToast("주계좌는 삭제할 수 없습니다.")
                                        return@addOnSuccessListener
                                    } else {
                                        firestoreDB.collection("VillaAccount")
                                            .document(AccountListItems[position].accountId)
                                            .delete()
                                    }
                                }

                            //-----------------------------------------------------------------------------
//                            Thread(Runnable {
//                                villadb!!.VillaNoticeDao().deleteAccount(
//                                    MyApplication.prefs.getString("villaAddress", "").trim(),
//                                    AccountListItems[position].accountId.toString().toLong()
//                                )
//                                runOnUiThread {
//                                    initAccountsItems()
//                                }
//                            }).start()
                            //-----------------------------------------------------------------------------

                        }

                    }
//                }

            }

        })

        AccountListAdapter.setItemClickListener( object :AccountsAdapter.OnItemClickListener{
            override fun onClick(v: View, imageView: ImageView, position: Int) {


                firestoreDB.collection("VillaAccount")
                    .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
                    .get()
                    .addOnSuccessListener { results ->
                        for(i in results){
                            firestoreDB.collection("VillaAccount")
                                .document(i.id)
                                .update(mapOf(
                                    "favorite" to ""
                                ))
                        }
                        firestoreDB.collection("VillaAccount")
                            .document(AccountListItems[position].accountId)
                            .update(mapOf(
                                "favorite" to "favorite"
                            ))
                    }

                AccountListAdapter.notifyDataSetChanged()
//---------------------------------------------------------------------
//                val villadb = VillaNoticeHelper.getInstance(applicationContext)
//                Thread(Runnable {
//                    villadb!!.VillaNoticeDao().updateNoneFavorite(
//                        "",
//                        MyApplication.prefs.getString("villaAddress", "").trim()
//                    )
//
//                    villadb!!.VillaNoticeDao().updateFavorite(
//                        "favorite",
//                        MyApplication.prefs.getString("villaAddress", "").trim(),
//                        AccountListItems[position].accountId.toString().toLong()
//                    )
//                    runOnUiThread {
//                        initAccountsItems()
////                        imageView.setImageResource(R.drawable.ic_circle_fav)
//
//                    }
//                }).start()
//---------------------------------------------------------------------
            }
        })


    }


    override fun onBackPressed() {
        super.onBackPressed()
        val accountListToCostDiv = Intent(this, MgrCostDivActivity::class.java)
        startActivity(accountListToCostDiv)
    }

    private fun initAccountsItems() {

        firestoreDB?.collection("VillaAccount")
            .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
            .orderBy("bankName", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                AccountListItems.clear()

                if (e != null){
//                    showToast(e.message.toString())
                    Log.d("VillaAccount/AccountsAdapter------------------>", e.message.toString())
                    return@addSnapshotListener
                }

                for (snapshot in querySnapshot!!.documents){
                    val item = snapshot.toObject(VillaAccount::class.java)
                    item!!.accountId = snapshot.id
                    AccountListItems.add(item!!)
                }

                binding.mgrAccountsNull.isVisible = AccountListItems.count() <= 0

                AccountListAdapter.notifyDataSetChanged()
            }

//-------------------------------------------------------------------------------------------------------
//        AccountListItems.clear()
//
//        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//        Thread(Runnable {
//
//            val listAccounts = villaNoticedb!!.VillaNoticeDao()
//                .getAllVillaAccounts(MyApplication.prefs.getString("villaAddress", ""))
//
//            val AccountCount = villaNoticedb!!.VillaNoticeDao()
//                .isAccount(MyApplication.prefs.getString("villaAddress", ""))
//
//            for (Account in listAccounts) {
//                // 결과를 리싸이클러 뷰에 추가
//                val item = AccountLayout(
//                    Account.accountId,
//                    Account.bankName,
//                    Account.accountHolder,
//                    Account.accountNumber,
//                    Account.favorite
//                )
//                AccountListItems.add(item)
//
//
//            }
//
//            runOnUiThread {
//                AccountListAdapter.notifyDataSetChanged()
//
//                binding.mgrAccountsNull.isVisible = AccountCount <= 0
//            }
//        }).start()
//-------------------------------------------------------------------------------------------------------

    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.AccountsListToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val accountListToCostDiv = Intent(this, MgrCostDivActivity::class.java)
        startActivity(accountListToCostDiv)
//
//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
        return true

        return super.onOptionsItemSelected(item)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}