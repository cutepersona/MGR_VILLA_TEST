package fastcampus.aop.part2.mgr_villa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.customdialog.LogOutDialog
import fastcampus.aop.part2.mgr_villa.customdialog.SignOutDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMypageBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class MyPageActivity : AppCompatActivity() {

    private val binding: ActivityMypageBinding by lazy {
        ActivityMypageBinding.inflate(
            layoutInflater
        )
    }

    val firestoreDB = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initMyInfo()
        initSignOut()
        initChangPw()
        initChangePhoneNumber()
    }

    // 비밀번호 변경하기
    private fun initChangPw() {
        binding.MyPageChangePassword.setOnClickListener {
            val toMyPageChangePw = Intent(this, MyPageChangePwActivity::class.java)
            toMyPageChangePw.putExtra("email", binding.MyPageUserEmail.text.toString().trim())
            startActivity(toMyPageChangePw)
        }
    }

    // 전화번호 변경하기
    private fun initChangePhoneNumber(){
        binding.MyPageChangePhoneNumber.setOnClickListener {
            val toMyPageChangePhoneNum = Intent(this, MyPageChangePhoneNumActivity::class.java)
            toMyPageChangePhoneNum.putExtra("email", binding.MyPageUserEmail.text.toString().trim())
            startActivity(toMyPageChangePhoneNum)
        }
    }

    // 회원 탈퇴 하기
    private fun initSignOut() {
        binding.MyPageDeleteUser.setOnClickListener {
            val signOutDialog = SignOutDialog(this)
            signOutDialog.showDialog()


            signOutDialog.setOnClickListener(object : SignOutDialog.OnDialogClickListener{
                override fun onClicked(signOut: String, context: Context) {
                    // 관리자 정보 삭제
                    if (MyApplication.prefs.getString("userType","").equals("MGR")){
                        // 관리자는 계정 정보 및 주소정보, 계좌 정보 모든 정보 삭제
                            // 계정정보 삭제
                        firestoreDB.collection("VillaUsers")
                            .document(MyApplication.prefs.getString("email",""))
                            .delete()
                            .addOnSuccessListener {
                                // 집주소 정보 삭제
                                firestoreDB.collection("VillaInfo")
                                    .document(MyApplication.prefs.getString("villaAddress",""))
                                    .delete()

                                // 세입자 정보 삭제
                                firestoreDB.collection("VillaTenant")
                                    .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress",""))
                                    .get()
                                    .addOnSuccessListener { results ->
                                            for(i in results!!){
                                                firestoreDB.collection("VillaTenant")
                                                    .document(i.id)
                                                    .delete()
                                            }
                                        }

                                // 공지정보 삭제
                                firestoreDB.collection("VillaNotice")
                                    .whereEqualTo("noticeNo",MyApplication.prefs.getString("email",""))
                                    .get()
                                    .addOnSuccessListener { results ->
                                            for(i in results){
                                                firestoreDB.collection("VillaNotice")
                                                    .document(i.id)
                                                    .delete()
                                            }
                                        }

                                // 계좌정보 삭제
                                firestoreDB.collection("VillaAccount")
                                    .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress",""))
                                    .get()
                                    .addOnSuccessListener { results ->
                                        for(i in results){
                                            firestoreDB.collection("VillaAccount")
                                                .document(i.id)
                                                .delete()
                                        }
                                    }


                                MyApplication.prefs.clear()
                                val toMain = Intent(context, MainActivity::class.java)
                                startActivity(toMain)
                            }.addOnFailureListener{
                                showToast("회원탈퇴에 실패하였습니다. 관리자에게 문의 바랍니다.")
                                return@addOnFailureListener
                            }


                    } else {
                        // 세입자 정보 삭제
                        // 계정 정보 및 전입정보 삭제
                        firestoreDB.collection("VillaUsers")
                            .document(MyApplication.prefs.getString("email",""))
                            .delete()
                            .addOnSuccessListener {
                                firestoreDB.collection("VillaTenant")
                                    .whereEqualTo("tenantEmail",MyApplication.prefs.getString("email","")).limit(1)
                                    .get()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful){
                                            for(i in task.result!!){
//                                                showToast(i.data["roomId"].toString())
                                                firestoreDB.collection("VillaTenant")
                                                    .document(i.data["roomId"].toString())
                                                    .update(mapOf(
                                                        "roomId" to "0"
                                                        ,"tenantContractDate" to ""
                                                        ,"tenantEmail" to ""
                                                        ,"tenantLeaveDate" to ""
                                                        ,"tenantStatus" to ""
                                                    ))
                                                break
                                            }
                                        }
                                    }
                                MyApplication.prefs.clear()
                                val toMain = Intent(context, MainActivity::class.java)
                                startActivity(toMain)
                            }.addOnFailureListener{
                                showToast("회원탈퇴에 실패하였습니다. 관리자에게 문의 바랍니다.")
                                return@addOnFailureListener
                            }



                    }



                }
            })

//-----------------------------------------------------------------------------
//            val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//            signOutDialog.setOnClickListener(object : SignOutDialog.OnDialogClickListener {
//                override fun onClicked(signOut: String, context: Context) {
//                    Thread(Runnable {
//                        Thread.sleep(100L)
//                        villadb!!.VillaNoticeDao().deleteUser(
//                            binding.MyPageUserEmail.text.toString().trim()
//                        )
//                        Thread.sleep(100L)
//                        villadb!!.VillaNoticeDao().signOutTenant(
//                            MyApplication.prefs.getString("villaAddress","")
//                            ,binding.MyPageUserEmail.text.toString().trim()
//                        )
//
//                        runOnUiThread {
//                            MyApplication.prefs.clear()
//                            val toMain = Intent(context, MainActivity::class.java)
//                            startActivity(toMain)
//                        }
//                    }).start()
//
//
//                }
//            })
//-----------------------------------------------------------------------------
        }


    }

    // 내 기본정보 가져오기
    private fun initMyInfo() {

        firestoreDB.collection("VillaUsers")
            .document(MyApplication.prefs.getString("email",""))
            .get()
            .addOnSuccessListener { task ->
                binding.MyName.setText(task["userName"].toString())
                binding.MyPageUserEmail.setText(task["mailAddress"].toString())
                binding.MyPageUserPhone.setText(initPhoneRegax(task["phoneNumber"].toString()))
            }
        //----------------------------------------------------------------------------------
//
//        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//        Thread(Runnable {
//            val userInfo = villaNoticedb!!.VillaNoticeDao().getUser(
//                MyApplication.prefs.getString("email", "").trim()
//            )
//
//            runOnUiThread {
//                binding.MyName.setText(userInfo.userName)
//                binding.MyPageUserEmail.setText(userInfo.mailAddress)
//                binding.MyPageUserPhone.setText(initPhoneRegax(userInfo.phoneNumber))
//
//            }
//        }).start()
        //----------------------------------------------------------------------------------
    }

    // 전화번호 정규식 적용하기
    private fun initPhoneRegax(phoneNumber: String): String {
        var regaxPhoneNum: String = ""

        val reg = Regex("^\\d{3}-\\d{3,4}-\\d{4}\$")

        if (phoneNumber.length == 11) {
            if (reg.matches(phoneNumber)) {
                val first = phoneNumber.slice(IntRange(0, 2))
                val second = phoneNumber.slice(IntRange(3, 6))
                val last = phoneNumber.slice(IntRange(7, 10))
                regaxPhoneNum = "$first-$second-$last"
            }
        } else {
            val first = phoneNumber.slice(IntRange(0, 1))
            val second = phoneNumber.slice(IntRange(2, 5))
            val last = phoneNumber.slice(IntRange(6, 9))
            regaxPhoneNum = "$first-$second-$last"
        }
        return regaxPhoneNum
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val toInfo = Intent(this, MyInfoActivity::class.java)
        startActivity(toInfo)
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.MyPageToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val toInfo = Intent(this, MyInfoActivity::class.java)
        startActivity(toInfo)


        return true
//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}