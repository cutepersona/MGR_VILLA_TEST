package fastcampus.aop.part2.mgr_villa

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityVillainfoBinding
import fastcampus.aop.part2.mgr_villa.model.VillaInfo
import fastcampus.aop.part2.mgr_villa.model.VillaUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class VillaInfoActivity : AppCompatActivity() {

    private val binding: ActivityVillainfoBinding by lazy {
        ActivityVillainfoBinding.inflate(
            layoutInflater
        )
    }

    private var tenantCountFlag = false
    val firestoreDB = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initTenantCountCheck()
        villaInfoInsert()

        if (intent.hasExtra("email")) {
            binding.villaInfoEmailHidden.setText(intent.getStringExtra("email"))
        }

        if (intent.hasExtra("address")) {
            binding.villaAddressEditText.setText(intent.getStringExtra("address").toString())
        }

        if (intent.hasExtra("roadAddress")) {
            binding.roadAddressEditText.setText(intent.getStringExtra("roadAddress").toString())
        }
        if (intent.hasExtra("villa_name")) {
            if (intent.getStringExtra("villa_name").toString().isNullOrEmpty()){
                binding.villaNameEditText.setText("")
            }else{
                binding.villaNameEditText.setText(intent.getStringExtra("villa_name").toString())
            }

        }


    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.VillaInfoToolbar)
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

    // 빌라정보등록
    private fun villaInfoInsert() {
        binding.InsertVillaInfoButton.setOnClickListener {
            if (!checkForm()) {
                return@setOnClickListener
            } else {

                val villaInfo = firestoreDB.collection("VillaInfo")

                val VillaInfo = hashMapOf(
                    "villaAddress" to binding.villaAddressEditText.text.toString().trim(),
                    "roadAddress" to binding.roadAddressEditText.text.toString().trim(),
                    "villaName" to binding.villaNameEditText.text.toString().trim(),
                    "villaAlias" to "",
                    "villaTenantCount" to binding.villaTenantCountEditText.text.toString(),
                    "villaParkCount" to 0,
                    "villaElevator" to binding.elevatorSwitch.isChecked,
                    "mailAddress" to binding.villaInfoEmailHidden.text.toString(),
                    "roomNumber" to ""
                )

                // 회원정보 체크하기
                firestoreDB.collection("VillaInfo")
//                    .whereEqualTo("mailAddress", binding.villaInfoEmailHidden.text.toString())
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (!task.result!!.isEmpty){
                                for (i in task.result!!) {
                                    showToast(i.id)
                                    if (i.id == binding.villaAddressEditText.text.toString().trim()) {
                                        showToast("이미 등록된 주소입니다.")
                                        return@addOnCompleteListener
                                        break
                                    }
                                }
                            } else {
                                // 새주소 등록
                                    villaInfo.document(binding.villaAddressEditText.text.toString().trim())
                                        .set(VillaInfo)
                                        .addOnSuccessListener { documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                                            CoroutineScope(Dispatchers.IO).launch {
//                                                userdb!!.VillaNoticeDao().insert(
//                                                    VillaUsers(
//                                                        userEmailEditText.text.toString().trim(),
//                                                        "1",
//                                                        userNameEditText.text.toString().trim(),
//                                                        userPasswordEditText1.text.toString()
//                                                            .trim(),
//                                                        userPhoneNumberEditText.text.toString()
//                                                            .trim(),
//                                                        binding.emptyButtomUp.text.toString().trim()
//                                                    )
//                                                )
//                                            }

//                                            showToast("회원가입을 환영합니다.")
                        val homeActivity = Intent(this, VillaHomeActivity::class.java)
                        homeActivity.putExtra("email", binding.villaInfoEmailHidden.text.toString())
                        startActivity(homeActivity)
                                        }
                                        .addOnFailureListener { e ->
                                            showToast("등록에 실패하였습니다.")
                                            Log.w(ContentValues.TAG, "Error adding document", e)
                                            return@addOnFailureListener
                                        }
                                }
                        }
                        else {
                            showToast("정보를 불러오지 못했습니다.")
                            return@addOnCompleteListener
                        }
                    }
                    .addOnFailureListener {
                        showToast("등록에 실패 하였습니다.")
                        return@addOnFailureListener
                    }

//
//                //----------------------------------------------------------------------------------------------------
//                val villadb = VillaNoticeHelper.getInstance(applicationContext)
//
//                Thread(Runnable {
//
//                    villadb!!.VillaNoticeDao()?.insert(
//                        VillaInfo(
//                            binding.villaAddressEditText.text.toString().trim(),
//                            binding.roadAddressEditText.text.toString().trim(),
//                            binding.villaNameEditText.text.toString().trim(),
//                            "",
//                            binding.villaTenantCountEditText.text.toString(),
//                            0,
//                            binding.elevatorSwitch.isChecked,
//                            binding.villaInfoEmailHidden.text.toString(),
//                            ""
//                        )
//                    )
//
//                    runOnUiThread {
//
//                        val homeActivity = Intent(this, VillaHomeActivity::class.java)
//                        homeActivity.putExtra("email", binding.villaInfoEmailHidden.text.toString())
//                        startActivity(homeActivity)
//
//
////                        if (user == null) {
////                            showToast("회원정보가 없습니다.")
////                        }
////                        else {
////                            if (villaInfo >= 1) {
////                                // TODO 홈화면으로 이동해야함.
////                            }else{
////                                if (villaInfo < 1 && user.userType.equals("MGR")) {
//////                            showToast("villaInfo==null")
////                                    val addrSearchActivity =
////                                        Intent(this, AddressSearchActivity::class.java)
////                                    startActivity(addrSearchActivity)
////                                }
////                            }
////
////                        }
//                    }
//                }).start()
////----------------------------------------------------------------------------------------------------

            }
        }
    }

    private fun checkForm(): Boolean {
        checkTenantCount()

        return (tenantCountFlag)

    }

    // 가구수 체크
    private fun initTenantCountCheck() {
        try {
            binding.villaTenantCountEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkTenantCount()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        } catch (e: Exception) {
            Log.d("initTenantCountCheck---------------------------->", e.stackTraceToString())
        }

    }

    private fun checkTenantCount() {
        var tenantCount = binding.villaTenantCountEditText.text.toString().trim()

        if (tenantCount.isNullOrEmpty()) {
            binding.villaTenantCountValid.setTextColor(-65535)
            binding.villaTenantCountValid.isInvisible = false
            binding.villaTenantCountValid.setText(R.string.must_insert)
            tenantCountFlag = false
        } else {
            binding.villaTenantCountValid.setTextColor(R.color.black.toInt())
            binding.villaTenantCountValid.isInvisible = true
            tenantCountFlag = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}