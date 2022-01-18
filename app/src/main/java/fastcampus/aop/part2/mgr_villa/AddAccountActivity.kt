package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig
import fastcampus.aop.part2.mgr_villa.adapter.BankDialogAdapter
import fastcampus.aop.part2.mgr_villa.customdialog.mgrAddAccountDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityAccountBinding
import fastcampus.aop.part2.mgr_villa.model.VillaAccount
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class AddAccountActivity: AppCompatActivity() {


    private val binding: ActivityAccountBinding by lazy { ActivityAccountBinding.inflate(layoutInflater)}

    private var BankNameFlag =  false
    private var AccountHolderFlag = false
    private var AccountNumberFlag = false
    private var AccountId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bankList = resources.getStringArray(R.array.bankList)

        initToolBar()
        initBankDialog(bankList)

        initButtonSetOnClick()

        if(intent.hasExtra("accountId")){
            AccountId = intent.getLongExtra("accountId",0)
            getAccountContent()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val AddAccountToList = Intent(this, VillaMgrAccountsListActivity::class.java)
        startActivity(AddAccountToList)

    }

    // 은행 다이얼로그 호출
    private fun initBankDialog(bankList: Array<String>) {
        binding.bankSpinnerArea.setOnClickListener {

//            val bankdialog = Dialog(this)
            val BankListAdapter = BankDialogAdapter(bankList)            // 리싸이클러 뷰 어댑터
//            bankdialog.setContentView(R.layout.mgr_addaccount)
//            bankdialog.setCanceledOnTouchOutside(false)
//            bankdialog.rv_banks.adapter = BankListAdapter
//            bankdialog.rv_banks.layoutManager = LinearLayoutManager(this)
//            bankdialog.show()

            val mgrBankDialog = mgrAddAccountDialog(this)
            mgrBankDialog.showDialog(BankListAdapter)


            mgrBankDialog.setOnClickListener( object : mgrAddAccountDialog.OnDialogClickListener{
                override fun onClicked(bank: String) {
                    binding.bankNameText.setText(bank)
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

    // 계좌정보 불러오기
    private fun getAccountContent() {
        val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

        Thread(Runnable {
            val account = villaNoticedb!!.VillaNoticeDao().getVillaAccount(AccountId)

            runOnUiThread {
                binding.accountId.setText(account.accountId.toString())
                binding.bankNameText.setText(account.bankName)
                binding.accountHolderEditText.setText(account.accountHolder)
                binding.accountNumberEditText.setText(account.accountNumber)
            }
        }).start()


    }

    private fun initButtonSetOnClick() {
        try {
            binding.AddAccountButton.setOnClickListener {

                val villaNoticedb = VillaNoticeHelper.getInstance(applicationContext)

                if (!checkForm()) {
                    return@setOnClickListener
                } else {
                    if(AccountId <= 1){
                        Thread(Runnable {
                            villaNoticedb!!.VillaNoticeDao().villaAccountInsert(
                                VillaAccount(
                                    null,
                                    binding.bankNameText.text.toString().trim(),
                                    binding.accountHolderEditText.text.toString().trim(),
                                    binding.accountNumberEditText.text.toString().trim(),
                                    "",
                                    MyApplication.prefs.getString("villaAddress","").trim()
                                )
                            )

                            runOnUiThread {
                                val AccountListActivity = Intent(this, VillaMgrAccountsListActivity::class.java)
                                startActivity(AccountListActivity)
                            }
                        }).start()
                    } else {
                        Thread(Runnable {
                            villaNoticedb!!.VillaNoticeDao().updateAccount(
                                    binding.bankNameText.text.toString().trim(),
                                    binding.accountHolderEditText.text.toString().trim(),
                                    binding.accountNumberEditText.text.toString().trim(),
                                    MyApplication.prefs.getString("villaAddress","").trim(),
                                    AccountId
                            )

                            runOnUiThread {
                                val AccountListActivity = Intent(this, VillaMgrAccountsListActivity::class.java)
                                startActivity(AccountListActivity)
                            }
                        }).start()
                    }



                }
            }


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



        } catch ( e: Exception){
            Log.d("AccountInsert---------------->",e.stackTrace.toString())
        }


    }

    private fun checkForm(): Boolean {
        checkBankName()
        checkAccountHolder()
        checkAccountNumber()
        return (BankNameFlag
                && AccountHolderFlag
                && AccountHolderFlag)
    }

    private fun checkBankName() {
        var bankName = binding.bankNameText.text.toString().trim()

        if (bankName.isNullOrEmpty()) {
            binding.bankSelectValid.setTextColor(-65535)
            binding.bankSelectValid.isInvisible = false
            binding.bankSelectValid.setText(R.string.must_insert)
            BankNameFlag = false
        } else {
            binding.bankSelectValid.setTextColor(R.color.black.toInt())
            binding.bankSelectValid.isInvisible = true
            BankNameFlag = true
        }
    }

    private fun checkAccountHolder() {
        var accountHolder = binding.accountHolderEditText.text.toString().trim()

        if (accountHolder.isNullOrEmpty()) {
            binding.accountHolderValid.setTextColor(-65535)
            binding.accountHolderValid.isInvisible = false
            binding.accountHolderValid.setText(R.string.must_insert)
            AccountHolderFlag = false
        } else {
            binding.accountHolderValid.setTextColor(R.color.black.toInt())
            binding.accountHolderValid.isInvisible = true
            AccountHolderFlag = true
        }
    }

    private fun checkAccountNumber() {
        var accountNum = binding.accountNumberEditText.text.toString().trim()

        if (accountNum.isNullOrEmpty()) {
            binding.accountNumberValid.setTextColor(-65535)
            binding.accountNumberValid.isInvisible = false
            binding.accountNumberValid.setText(R.string.must_insert)
            AccountHolderFlag = false
        } else {
            binding.accountNumberValid.setTextColor(R.color.black.toInt())
            binding.accountNumberValid.isInvisible = true
            AccountHolderFlag = true
        }
    }



    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.AddAccountToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val AddAccountToList = Intent(this, VillaMgrAccountsListActivity::class.java)
        startActivity(AddAccountToList)

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