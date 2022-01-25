package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.databinding.ActivityRoomcostfortenantBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TenantRoomCostForTenantActivity : AppCompatActivity() {


    private val binding : ActivityRoomcostfortenantBinding by lazy { ActivityRoomcostfortenantBinding.inflate(layoutInflater)}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initCalendar()

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
            val formatter = DateTimeFormatter.ofPattern("yyyy")
            val formatted = current.format(formatter)

            year.maxValue = formatted.toInt()
            month.maxValue = 12



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




}