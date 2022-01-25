package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import fastcampus.aop.part2.mgr_villa.customdialog.NoTenantCostDialog
import fastcampus.aop.part2.mgr_villa.customdialog.RequestTenantDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityRoomcostformgrBinding
import fastcampus.aop.part2.mgr_villa.model.VillaAccount
import fastcampus.aop.part2.mgr_villa.model.VillaTenantCost
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.coroutines.*
import java.lang.Runnable
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class TenantRoomCostForMGRActivity: AppCompatActivity() {

    private val binding: ActivityRoomcostformgrBinding by lazy {ActivityRoomcostformgrBinding.inflate(layoutInflater)}
    private var tenantRoomId : Long = 0

    private var tonCostAmount: String = ""
    private var cleanCostAmount: String = ""
    private var usunCostAmount: String = ""
    private var mgrCostAmount: String = ""
    private var waterCostAmount: String = ""
    private var totalCostAmount: String = ""

    private var waterValue: Float = 0F
    private var totalValue: Int = 0

    private var tonUpdateFlag: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra("tenantRoomId")){
            tenantRoomId = intent.getLongExtra("tenantRoomId", 0)
        }

        initToolBar()


        // 오늘 날짜
        val currentDay = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        binding.CostYearMonth.setText(currentDay.format(formatter))

        initChangeYearMonth()

        initConstCost()

        initWriteTon()
        initCalendar()
        initInsertMgrCost()





    }

    // 년월 바뀌는 경우
    private fun initChangeYearMonth() {
        binding.CostYearMonth.addTextChangedListener(object:TextWatcher{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                Thread(Runnable {
                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
                    val ConstCost = villaNoticedb!!.VillaNoticeDao().getStandardCost(
                        MyApplication.prefs.getString("villaAddress", "").trim()
                    )

                    val roomNumber = villaNoticedb!!.VillaNoticeDao().getTenantRoom(
                        tenantRoomId
                    )

                    val favoriteAccount = villaNoticedb!!.VillaNoticeDao().getFavoriteAccount()

                    val isTenantCost = villaNoticedb!!.VillaNoticeDao().getTenantCost(
                        MyApplication.prefs.getString("villaAddress", "").trim(),
                        binding.CostYearMonth.text.substring(0, 4),
                        binding.CostYearMonth.text.substring(5, 7),
                        roomNumber
                    )

                    runOnUiThread {


                        if (isTenantCost == null) {

                            val noTenantCostDialog = NoTenantCostDialog(this@TenantRoomCostForMGRActivity)
                            noTenantCostDialog.showDialog()
//
//                        showToast(binding.CostYearMonth.text.substring(0,4) + "-" + binding.CostYearMonth.text.substring(5, 7)
//                        + "의 등록된 관리비 정보가 없습니다.")


//                            // 오늘 날짜
//                            val currentDay = LocalDateTime.now()
//                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
//                            binding.CostYearMonth.setText(currentDay.format(formatter))
//                            binding.CostYearMonth.setText(
//                                binding.CostYearMonth.text.substring(0,4) + "-" + binding.CostYearMonth.text.substring(5, 7)
//                            )

                            binding.ConstTonCost.setText(ConstCost.tonCost.toString())
                            binding.ConstCleanCost.setText(ConstCost.cleanCost.toString())
                            binding.ConstUsunCost.setText(ConstCost.usunCost.toString())
                            binding.ConstMgrCost.setText(ConstCost.mgrCost.toString())

                            // 초기 톤수 셋팅 1.0 값
                            binding.WriteWaterTon.setText(1.toFloat().toString())

                            waterValue = binding.WriteWaterTon.text.toString()
                                .toFloat() * binding.ConstTonCost.text.toString().replace(",", "")
                                .toFloat()
                            binding.waterCost.setText(waterValue.toInt().toString())

                            val total =
                                waterValue.toInt() + ConstCost.cleanCost + ConstCost.usunCost + ConstCost.mgrCost

                            binding.TotalCostValue.setText(total.toString())

                            binding.CostRoomNumber.setText(roomNumber)

                            binding.CostBankName.setText(favoriteAccount.bankName)
                            binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
                            binding.CostAccountNumber.setText(favoriteAccount.accountNumber)


                        }
                                                else {
                            binding.CostUid.setText(isTenantCost.costId.toString())
                            binding.CostRoomNumber.setText(isTenantCost.roomNumber)
//                            binding.CostYearMonth.setText(isTenantCost.costYear + "-" + isTenantCost.costMonth)
                            binding.TotalCostValue.setText(isTenantCost.totalCost.toString())
                            binding.WriteWaterTon.setText(((isTenantCost.useTon*10).roundToInt() / 10f).toString())
                            binding.ConstTonCost.setText(isTenantCost.costTon.toString())
                            binding.waterCost.setText(isTenantCost.totalUseTon.toString())
                            binding.ConstCleanCost.setText(isTenantCost.costClean.toString())
                            binding.ConstUsunCost.setText(isTenantCost.costUsun.toString())
                            binding.ConstMgrCost.setText(isTenantCost.costMgr.toString())

                            binding.CostBankName.setText(favoriteAccount.bankName)
                            binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
                            binding.CostAccountNumber.setText(favoriteAccount.accountNumber)
                        }


//                waterValue = binding.WriteWaterTon.toString().toFloat() * ConstCost.tonCost.toFloat()

                    }
                }).start()

//                runBlocking {
//                    delay(500L)
//                    job.cancel()
//                }

            }

        })
    }

    // 관리비 등록하기
    private fun initInsertMgrCost() {
        binding.WriteRoomCostButton.setOnClickListener {
            Thread(Runnable {
                if (binding.CostUid.text.isNullOrEmpty()){
                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

                    villaNoticedb!!.VillaNoticeDao().tenantCostInsert(
                        VillaTenantCost(
                            null
                            ,binding.CostRoomNumber.text.toString().trim()
                            ,binding.TotalCostValue.text.toString().replace(",","").toInt()
                            ,binding.CostYearMonth.text.substring(0,4)
                            ,binding.CostYearMonth.text.substring(5,7)
                            ,binding.WriteWaterTon.text.toString().replace(",","").toFloat()
                            ,binding.ConstTonCost.text.toString().replace(",","").toInt()
                            ,binding.waterCost.text.toString().replace(",","").toInt()
                            ,binding.ConstCleanCost.text.toString().replace(",","").toInt()
                            ,binding.ConstUsunCost.text.toString().replace(",","").toInt()
                            ,binding.ConstMgrCost.text.toString().replace(",","").toInt()
                            ,""
                            ,MyApplication.prefs.getString("villaAddress","").trim()
                        )
                    )

                    runOnUiThread {
                        showToast(binding.CostRoomNumber.text.toString().trim()+" 의 " + binding.CostYearMonth.text.substring(0,4) + "년 " + binding.CostYearMonth.text.substring(5,7) +"월 관리비가 등록되었습니다.")
//                        val toCostList = Intent(this, TenantCostListActivity::class.java)
//                        startActivity(toCostList)
                    }

                } else {
                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

                    villaNoticedb!!.VillaNoticeDao().updateTenantCostForId(
                        binding.TotalCostValue.text.toString().replace(",","").toInt()
                        ,binding.WriteWaterTon.text.toString().replace(",","").toFloat()
                        ,binding.ConstTonCost.text.toString().replace(",","").toInt()
                        ,binding.waterCost.text.toString().replace(",","").toInt()
                        ,binding.ConstCleanCost.text.toString().replace(",","").toInt()
                        ,binding.ConstUsunCost.text.toString().replace(",","").toInt()
                        ,binding.ConstMgrCost.text.toString().replace(",","").toInt()
                         ,binding.CostUid.text.toString().toLong()
                        )
                    runOnUiThread {
                        if (!tonUpdateFlag){
                            showToast("톤 수 수정후 완료 버튼을 클릭해주세요.")
                        } else {
                            showToast(binding.CostRoomNumber.text.toString().trim()+" 의 " + binding.CostYearMonth.text.substring(0,4) + "년 " + binding.CostYearMonth.text.substring(5,7) +"월 관리비가 수정되었습니다.")
//                            val toCostList = Intent(this, TenantCostListActivity::class.java)
//                            startActivity(toCostList)
                        }
                    }
                }

            }).start()
        }
    }

    // 달력 초기화
    private fun initCalendar() {
        binding.CostCalendar.setOnClickListener {

            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.CostYearMonth.setText("${year}-${String.format("%02d", month+1)}")
            }

            val datePickerDialog = DatePickerDialog(this, R.style.DatePickerStyle , dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

    }

    // 톤수 입력하는 부분
    private fun initWriteTon() {
        binding.WriteWaterTon.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if(actionId == EditorInfo.IME_ACTION_DONE){
                waterValue = binding.WriteWaterTon.text.toString().toFloat() * binding.ConstTonCost.text.toString().replace(",","").toFloat()
//                waterCostAmount = waterValue.toInt().toString()
                binding.waterCost.setText(waterValue.toInt().toString())

                totalValue = waterValue.toInt() + binding.ConstCleanCost.text.toString().replace(",","").toInt() + binding.ConstUsunCost.text.toString().replace(",","").toInt() +binding.ConstMgrCost.text.toString().replace(",","").toInt()
                binding.TotalCostValue.setText(totalValue.toString())

//                handled = true
                tonUpdateFlag = true
            }
            handled
        }
    }


    // 기준 관리비 셋팅
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initConstCost() {

        // 초기 톤수 셋팅 1.0 값
        binding.WriteWaterTon.setText(1.toFloat().toString())


        binding.ConstTonCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(tonCostAmount)) {
                    tonCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.ConstTonCost.setText(tonCostAmount)
                }
            }
        })

        binding.ConstCleanCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(cleanCostAmount)) {
                    cleanCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.ConstCleanCost.setText(cleanCostAmount)
                }
            }
        })

        binding.ConstUsunCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(usunCostAmount)) {
                    usunCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.ConstUsunCost.setText(usunCostAmount)
                }
            }
        })

        binding.ConstMgrCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(mgrCostAmount)) {
                    mgrCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.ConstMgrCost.setText(mgrCostAmount)
                }
            }
        })



        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {
            val ConstCost = villaNoticedb!!.VillaNoticeDao().getStandardCost(
                MyApplication.prefs.getString("villaAddress","").trim()
            )

            val roomNumber = villaNoticedb!!.VillaNoticeDao().getTenantRoom(
                tenantRoomId
            )

            val favoriteAccount = villaNoticedb!!.VillaNoticeDao().getFavoriteAccount()

            val isTenantCost = villaNoticedb!!.VillaNoticeDao().getTenantCost(
                MyApplication.prefs.getString("villaAddress","").trim(),binding.CostYearMonth.text.substring(0,4),binding.CostYearMonth.text.substring(5,7),roomNumber
            )

            runOnUiThread {

                if (isTenantCost==null){

                    binding.ConstTonCost.setText(ConstCost.tonCost.toString())
                    binding.ConstCleanCost.setText(ConstCost.cleanCost.toString())
                    binding.ConstUsunCost.setText(ConstCost.usunCost.toString())
                    binding.ConstMgrCost.setText(ConstCost.mgrCost.toString())


                    waterValue = binding.WriteWaterTon.text.toString().toFloat() * binding.ConstTonCost.text.toString().replace(",","").toFloat()
                    binding.waterCost.setText(waterValue.toInt().toString())

                    val total = waterValue.toInt() + ConstCost.cleanCost + ConstCost.usunCost + ConstCost.mgrCost

                    binding.TotalCostValue.setText(total.toString())

                    binding.CostRoomNumber.setText(roomNumber)

                    binding.CostBankName.setText(favoriteAccount.bankName)
                    binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
                    binding.CostAccountNumber.setText(favoriteAccount.accountNumber)

                } else {
                    binding.CostUid.setText(isTenantCost.costId.toString())
                    binding.CostRoomNumber.setText(isTenantCost.roomNumber)
                    binding.CostYearMonth.setText(isTenantCost.costYear + "-" + isTenantCost.costMonth)
                    binding.TotalCostValue.setText(isTenantCost.totalCost.toString())
                    binding.WriteWaterTon.setText(((isTenantCost.useTon*10).roundToInt() / 10f).toString())
                    binding.ConstTonCost.setText(isTenantCost.costTon.toString())
                    binding.waterCost.setText(isTenantCost.totalUseTon.toString())
                    binding.ConstCleanCost.setText(isTenantCost.costClean.toString())
                    binding.ConstUsunCost.setText(isTenantCost.costUsun.toString())
                    binding.ConstMgrCost.setText(isTenantCost.costMgr.toString())

                    binding.CostBankName.setText(favoriteAccount.bankName)
                    binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
                    binding.CostAccountNumber.setText(favoriteAccount.accountNumber)
                }


//                waterValue = binding.WriteWaterTon.toString().toFloat() * ConstCost.tonCost.toFloat()

            }
        }).start()


        binding.TotalCostValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(totalCostAmount)) {
                    totalCostAmount = makeCommaNumber(Integer.parseInt(s.toString().replace(",","") ))
                    binding.TotalCostValue.setText(totalCostAmount)
                }
            }
        })

        binding.waterCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(waterCostAmount)) {
                    waterCostAmount = makeCommaNumber(Integer.parseInt(s.toString().replace(",","") ))
                    binding.waterCost.setText(waterCostAmount)
                }
            }
        })




    }

    override fun onBackPressed() {
        super.onBackPressed()
        val TenantCostList = Intent(this, TenantCostListActivity::class.java)
        startActivity(TenantCostList)

    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.MgrCostToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val TenantCostList = Intent(this, TenantCostListActivity::class.java)
        startActivity(TenantCostList)

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

    private fun makeCommaNumber(input:Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}