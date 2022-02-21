package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.adapter.TenantRequestAdapter
import fastcampus.aop.part2.mgr_villa.customdialog.RequestDialog
import fastcampus.aop.part2.mgr_villa.customdialog.mgrAddAccountDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantrequestlistBinding
import fastcampus.aop.part2.mgr_villa.model.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TenantListForRequestActivity: AppCompatActivity() {

    private val binding:ActivityTenantrequestlistBinding by lazy { ActivityTenantrequestlistBinding.inflate(layoutInflater)}

    val firestoreDB = Firebase.firestore

    private var requestAddress:String =""

    private var TenantRequestListItems = arrayListOf<VillaTenant>()                   // 리싸이클러 뷰 아이템
    private val TenantRequestListAdapter = TenantRequestAdapter(TenantRequestListItems)            // 리싸이클러 뷰 어댑터

//
//    private var isNoticeFabOpen = false

    private var naverCheck : String = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(intent.hasExtra("N")){
            naverCheck = intent.getStringExtra("N").toString()
        }

        if (intent.hasExtra("requestAddress")){
            requestAddress = intent.getStringExtra("requestAddress").toString()
            MyApplication.prefs.setString("requestAddress", requestAddress)
        }

        binding.rvRequestTenants.adapter = TenantRequestListAdapter

        initToolBar()
        initTenantRooms()
//        TenantRequestListAdapter.notifyDataSetChanged()
//        initAddTenantRoom()

//
//        initNoticeFabButtons()
//        addItemsNotices()



        val RequestDialog = RequestDialog(this)

        RequestDialog.setOnClickListener(object : RequestDialog.OnDialogClickListener{
            override fun onClicked(context: Context,requestResult: String, roomId: String) {
                if (!requestResult.isEmpty()){
                    firestoreDB.collection("VillaTenant").document(roomId)
                        .update(mapOf(
                            "roomId" to roomId
                            ,"tenantEmail" to MyApplication.prefs.getString("email","")
                            ,"tenantStatus" to "Request"
                        ))

                        showToast("전입을 요청하였습니다. 관리자 확인이 필요합니다.")

                        if (naverCheck.isNullOrEmpty()){
                            val toLogin = Intent(context, LoginActivity::class.java )
                            startActivity(toLogin)
                        } else {
                            val toMain = Intent(context, MainActivity::class.java )
                            startActivity(toMain)
                        }






//------------------------------------------------------------------------------------
//                    Thread(Runnable {
//                        val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//                        villadb!!.VillaNoticeDao().requestTenant(
//                            MyApplication.prefs.getString("email","")
//                            ,requestAddress
//                            ,roomId
//                        )
//
//                        val villaInfo = villadb?.VillaNoticeDao()?.getTenantInfo(roomId)
//
//                        runOnUiThread {
//                                showToast(villaInfo?.roomNumber.toString() + "로의 전입을 요청하였습니다.")
//                                val toLogin = Intent(context, LoginActivity::class.java )
//                                startActivity(toLogin)
//                        }
//                    }).start()
//------------------------------------------------------------------------------------
                }
            }
        })

        TenantRequestListAdapter.setItemClickListener(object : TenantRequestAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

//                showToast(TenantRequestListItems[position].roomId)

                firestoreDB.collection("VillaTenant")
                    .document(TenantRequestListItems[position].roomId)
//                    .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress",""))
                    .get()
                    .addOnSuccessListener { task ->
                        if(!task["tenantEmail"].toString().equals("")){
                            showToast("이미 거주 중에 있어 입주요청 할 수 없습니다.")
                            return@addOnSuccessListener
                        } else {
                            RequestDialog.showDialog(TenantRequestListItems[position].roomNumber, TenantRequestListItems[position].roomId)
                        }
                    }.addOnFailureListener {
                        showToast("전입요청에 문제가 발생했습니다. 관리자에게 문의 바랍니다.")
                        return@addOnFailureListener
                    }


//---------------------------------------------------------------------------------
//                Thread(Runnable {
//                    val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//                    val requestTenantInfo =  villadb!!.VillaNoticeDao().getTenantInfo(TenantRequestListItems[position].roomId.toString().toLong())
//
//                    runOnUiThread {
//                        if (!requestTenantInfo?.tenantEmail.isNullOrEmpty()){
//                            showToast("이미 거주 중에 있어 입주요청 할 수 없습니다.")
//                        } else {
//                            RequestDialog.showDialog(requestTenantInfo!!.roomNumber, TenantRequestListItems[position].roomId.toString().toLong())
//                        }
//                    }
//                }).start()
// ---------------------------------------------------------------------------------
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toAddressSearch = Intent(this, AddressSearchForTenantActivity::class.java)
        startActivity(toAddressSearch)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initTenantRooms(){
        CoroutineScope(Dispatchers.Main).launch {
            firestoreDB?.collection("VillaTenant")
                .whereEqualTo("villaAddr", requestAddress).limit(20)
                .orderBy("roomNumber", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, e ->
                    TenantRequestListItems.clear()

                    if (e != null){
//                    showToast(e.message.toString())
                        Log.d("VillaTenant/TenantRequestAdapter------------------>", e.message.toString())
                        return@addSnapshotListener
                    }

                    for (snapshot in querySnapshot!!.documents){
                        val item = snapshot.toObject(VillaTenant::class.java)
                        item!!.roomId = snapshot.id
                        TenantRequestListItems.add(item!!)

                    }

                }
            TenantRequestListAdapter.notifyDataSetChanged()
        }

        //-----------------------------------------------------------------------------------------------
//        TenantRequestListItems.clear()
//
//        val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//        Thread(Runnable {
//
//            val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(requestAddress.trim())
//
//            for(VillaTenant in tenantRooms) {
//                // 결과를 리싸이클러 뷰에 추가
//                var item = TenantRequestLayout(
//                    VillaTenant.roomId
//                    ,VillaTenant.roomNumber
//                    ,VillaTenant.tenantContractDate.toString()
//                    ,VillaTenant.tenantLeaveDate.toString()
//                )
//                TenantRequestListItems.add(item)
//            }
//
//            runOnUiThread {
//                TenantRequestListAdapter.notifyDataSetChanged()
//            }
//        }).start()
        //-----------------------------------------------------------------------------------------------
    }
//
//    private fun initAddTenantRoom(){
//
//        binding.AddTenantButton.setOnClickListener {
//
//            val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//            if(binding.tenantRoomNumberEditText.text.isNullOrEmpty()){
//                showToast("호수는 반드시 입력해야 합니다.")
//                return@setOnClickListener
//            }
//
//            var checkTenantCount: Int? = 0
//
//            Thread(Runnable {
//                    checkTenantCount = villadb?.VillaNoticeDao()?.checkTenantCount(
//                        MyApplication.prefs.getString("villaAddress","").trim()
//                    )
//                runOnUiThread{
//
//                }
//            }).start()
//
////            showToast(TenantRoomListItems.count().toString())
////            showToast(checkTenantCount.toString())
//
//            // 스레드 슬립 필요!!
//            Thread.sleep(500)
//
//            if(checkTenantCount == TenantRequestListItems.count()){
//                showToast("최대 등록호수에 도달했습니다.")
//                return@setOnClickListener
//            }
//
//
//
//            TenantRequestListItems.clear()
//
//            Thread(Runnable {
//                villadb?.VillaNoticeDao()?.villaTenantInsert(
//                    VillaTenant(
//                        null,
//                        binding.tenantRoomNumberEditText.text.toString().trim(),
//                        "",
//                        "",
//                        "",
//                        "",
//                         MyApplication.prefs.getString("villaAddress","").trim()
//                    )
//                )
//
//                val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(MyApplication.prefs.getString("villaAddress","").trim())
//
//                for(VillaTenant in tenantRooms) {
//                    // 결과를 리싸이클러 뷰에 추가
//                    var item = TenantRequestLayout(
//                        VillaTenant.roomId
//                        ,VillaTenant.roomNumber
//                        ,VillaTenant.tenantContractDate.toString()
//                        ,VillaTenant.tenantLeaveDate.toString()
//                    )
//                    TenantRequestListItems.add(item)
//                }
//                runOnUiThread {
//                    TenantRequestListAdapter.notifyDataSetChanged()
//                    binding.tenantRoomNumberEditText.setText("")
//                }
//            }).start()
//        }
//    }

//
//    private fun addItemsNotices(){
//
//         NoticeListItems.clear()
//
//        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//        Thread(Runnable {
//
//            val listNotice = villaNoticedb!!.VillaNoticeDao().getAllNotice(MyApplication.prefs.getString("villaAddress",""))
//
//            for(Notice in listNotice){
//                // 결과를 리싸이클러 뷰에 추가
//                var item = NoticeLayout(Notice.noticeNo
//                    ,Notice.noticeTitle
//                    ,Notice.noticeDatetime
//                )
//                NoticeListItems.add(item)
//            }
//
//            runOnUiThread {
//                NoticeListAdapter.notifyDataSetChanged()
//            }
//        }).start()
//
//    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.TenantRequestListToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val toAddressSearch = Intent(this, AddressSearchForTenantActivity::class.java)
        startActivity(toAddressSearch)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}