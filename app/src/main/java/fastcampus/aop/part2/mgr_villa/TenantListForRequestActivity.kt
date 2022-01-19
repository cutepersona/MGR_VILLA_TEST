package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.adapter.TenantAdapter
import fastcampus.aop.part2.mgr_villa.adapter.TenantRequestAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantlistBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantrequestlistBinding
import fastcampus.aop.part2.mgr_villa.model.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_noticelist.*
import kotlinx.android.synthetic.main.recycleview_tenants.*
import kotlinx.android.synthetic.main.recycleview_tenants.view.*

class TenantListForRequestActivity: AppCompatActivity() {

    private val binding:ActivityTenantrequestlistBinding by lazy { ActivityTenantrequestlistBinding.inflate(layoutInflater)}

    private var TenantRequestListItems = arrayListOf<TenantRequestLayout>()                   // 리싸이클러 뷰 아이템
    private val TenantRequestListAdapter = TenantRequestAdapter(TenantRequestListItems)            // 리싸이클러 뷰 어댑터
//
//    private var isNoticeFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvRequestTenants.adapter = TenantRequestListAdapter

        initToolBar()
//        initTenantRooms()
//        initAddTenantRoom()

//
//        initNoticeFabButtons()
//        addItemsNotices()


        // Room 리스트 수정,삭제 클릭
        TenantRequestListAdapter.setSlideButtonClickListener(object : TenantRequestAdapter.OnSlideButtonClickListener{
            override fun onSlideButtonClick(v: View, imageView: ImageView, position: Int) {

                val mgrCheck = MyApplication.prefs.getString("userType","")
                if (mgrCheck.equals("MGR")
                    && !mgrCheck.equals("")){
                    when(imageView) {
                        imageView.tenantUpdate -> {
                            Thread(Runnable {
                                runOnUiThread {
                                    val tenantUpdate = Intent(v.context, TenantInOutVillaActivity::class.java)
                                    tenantUpdate.putExtra("roomId",TenantRequestListItems[position].tenantRoomId.toString())
                                    tenantUpdate.putExtra("roomNumber",TenantRequestListItems[position].tenantRoomNumber)
                                    startActivity(tenantUpdate)
                                }
                            }).start()
                        }
                        imageView.tenantDelete -> {
                            // 호 삭제
                            val villadb = VillaNoticeHelper.getInstance(applicationContext)

                            Thread(Runnable {

                                villadb!!.VillaNoticeDao().deleteTenant(
                                    MyApplication.prefs.getString("villaAddress","").trim()
                                    ,TenantRequestListItems[position].tenantRoomId.toString().toLong()
                                )
                                runOnUiThread {
                                    initTenantRooms()
                                }
                            }).start()
//                            showToast(TenantRoomListItems[position].tenantRoomId.toString())
                        }

                    }
                }

            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val ToHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(ToHome)

    }

    private fun initTenantRooms(){
        TenantRequestListItems.clear()

        val villadb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {

            val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(MyApplication.prefs.getString("villaAddress","").trim())

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

        val ToHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(ToHome)
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