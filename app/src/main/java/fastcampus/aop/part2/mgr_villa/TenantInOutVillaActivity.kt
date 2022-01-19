package fastcampus.aop.part2.mgr_villa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityTenantinoutBinding
import fastcampus.aop.part2.mgr_villa.model.VillaTenantCost
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TenantInOutVillaActivity : AppCompatActivity() {

    val binding: ActivityTenantinoutBinding by lazy { ActivityTenantinoutBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initTenantSelect()
        initContractCalendar()

        initToday()

        initMgrIn()
        initTenenatIntoVilla()


        if (intent.hasExtra("roomId")){
            binding.TenantIORoomId.setText(intent.getStringExtra("roomId"))
        }
        if (intent.hasExtra("roomNumber")){
            binding.tenantIORoomNumber.setText(intent.getStringExtra("roomNumber"))
        }


    }

    // 전입하기
    private fun initTenenatIntoVilla() {
        binding.RequestTenantIntoVillaButton.setOnClickListener {
            Thread(Runnable {
                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

                val mgrUser = villaNoticedb!!.VillaNoticeDao().intoTenant(
                    binding.tenantIOEmail.text.toString().trim()
                    ,binding.tenantIOContractDate.text.toString().trim()
                    ,binding.tenantIOLeaveDate.text.toString().trim()
                    ,MyApplication.prefs.getString("villaAddress","").trim()
                    ,binding.TenantIORoomId.text.toString().toLong()
                )

                runOnUiThread {
//                        showToast(binding.tenantIOEmail.text.toString().trim())
//                    showToast(binding.tenantIOContractDate.text.toString().trim())
//                    showToast(binding.tenantIOLeaveDate.text.toString().trim())
//                    showToast(MyApplication.prefs.getString("villaAddress","").trim())
//                    showToast(binding.TenantIORoomId.text.toString())

                    showToast("전입이 완료되었습니다.")
                    val toTenantList = Intent(this, TenantListActivity::class.java)
                    startActivity(toTenantList)
                }
            }).start()
        }
    }

    // 관리자 전입 요청
    private fun initMgrIn() {
        binding.mgrTenantIn.setOnClickListener {

            Thread(Runnable {
                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

                    val mgrUser = villaNoticedb!!.VillaNoticeDao().getMgrUser(
                            MyApplication.prefs.getString("villaAddress","").trim()
                    )

                    runOnUiThread {
                        binding.tenantIOEmail.setText(mgrUser.mailAddress)
                        binding.tenantIOTenantName.setText(mgrUser.userName)
                        binding.tenantIOContractDate.setText("-")
                        binding.tenantIOLeaveDate.setText("-")
                    }
            }).start()

        }
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