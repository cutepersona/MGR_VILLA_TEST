package fastcampus.aop.part2.mgr_villa

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fastcampus.aop.part2.mgr_villa.customdialog.LogOutDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityHomeBinding
import fastcampus.aop.part2.mgr_villa.fragment.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_home.*

class VillaHomeActivity : AppCompatActivity() {

//    private lateinit var userHelper: VillaUsersHelper

//    private var userList = mutableListOf<VillaUsers>()

    private val binding: ActivityHomeBinding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    private var userEmail: String = ""
//    private var roomNumber: String =""
//    private var roadAddress: String = ""
//    private var address: String = ""
    private val bnv_Home: BottomNavigationView by lazy{
        findViewById(R.id.bnv_Home)
    }

    var backKeyPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()

        if (intent.hasExtra("email")) {
            userEmail = intent.getStringExtra("email").toString()
            initLoginData(userEmail)
        } else {
            userEmail = MyApplication.prefs.getString("email","")
            initLoginData(userEmail)
        }

//        showToast(roomNumber + "\n" + roadAddress + "\n" + address)

//        showToast(MyApplication.prefs.getString("villaAddress","").trim())

        initHomeFragment()
        initHomeBottomNavigationBar()

//        setBindingFragment()

    }

    // 하단 네비게이션 바
    private fun initHomeBottomNavigationBar() {

//        supportFragmentManager.beginTransaction().add(R.id.fl_container, MgrHomeFragment()
//        ).commit()

//        val toHomeActivity = Intent(this, VillaHomeActivity::class.java)

        bnv_Home.setOnNavigationItemSelectedListener {
            when(it.itemId){
//                R.id.nv_Home ->
//                    startActivity(toHomeActivity)
            }

//            replaceFragment(
//                when(it.itemId){
//                    R.id.nv_Home -> MgrHomeFragment()
//                    else -> MgrHomeFragment()
//                }
//            )
            true
        }


//        bnv_Home.run {  setOn {
//            when(it.itemId){
//                R.id.nv_Home -> {
//
//                    showToast("HOME")
////                    val nvToHomeFragment = MgrHomeFragment()
////                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, nvToHomeFragment).commit()
//                }
//                R.id.nv_All -> {
//                    showToast("전체")
//                }
//
//            }
//            selectedItemId = R.id.nv_Home
//        }
//
//        }


    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fl_container.id, fragment).commit()
    }


    override fun onBackPressed() {
//        super.onBackPressed()

        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()

            return
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finishAffinity()
        }

    }


    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.HomeToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val logOutDialog = LogOutDialog(this)
        logOutDialog.showDialog()


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

    // initFragment
    private fun initHomeFragment(){
        Thread(Runnable {
            Thread.sleep(100L)              // 현재 세입자 정보를 바로 못가져와서 Sleep 줌
            val userdb = VillaNoticeHelper.getInstance(applicationContext)

            var currentTenantCount: Int? = 0

            val user = userdb?.VillaNoticeDao()?.getUser(
                userEmail
            )

            val tenantInfo = userdb?.VillaNoticeDao()?.getTenantFromEmail(userEmail)

            currentTenantCount = userdb?.VillaNoticeDao()?.getCurrentTenantCount(
                MyApplication.prefs.getString("villaAddress","").trim()
            )

            val villaInfo = userdb?.VillaNoticeDao()?.getVillaInfo(
                userEmail
            )

            runOnUiThread {
                if (user?.userType.equals("MGR")){
                    if (tenantInfo != null) {
                        val bundle = Bundle()
                        bundle.putString("roomNumber",tenantInfo?.roomNumber)
                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))

//                        showToast(currentTenantCount.toString())

                        bundle.putString("currentTenantCount", currentTenantCount.toString())
                        bundle.putString("totalTenantCount", villaInfo?.villaTenantCount.toString())

                        // 집 주소 및 전입호수 전달
                        val mgrFrag = MgrHomeFragment()
                        mgrFrag.arguments = bundle

//                        // 입주자 수 정보 전달.
//                        val homeTenantFrag = VillaTenantFragment()
//                        homeTenantFrag.arguments = bundle

                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
                        transaction.commit()
                    } else {
                        val bundle = Bundle()
//                        val tenantFragmentBundle = Bundle()
                        bundle.putString("roomNumber","")
                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))
                        bundle.putString("currentTenantCount", currentTenantCount.toString())
                        bundle.putString("totalTenantCount", villaInfo?.villaTenantCount.toString())

                        // 집 주소 및 전입호수 전달
                        val mgrFrag = MgrHomeFragment()
                        mgrFrag.arguments = bundle

//                        // 입주자 수 정보 전달.
//                        val homeTenantFrag = VillaTenantFragment()
//                        homeTenantFrag.arguments = bundle

                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
                        transaction.commit()
                    }
                } else {
                    if (tenantInfo != null) {
                        val bundle = Bundle()
                        bundle.putString("roomNumber",tenantInfo?.roomNumber)
                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))

                        // 집 주소 및 전입호수 전달
                        val mgrFrag = MgrHomeFragment()
                        mgrFrag.arguments = bundle

                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
                        transaction.commit()
                    } else {
                        val bundle = Bundle()
                        bundle.putString("roomNumber","")
                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))

                        val mgrFrag = MgrHomeFragment()
                        mgrFrag.arguments = bundle

                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
                        transaction.commit()
                    }
                }


            }
        }).start()
    }

    // HomeFragment에서 하위 Fragment생성
    fun createSubFragment(){

        Thread(Runnable {
            val userdb = VillaNoticeHelper.getInstance(applicationContext)

            var checkTenantCount: Int? = 0

            checkTenantCount = userdb?.VillaNoticeDao()?.checkTenantCount(
                MyApplication.prefs.getString("villaAddress","").trim()
            )

            val villaInfo = userdb?.VillaNoticeDao()?.getVillaInfo(
                MyApplication.prefs.getString("email","").trim()
            )

            runOnUiThread {
                val bundle = Bundle()
                bundle.putString("currentTenantCount", checkTenantCount.toString())
                bundle.putString("totalTenantCount", villaInfo?.villaTenantCount.toString())

            }
        }).start()

//        val transaction = supportFragmentManager.beginTransaction()

    }
//
//    fun changeText(string:String){
//        tenantFragment.setText(string)
//    }


    // 로그인 정보 가져오기
    private fun initLoginData(email: String) {

        val userdb = VillaNoticeHelper.getInstance(applicationContext)
//        val villadb = VillaInfoHelper.getInstance(applicationContext)

        if (MyApplication.prefs.getString("userType","").equals("TENANT")){
            Thread(Runnable {
                val user = userdb?.VillaNoticeDao()?.getUser(
                    email
                )

                val villaInfo = userdb?.VillaNoticeDao()?.getVillaInfo(email)
                val tenantInfo = userdb?.VillaNoticeDao()?.getTenantFromEmail(email)

                MyApplication.prefs.setString("villaAddress", tenantInfo?.villaAddr.toString())
                MyApplication.prefs.setString("roadAddress", tenantInfo?.roadAddress.toString())
//            MyApplication.prefs.setString("roomNumber", tenantInfo?.roomNumber.toString())


                runOnUiThread {
                    if (user == null) {
                        showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
                        return@runOnUiThread
                    } else {
//                        roomNumber = tenantInfo?.roomNumber.toString()
//                        address = MyApplication.prefs.getString("villaAddress", "")
//                        roadAddress = MyApplication.prefs.getString("roadAddress", "")
                        binding.hUserName.setText(user.userName)
                    }
                }
            }).start()
        } else {
            Thread(Runnable {
                val user = userdb?.VillaNoticeDao()?.getUser(
                    email
                )

                val villaInfo = userdb?.VillaNoticeDao()?.getVillaInfo(email)
                val tenantInfo = userdb?.VillaNoticeDao()?.getTenantFromEmail(email)

                MyApplication.prefs.setString("villaAddress", villaInfo?.villaAddress.toString())
                MyApplication.prefs.setString("roadAddress", villaInfo?.roadAddress.toString())
//            MyApplication.prefs.setString("roomNumber", tenantInfo?.roomNumber.toString())


                runOnUiThread {
                    if (user == null || villaInfo == null) {
                        showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
                        return@runOnUiThread
                    } else {
//                        roomNumber = tenantInfo?.roomNumber.toString()
//                        address = MyApplication.prefs.getString("villaAddress", "")
//                        roadAddress = MyApplication.prefs.getString("roadAddress", "")
                        binding.hUserName.setText(user.userName)

                    }
                }
            }).start()
        }

    }
//
//    fun getAddress():String{
//        return address
//    }
//
//    fun getRoadAddress():String{
//        return roadAddress
//    }
//
//    fun getRoomNumber():String{
//        return roomNumber
//    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}


