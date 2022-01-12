package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import fastcampus.aop.part2.mgr_villa.adapter.KakaoApiAdapter
import fastcampus.aop.part2.mgr_villa.adapter.NoticeAdapter
import fastcampus.aop.part2.mgr_villa.customdialog.mgrAddAccountDialog
import fastcampus.aop.part2.mgr_villa.customdialog.mgrCheckDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityAccountBinding
import fastcampus.aop.part2.mgr_villa.databinding.ActivityNoticeBinding
import fastcampus.aop.part2.mgr_villa.model.AddrLayout
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import java.lang.Exception
import java.time.LocalDate

class AddAccountActivity: AppCompatActivity() {


    private val binding: ActivityAccountBinding by lazy { ActivityAccountBinding.inflate(layoutInflater)}

    private var NoticeTitleFlag =  false
    private var NoticeNo: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initBankDialog()
//
//        initButtonSetOnClick()
//
//        if(!intent.hasExtra("noticeNo")){
//            binding.WriteNoticeButton.isVisible = true
//            binding.UpdateNoticeButton.isVisible = false
//            binding.DeleteNoticeButton.isVisible = false
//        } else {
//            binding.WriteNoticeButton.isVisible = false
//            binding.UpdateNoticeButton.isVisible = true
//            binding.DeleteNoticeButton.isVisible = true
//
//            NoticeNo = intent.getLongExtra("noticeNo",0)
//            getNoticeContent()
//        }
    }

    private fun initBankDialog(){
        binding.bankSpinnerArea.setOnClickListener {
            val mgrBankDialog = mgrAddAccountDialog(this)
            mgrBankDialog.showDialog()
        }
    }

//
//    // 공지사항 불러오기
//    private fun getNoticeContent() {
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
//
//
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun initButtonSetOnClick() {
//        try {
//            binding.WriteNoticeButton.setOnClickListener {
//                val now = LocalDate.now()
//                if (!checkForm()) {
//                    return@setOnClickListener
//                } else {
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
//                }
//            }
//
//            binding.UpdateNoticeButton.setOnClickListener {
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
//            }
//
//            binding.DeleteNoticeButton.setOnClickListener {
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
//            }
//
//
//
//        } catch ( e: Exception){
//            Log.d("noticeInsert---------------->",e.stackTrace.toString())
//        }
//
//
//    }
//
//    private fun checkForm(): Boolean {
//        checkNoticeTitle()
//
//        return (NoticeTitleFlag)
//
//    }
//
//    private fun checkNoticeTitle() {
//        var noticeTitle = binding.villaNoticeTitleEditText.text.toString().trim()
//
//        if (noticeTitle.isNullOrEmpty()) {
//            binding.villaNoticeValid.setTextColor(-65535)
//            binding.villaNoticeValid.isInvisible = false
//            binding.villaNoticeValid.setText(R.string.must_insert)
//            NoticeTitleFlag = false
//        } else {
//            binding.villaNoticeValid.setTextColor(R.color.black.toInt())
//            binding.villaNoticeValid.isInvisible = true
//            NoticeTitleFlag = true
//        }
//    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.AddAccountToolbar)
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