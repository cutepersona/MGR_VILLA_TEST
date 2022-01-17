package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.adapter.CostTenantAdapter
import fastcampus.aop.part2.mgr_villa.adapter.TenantAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityCosttenantlistBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantlistBinding
import fastcampus.aop.part2.mgr_villa.model.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_noticelist.*
import kotlinx.android.synthetic.main.recycleview_tenants.*
import kotlinx.android.synthetic.main.recycleview_tenants.view.*

class TenantCostListActivity: AppCompatActivity() {

    private val binding:ActivityCosttenantlistBinding by lazy { ActivityCosttenantlistBinding.inflate(layoutInflater)}

    private var TenantCostListItems = arrayListOf<CostTenantLayout>()                   // 리싸이클러 뷰 아이템
    private val TenantCostListAdapter = CostTenantAdapter(TenantCostListItems)            // 리싸이클러 뷰 어댑터
//
//    private var isNoticeFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvTenants.adapter = TenantCostListAdapter

        initToolBar()
        initCostTenantRooms()
//        initAddTenantRoom()

//
//        initNoticeFabButtons()
//        addItemsNotices()




        TenantCostListAdapter.setItemClickListener(object : CostTenantAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

                Thread(Runnable {
                    val ConstCost = villaNoticedb!!.VillaNoticeDao().isConstCost(
                        MyApplication.prefs.getString("villaAddress","").trim()
                    )

                    runOnUiThread {
                        if (ConstCost <= 0) {
                            showToast("기준관리비가 등록되어 있지 않습니다.")
                        } else {
                            val RoolCostForMgr = Intent(v.context, TenantRoomCostForMGRActivity::class.java)
                            RoolCostForMgr.putExtra("tenantRoomId",TenantCostListItems[position].CostTenantRoomId)
                            startActivity(RoolCostForMgr)
                        }

                    }
                }).start()



            }

        })



//
//        // Room 리스트 수정,삭제 클릭
//        TenantRoomListAdapter.setSlideButtonClickListener(object : TenantAdapter.OnSlideButtonClickListener{
//            override fun onSlideButtonClick(v: View, imageView: ImageView, position: Int) {
//
//                val mgrCheck = MyApplication.prefs.getString("userType","")
//                if (mgrCheck.equals("MGR")
//                    && !mgrCheck.equals("")){
//                    when(imageView) {
//                        imageView.tenantUpdate -> {
//                            showToast(imageView.toString())
//                        }
//                        imageView.tenantDelete -> {
//                            // 호 삭제
//                            val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//                            Thread(Runnable {
//
//                                villadb!!.VillaNoticeDao().deleteTenant(
//                                    MyApplication.prefs.getString("villaAddress","").trim()
//                                    ,TenantRoomListItems[position].tenantRoomId.toString().toLong()
//                                )
//                                runOnUiThread {
//                                    initTenantRooms()
//                                }
//                            }).start()
////                            showToast(TenantRoomListItems[position].tenantRoomId.toString())
//                        }
//
//                    }
//                }
//
//            }
//
//        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val TenantCostListToDiv = Intent(this, MgrCostDivActivity::class.java)
        startActivity(TenantCostListToDiv)

    }

    private fun initCostTenantRooms(){
        TenantCostListItems.clear()

        val villadb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {

            val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(MyApplication.prefs.getString("villaAddress","").trim())

            for(VillaTenant in tenantRooms) {
                // 결과를 리싸이클러 뷰에 추가
                val item = CostTenantLayout(
                    VillaTenant.roomId
                    ,VillaTenant.roomNumber
                    ,VillaTenant.tenantContractDate.toString()
                    ,VillaTenant.tenantLeaveDate.toString()
                )
                TenantCostListItems.add(item)
            }

            runOnUiThread {
                TenantCostListAdapter.notifyDataSetChanged()
            }
        }).start()
    }
//
//    private fun initAddTenantRoom(){
//
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
//            if(checkTenantCount == TenantRoomListItems.count()){
//                showToast("최대 등록호수에 도달했습니다.")
//                return@setOnClickListener
//            }
//
//
//
//            TenantRoomListItems.clear()
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
//                    var item = TenantLayout(
//                        VillaTenant.roomId
//                        ,VillaTenant.roomNumber
//                        ,VillaTenant.tenantContractDate.toString()
//                        ,VillaTenant.tenantLeaveDate.toString()
//                    )
//                    TenantRoomListItems.add(item)
//                }
//                runOnUiThread {
//                    TenantRoomListAdapter.notifyDataSetChanged()
//                    binding.tenantRoomNumberEditText.setText("")
//                }
//            }).start()
//        }
//    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.CostTenantListToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val TenantCostListToDiv = Intent(this, MgrCostDivActivity::class.java)
        startActivity(TenantCostListToDiv)


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