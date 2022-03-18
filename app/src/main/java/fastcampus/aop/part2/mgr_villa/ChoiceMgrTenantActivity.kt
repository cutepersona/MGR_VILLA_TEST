package fastcampus.aop.part2.mgr_villa

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nhn.android.naverlogin.OAuthLogin
import fastcampus.aop.part2.mgr_villa.customdialog.mgrCheckDialog
import fastcampus.aop.part2.mgr_villa.databinding.ActivityChoiceMgrTenantBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class ChoiceMgrTenantActivity: AppCompatActivity() {

    private val binding: ActivityChoiceMgrTenantBinding by lazy { ActivityChoiceMgrTenantBinding.inflate(layoutInflater) }

    val firestoreDB = Firebase.firestore
    val auth = Firebase.auth

    lateinit var mOAuthLoginInstance : OAuthLogin

    var Nemail: String = ""
    var Nname: String = ""
    var Nmobile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra("N")){
//            showToast(intent.getStringExtra("N").toString())
            Nemail = intent.getStringExtra("Nemail").toString()
            Nname = intent.getStringExtra("Nname").toString()
            Nmobile = intent.getStringExtra("Nmobile").toString()
        }

        mOAuthLoginInstance = OAuthLogin.getInstance()
//        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)

        initToolBar()
        initTenantButton()
        initMgrButton()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mOAuthLoginInstance.logout(applicationContext)
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)

    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.UserTypeToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        mOAuthLoginInstance.logout(applicationContext)
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)

        return true

//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//
//        mOAuthLoginInstance.logout(applicationContext)
//
//        return super.onOptionsItemSelected(item)
    }



    // tenant 버튼 클릭
    private fun initTenantButton() {
        binding.TenantButton.setOnClickListener {

            if (intent.hasExtra("N")){

                val users = firestoreDB.collection("VillaUsers")

                val VillaUsers = hashMapOf(
                    "mailAddress" to Nemail.trim(),
                    "roomNumber" to "0",
                    "userName" to Nname.trim(),
                    "passWord" to "",
                    "phoneNumber" to Nmobile.trim(),
                    "userType" to "TENANT",
                    "signUpType" to "N"
                )

                auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser


                            users.document(Nemail.trim())
                                .set(VillaUsers)
                                .addOnSuccessListener { documentReference ->

//                        showToast("회원가입을 환영합니다.")


                                    firestoreDB.collection("VillaTenant")
                                        .whereEqualTo("tenantEmail", Nemail.trim())
                                        .get()
                                        .addOnSuccessListener { task ->
                                            if(task.isEmpty){
                                                val addrSearchTenantActivity =
                                                    Intent(this, AddressSearchForTenantActivity::class.java)
                                                addrSearchTenantActivity.putExtra("N", "N")
                                                addrSearchTenantActivity.putExtra("email", Nemail.trim())
                                                startActivity(addrSearchTenantActivity)
                                            }
                                        }

//                        val toLogin = Intent(this, LoginActivity::class.java)
//                        startActivity(toLogin)
                                }
                                .addOnFailureListener { e ->
                                    showToast("회원가입에 실패하였습니다.")
                                    Log.w(ContentValues.TAG, "Error adding document", e)
                                    return@addOnFailureListener
                                }


                        }
                    }


//                tenantSignUpActivity.putExtra("N","N")
//                tenantSignUpActivity.putExtra("Nemail",Nemail)
//                tenantSignUpActivity.putExtra("Nname",Nname)
//                tenantSignUpActivity.putExtra("Nmobile",Nmobile)
            } else {
                val tenantSignUpActivity = Intent(this, SignUpActivity::class.java)
                tenantSignUpActivity.putExtra("tenant","TENANT")
                startActivity(tenantSignUpActivity)
            }



        }
    }

    // mgr 버튼 클릭
    private fun initMgrButton() {
        binding.MgrButton.setOnClickListener {

            val mgrCheckDialog = mgrCheckDialog(this)
            mgrCheckDialog.showDialog()
            mgrCheckDialog.setOnClickListener(object : mgrCheckDialog.OnDialogClickListener {
                override fun onClicked(mgrPassword: String) {
                    if (mgrPassword.equals("cjtel")){
                        callMgrSighUpActivity()
                    } else {
                        showToast("관리자 비밀번호가 일치하지 않습니다.")
                    }

                }

            })

        }
    }

    private fun callMgrSighUpActivity(){

        if (intent.hasExtra("N")){

            val users = firestoreDB.collection("VillaUsers")

            val VillaUsers = hashMapOf(
                "mailAddress" to Nemail.trim(),
                "roomNumber" to "0",
                "userName" to Nname.trim(),
                "passWord" to "",
                "phoneNumber" to Nmobile.trim(),
                "userType" to "MGR",
                "signUpType" to "N"
            )

            users.document(Nemail.trim())
                .set(VillaUsers)
                .addOnSuccessListener { documentReference ->

//                        showToast("회원가입을 환영합니다.")

                    firestoreDB.collection("VillaTenant")
                        .whereEqualTo("tenantEmail", Nemail.trim())
                        .get()
                        .addOnSuccessListener { task ->
                            if(task.isEmpty){
                                val addrSearchActivity =
                                    Intent(this, AddressSearchActivity::class.java)
                                addrSearchActivity.putExtra("N", "N")
                                addrSearchActivity.putExtra("email", Nemail.trim())
                                startActivity(addrSearchActivity)
                            }
                        }

//                        val toLogin = Intent(this, LoginActivity::class.java)
//                        startActivity(toLogin)
                }
                .addOnFailureListener { e ->
                    showToast("회원가입에 실패하였습니다.")
                    Log.w(ContentValues.TAG, "Error adding document", e)
                    return@addOnFailureListener
                }

        } else {
            val callSignUpActivity = Intent(this, SignUpActivity::class.java)
            callSignUpActivity.putExtra("mgr", "MGR")
//            callSignUpActivity.putExtra("N", "N")
//            callSignUpActivity.putExtra("Nemail", Nemail)
//            callSignUpActivity.putExtra("Nname", Nname)
//            callSignUpActivity.putExtra("Nmobile", Nmobile)
            startActivity(callSignUpActivity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}