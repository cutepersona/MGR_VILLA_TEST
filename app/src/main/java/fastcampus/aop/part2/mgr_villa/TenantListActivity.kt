package fastcampus.aop.part2.mgr_villa

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.adapter.TenantAdapter
import fastcampus.aop.part2.mgr_villa.customdialog.RequestDialog
import fastcampus.aop.part2.mgr_villa.customdialog.TenantDeleteDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantlistBinding
import fastcampus.aop.part2.mgr_villa.fragment.MgrHomeFragment
import fastcampus.aop.part2.mgr_villa.model.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_noticelist.*
import kotlinx.android.synthetic.main.recycleview_tenants.*
import kotlinx.android.synthetic.main.recycleview_tenants.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TenantListActivity: AppCompatActivity() {

    private val binding: ActivityTenantlistBinding by lazy {
        ActivityTenantlistBinding.inflate(
            layoutInflater
        )
    }

    val firestoreDB = Firebase.firestore

    private var TenantRoomListItems = arrayListOf<VillaTenant>()                   // 리싸이클러 뷰 아이템
    private val TenantRoomListAdapter = TenantAdapter(TenantRoomListItems)            // 리싸이클러 뷰 어댑터
//
//    private var isNoticeFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvTenants.adapter = TenantRoomListAdapter
//        binding.rvTenants.layoutManager = LinearLayoutManager(this)

        initToolBar()
        initTenantRooms()
        initAddTenantRoom()

//        // 공지항목 클릭
//        TenantRoomListAdapter.setItemClickListener(object : TenantAdapter.OnItemClickListener{
//            override fun onClick(v: View, position: Int) {
//                showToast(TenantRoomListItems[position].roomId)
//            }
//
//        })

//
//        initNoticeFabButtons()
//        addItemsNotices()


        val TenantDeleteDialog = TenantDeleteDialog(this)

        // Room 리스트 수정,삭제 클릭
        TenantRoomListAdapter.setSlideButtonClickListener(object :
            TenantAdapter.OnSlideButtonClickListener {
            override fun onSlideButtonClick(v: View, imageView: ImageView, position: Int) {

                val mgrCheck = MyApplication.prefs.getString("userType", "")
                if (mgrCheck.equals("MGR")
                    && !mgrCheck.equals("")
                ) {
                    when (imageView) {
                        imageView.tenantUpdate -> {
//                            Thread(Runnable {
//                                runOnUiThread {
//                            showToast(TenantRoomListItems[position].roomId)
                                    val tenantUpdate =
                                        Intent(v.context, TenantInOutVillaActivity::class.java)
                                    tenantUpdate.putExtra("roomId",TenantRoomListItems[position].roomId)
                                    tenantUpdate.putExtra("roomNumber",TenantRoomListItems[position].roomNumber)
                                    startActivity(tenantUpdate)
//                                }
//                            }).start()
                        }
                        imageView.tenantDelete -> {
                            TenantDeleteDialog.showDialog(
                                TenantRoomListItems[position].roomNumber,
                                TenantRoomListItems[position].roomId
                            )

                        }

                    }
                }

            }

        })



        TenantDeleteDialog.setOnClickListener(object : TenantDeleteDialog.OnDialogClickListener {
            override fun onClicked(context: Context, requestDelete: String, roomId: String) {
                if (!requestDelete.isEmpty()) {
                    // 호 삭제
                    val villadb = VillaNoticeHelper.getInstance(applicationContext)

                    Thread(Runnable {
                        villadb!!.VillaNoticeDao().deleteTenant(
                            MyApplication.prefs.getString("villaAddress", "").trim(), roomId
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

    private fun initTenantRooms() {

//        showToast(MyApplication.prefs.getString("villaAddress", "").trim())

        firestoreDB?.collection("VillaTenant")
            .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
            .orderBy("roomNumber", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
            TenantRoomListItems.clear()

                if (e != null){
//                    showToast(e.message.toString())
                    Log.d("VillaTenant/TenantAdapter------------------>", e.message.toString())
                    return@addSnapshotListener
                }

            for (snapshot in querySnapshot!!.documents){
                val item = snapshot.toObject(VillaTenant::class.java)
                item!!.roomId = snapshot.id
                TenantRoomListItems.add(item!!)
            }
            TenantRoomListAdapter.notifyDataSetChanged()
        }

        //------------------------------------------------------------------------------------
//        val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//        Thread(Runnable {
//
//            val tenantRooms = villadb!!.VillaNoticeDao()
//                .getAllTenantRooms(MyApplication.prefs.getString("villaAddress", "").trim())
//
//            for (VillaTenant in tenantRooms) {
//                // 결과를 리싸이클러 뷰에 추가
//                var item = TenantLayout(
//                    VillaTenant.roomId,
//                    VillaTenant.roomNumber,
//                    VillaTenant.tenantContractDate.toString(),
//                    VillaTenant.tenantLeaveDate.toString()
//                )
//                TenantRoomListItems.add(item)
//            }
//
//            runOnUiThread {
//                TenantRoomListAdapter.notifyDataSetChanged()
//            }
//        }).start()
        //------------------------------------------------------------------------------------
    }

    private fun initAddTenantRoom() {
        var checkTenantCount = 0
        firestoreDB.collection("VillaInfo")
            .whereEqualTo("villaAddress", MyApplication.prefs.getString("villaAddress", ""))
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (i in task.result!!) {
                        checkTenantCount = i.data["villaTenantCount"].toString().toInt()
//                        showToast(i.data["villaTenantCount"].toString())
                        break
                    }
                }
            }

        binding.AddTenantButton.setOnClickListener {

            if (binding.tenantRoomNumberEditText.text.isNullOrEmpty()) {
                showToast("호수는 반드시 입력해야 합니다.")
                return@setOnClickListener
            }

            if (checkTenantCount == TenantRoomListItems.count()) {
                showToast("최대 등록호수에 도달했습니다.")
                return@setOnClickListener
            }

            TenantRoomListItems.clear()


            val villaTenant = firestoreDB.collection("VillaTenant")


            val VillaTenant = hashMapOf(
                "roomId" to "0",
                "roomNumber" to binding.tenantRoomNumberEditText.text.toString().trim(),
                "tenantEmail" to "",
                "tenantContractDate" to "",
                "tenantLeaveDate" to "",
                "tenantStatus" to "",
                "villaAddr" to MyApplication.prefs.getString("villaAddress", "").trim(),
                "roadAddress" to MyApplication.prefs.getString("roadAddress", "").trim()
            )

            villaTenant.document()
                .set(VillaTenant)
                .addOnSuccessListener { documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                    binding.tenantRoomNumberEditText.setText("")
                    initTenantRooms()

                }
                .addOnFailureListener { e ->
                    showToast("정보를 불러오지 못했습니다.")
                    Log.w(ContentValues.TAG, "Error adding document", e)
                    return@addOnFailureListener
                }


            //----------------------------------------------------------------------------------------
//
//            val villadb = VillaNoticeHelper.getInstance(applicationContext)
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
//            showToast(TenantRoomListItems.count().toString())
//            showToast(checkTenantCount.toString())
//
//             스레드 슬립 필요!!
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
//                         MyApplication.prefs.getString("villaAddress","").trim(),
//                        MyApplication.prefs.getString("roadAddress", "").trim()
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

            //----------------------------------------------------------------------------------------
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



