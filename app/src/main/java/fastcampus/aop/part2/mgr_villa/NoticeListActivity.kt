package fastcampus.aop.part2.mgr_villa

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.adapter.KakaoApiAdapter
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityNoticelistBinding
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoData
import fastcampus.aop.part2.mgr_villa.model.AddrLayout
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.model.VillaTenant
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_noticelist.*

class NoticeListActivity: AppCompatActivity() {

    private val binding:ActivityNoticelistBinding by lazy { ActivityNoticelistBinding.inflate(layoutInflater)}

    val firestoreDB = Firebase.firestore

    private var NoticeListItems = arrayListOf<VillaNotice>()                   // 리싸이클러 뷰 아이템
    private val NoticeListAdapter = NoticeAdapter(NoticeListItems)            // 리싸이클러 뷰 어댑터

    private var isNoticeFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        binding.rvNotices.adapter = NoticeListAdapter

        if (MyApplication.prefs.getString("userType","").equals("TENANT")){
            binding.noticeFabMain.isVisible = false
            binding.noticeFabWrite.isVisible = false
        }

        initNoticeFabButtons()
        addItemsNotices()

        // 공지항목 클릭
        NoticeListAdapter.setItemClickListener(object : NoticeAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                val noticeActivity = Intent(v.context, NoticeActivity::class.java)
                noticeActivity.putExtra("noticeNo", NoticeListItems[position].noticeNo)
                startActivity(noticeActivity)

            }

        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(toHome)
    }


    // fab버튼 기능 초기화
    private fun initNoticeFabButtons(){
        binding.noticeFabMain.setOnClickListener {
            toggleNoticeFab()
        }

        binding.noticeFabWrite.setOnClickListener {
            val noticeWUActivity = Intent(this, NoticeActivity::class.java)
            startActivity(noticeWUActivity)
        }

    }

    private fun addItemsNotices(){

         NoticeListItems.clear()

        firestoreDB?.collection("VillaNotice")
            .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
            .orderBy("noticeDatetime", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, e ->
                NoticeListItems.clear()

                if (e != null){
//                    showToast(e.message.toString())
                    Log.d("VillaNotice/NoticeAdapter------------------>", e.message.toString())
                    return@addSnapshotListener
                }

                for (snapshot in querySnapshot!!.documents){
                    val item = snapshot.toObject(VillaNotice::class.java)
                    item!!.noticeNo = snapshot.id
                    NoticeListItems.add(item!!)
                }
                NoticeListAdapter.notifyDataSetChanged()
            }



        //-----------------------------------------------------------------------------------------------
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


    //-----------------------------------------------------------------------------------------------

    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.NoticeListToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val toHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(toHome)
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

    /*
        floatingActionButton 클릭시 동작하는 에니메이션 효과 세팅
     */
    private fun toggleNoticeFab(){
        if (isNoticeFabOpen){
            ObjectAnimator.ofFloat(noticeFabWrite, "translationY", 0f).apply { start() }
            noticeFabMain.setImageResource(R.drawable.ic_create_write)
        } else {
            ObjectAnimator.ofFloat(noticeFabWrite, "translationY", -200f).apply { start() }
            noticeFabMain.setImageResource(R.drawable.ic_fab_close)
        }

        isNoticeFabOpen = !isNoticeFabOpen

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}