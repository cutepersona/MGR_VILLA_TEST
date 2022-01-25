package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.customdialog.NoTenantCostDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityRoomcostfortenantBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class TenantRoomCostForTenantActivity : AppCompatActivity() {


    private val binding : ActivityRoomcostfortenantBinding by lazy { ActivityRoomcostfortenantBinding.inflate(layoutInflater)}

    private var tenantRoomId : Long = 0
    private var tenantAddress : String = ""
    private var tenantRoomNumber : String = ""

    private var tonCostAmount: String = ""
    private var cleanCostAmount: String = ""
    private var usunCostAmount: String = ""
    private var mgrCostAmount: String = ""
    private var waterCostAmount: String = ""
    private var totalCostAmount: String = ""

    private var waterValue: Float = 0F
    private var totalValue: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        if (intent.hasExtra("roomNumber")){
            tenantRoomNumber = intent.getStringExtra("roomNumber").toString()
            binding.TenantCostRoomNumber.setText(tenantRoomNumber)
        }
        if (intent.hasExtra("address")){
            tenantAddress = intent.getStringExtra("address").toString()
        }


        initCalendar()

        // 오늘 날짜
        val currentDay = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        binding.TenantCostYearMonth.setText(currentDay.format(formatter))

        initChangeYearMonth()
        initConstCost()

    }

    // 년월 바뀌는 경우
    private fun initChangeYearMonth() {
        binding.TenantCostYearMonth.addTextChangedListener(object: TextWatcher {
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

                    val favoriteAccount = villaNoticedb!!.VillaNoticeDao().getFavoriteAccount()

                    val isTenantCost = villaNoticedb!!.VillaNoticeDao().getTenantCost(
                        MyApplication.prefs.getString("villaAddress", "").trim(),
                        binding.TenantCostYearMonth.text.substring(0, 4),
                        binding.TenantCostYearMonth.text.substring(5, 7),
                        tenantRoomNumber
                    )

                    runOnUiThread {
                        if (isTenantCost == null) {
                            val noTenantCostDialog = NoTenantCostDialog(this@TenantRoomCostForTenantActivity)
                            noTenantCostDialog.showDialog()

                            binding.TenantConstTonCost.setText("0")
                            binding.TenantConstCleanCost.setText("0")
                            binding.TenantConstUsunCost.setText("0")
                            binding.TenantConstMgrCost.setText("0")

                            // 초기 톤수 셋팅 1.0 값
                            binding.TenantWriteWaterTon.setText(0.toFloat().toString())

                            waterValue = binding.TenantWriteWaterTon.text.toString()
                                .toFloat() * binding.TenantConstTonCost.text.toString().replace(",", "")
                                .toFloat()
                            binding.TenantWaterCost.setText(waterValue.toInt().toString())

//                            val total =
//                                waterValue.toInt() + ConstCost.cleanCost + ConstCost.usunCost + ConstCost.mgrCost

                            binding.TenantTotalCostValue.setText("0")

//                            binding.TenantCostRoomNumber.setText(tenantRoomNumber)

                            binding.TenantCostBankName.setText(favoriteAccount.bankName)
                            binding.TenantCostAccountHolder.setText(favoriteAccount.accountHolder)
                            binding.TenantCostAccountNumber.setText(favoriteAccount.accountNumber)


                        }
                        else {
//                            binding.TenantCostUid.setText(isTenantCost.costId.toString())
//                            binding.TenantCostRoomNumber.setText(isTenantCost.roomNumber)
//                            binding.CostYearMonth.setText(isTenantCost.costYear + "-" + isTenantCost.costMonth)
                            binding.TenantTotalCostValue.setText(isTenantCost.totalCost.toString())
                            binding.TenantWriteWaterTon.setText(((isTenantCost.useTon*10).roundToInt() / 10f).toString())
                            binding.TenantConstTonCost.setText(isTenantCost.costTon.toString())
                            binding.TenantWaterCost.setText(isTenantCost.totalUseTon.toString())
                            binding.TenantConstCleanCost.setText(isTenantCost.costClean.toString())
                            binding.TenantConstUsunCost.setText(isTenantCost.costUsun.toString())
                            binding.TenantConstMgrCost.setText(isTenantCost.costMgr.toString())

                            binding.TenantCostBankName.setText(favoriteAccount.bankName)
                            binding.TenantCostAccountHolder.setText(favoriteAccount.accountHolder)
                            binding.TenantCostAccountNumber.setText(favoriteAccount.accountNumber)
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

    // 기준 관리비 셋팅
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initConstCost() {

        // 초기 톤수 셋팅 1.0 값
        binding.TenantWriteWaterTon.setText(1.toFloat().toString())


        binding.TenantConstTonCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(tonCostAmount)) {
                    tonCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.TenantConstTonCost.setText(tonCostAmount)
                }
            }
        })

        binding.TenantConstCleanCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(cleanCostAmount)) {
                    cleanCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.TenantConstCleanCost.setText(cleanCostAmount)
                }
            }
        })

        binding.TenantConstUsunCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(usunCostAmount)) {
                    usunCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.TenantConstUsunCost.setText(usunCostAmount)
                }
            }
        })

        binding.TenantConstMgrCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(mgrCostAmount)) {
                    mgrCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.TenantConstMgrCost.setText(mgrCostAmount)
                }
            }
        })



        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {
            val ConstCost = villaNoticedb!!.VillaNoticeDao().getStandardCost(
                MyApplication.prefs.getString("villaAddress","").trim()
            )

//            val roomNumber = villaNoticedb!!.VillaNoticeDao().getTenantRoom(
//                tenantRoomId
//            )

            val favoriteAccount = villaNoticedb!!.VillaNoticeDao().getFavoriteAccount()

            val isTenantCost = villaNoticedb!!.VillaNoticeDao().getTenantCost(
                MyApplication.prefs.getString("villaAddress","").trim()
                ,binding.TenantCostYearMonth.text.substring(0,4)
                ,binding.TenantCostYearMonth.text.substring(5,7)
                ,tenantRoomNumber
            )

            runOnUiThread {

                if (isTenantCost==null){

                    binding.TenantConstTonCost.setText(ConstCost.tonCost.toString())
                    binding.TenantConstCleanCost.setText(ConstCost.cleanCost.toString())
                    binding.TenantConstUsunCost.setText(ConstCost.usunCost.toString())
                    binding.TenantConstMgrCost.setText(ConstCost.mgrCost.toString())


                    waterValue = binding.TenantWriteWaterTon.text.toString().toFloat() * binding.TenantConstTonCost.text.toString().replace(",","").toFloat()
                    binding.TenantWaterCost.setText(waterValue.toInt().toString())

                    val total = waterValue.toInt() + ConstCost.cleanCost + ConstCost.usunCost + ConstCost.mgrCost

                    binding.TenantTotalCostValue.setText(total.toString())

//                    binding.TenantCostRoomNumber.setText(roomNumber)

                    binding.TenantCostBankName.setText(favoriteAccount.bankName)
                    binding.TenantCostAccountHolder.setText(favoriteAccount.accountHolder)
                    binding.TenantCostAccountNumber.setText(favoriteAccount.accountNumber)

                } else {
//                    binding.TenantCostUid.setText(isTenantCost.costId.toString())
//                    binding.TenantCostRoomNumber.setText(isTenantCost.roomNumber)
                    binding.TenantCostYearMonth.setText(isTenantCost.costYear + "-" + isTenantCost.costMonth)
                    binding.TenantTotalCostValue.setText(isTenantCost.totalCost.toString())
                    binding.TenantWriteWaterTon.setText(((isTenantCost.useTon*10).roundToInt() / 10f).toString())
                    binding.TenantConstTonCost.setText(isTenantCost.costTon.toString())
                    binding.TenantWaterCost.setText(isTenantCost.totalUseTon.toString())
                    binding.TenantConstCleanCost.setText(isTenantCost.costClean.toString())
                    binding.TenantConstUsunCost.setText(isTenantCost.costUsun.toString())
                    binding.TenantConstMgrCost.setText(isTenantCost.costMgr.toString())

                    binding.TenantCostBankName.setText(favoriteAccount.bankName)
                    binding.TenantCostAccountHolder.setText(favoriteAccount.accountHolder)
                    binding.TenantCostAccountNumber.setText(favoriteAccount.accountNumber)
                }


//                waterValue = binding.WriteWaterTon.toString().toFloat() * ConstCost.tonCost.toFloat()

            }
        }).start()


        binding.TenantTotalCostValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(totalCostAmount)) {
                    totalCostAmount = makeCommaNumber(Integer.parseInt(s.toString().replace(",","") ))
                    binding.TenantTotalCostValue.setText(totalCostAmount)
                }
            }
        })

        binding.TenantWaterCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(waterCostAmount)) {
                    waterCostAmount = makeCommaNumber(Integer.parseInt(s.toString().replace(",","") ))
                    binding.TenantWaterCost.setText(waterCostAmount)
                }
            }
        })




    }


    // 달력 초기화
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCalendar() {
        binding.TenantCostCalendar.setOnClickListener {


            val dialog = AlertDialog.Builder(this).create()
            val edialog : LayoutInflater = LayoutInflater.from(this)
            val mView : View = edialog.inflate(R.layout.dialog_yearmonthpicker, null)

            val year : NumberPicker = mView.findViewById(R.id.CostTenantYear)
            val month : NumberPicker = mView.findViewById(R.id.CostTenantMonth)
            val cancel : Button = mView.findViewById(R.id.CostTenantCancelButton)
            val ok : Button = mView.findViewById(R.id.CostTenantOkButton)

            //  순환 안되게 막기
            year.wrapSelectorWheel = false
            month.wrapSelectorWheel = false

            //  editText 설정 해제
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            // 최소값 설정
            year.minValue = 2017
            month.minValue = 1

            // 최대값 설정
            val current = LocalDateTime.now()
            val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
            val yearFormatted = current.format(yearFormatter)

            val monthFormatter = DateTimeFormatter.ofPattern("MM")
            val monthFormatted = current.format(monthFormatter)

            year.maxValue = yearFormatted.toInt()
            month.maxValue = 12

            year.value = yearFormatted.toInt()
            month.value = monthFormatted.toInt()

            //  취소 버튼 클릭 시
            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }

            //  완료 버튼 클릭 시
            ok.setOnClickListener {
                binding.TenantCostYearMonth.setText(year.value.toString() + "-" + String.format("%02d",month.value))
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.create()
            dialog.show()

        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        val ToHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(ToHome)
    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.TenantCostToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val ToHome = Intent(this, VillaHomeActivity::class.java)
        startActivity(ToHome)

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