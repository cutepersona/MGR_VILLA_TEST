package fastcampus.aop.part2.mgr_villa

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.customdialog.NoTenantCostDialog
import fastcampus.aop.part2.mgr_villa.customdialog.RequestTenantDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityRoomcostformgrBinding
import fastcampus.aop.part2.mgr_villa.model.VillaAccount
import fastcampus.aop.part2.mgr_villa.model.VillaTenantCost
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.coroutines.*
import java.lang.Exception
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

    val firestoreDB = Firebase.firestore

    private var tenantRoomId : String = ""
    private var tenantRoomNumber : String = ""

    private var tonCostAmount: String = ""
    private var cleanCostAmount: String = ""
    private var usunCostAmount: String = ""
    private var mgrCostAmount: String = ""
    private var waterCostAmount: String = ""
    private var totalCostAmount: String = ""

    private var waterValue: Float = 0F
    private var totalValue: Int = 0

    private var tonUpdateFlag: Boolean = false

    var scrollY: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra("tenantRoomId")){
            tenantRoomId = intent.getStringExtra("tenantRoomId").toString()
        }
        if (intent.hasExtra("tenantRoomNumber")){
            tenantRoomNumber = intent.getStringExtra("tenantRoomNumber").toString()
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

        initFocusEditText()
        initEmailEditTextCheck()



    }

    private fun initFocusEditText() {
        binding.WriteWaterTon.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    scrollY = 0
                    binding.MgrCostScrollView.smoothScrollToView(binding.WriteWaterTon)
                }
            }
        })
    }

    fun ScrollView.smoothScrollToView(
        view: View,
        marginTop: Int = 0,
        maxDuration: Long = 500L,
        onEnd: () -> Unit = {}
    ) {
        if (this.getChildAt(0).height <= this.height) { // 스크롤의 의미가 없다.
            onEnd()
            return
        }
        val y = computeDistanceToView(view) - marginTop
        val ratio = Math.abs(y - this.scrollY) / (this.getChildAt(0).height - this.height).toFloat()
        ObjectAnimator.ofInt(this, "scrollY", y).apply {
            duration = (maxDuration * ratio).toLong()
            interpolator = AccelerateDecelerateInterpolator()
            doOnEnd {
                onEnd()
            }
            start()
        }
    }

    fun computeDistanceToView(view: View): Int {
        return Math.abs(
            calculateRectOnScreen(binding.MgrCostScrollView).top - (this.scrollY + calculateRectOnScreen(view).top)
        )
    }

    fun calculateRectOnScreen(view: View): Rect {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Rect(
            location[0],
            location[1],
            location[0] + view.measuredWidth,
            location[1] + view.measuredHeight
        )
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

                firestoreDB.collection("VillaTenantCost")
                    .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
                    .whereEqualTo("costYear",binding.CostYearMonth.text.substring(0, 4))
                    .whereEqualTo("costMonth",binding.CostYearMonth.text.substring(5, 7))
                    .whereEqualTo("roomNumber",tenantRoomNumber)
                    .get()
                    .addOnSuccessListener { tenantCostResult ->
                        // 관리비 등록 이력이 있을때
                        if (!tenantCostResult.isEmpty) {
                            for(i in tenantCostResult!!){
                                binding.CostUid.setText(i.id)

                                binding.CostRoomNumber.setText(i.data["roomNumber"].toString())
                                binding.TotalCostValue.setText(i.data["totalCost"].toString())
                                binding.WriteWaterTon.setText( ( ( i.data["useTon"].toString().toFloat() * 10 ).roundToInt() / 10f ).toString() )
                                binding.ConstTonCost.setText(i.data["costTon"].toString())
                                binding.waterCost.setText(i.data["totalUseTon"].toString())
                                binding.ConstCleanCost.setText(i.data["costClean"].toString())
                                binding.ConstUsunCost.setText(i.data["costUsun"].toString())
                                binding.ConstMgrCost.setText(i.data["costMgr"].toString())
                                break
                            }
                            firestoreDB.collection("VillaAccount")
                                .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
                                .whereEqualTo("favorite", "favorite")
                                .get()
                                .addOnSuccessListener { result ->
                                    if (!result.isEmpty){
                                        for (i in result!!) {
                                            binding.CostBankName.setText(i.data["bankName"].toString().trim())
                                            binding.CostAccountHolder.setText(i.data["accountHolder"].toString().trim())
                                            binding.CostAccountNumber.setText(i.data["accountNumber"].toString().trim())
                                            break
                                        }
                                    }
                                }
                        } else {

                            val noTenantCostDialog = NoTenantCostDialog(this@TenantRoomCostForMGRActivity)
                            noTenantCostDialog.showDialog()

                            binding.CostRoomNumber.setText(tenantRoomNumber)

                            binding.ConstTonCost.setText("0")
                            binding.ConstCleanCost.setText("0")
                            binding.ConstUsunCost.setText("0")
                            binding.ConstMgrCost.setText("0")

                            // 초기 톤수 셋팅 1.0 값
                            binding.WriteWaterTon.setText(0.toFloat().toString())

                            waterValue = binding.WriteWaterTon.text.toString()
                                .toFloat() * binding.ConstTonCost.text.toString().replace(",", "")
                                .toFloat()
                            binding.waterCost.setText(waterValue.toInt().toString())
                            binding.TotalCostValue.setText("0")

                            firestoreDB.collection("VillaAccount")
                                .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
                                .whereEqualTo("favorite", "favorite")
                                .get()
                                .addOnSuccessListener { result ->
                                    if (!result.isEmpty){
                                        for (i in result!!) {
                                            binding.CostBankName.setText(i.data["bankName"].toString().trim())
                                            binding.CostAccountHolder.setText(i.data["accountHolder"].toString().trim())
                                            binding.CostAccountNumber.setText(i.data["accountNumber"].toString().trim())
                                            break
                                        }
                                    }
                                }

                            // 등록이력이 없을 때
//                            firestoreDB.collection("StandardCost")
//                                .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
//                                .get()
//                                .addOnSuccessListener { result ->
//                                    if (!result.isEmpty) {
//                                        for (i in result!!) {
//                                            binding.ConstTonCost.setText(i.data["tonCost"].toString())
//                                            binding.ConstCleanCost.setText(i.data["cleanCost"].toString())
//                                            binding.ConstUsunCost.setText(i.data["usunCost"].toString())
//                                            binding.ConstMgrCost.setText(i.data["mgrCost"].toString())
//
//                                            val total = waterValue.toInt() + i.data["cleanCost"].toString()
//                                                .toInt() + i.data["usunCost"].toString()
//                                                .toInt() + i.data["mgrCost"].toString().toInt()
//
//                                            waterValue = binding.WriteWaterTon.text.toString()
//                                                .toFloat() * binding.ConstTonCost.text.toString()
//                                                .replace(",", "").toFloat()
//                                            binding.waterCost.setText(waterValue.toInt().toString())
//
//                                            binding.TotalCostValue.setText(total.toString())
//
//                                            binding.CostRoomNumber.setText(tenantRoomNumer)
//
//                                            break
//                                        }
//                                        firestoreDB.collection("VillaAccount")
//                                            .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
//                                            .get()
//                                            .addOnSuccessListener { result ->
//                                                if (!result.isEmpty){
//                                                    for (i in result!!) {
//                                                        binding.CostBankName.setText(i.data["bankName"].toString().trim())
//                                                        binding.CostAccountHolder.setText(i.data["accountHolder"].toString().trim())
//                                                        binding.CostAccountNumber.setText(i.data["accountNumber"].toString().trim())
//                                                        break
//                                                    }
//                                                }
//                                            }
//
//                                    }
//                                }
                        }
                    }

//                firestoreDB.collection("VillaTenantCost")
//                    .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
//                    .get()
//                    .addOnSuccessListener { results ->
//                        if(results.isEmpty){
//                            showToast("주 계좌가 등록되어 있지 않습니다.")
//                            return@addOnSuccessListener
//                        } else {
//                            firestoreDB.collection("VillaTenantCost")
//                                .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
//                                .whereEqualTo("costYear",binding.CostYearMonth.text.substring(0, 4))
//                                .whereEqualTo("costMonth",binding.CostYearMonth.text.substring(5, 7))
//                                .whereEqualTo("costMonth",binding.CostYearMonth.text.substring(5, 7))
//                                .get()
//                                .addOnSuccessListener { results ->
//                                    if(results.isEmpty){
//                                        showToast("기준관리비가 등록되어 있지 않습니다.")
//                                        return@addOnSuccessListener
//                                    } else {
//                                        val TenantCostListActivity = Intent(this, TenantCostListActivity::class.java)
//                                        startActivity(TenantCostListActivity)
//                                    }
//                                }
//                        }
//                    }


//------------------------------------------------------------------------------------------------------------------------------------
//                Thread(Runnable {
//                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//                    val ConstCost = villaNoticedb!!.VillaNoticeDao().getStandardCost(
//                        MyApplication.prefs.getString("villaAddress", "").trim()
//                    )
//
//                    val roomNumber = villaNoticedb!!.VillaNoticeDao().getTenantRoom(
//                        tenantRoomId
//                    )
//
//                    val favoriteAccount = villaNoticedb!!.VillaNoticeDao().getFavoriteAccount()
//
//                    val isTenantCost = villaNoticedb!!.VillaNoticeDao().getTenantCost(
//                        MyApplication.prefs.getString("villaAddress", "").trim(),
//                        binding.CostYearMonth.text.substring(0, 4),
//                        binding.CostYearMonth.text.substring(5, 7),
//                        roomNumber
//                    )
//
//                    runOnUiThread {
//
//
//                        if (isTenantCost == null) {
//
//                            val noTenantCostDialog = NoTenantCostDialog(this@TenantRoomCostForMGRActivity)
//                            noTenantCostDialog.showDialog()
//
//                            binding.ConstTonCost.setText(ConstCost.tonCost.toString())
//                            binding.ConstCleanCost.setText(ConstCost.cleanCost.toString())
//                            binding.ConstUsunCost.setText(ConstCost.usunCost.toString())
//                            binding.ConstMgrCost.setText(ConstCost.mgrCost.toString())
//
//                            // 초기 톤수 셋팅 1.0 값
//                            binding.WriteWaterTon.setText(1.toFloat().toString())
//
//                            waterValue = binding.WriteWaterTon.text.toString()
//                                .toFloat() * binding.ConstTonCost.text.toString().replace(",", "")
//                                .toFloat()
//                            binding.waterCost.setText(waterValue.toInt().toString())
//
//                            val total =
//                                waterValue.toInt() + ConstCost.cleanCost + ConstCost.usunCost + ConstCost.mgrCost
//
//                            binding.TotalCostValue.setText(total.toString())
//
//                            binding.CostRoomNumber.setText(roomNumber)
//
//                            binding.CostBankName.setText(favoriteAccount.bankName)
//                            binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
//                            binding.CostAccountNumber.setText(favoriteAccount.accountNumber)
//
//
//                        }
//                                                else {
//                            binding.CostUid.setText(isTenantCost.costId.toString())
//                            binding.CostRoomNumber.setText(isTenantCost.roomNumber)
////                            binding.CostYearMonth.setText(isTenantCost.costYear + "-" + isTenantCost.costMonth)
//                            binding.TotalCostValue.setText(isTenantCost.totalCost.toString())
//                            binding.WriteWaterTon.setText(((isTenantCost.useTon*10).roundToInt() / 10f).toString())
//                            binding.ConstTonCost.setText(isTenantCost.costTon.toString())
//                            binding.waterCost.setText(isTenantCost.totalUseTon.toString())
//                            binding.ConstCleanCost.setText(isTenantCost.costClean.toString())
//                            binding.ConstUsunCost.setText(isTenantCost.costUsun.toString())
//                            binding.ConstMgrCost.setText(isTenantCost.costMgr.toString())
//
//                            binding.CostBankName.setText(favoriteAccount.bankName)
//                            binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
//                            binding.CostAccountNumber.setText(favoriteAccount.accountNumber)
//                        }
//                    }
//                }).start()
//------------------------------------------------------------------------------------------------------------------------------------


            }

        })
    }

    // 관리비 등록하기
    private fun initInsertMgrCost() {
        binding.WriteRoomCostButton.setOnClickListener {

            firestoreDB.collection("VillaTenantCost")
                .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
                .whereEqualTo("costYear",binding.CostYearMonth.text.substring(0, 4))
                .whereEqualTo("costMonth",binding.CostYearMonth.text.substring(5, 7))
                .whereEqualTo("roomNumber",tenantRoomNumber)
                .get()
                .addOnSuccessListener { tenantCostResult ->
                    // 관리비 등록 이력이 있을때
                    if (!tenantCostResult.isEmpty) {
                        for (i in tenantCostResult!!) {
                            if (tonUpdateFlag) {
                                firestoreDB.collection("VillaTenantCost")
                                    .document(i.id)
                                    .update(
                                        mapOf(
                                            "costId" to binding.CostUid.text.toString().trim(),
                                            "roomNumber" to binding.CostRoomNumber.text.toString().trim(),
                                            "totalCost" to binding.TotalCostValue.text.toString().replace(",", "").toInt(),
                                            "costYear" to binding.CostYearMonth.text.substring(0,4),
                                            "costMonth" to binding.CostYearMonth.text.substring(5,7),
                                            "useTon" to binding.WriteWaterTon.text.toString().replace(",", "").toFloat(),
                                            "costTon" to binding.ConstTonCost.text.toString().replace(",", "").toInt(),
                                            "totalUseTon" to binding.waterCost.text.toString().replace(",", "").toInt(),
                                            "costClean" to binding.ConstCleanCost.text.toString().replace(",", "").toInt(),
                                            "costUsun" to binding.ConstUsunCost.text.toString().replace(",", "").toInt(),
                                            "costMgr" to binding.ConstMgrCost.text.toString().replace(",", "").toInt(),
                                            "costStatus" to "",
                                            "villaAddr" to MyApplication.prefs.getString("villaAddress","").trim()
                                        )
                                    )
                                    .addOnSuccessListener {
                                        val toCostList =
                                            Intent(this, TenantCostListActivity::class.java)
                                        startActivity(toCostList)
                                    }
                                break
                            }
                        }
                    } else {
                        // 신규등록하는 경우
                        if (tonUpdateFlag){
                            val villaTenantCost = firestoreDB.collection("VillaTenantCost")

                            val VillaTenantCost = hashMapOf(
                                "costId" to "0",
                                "roomNumber" to tenantRoomNumber,
                                "totalCost" to binding.TotalCostValue.text.toString().replace(",","").toInt(),
                                "costYear" to binding.CostYearMonth.text.substring(0,4),
                                "costMonth" to binding.CostYearMonth.text.substring(5,7),
                                "useTon" to binding.WriteWaterTon.text.toString().replace(",","").toFloat(),
                                "costTon" to binding.ConstTonCost.text.toString().replace(",","").toInt(),
                                "totalUseTon" to binding.waterCost.text.toString().replace(",","").toInt(),
                                "costClean" to binding.ConstCleanCost.text.toString().replace(",","").toInt(),
                                "costUsun" to binding.ConstUsunCost.text.toString().replace(",","").toInt(),
                                "costMgr" to binding.ConstMgrCost.text.toString().replace(",","").toInt(),
                                "costStatus" to "",
                                "villaAddr" to MyApplication.prefs.getString("villaAddress", "").trim()
                            )

                            villaTenantCost.document(MyApplication.prefs.getString("villaAddress", "").trim() + "_" + tenantRoomNumber.trim() + "_" + binding.CostYearMonth.text.substring(0,4) + "_" + binding.CostYearMonth.text.substring(5,7))
                                .set(VillaTenantCost)
                                .addOnSuccessListener { documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                    val toCostList = Intent(this, TenantCostListActivity::class.java)
                                    startActivity(toCostList)
                                }
                                .addOnFailureListener { e ->
                                    showToast("관리비 등록에 실패하였습니다.")
                                    Log.w(ContentValues.TAG, "Error adding document", e)
                                    return@addOnFailureListener
                                }
                        } else {
                            showToast("톤 수 수정후 완료 버튼을 클릭해주세요.")
                            return@addOnSuccessListener
                        }
                    }
                }









//------------------------------------------------------------------------------------------------------------
//            Thread(Runnable {
//                if (binding.CostUid.text.isNullOrEmpty()){
//                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//                    villaNoticedb!!.VillaNoticeDao().tenantCostInsert(
//                        VillaTenantCost(
//                            ""
//                            ,binding.CostRoomNumber.text.toString().trim()
//                            ,binding.TotalCostValue.text.toString().replace(",","").toInt()
//                            ,binding.CostYearMonth.text.substring(0,4)
//                            ,binding.CostYearMonth.text.substring(5,7)
//                            ,binding.WriteWaterTon.text.toString().replace(",","").toFloat()
//                            ,binding.ConstTonCost.text.toString().replace(",","").toInt()
//                            ,binding.waterCost.text.toString().replace(",","").toInt()
//                            ,binding.ConstCleanCost.text.toString().replace(",","").toInt()
//                            ,binding.ConstUsunCost.text.toString().replace(",","").toInt()
//                            ,binding.ConstMgrCost.text.toString().replace(",","").toInt()
//                            ,""
//                            ,MyApplication.prefs.getString("villaAddress","").trim()
//                        )
//                    )
//
//                    runOnUiThread {
//                        showToast(binding.CostRoomNumber.text.toString().trim()+" 의 " + binding.CostYearMonth.text.substring(0,4) + "년 " + binding.CostYearMonth.text.substring(5,7) +"월 관리비가 등록되었습니다.")
//                        val toCostList = Intent(this, TenantCostListActivity::class.java)
//                        startActivity(toCostList)
//                    }
//
//                } else {
//                    val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//                    villaNoticedb!!.VillaNoticeDao().updateTenantCostForId(
//                        binding.TotalCostValue.text.toString().replace(",","").toInt()
//                        ,binding.WriteWaterTon.text.toString().replace(",","").toFloat()
//                        ,binding.ConstTonCost.text.toString().replace(",","").toInt()
//                        ,binding.waterCost.text.toString().replace(",","").toInt()
//                        ,binding.ConstCleanCost.text.toString().replace(",","").toInt()
//                        ,binding.ConstUsunCost.text.toString().replace(",","").toInt()
//                        ,binding.ConstMgrCost.text.toString().replace(",","").toInt()
//                         ,binding.CostUid.text.toString().toLong()
//                        )
//                    runOnUiThread {
//                        if (!tonUpdateFlag){
//                            showToast("톤 수 수정후 완료 버튼을 클릭해주세요.")
//                        } else {
//                            showToast(binding.CostRoomNumber.text.toString().trim()+" 의 " + binding.CostYearMonth.text.substring(0,4) + "년 " + binding.CostYearMonth.text.substring(5,7) +"월 관리비가 수정되었습니다.")
//                            val toCostList = Intent(this, TenantCostListActivity::class.java)
//                            startActivity(toCostList)
//                        }
//                    }
//                }
//
//            }).start()
//------------------------------------------------------------------------------------------------------------


        }
    }

    // 달력 초기화
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCalendar() {
        binding.CostCalendar.setOnClickListener {

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
            year.minValue = 2019
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
                binding.CostYearMonth.setText(year.value.toString() + "-" + String.format("%02d",month.value))
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.create()
            dialog.show()
//
//            val cal = Calendar.getInstance()    //캘린더뷰 만들기
//            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
//                binding.CostYearMonth.setText("${year}-${String.format("%02d", month+1)}")
//            }
//
//            val datePickerDialog = DatePickerDialog(this, R.style.DatePickerStyle , dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
//            datePickerDialog.show()
        }

    }


    private fun initEmailEditTextCheck() {
        binding.WriteWaterTon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.WriteWaterTon.text.isNullOrEmpty()){
                    showToast("톤수를 입력해 주세요.")
                    binding.WriteWaterTon.setText("1.0")
                    binding.WriteWaterTon.setSelection(binding.WriteWaterTon.length())
                    return
                }
            }
        })
    }

    // 톤수 입력하는 부분
    private fun initWriteTon() {
        binding.WriteWaterTon.setOnEditorActionListener { v, actionId, event ->

            val handled = false

                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    firestoreDB.collection("StandardCost")
                        .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                for (i in result!!) {
                                    binding.ConstTonCost.setText(i.data["tonCost"].toString())
                                    binding.ConstCleanCost.setText(i.data["cleanCost"].toString())
                                    binding.ConstUsunCost.setText(i.data["usunCost"].toString())
                                    binding.ConstMgrCost.setText(i.data["mgrCost"].toString())
                                    break
                                }

                                waterValue = binding.WriteWaterTon.text.toString().toFloat() * binding.ConstTonCost.text.toString().replace(",","").toFloat()
//                waterCostAmount = waterValue.toInt().toString()
                                binding.waterCost.setText(waterValue.toInt().toString())

                                totalValue = waterValue.toInt() + binding.ConstCleanCost.text.toString().replace(",","").toInt() + binding.ConstUsunCost.text.toString().replace(",","").toInt() +binding.ConstMgrCost.text.toString().replace(",","").toInt()
                                binding.TotalCostValue.setText(totalValue.toString())

//                handled = true
                                tonUpdateFlag = true
                            }


                        }

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

        firestoreDB.collection("VillaTenantCost")
            .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
            .whereEqualTo("costYear",binding.CostYearMonth.text.substring(0, 4))
            .whereEqualTo("costMonth",binding.CostYearMonth.text.substring(5, 7))
            .whereEqualTo("roomNumber",tenantRoomNumber)
            .get()
            .addOnSuccessListener { tenantCostResult ->
                // 관리비 등록 이력이 있을때
                if (!tenantCostResult.isEmpty) {
                    for(i in tenantCostResult!!){
                        binding.CostUid.setText(i.id)
                        binding.CostRoomNumber.setText(i.data["roomNumber"].toString())
                        binding.CostYearMonth.setText(i.data["costYear"].toString() + "-" + i.data["costMonth"].toString())
                        binding.TotalCostValue.setText(i.data["totalCost"].toString())
                        binding.WriteWaterTon.setText( ( ( i.data["useTon"].toString().toFloat() * 10 ).roundToInt() / 10f ).toString() )
                        binding.ConstTonCost.setText(i.data["costTon"].toString())
                        binding.waterCost.setText(i.data["totalUseTon"].toString())
                        binding.ConstCleanCost.setText(i.data["costClean"].toString())
                        binding.ConstUsunCost.setText(i.data["costUsun"].toString())
                        binding.ConstMgrCost.setText(i.data["costMgr"].toString())
                        break
                    }
                    firestoreDB.collection("VillaAccount")
                        .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
                        .whereEqualTo("favorite", "favorite")
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty){
                                for (i in result!!) {
                                    binding.CostBankName.setText(i.data["bankName"].toString().trim())
                                    binding.CostAccountHolder.setText(i.data["accountHolder"].toString().trim())
                                    binding.CostAccountNumber.setText(i.data["accountNumber"].toString().trim())
                                    break
                                }
                            }
                        }
                } else {

                    // 등록이력이 없을 때
                    val noTenantCostDialog = NoTenantCostDialog(this@TenantRoomCostForMGRActivity)
                    noTenantCostDialog.showDialog()

                    binding.CostRoomNumber.setText(tenantRoomNumber.trim())

                    binding.ConstTonCost.setText("0")
                    binding.ConstCleanCost.setText("0")
                    binding.ConstUsunCost.setText("0")
                    binding.ConstMgrCost.setText("0")


                    // 초기 톤수 셋팅 1.0 값
                    binding.WriteWaterTon.setText(1.toFloat().toString())

                    waterValue = binding.WriteWaterTon.text.toString()
                        .toFloat() * binding.ConstTonCost.text.toString().replace(",", "")
                        .toFloat()
                    binding.waterCost.setText(waterValue.toInt().toString())
                    binding.TotalCostValue.setText("0")

                    firestoreDB.collection("VillaAccount")
                        .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
                        .whereEqualTo("favorite", "favorite")
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty){
                                for (i in result!!) {
                                    binding.CostBankName.setText(i.data["bankName"].toString().trim())
                                    binding.CostAccountHolder.setText(i.data["accountHolder"].toString().trim())
                                    binding.CostAccountNumber.setText(i.data["accountNumber"].toString().trim())
                                    break
                                }
                            }
                        }

//
//                    firestoreDB.collection("StandardCost")
//                        .whereEqualTo("villaAddr",MyApplication.prefs.getString("villaAddress", "").trim())
//                        .get()
//                        .addOnSuccessListener { result ->
//                            if (!result.isEmpty) {
//                                for (i in result!!) {
//                                    binding.ConstTonCost.setText(i.data["tonCost"].toString())
//                                    binding.ConstCleanCost.setText(i.data["cleanCost"].toString())
//                                    binding.ConstUsunCost.setText(i.data["usunCost"].toString())
//                                    binding.ConstMgrCost.setText(i.data["mgrCost"].toString())
//
//                                    val total = waterValue.toInt() + i.data["cleanCost"].toString()
//                                        .toInt() + i.data["usunCost"].toString()
//                                        .toInt() + i.data["mgrCost"].toString().toInt()
//
//                                    waterValue = binding.WriteWaterTon.text.toString()
//                                        .toFloat() * binding.ConstTonCost.text.toString()
//                                        .replace(",", "").toFloat()
//                                    binding.waterCost.setText(waterValue.toInt().toString())
//
//                                    binding.TotalCostValue.setText(total.toString())
//
//                                    binding.CostRoomNumber.setText(tenantRoomNumber)
//
//                                    break
//                                }
//                                firestoreDB.collection("VillaAccount")
//                                    .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
//                                    .whereEqualTo("favorite", "favorite")
//                                    .get()
//                                    .addOnSuccessListener { result ->
//                                        if (!result.isEmpty){
//                                            for (i in result!!) {
//                                                binding.CostBankName.setText(i.data["bankName"].toString().trim())
//                                                binding.CostAccountHolder.setText(i.data["accountHolder"].toString().trim())
//                                                binding.CostAccountNumber.setText(i.data["accountNumber"].toString().trim())
//                                                break
//                                            }
//                                        }
//                                    }
//
//                            }
//                        }
                }
            }

//--------------------------------------------------------------------------------------------
//        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
//
//        Thread(Runnable {
//            val ConstCost = villaNoticedb!!.VillaNoticeDao().getStandardCost(
//                MyApplication.prefs.getString("villaAddress","").trim()
//            )
//
//            val roomNumber = villaNoticedb!!.VillaNoticeDao().getTenantRoom(
//                tenantRoomId
//            )
//
//            val favoriteAccount = villaNoticedb!!.VillaNoticeDao().getFavoriteAccount()
//
//            val isTenantCost = villaNoticedb!!.VillaNoticeDao().getTenantCost(
//                MyApplication.prefs.getString("villaAddress","").trim(),binding.CostYearMonth.text.substring(0,4),binding.CostYearMonth.text.substring(5,7),roomNumber
//            )
//
//            runOnUiThread {
//
//                if (isTenantCost==null){
//
//                    binding.ConstTonCost.setText(ConstCost.tonCost.toString())
//                    binding.ConstCleanCost.setText(ConstCost.cleanCost.toString())
//                    binding.ConstUsunCost.setText(ConstCost.usunCost.toString())
//                    binding.ConstMgrCost.setText(ConstCost.mgrCost.toString())
//
//
//                    waterValue = binding.WriteWaterTon.text.toString().toFloat() * binding.ConstTonCost.text.toString().replace(",","").toFloat()
//                    binding.waterCost.setText(waterValue.toInt().toString())
//
//                    val total = waterValue.toInt() + ConstCost.cleanCost + ConstCost.usunCost + ConstCost.mgrCost
//
//                    binding.TotalCostValue.setText(total.toString())
//
//                    binding.CostRoomNumber.setText(roomNumber)
//
//                    binding.CostBankName.setText(favoriteAccount.bankName)
//                    binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
//                    binding.CostAccountNumber.setText(favoriteAccount.accountNumber)
//
//                } else {
//                    binding.CostUid.setText(isTenantCost.costId.toString())
//                    binding.CostRoomNumber.setText(isTenantCost.roomNumber)
//                    binding.CostYearMonth.setText(isTenantCost.costYear + "-" + isTenantCost.costMonth)
//                    binding.TotalCostValue.setText(isTenantCost.totalCost.toString())
//                    binding.WriteWaterTon.setText(((isTenantCost.useTon*10).roundToInt() / 10f).toString())
//                    binding.ConstTonCost.setText(isTenantCost.costTon.toString())
//                    binding.waterCost.setText(isTenantCost.totalUseTon.toString())
//                    binding.ConstCleanCost.setText(isTenantCost.costClean.toString())
//                    binding.ConstUsunCost.setText(isTenantCost.costUsun.toString())
//                    binding.ConstMgrCost.setText(isTenantCost.costMgr.toString())
//
//                    binding.CostBankName.setText(favoriteAccount.bankName)
//                    binding.CostAccountHolder.setText(favoriteAccount.accountHolder)
//                    binding.CostAccountNumber.setText(favoriteAccount.accountNumber)
//                }
//
//
//
//            }
//        }).start()
// --------------------------------------------------------------------------------------------




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