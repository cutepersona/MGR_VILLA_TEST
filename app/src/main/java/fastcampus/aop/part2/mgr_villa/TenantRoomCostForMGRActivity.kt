package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityRoomcostformgrBinding
import fastcampus.aop.part2.mgr_villa.model.VillaAccount
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import java.math.BigDecimal
import java.text.DecimalFormat

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra("tenantRoomId")){
            tenantRoomId = intent.getLongExtra("tenantRoomId", 0)
        }

        initToolBar()
        initConstCost()



        // editText
        binding.WriteWaterTon.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if(actionId == EditorInfo.IME_ACTION_DONE){
                // todo 구현

                waterValue = binding.WriteWaterTon.text.toString().toFloat() * binding.ConstTonCost.text.toString().replace(",","").toFloat()
//                waterCostAmount = waterValue.toInt().toString()
                binding.waterCost.setText(waterValue.toInt().toString())

                totalValue = waterValue.toInt() + binding.ConstCleanCost.text.toString().replace(",","").toInt() + binding.ConstUsunCost.text.toString().replace(",","").toInt() +binding.ConstMgrCost.text.toString().replace(",","").toInt()
                binding.TotalCostValue.setText(totalValue.toString())


//                handled = true
            }
            handled
        }


    }

    // 기준 관리비 셋팅
    private fun initConstCost() {

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

            runOnUiThread {

//                waterValue = binding.WriteWaterTon.toString().toFloat() * ConstCost.tonCost.toFloat()

                binding.ConstTonCost.setText(ConstCost.tonCost.toString())
                binding.ConstCleanCost.setText(ConstCost.cleanCost.toString())
                binding.ConstUsunCost.setText(ConstCost.usunCost.toString())
                binding.ConstMgrCost.setText(ConstCost.mgrCost.toString())


                waterValue = binding.WriteWaterTon.text.toString().toFloat() * binding.ConstTonCost.text.toString().replace(",","").toFloat()
                binding.waterCost.setText(waterValue.toInt().toString())

                val total = waterValue.toInt() + ConstCost.cleanCost + ConstCost.usunCost + ConstCost.mgrCost

                binding.TotalCostValue.setText(total.toString())

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