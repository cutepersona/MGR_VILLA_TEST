package fastcampus.aop.part2.mgr_villa

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.customdialog.RequestTenantDialog
import fastcampus.aop.part2.mgr_villa.customdialog.TenantOutDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantinoutBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TenantInOutVillaActivity : AppCompatActivity() {

    val binding: ActivityTenantinoutBinding by lazy { ActivityTenantinoutBinding.inflate(layoutInflater) }

    val firestoreDB = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
//        initRequestTenantDialog()
        initContractCalendar()


        if (intent.hasExtra("roomId")){
            binding.TenantIORoomId.setText(intent.getStringExtra("roomId"))
        }
        if (intent.hasExtra("roomNumber")){
            binding.tenantIORoomNumber.setText(intent.getStringExtra("roomNumber"))
        }

        if (!binding.TenantIORoomId.text.isNullOrEmpty()){
            initTenantInfo()
        } else {
            initToday()
        }

        initMgrIn()
        initTenantIntoVilla()

        mgrInCheck()

        val TenantOutDialog = TenantOutDialog(this)
        initTenantOutVilla(TenantOutDialog)
        TenantOutDialog.setOnClickListener(object : TenantOutDialog.OnDialogClickListener{
            override fun onClicked(context: Context, requestDelete: String, roomId: String) {
                if (!requestDelete.isEmpty()){

                    firestoreDB.collection("VillaTenant").document(binding.TenantIORoomId.text.toString())
                        .update(mapOf(
                            "tenantEmail" to ""
                            ,"tenantContractDate" to ""
                            ,"tenantLeaveDate" to ""
                            ,"tenantStatus" to ""
                        ))

                    val toTenantList = Intent(context, TenantListActivity::class.java)
                    startActivity(toTenantList)

//                    Thread(Runnable {
//                        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//                        villaNoticedb!!.VillaNoticeDao().leaveTenant(
//                            MyApplication.prefs.getString("villaAddress","").trim()
//                            ,binding.TenantIORoomId.text.toString().toLong()
//                        )
//
//                        runOnUiThread {
//                            val toTenantList = Intent(context, TenantListActivity::class.java)
//                            startActivity(toTenantList)
//                        }
//                    }).start()
                }
            }
        })


    }



    // 입주요청 다이얼로그 호출
    private fun initRequestTenantDialog() {
        binding.tenantIOSelectTenantArea.setOnClickListener {

//
//            Thread(Runnable {
//                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//                val mgrUser = villaNoticedb!!.VillaNoticeDao().intoTenant(
//                    binding.tenantIOEmail.text.toString().trim()
//                    ,binding.tenantIOContractDate.text.toString().trim()
//                    ,binding.tenantIOLeaveDate.text.toString().trim()
//                    ,MyApplication.prefs.getString("villaAddress","").trim()
//                    ,binding.TenantIORoomId.text.toString().toLong()
//                )
//
//                runOnUiThread {
//
//                    showToast("전입이 완료되었습니다.")
//                    val toTenantList = Intent(this, TenantListActivity::class.java)
//                    startActivity(toTenantList)
//                }
//            }).start()

//            val RequestTenantListAdapter = RequestTenantDialogAdapter(RequestTenantList)            // 리싸이클러 뷰 어댑터

            val requestDialog = RequestTenantDialog(this)
//            requestDialog.showDialog(RequestTenantListAdapter)


            requestDialog.setOnClickListener( object : RequestTenantDialog.OnDialogClickListener{
                override fun onClicked(
                    tenantEmail: String,
                    tenantName: String,
                    tenantPhone: String
                ) {
                    showToast(tenantEmail + "/ " + tenantName + "/ " + tenantPhone)
                }
            })

            // 다이얼로그에서 ItemClick로 값 바로 가져오기
//--------------------------------------------------
//            BankListAdapter.setItemClickListener(object : BankDialogAdapter.OnItemClickListener{
//                override fun onClick(v: View, position: Int) {
//                    binding.bankNameText.setText(BankListAdapter.bankList[position])
////                    showToast(BankListAdapter.bankList[position])
//                    mgrBankDialog.DisMiss()
//                }
//            })
//----------------------------------
        }
    }

    // 전입정보 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initTenantInfo(){

//        showToast(binding.TenantIORoomId.text.toString())

        firestoreDB.collection("VillaTenant").document(binding.TenantIORoomId.text.toString())
//            .whereEqualTo("roomId", binding.TenantIORoomId.text)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    binding.tenantIOEmail.setText(document["tenantEmail"].toString().trim())
                    binding.tenantIOContractDate.setText(document["tenantContractDate"].toString().trim())
                    binding.tenantIOLeaveDate.setText(document["tenantLeaveDate"].toString().trim())

                    if (!document["tenantEmail"].toString().trim().isNullOrEmpty()){
                        // 로그인 회원정보 가져오기
                        firestoreDB.collection("VillaUsers").document(document["tenantEmail"].toString().trim())
                            .get()
                            .addOnSuccessListener { user ->
                                if (user != null){
                                    binding.tenantIOTenantName.setText(user["userName"].toString().trim())
                                    binding.tenantIOTenantPhone.setText(initPhoneRegax(user["phoneNumber"].toString()).trim())
                                }
                            }
                    }

                }



//                        checkTenantCount = i.data["villaTenantCount"].toString().toInt()
//                        showToast(i.data["villaTenantCount"].toString())

            }
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    for (i in task.result!!) {
//                            binding.tenantIOEmail.setText(i.data["tenantEmail"].toString())
//                            binding.tenantIOContractDate.setText(i.data["tenantContractDate"].toString())
//                            binding.tenantIOLeaveDate.setText(i.data["tenantLeaveDate"].toString())
//
//                        // 로그인 회원정보 가져오기
//                        firestoreDB.collection("VillaUsers")
//                            .whereEqualTo("mailAddress", i.data["tenantEmail"].toString())
//                            .get()
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful){
//                                    for (j in task.result!!){
//                                        binding.tenantIOTenantName.setText(j.data["userName"].toString())
//                                        binding.tenantIOTenantPhone.setText(initPhoneRegax(j.data["phoneNumber"].toString()))
//                                        break
//                                        }
//                                    }
//                                }
////                        checkTenantCount = i.data["villaTenantCount"].toString().toInt()
////                        showToast(i.data["villaTenantCount"].toString())
//                        break
//                    }
//                }
//            }


//        val villadb = VillaNoticeHelper.getInstance(applicationContext)
//        Thread(Runnable {
//
//            val tenantRooms =  villadb?.VillaNoticeDao()?.getTenantInfo(binding.TenantIORoomId.text.toString().toLong())
//            val tenantInfo = villadb?.VillaNoticeDao()?.getUser(tenantRooms?.tenantEmail.toString())
//
//            runOnUiThread {
//                if (tenantRooms != null
//                    && tenantInfo != null
//                ){
//                    binding.tenantIOEmail.setText(tenantRooms.tenantEmail)
//                    binding.tenantIOContractDate.setText(tenantRooms.tenantContractDate)
//                    binding.tenantIOLeaveDate.setText(tenantRooms.tenantLeaveDate)
//                    binding.tenantIOTenantName.setText(tenantInfo.userName)
//                    binding.tenantIOTenantPhone.setText(initPhoneRegax(tenantInfo.phoneNumber))
//                }
//            }
//        }).start()
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

    // 전입하기
    private fun initTenantIntoVilla() {
        binding.RequestTenantIntoVillaButton.setOnClickListener {

            if (!checkForm()){
                showToast("입력 영역을 확인해 주세요.")
                return@setOnClickListener
            }

            firestoreDB.collection("VillaTenant").document(binding.TenantIORoomId.text.toString())
                .update(mapOf(
                    "tenantEmail" to binding.tenantIOEmail.text.toString().trim()
                    ,"tenantContractDate" to binding.tenantIOContractDate.text.toString().trim()
                    ,"tenantLeaveDate" to binding.tenantIOLeaveDate.text.toString().trim()
                    ,"tenantStatus" to "IntoDone"
                ))

                    showToast("전입이 완료되었습니다.")
                    val toTenantList = Intent(this, TenantListActivity::class.java)
                    startActivity(toTenantList)

//            Thread(Runnable {
//                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//                val mgrUser = villaNoticedb!!.VillaNoticeDao().intoTenant(
//                    binding.tenantIOEmail.text.toString().trim()
//                    ,binding.tenantIOContractDate.text.toString().trim()
//                    ,binding.tenantIOLeaveDate.text.toString().trim()
//                    ,MyApplication.prefs.getString("villaAddress","").trim()
//                    ,binding.TenantIORoomId.text.toString().toLong()
//                )
//
//                runOnUiThread {
//
//                    showToast("전입이 완료되었습니다.")
//                    val toTenantList = Intent(this, TenantListActivity::class.java)
//                    startActivity(toTenantList)
//                }
//            }).start()
        }
    }

    // 퇴거하기
    private fun initTenantOutVilla(TenantOutDialog: TenantOutDialog) {
        binding.RequestTenantOutVillaButton.setOnClickListener {


            //-------------------------------------------------------------------------------------
//            Thread(Runnable {
//                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//                villaNoticedb!!.VillaNoticeDao().leaveTenant(
//                   MyApplication.prefs.getString("villaAddress","").trim()
//                   ,binding.TenantIORoomId.text.toString().toLong()
//                )
//
//                runOnUiThread {
                    TenantOutDialog.showDialog(binding.tenantIORoomNumber.text.toString(), binding.TenantIORoomId.text.toString())
//                    val toTenantList = Intent(this, TenantListActivity::class.java)
//                    startActivity(toTenantList)
//                }
//            }).start()
            //-------------------------------------------------------------------------------------
        }
    }

    // 관리자 전입 체크
    private fun mgrInCheck() {
        Thread(Runnable {
            val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

            val mgrUser = villaNoticedb!!.VillaNoticeDao().isIntoMgrCheck(
                MyApplication.prefs.getString("villaAddress","").trim()
            )

            runOnUiThread {
                if (mgrUser > 0) {
                    binding.mgrTenantIn.isVisible = false
                } else {
                    binding.mgrTenantIn.isVisible = true
                }
            }
        }).start()
    }


    // 관리자 전입 요청
    private fun initMgrIn() {
        binding.mgrTenantIn.setOnClickListener {

            // 관리자 정보 가져오기
            firestoreDB.collection("VillaUsers")
                .whereEqualTo("mailAddress", MyApplication.prefs.getString("email","").trim())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (i in task.result!!) {
                            binding.tenantIOEmail.setText(i.data["mailAddress"].toString())
                            binding.tenantIOTenantName.setText(i.data["userName"].toString())
                            binding.tenantIOContractDate.setText("-")
                            binding.tenantIOLeaveDate.setText("-")
                            binding.tenantIOTenantPhone.setText(initPhoneRegax(i.data["phoneNumber"].toString()))
                            break
                        }
                    }
                }

//            Thread(Runnable {
//                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//                    val mgrUser = villaNoticedb!!.VillaNoticeDao().getMgrUser(
//                            MyApplication.prefs.getString("villaAddress","").trim()
//                    )
//
//                    runOnUiThread {
//                        binding.tenantIOEmail.setText(mgrUser.mailAddress)
//                        binding.tenantIOTenantName.setText(mgrUser.userName)
//                        binding.tenantIOContractDate.setText("-")
//                        binding.tenantIOLeaveDate.setText("-")
//                        binding.tenantIOTenantPhone.setText(mgrUser.phoneNumber)
//                    }
//            }).start()

        }
    }

    // 양식 체크
    private fun checkForm() : Boolean{
        if (binding.tenantIOEmail.text.isNullOrEmpty()
            || binding.tenantIOTenantName.text.isNullOrEmpty()
            || binding.tenantIOTenantPhone.text.isNullOrEmpty()
            || binding.tenantIOContractDate.text.isNullOrEmpty()
            || binding.tenantIOLeaveDate.text.isNullOrEmpty()
        ){
            return false
        }

        return true
    }

    // 계약일 달력 이벤트
    private fun initContractCalendar() {
        binding.ContractDateCalendar.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.tenantIOContractDate.setText("${year}-${String.format("%02d", month+1)}-${String.format("%02d", dayOfMonth+1)}")
            }
            DatePickerDialog(this, R.style.DatePickerStyle , dateSetListener, cal.get(Calendar.YEAR),cal.get(
                Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        binding.LeaveDateCalendar.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.tenantIOLeaveDate.setText("${year}-${String.format("%02d", month+1)}-${String.format("%02d", dayOfMonth+1)}")
            }
            DatePickerDialog(this, R.style.DatePickerStyle , dateSetListener, cal.get(Calendar.YEAR),cal.get(
                Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()

        }

    }

    // 세입자 요청 선택하기
    private fun initTenantSelect() {
        binding.tenantIOSelectTenantArea.setOnClickListener {
            val requestTenantList = Intent(this,RequestTenantActivity::class.java)
            startActivity(requestTenantList)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initToday(){
        // 오늘 날짜
        val currentDay = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        binding.tenantIOContractDate.setText(currentDay.format(formatter))
        binding.tenantIOLeaveDate.setText(currentDay.format(formatter))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val ToTenantList = Intent(this, TenantListActivity::class.java)
        startActivity(ToTenantList)
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.TenantInOutToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val ToTenantList = Intent(this, TenantListActivity::class.java)
        startActivity(ToTenantList)
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