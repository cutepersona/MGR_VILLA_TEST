package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityMgrstandardcostBinding
import fastcampus.aop.part2.mgr_villa.model.StandardCost
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import java.text.DecimalFormat
import java.time.LocalDate

class MgrStandardCostActivity: AppCompatActivity() {

    private val binding: ActivityMgrstandardcostBinding by lazy { ActivityMgrstandardcostBinding.inflate(layoutInflater) }

    private var StandardCostFlag = false
    private var tonCostAmount: String = ""
    private var cleanCostAmount: String = ""
    private var usunCostAmount: String = ""
    private var mgrCostAmount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initAddStandardCostButton()
        initTonCostTextCheck()
        initSetStandardCost()
    }




    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.StandardCostToolbar)
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


    // 관리비 기준금액 등록 후 확인
    private fun initSetStandardCost() {
        val villadb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {
            val standardCost = villadb?.VillaNoticeDao()?.getStandardCost(
                    MyApplication.prefs.getString("villaAddress","").trim()
            )

            runOnUiThread {
                if (standardCost == null) {
                    binding.tonCostEditText.setText("")
                    binding.cleanCostEditText.setText("")
                    binding.usunCostEditText.setText("")
                    binding.mgrCostEditText.setText("")
                } else {
                    binding.tonCostEditText.setText(standardCost.tonCost.toString())
                    binding.cleanCostEditText.setText(standardCost.cleanCost.toString())
                    binding.usunCostEditText.setText(standardCost.usunCost.toString())
                    binding.mgrCostEditText.setText(standardCost.mgrCost.toString())
                }

            }
        }).start()

    }

    // 관리비 기준금액 등록 버튼
    private fun initAddStandardCostButton() {
        binding.InsertMgrStandardCostButton.setOnClickListener {
            if (!checkForm()) {
                return@setOnClickListener
            } else {
                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)
                Thread(Runnable {
                    villaNoticedb!!.VillaNoticeDao().standardCostInsert(
                        StandardCost(
                            MyApplication.prefs.getString("villaAddress","").trim(),
                            binding.tonCostEditText.text.toString().trim().replace(",","").toInt(),
                            binding.cleanCostEditText.text.toString().trim().replace(",","").toInt(),
                            binding.usunCostEditText.text.toString().trim().replace(",","").toInt(),
                            binding.mgrCostEditText.text.toString().trim().replace(",","").toInt()
                        )
                    )

                    runOnUiThread {
                        val MgrCostDivActivity = Intent(this, MgrCostDivActivity::class.java)
                        startActivity(MgrCostDivActivity)
                    }
                }).start()
            }
        }
    }

    // 등록전 입력항목 체크
    private fun checkForm(): Boolean {
        checkStandardCost()

        return (StandardCostFlag)

    }

    // 기준관리비 등록하기
    private fun checkStandardCost() {
        val tonCost = binding.tonCostEditText.text.toString().trim()
        val cleanCost = binding.cleanCostEditText.text.toString().trim()
        val usunCost = binding.usunCostEditText.text.toString().trim()
        val mgrCost = binding.mgrCostEditText.text.toString().trim()

        if (tonCost.isEmpty()
            || cleanCost.isEmpty()
            || usunCost.isEmpty()
            || mgrCost.isEmpty()) {
            showToast("기준 관리비는 필수 입력사항 입니다.")
            StandardCostFlag = false
        } else {
            StandardCostFlag = true
        }
    }

    // 기준 금액 정보 확인
    private fun initTonCostTextCheck() {
        binding.tonCostEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(tonCostAmount)) {
                    tonCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.tonCostEditText.setText(tonCostAmount)
                    binding.tonCostEditText.setSelection(tonCostAmount.length)  //커서를 오른쪽 끝으로 보냄
                }
            }
        })

        binding.cleanCostEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(cleanCostAmount)) {
                    cleanCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.cleanCostEditText.setText(cleanCostAmount)
                    binding.cleanCostEditText.setSelection(cleanCostAmount.length)  //커서를 오른쪽 끝으로 보냄
                }
            }
        })

        binding.usunCostEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(usunCostAmount)) {
                    usunCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.usunCostEditText.setText(usunCostAmount)
                    binding.usunCostEditText.setSelection(usunCostAmount.length)  //커서를 오른쪽 끝으로 보냄
                }
            }
        })

        binding.mgrCostEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(mgrCostAmount)) {
                    mgrCostAmount = makeCommaNumber(Integer.parseInt( s.toString().replace(",","") ))
                    binding.mgrCostEditText.setText(mgrCostAmount)
                    binding.mgrCostEditText.setSelection(mgrCostAmount.length)  //커서를 오른쪽 끝으로 보냄
                }
            }
        })


    }

    private fun makeCommaNumber(input:Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}