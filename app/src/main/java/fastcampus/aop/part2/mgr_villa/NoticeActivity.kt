package fastcampus.aop.part2.mgr_villa

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.adapter.KakaoApiAdapter
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityNoticeBinding
import fastcampus.aop.part2.mgr_villa.model.AddrLayout
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import java.lang.Exception
import java.time.LocalDate

class NoticeActivity: AppCompatActivity() {


    private val binding: ActivityNoticeBinding by lazy { ActivityNoticeBinding.inflate(layoutInflater)}

    val firestoreDB = Firebase.firestore

    private var NoticeTitleFlag =  false
    private var NoticeNo: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        initButtonSetOnClick()

        if (MyApplication.prefs.getString("userType","").equals("TENANT")){
            binding.WriteNoticeButton.isVisible = false
            binding.UpdateNoticeButton.isVisible = false
            binding.DeleteNoticeButton.isVisible = false
            binding.villaNoticeTitleEditText.isFocusableInTouchMode = false
            binding.villaNoticeContentEditText.isFocusableInTouchMode = false

            NoticeNo = intent.getStringExtra("noticeNo").toString()
            getNoticeContent()
        } else {
            if(!intent.hasExtra("noticeNo")){
                binding.WriteNoticeButton.isVisible = true
                binding.UpdateNoticeButton.isVisible = false
                binding.DeleteNoticeButton.isVisible = false
            } else {
                binding.WriteNoticeButton.isVisible = false
                binding.UpdateNoticeButton.isVisible = true
                binding.DeleteNoticeButton.isVisible = true

                NoticeNo = intent.getStringExtra("noticeNo").toString()
                getNoticeContent()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toNoticeList = Intent(this, NoticeListActivity::class.java)
        startActivity(toNoticeList)

    }


    // 공지사항 불러오기
    private fun getNoticeContent() {


        firestoreDB.collection("VillaNotice").document(NoticeNo)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    binding.villaNoticeTitleEditText.setText(document["noticeTitle"].toString().trim())
                    binding.villaNoticeContentEditText.setText(document["noticeContent"].toString().trim())
                }
            }

        //----------------------------------------------------------------------------------------------
//        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//        Thread(Runnable {
//            var notice = villaNoticedb!!.VillaNoticeDao().getNotice(NoticeNo)
//
//            runOnUiThread {
//                binding.villaNoticeTitleEditText.setText(notice.noticeTitle)
//                binding.villaNoticeContentEditText.setText(notice.noticeContent)
//            }
//        }).start()
        //----------------------------------------------------------------------------------------------


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initButtonSetOnClick() {
        try {
            binding.WriteNoticeButton.setOnClickListener {
                val now = LocalDate.now()
                if (!checkForm()) {
                    return@setOnClickListener
                } else {
                    val villaNotice = firestoreDB.collection("VillaNotice")

                    val VillaNotice = hashMapOf(
                        "noticeNo" to "0",
                        "noticeTitle" to binding.villaNoticeTitleEditText.text.toString().trim(),
                        "noticeContent" to binding.villaNoticeContentEditText.text.toString().trim(),
                        "noticeDatetime" to now.toString(),
                        "villaAddr" to MyApplication.prefs.getString("villaAddress", "").trim()
                    )

                    villaNotice.document()
                        .set(VillaNotice)
                        .addOnSuccessListener { documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        val NoticeListActivity = Intent(this, NoticeListActivity::class.java)
                        startActivity(NoticeListActivity)

                        }
                        .addOnFailureListener { e ->
                            showToast("공지 등록에 실패하였습니다.")
                            Log.w(ContentValues.TAG, "Error adding document", e)
                            return@addOnFailureListener
                        }



                    //-----------------------------------------------------------------------------
//                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//                    Thread(Runnable {
//                        villaNoticedb!!.VillaNoticeDao().villaNoticeInsert(
//                            VillaNotice(
//                                null,
//                                binding.villaNoticeTitleEditText.text.toString().trim(),
//                                binding.villaNoticeContentEditText.text.toString().trim(),
//                                now.toString(),
//                                MyApplication.prefs.getString("villaAddress","").trim()
//                            )
//                        )
//
//                        runOnUiThread {
//                        val NoticeListActivity = Intent(this, NoticeListActivity::class.java)
//                        startActivity(NoticeListActivity)
//                        }
//                    }).start()
                    //-----------------------------------------------------------------------------
                }
            }

            binding.UpdateNoticeButton.setOnClickListener {


                firestoreDB.collection("VillaNotice").document(NoticeNo)
                    .update(mapOf(
                        "noticeTitle" to binding.villaNoticeTitleEditText.text.toString().trim()
                        ,"noticeContent" to binding.villaNoticeContentEditText.text.toString().trim()
                    ))

                val NoticeListActivity = Intent(this, NoticeListActivity::class.java)
                startActivity(NoticeListActivity)


                //--------------------------------------------------------------------------------
//                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//                Thread(Runnable {
//                    villaNoticedb!!.VillaNoticeDao().updateNotice(
//                            binding.villaNoticeTitleEditText.text.toString().trim(),
//                            binding.villaNoticeContentEditText.text.toString().trim(),
//                            NoticeNo
//                    )
//
//                    runOnUiThread {
//                        val NoticeListActivity = Intent(this, NoticeListActivity::class.java)
//                        startActivity(NoticeListActivity)
//                    }
//                }).start()
                //--------------------------------------------------------------------------------
            }

            binding.DeleteNoticeButton.setOnClickListener {


                firestoreDB?.collection("VillaNotice")
                    .document(NoticeNo)
                    .delete()
                    .addOnSuccessListener {
                        val NoticeListActivity = Intent(this, NoticeListActivity::class.java)
                        startActivity(NoticeListActivity)
                    }
                    .addOnFailureListener {
                        showToast("삭제처리 되지 않았습니다.")
                        return@addOnFailureListener
                    }

                //--------------------------------------------------------------------------------
//                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//                Thread(Runnable {
//                    villaNoticedb!!.VillaNoticeDao().deleteNotice(
//                        NoticeNo
//                    )
//
//                    runOnUiThread {
//                        val NoticeListActivity = Intent(this, NoticeListActivity::class.java)
//                        startActivity(NoticeListActivity)
//                    }
//                }).start()
                //--------------------------------------------------------------------------------
            }



        } catch ( e: Exception){
            Log.d("noticeInsert---------------->",e.stackTrace.toString())
        }


    }

    private fun checkForm(): Boolean {
        checkNoticeTitle()

        return (NoticeTitleFlag)

    }

    private fun checkNoticeTitle() {
        var noticeTitle = binding.villaNoticeTitleEditText.text.toString().trim()

        if (noticeTitle.isNullOrEmpty()) {
            binding.villaNoticeValid.setTextColor(-65535)
            binding.villaNoticeValid.isInvisible = false
            binding.villaNoticeValid.setText(R.string.must_insert)
            NoticeTitleFlag = false
        } else {
            binding.villaNoticeValid.setTextColor(R.color.black.toInt())
            binding.villaNoticeValid.isInvisible = true
            NoticeTitleFlag = true
        }
    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.NoticeToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val toNoticeList = Intent(this, NoticeListActivity::class.java)
        startActivity(toNoticeList)
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