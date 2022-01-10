package fastcampus.aop.part2.mgr_villa

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.adapter.KakaoApiAdapter
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.adapter.TenantAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityNoticelistBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantlistBinding
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoData
import fastcampus.aop.part2.mgr_villa.model.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_noticelist.*

class TenantListActivity: AppCompatActivity() {

    private val binding:ActivityTenantlistBinding by lazy { ActivityTenantlistBinding.inflate(layoutInflater)}

    private var TenantRoomListItems = arrayListOf<TenantLayout>()                   // 리싸이클러 뷰 아이템
    private val TenantRoomListAdapter = TenantAdapter(TenantRoomListItems)            // 리싸이클러 뷰 어댑터
//
//    private var isNoticeFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvTenants.adapter = TenantRoomListAdapter

        initToolBar()
        initTenantRooms()
        initAddTenantRoom()

//
//        initNoticeFabButtons()
//        addItemsNotices()
//
//        // 리스트 주소 클릭
//        NoticeListAdapter.setItemClickListener(object : NoticeAdapter.OnItemClickListener{
//            override fun onClick(v: View, position: Int) {
//
//                val NoticeUDActivity = Intent(v.context, NoticeActivity::class.java)
//                NoticeUDActivity.putExtra("noticeNo", NoticeListItems[position].noticeNo)
//                startActivity(NoticeUDActivity)
//
//            }
//
//        })

    }

    private fun initTenantRooms(){
        TenantRoomListItems.clear()

        val villadb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {

            val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(MyApplication.prefs.getString("villaAddress","").trim())

            for(VillaTenant in tenantRooms) {
                // 결과를 리싸이클러 뷰에 추가
                var item = TenantLayout(
                    VillaTenant.roomNumber,
                    VillaTenant.tenantContractDate.toString()
                    ,VillaTenant.tenantLeaveDate.toString()
                )
                TenantRoomListItems.add(item)
            }

            runOnUiThread {
                TenantRoomListAdapter.notifyDataSetChanged()
            }
        }).start()
    }

    private fun initAddTenantRoom(){


        binding.AddTenantButton.setOnClickListener {

            if(binding.tenantRoomNumberEditText.text.isNullOrEmpty()){
                showToast("호수는 반드시 입력해야 합니다.")
                return@setOnClickListener
            }

            TenantRoomListItems.clear()


            val villadb = VillaNoticeHelper.getInstance(applicationContext)


            Thread(Runnable {
                villadb?.VillaNoticeDao()?.villaTenantInsert(
                    VillaTenant(
                        null,
                        binding.tenantRoomNumberEditText.text.toString().trim(),
                        "",
                        "",
                        "",
                        "",
                         MyApplication.prefs.getString("villaAddress","").trim()
                    )
                )

                val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(MyApplication.prefs.getString("villaAddress","").trim())

                for(VillaTenant in tenantRooms) {
                    // 결과를 리싸이클러 뷰에 추가
                    var item = TenantLayout(
                        VillaTenant.roomNumber,
                        VillaTenant.tenantContractDate.toString()
                        ,VillaTenant.tenantLeaveDate.toString()
                    )
                    TenantRoomListItems.add(item)
                }
                runOnUiThread {
                    TenantRoomListAdapter.notifyDataSetChanged()
                }
            }).start()
        }
    }

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
        val toolbar = findViewById<Toolbar>(R.id.TenantListToolbar)
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