package fastcampus.aop.part2.mgr_villa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.adapter.TenantRequestAdapter
import fastcampus.aop.part2.mgr_villa.customdialog.RequestDialog
import fastcampus.aop.part2.mgr_villa.customdialog.mgrAddAccountDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantrequestlistBinding
import fastcampus.aop.part2.mgr_villa.model.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class TenantListForRequestActivity: AppCompatActivity() {

    private val binding:ActivityTenantrequestlistBinding by lazy { ActivityTenantrequestlistBinding.inflate(layoutInflater)}

    private var TenantRequestListItems = arrayListOf<TenantRequestLayout>()                   // 리싸이클러 뷰 아이템
    private val TenantRequestListAdapter = TenantRequestAdapter(TenantRequestListItems)            // 리싸이클러 뷰 어댑터

    private var requestAddress:String =""
//
//    private var isNoticeFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvRequestTenants.adapter = TenantRequestListAdapter

        if (intent.hasExtra("requestAddress")){
            requestAddress = intent.getStringExtra("requestAddress").toString()
        }

        initToolBar()
        initTenantRooms()
//        initAddTenantRoom()

//
//        initNoticeFabButtons()
//        addItemsNotices()

        val RequestDialog = RequestDialog(this)

        RequestDialog.setOnClickListener(object : RequestDialog.OnDialogClickListener{
            override fun onClicked(context: Context,requestResult: String, roomId: Long) {
                if (!requestResult.isEmpty()){
                    Thread(Runnable {
                        val villadb = VillaNoticeHelper.getInstance(applicationContext)

                        villadb!!.VillaNoticeDao().requestTenant(
                            MyApplication.prefs.getString("email","")
                            ,requestAddress
                            ,roomId
                        )

                        val villaInfo = villadb?.VillaNoticeDao()?.getTenantInfo(roomId)

                        runOnUiThread {
                                showToast(villaInfo?.roomNumber.toString() + "로의 전입을 요청하였습니다.")
                                val toLogin = Intent(context, LoginActivity::class.java )
                                startActivity(toLogin)
                        }
                    }).start()
                }
            }
        })

        TenantRequestListAdapter.setItemClickListener(object : TenantRequestAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                Thread(Runnable {
                    val villadb = VillaNoticeHelper.getInstance(applicationContext)

                    val requestTenantInfo =  villadb!!.VillaNoticeDao().getTenantInfo(TenantRequestListItems[position].tenantRoomId.toString().toLong())

                    runOnUiThread {
                        if (!requestTenantInfo?.tenantEmail.isNullOrEmpty()){
                            showToast("이미 거주 중에 있어 입주요청 할 수 없습니다.")
                        } else {
                            RequestDialog.showDialog(requestTenantInfo!!.roomNumber, TenantRequestListItems[position].tenantRoomId.toString().toLong())
                        }
                    }
                }).start()
            }
        })





    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toAddressSearch = Intent(this, AddressSearchForTenantActivity::class.java)
        startActivity(toAddressSearch)

    }

    private fun initTenantRooms(){
        TenantRequestListItems.clear()

        val villadb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {

            val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(requestAddress.trim())

            for(VillaTenant in tenantRooms) {
                // 결과를 리싸이클러 뷰에 추가
                var item = TenantRequestLayout(
                    VillaTenant.roomId
                    ,VillaTenant.roomNumber
                    ,VillaTenant.tenantContractDate.toString()
                    ,VillaTenant.tenantLeaveDate.toString()
                )
                TenantRequestListItems.add(item)
            }

            runOnUiThread {
                TenantRequestListAdapter.notifyDataSetChanged()
            }
        }).start()
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