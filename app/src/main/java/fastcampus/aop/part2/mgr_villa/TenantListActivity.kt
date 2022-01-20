package fastcampus.aop.part2.mgr_villa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.adapter.TenantAdapter
import fastcampus.aop.part2.mgr_villa.customdialog.RequestDialog
import fastcampus.aop.part2.mgr_villa.customdialog.TenantDeleteDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantlistBinding
import fastcampus.aop.part2.mgr_villa.model.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_noticelist.*
import kotlinx.android.synthetic.main.recycleview_tenants.*
import kotlinx.android.synthetic.main.recycleview_tenants.view.*

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


        val TenantDeleteDialog = TenantDeleteDialog(this)

        // Room 리스트 수정,삭제 클릭
        TenantRoomListAdapter.setSlideButtonClickListener(object : TenantAdapter.OnSlideButtonClickListener{
            override fun onSlideButtonClick(v: View, imageView: ImageView, position: Int) {

                val mgrCheck = MyApplication.prefs.getString("userType","")
                if (mgrCheck.equals("MGR")
                    && !mgrCheck.equals("")){
                    when(imageView) {
                        imageView.tenantUpdate -> {
                            Thread(Runnable {
                                runOnUiThread {
                                    val tenantUpdate = Intent(v.context, TenantInOutVillaActivity::class.java)
                                    tenantUpdate.putExtra("roomId",TenantRoomListItems[position].tenantRoomId.toString())
                                    tenantUpdate.putExtra("roomNumber",TenantRoomListItems[position].tenantRoomNumber)
                                    startActivity(tenantUpdate)
                                }
                            }).start()
                        }
                        imageView.tenantDelete -> {
                            TenantDeleteDialog.showDialog(TenantRoomListItems[position].tenantRoomNumber, TenantRoomListItems[position].tenantRoomId.toString().toLong())

                        }

                    }
                }

            }

        })

        TenantDeleteDialog.setOnClickListener(object : TenantDeleteDialog.OnDialogClickListener{
            override fun onClicked(context: Context, requestDelete: String, roomId: Long) {
                if (!requestDelete.isEmpty()){
                            // 호 삭제
                            val villadb = VillaNoticeHelper.getInstance(applicationContext)

                            Thread(Runnable {
                                villadb!!.VillaNoticeDao().deleteTenant(
                                    MyApplication.prefs.getString("villaAddress","").trim()
                                    ,roomId
                                )
                                runOnUiThread {
                                    initTenantRooms()
                                }
                            }).start()
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
        TenantRoomListItems.clear()

        val villadb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {

            val tenantRooms =  villadb!!.VillaNoticeDao().getAllTenantRooms(MyApplication.prefs.getString("villaAddress","").trim())

            for(VillaTenant in tenantRooms) {
                // 결과를 리싸이클러 뷰에 추가
                var item = TenantLayout(
                    VillaTenant.roomId
                    ,VillaTenant.roomNumber
                    ,VillaTenant.tenantContractDate.toString()
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

            val villadb = VillaNoticeHelper.getInstance(applicationContext)

            if(binding.tenantRoomNumberEditText.text.isNullOrEmpty()){
                showToast("호수는 반드시 입력해야 합니다.")
                return@setOnClickListener
            }

            var checkTenantCount: Int? = 0

            Thread(Runnable {
                    checkTenantCount = villadb?.VillaNoticeDao()?.checkTenantCount(
                        MyApplication.prefs.getString("villaAddress","").trim()
                    )
                runOnUiThread{

                }
            }).start()

//            showToast(TenantRoomListItems.count().toString())
//            showToast(checkTenantCount.toString())

            // 스레드 슬립 필요!!
            Thread.sleep(500)

            if(checkTenantCount == TenantRoomListItems.count()){
                showToast("최대 등록호수에 도달했습니다.")
                return@setOnClickListener
            }



            TenantRoomListItems.clear()

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
                        VillaTenant.roomId
                        ,VillaTenant.roomNumber
                        ,VillaTenant.tenantContractDate.toString()
                        ,VillaTenant.tenantLeaveDate.toString()
                    )
                    TenantRoomListItems.add(item)
                }
                runOnUiThread {
                    TenantRoomListAdapter.notifyDataSetChanged()
                    binding.tenantRoomNumberEditText.setText("")
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