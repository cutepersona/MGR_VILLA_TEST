package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nhn.android.naverlogin.OAuthLogin
import fastcampus.aop.part2.mgr_villa.customdialog.LogOutDialog
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityHomeBinding
import fastcampus.aop.part2.mgr_villa.fragment.*
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VillaHomeActivity : AppCompatActivity() {

    val firestoreDB = Firebase.firestore

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

        startShimmerEffect()

        val handler = Handler()
        handler.postDelayed({
            initHomeFragment()
        }, 1000)

        initHomeBottomNavigationBar()

//            binding.homeShimmerFrameLayout.visibility = View.GONE
//
//        if (binding.homeShimmerFrameLayout.isShimmerStarted){
//
//
//            binding.homeShimmerFrameLayout.visibility = View.GONE
//            binding.homeShimmerFrameLayout.stopShimmer()
//
//        }

//        setBindingFragment()

    }

    private fun startShimmerEffect() {
        binding.homeShimmerFrameLayout.visibility = View.VISIBLE
        binding.homeShimmerFrameLayout.startShimmer()
    }

//
//    override fun onResume() {
//        super.onResume()
//        binding.homeShimmerFrameLayout.startShimmer()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        binding.homeShimmerFrameLayout.stopShimmer()
//    }


    // 하단 네비게이션 바
    private fun initHomeBottomNavigationBar() {

//        supportFragmentManager.beginTransaction().add(R.id.fl_container, MgrHomeFragment()
//        ).commit()

        val toMyInfoActivity = Intent(this, MyInfoActivity::class.java)

        bnv_Home.setOnNavigationItemSelectedListener {
            when(it.itemId){
//                R.id.nv_Home ->
//                    startActivity(toHomeActivity)
                R.id.nv_All ->
                    startActivity(toMyInfoActivity)
            }

            true
        }

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

        // 관리자인 경우
        if (MyApplication.prefs.getString("userType","").equals("MGR")){

//            showToast(MyApplication.prefs.getString("villaAddress", "").trim())

            CoroutineScope(Dispatchers.Main).launch{

                var currentCount = 0
                firestoreDB.collection("VillaTenant")
                    .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
                    .whereNotEqualTo("tenantEmail", "")
                    .get()
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            for (i in task.result!!) {
                                currentCount++
//                        showToast(i.data["roomNumber"].toString())
//                        currentCount ++


                            }

//                            showToast(currentCount.toString())

                            firestoreDB.collection("VillaTenant")
                                .whereEqualTo("tenantEmail", userEmail)
                                .get()
                                .addOnSuccessListener { result ->
                                    if (result.isEmpty){

                                        // 빌라입주정보 가져오기
                                        firestoreDB.collection("VillaInfo")
                                            .whereEqualTo("villaAddress", MyApplication.prefs.getString("villaAddress", "")).limit(1)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                // 입주정보가 없을때
                                                if (task.isSuccessful){
                                                    for (i in task.result!!) {
                                                        val bundle = Bundle()
                                                        bundle.putString("roomNumber","")
                                                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
                                                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))
                                                        bundle.putString("currentTenantCount", currentCount.toString())
                                                        bundle.putString("totalTenantCount", i.data["villaTenantCount"].toString())

                                                        // 집 주소 및 전입호수 전달
                                                        val mgrFrag = MgrHomeFragment()
                                                        mgrFrag.arguments = bundle

                                                        //                        // 입주자 수 정보 전달.
                                                        //                        val homeTenantFrag = VillaTenantFragment()
                                                        //                        homeTenantFrag.arguments = bundle

                                                        val transaction = supportFragmentManager.beginTransaction()
                                                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
                                                        transaction.detach(mgrFrag).attach(mgrFrag)
                                                        transaction.commit()
                                                        break
                                                    }

                                                }
                                            }

                                    } else {

                                        var roomNum = ""

                                        // 입주 호 가져오기
                                        firestoreDB.collection("VillaTenant")
                                            .whereEqualTo("tenantEmail", userEmail).limit(1)
                                            .get()
                                            .addOnCompleteListener{ task ->
                                                if (task.isSuccessful){
                                                    for (i in task.result!!) {
                                                        roomNum = i.data["roomNumber"].toString()
                                                        break
                                                    }
                                                    // 빌라입주정보 가져오기
                                                    firestoreDB.collection("VillaInfo")
                                                        .whereEqualTo("villaAddress", MyApplication.prefs.getString("villaAddress", "")).limit(1)
                                                        .get()
                                                        .addOnCompleteListener { task ->
                                                            // 입주정보가 없을때
                                                            if (task.isSuccessful){
                                                                for (i in task.result!!) {
                                                                    val bundle = Bundle()
                                                                    bundle.putString("roomNumber",roomNum)
                                                                    bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
                                                                    bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))
                                                                    bundle.putString("currentTenantCount", currentCount.toString())
                                                                    bundle.putString("totalTenantCount", i.data["villaTenantCount"].toString())

                                                                    // 집 주소 및 전입호수 전달
                                                                    val mgrFrag = MgrHomeFragment()
                                                                    mgrFrag.arguments = bundle

                                                                    //                        // 입주자 수 정보 전달.
                                                                    //                        val homeTenantFrag = VillaTenantFragment()
                                                                    //                        homeTenantFrag.arguments = bundle

                                                                    val transaction = supportFragmentManager.beginTransaction()
                                                                    transaction.add(R.id.recycleViewConstraint, mgrFrag)
                                                                    transaction.detach(mgrFrag).attach(mgrFrag)
                                                                    transaction.commit()
                                                                }

                                                            }
                                                        }
                                                }
                                            }


                                    }
                                }





                        }
                    }



            }


        } else {
            CoroutineScope(Dispatchers.Main).launch {
                // 세입자 정보
                var roomNum = ""

                // 입주 호 가져오기
                firestoreDB.collection("VillaTenant")
                    .whereEqualTo("tenantEmail", userEmail).limit(1)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (i in task.result!!) {
                                roomNum = i.data["roomNumber"].toString()
                                break
                            }
                            // 빌라입주정보 가져오기
                            firestoreDB.collection("VillaTenant")
                                .whereEqualTo("tenantEmail", MyApplication.prefs.getString("email", ""))
                                .limit(1)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        for (i in task.result!!) {
                                            val bundle = Bundle()
                                            bundle.putString("roomNumber", roomNum)
                                            bundle.putString(
                                                "roadAddress",
                                                MyApplication.prefs.getString("roadAddress", "")
                                            )
                                            bundle.putString(
                                                "address",
                                                MyApplication.prefs.getString("villaAddress", "")
                                            )
                                            bundle.putString("currentTenantCount", "0")
                                            bundle.putString("totalTenantCount", "0")

                                            // 집 주소 및 전입호수 전달
                                            val mgrFrag = MgrHomeFragment()
                                            mgrFrag.arguments = bundle

                                            val transaction = supportFragmentManager.beginTransaction()
                                            transaction.add(R.id.recycleViewConstraint, mgrFrag)
                                            transaction.detach(mgrFrag).attach(mgrFrag)
                                            transaction.commit()
                                            break
                                        }

                                    }
                                }
                        }
                    }

            }


//-------------------------------------------------------------------------------------------
//        Thread(Runnable {
//            Thread.sleep(100L)              // 현재 세입자 정보를 바로 못가져와서 Sleep 줌
//            val userdb = VillaNoticeHelper.getInstance(applicationContext)
//
//            var currentTenantCount: Int? = 0
//
//            val user = userdb?.VillaNoticeDao()?.getUser(
//                userEmail
//            )
//
//            val tenantInfo = userdb?.VillaNoticeDao()?.getTenantFromEmail(userEmail)
//
//            currentTenantCount = userdb?.VillaNoticeDao()?.getCurrentTenantCount(
//                MyApplication.prefs.getString("villaAddress","").trim()
//            )
//
//            val villaInfo = userdb?.VillaNoticeDao()?.getVillaInfo(
//                userEmail
//            )
//
//            runOnUiThread {
//                if (user?.userType.equals("MGR")){
//                    if (tenantInfo != null) {
//                        val bundle = Bundle()
//                        bundle.putString("roomNumber",tenantInfo?.roomNumber)
//                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
//                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))
//
////                        showToast(currentTenantCount.toString())
//
//                        bundle.putString("currentTenantCount", currentTenantCount.toString())
//                        bundle.putString("totalTenantCount", villaInfo?.villaTenantCount.toString())
//
//                        // 집 주소 및 전입호수 전달
//                        val mgrFrag = MgrHomeFragment()
//                        mgrFrag.arguments = bundle
//
////                        // 입주자 수 정보 전달.
////                        val homeTenantFrag = VillaTenantFragment()
////                        homeTenantFrag.arguments = bundle
//
//                        val transaction = supportFragmentManager.beginTransaction()
//                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
//                        transaction.commit()
//                    } else {
//                        val bundle = Bundle()
////                        val tenantFragmentBundle = Bundle()
//                        bundle.putString("roomNumber","")
//                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
//                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))
//                        bundle.putString("currentTenantCount", currentTenantCount.toString())
//                        bundle.putString("totalTenantCount", villaInfo?.villaTenantCount.toString())
//
//                        // 집 주소 및 전입호수 전달
//                        val mgrFrag = MgrHomeFragment()
//                        mgrFrag.arguments = bundle
//
////                        // 입주자 수 정보 전달.
////                        val homeTenantFrag = VillaTenantFragment()
////                        homeTenantFrag.arguments = bundle
//
//                        val transaction = supportFragmentManager.beginTransaction()
//                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
//                        transaction.commit()
//                    }
//                } else {
//                    if (tenantInfo != null) {
//                        val bundle = Bundle()
//                        bundle.putString("roomNumber",tenantInfo?.roomNumber)
//                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
//                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))
//
//                        // 집 주소 및 전입호수 전달
//                        val mgrFrag = MgrHomeFragment()
//                        mgrFrag.arguments = bundle
//
//                        val transaction = supportFragmentManager.beginTransaction()
//                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
//                        transaction.commit()
//                    } else {
//                        val bundle = Bundle()
//                        bundle.putString("roomNumber","")
//                        bundle.putString("roadAddress",MyApplication.prefs.getString("roadAddress", ""))
//                        bundle.putString("address",MyApplication.prefs.getString("villaAddress", ""))
//
//                        val mgrFrag = MgrHomeFragment()
//                        mgrFrag.arguments = bundle
//
//                        val transaction = supportFragmentManager.beginTransaction()
//                        transaction.add(R.id.recycleViewConstraint, mgrFrag)
//                        transaction.commit()
//                    }
//                }
//
//
//            }
//        }).start()
            //-------------------------------------------------------------------------------------------
        }


        if (binding.homeShimmerFrameLayout.isShimmerStarted){
            binding.homeShimmerFrameLayout.stopShimmer()
            binding.homeShimmerFrameLayout.visibility = View.GONE
        }

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
        if (MyApplication.prefs.getString("userType","").equals("TENANT")){
            // 회원이름 가져오기
            firestoreDB.collection("VillaUsers")
                .whereEqualTo("mailAddress", email)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (i in task.result!!) {
                            binding.hUserName.text = i.data["userName"].toString()
                            break
                        }
                        // 집정보 가져오기
                        firestoreDB.collection("VillaTenant")
                            .whereEqualTo("tenantEmail", email)
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (i in task.result!!) {
                                        MyApplication.prefs.setString("villaAddress", i.data["villaAddr"].toString().trim())
                                        MyApplication.prefs.setString("roadAddress", i.data["roadAddress"].toString().trim())
                                        break
////            MyApplication.prefs.setString("roomNumber", tenantInfo?.roomNumber.toString())
                                    }
                                }
                            }
                    }
                }

        } else {
            // 회원이름 가져오기
            firestoreDB.collection("VillaUsers")
                .whereEqualTo("mailAddress", email)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (i in task.result!!) {
                            binding.hUserName.text = i.data["userName"].toString()
                            break
                        }
                        // 집정보 가져오기
                        firestoreDB.collection("VillaInfo")
                            .whereEqualTo("mailAddress", email)
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (i in task.result!!) {
                                        MyApplication.prefs.setString("villaAddress", i.data["villaAddress"].toString().trim())
                                        MyApplication.prefs.setString("roadAddress", i.data["roadAddress"].toString().trim())
////            MyApplication.prefs.setString("roomNumber", tenantInfo?.roomNumber.toString())
                                        break
                                    }

                                }
                            }
                    }
                }

        }
//
//        //---------------------------------------------------------------------------------------------
//        val userdb = VillaNoticeHelper.getInstance(applicationContext)
////        val villadb = VillaInfoHelper.getInstance(applicationContext)
//
//        if (MyApplication.prefs.getString("userType","").equals("TENANT")){
//            Thread(Runnable {
//                val user = userdb?.VillaNoticeDao()?.getUser(
//                    email
//                )
//
//                val villaInfo = userdb?.VillaNoticeDao()?.getVillaInfo(email)
//                val tenantInfo = userdb?.VillaNoticeDao()?.getTenantFromEmail(email)
//
//                MyApplication.prefs.setString("villaAddress", tenantInfo?.villaAddr.toString())
//                MyApplication.prefs.setString("roadAddress", tenantInfo?.roadAddress.toString())
////            MyApplication.prefs.setString("roomNumber", tenantInfo?.roomNumber.toString())
//
//
//                runOnUiThread {
//                    if (user == null) {
//                        showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
//                        return@runOnUiThread
//                    } else {
////                        roomNumber = tenantInfo?.roomNumber.toString()
////                        address = MyApplication.prefs.getString("villaAddress", "")
////                        roadAddress = MyApplication.prefs.getString("roadAddress", "")
//                        binding.hUserName.setText(user.userName)
//                    }
//                }
//            }).start()
//        } else {
//            Thread(Runnable {
//                val user = userdb?.VillaNoticeDao()?.getUser(
//                    email
//                )
//
//                val villaInfo = userdb?.VillaNoticeDao()?.getVillaInfo(email)
//                val tenantInfo = userdb?.VillaNoticeDao()?.getTenantFromEmail(email)
//
//                MyApplication.prefs.setString("villaAddress", villaInfo?.villaAddress.toString())
//                MyApplication.prefs.setString("roadAddress", villaInfo?.roadAddress.toString())
////            MyApplication.prefs.setString("roomNumber", tenantInfo?.roomNumber.toString())
//
//
//                runOnUiThread {
//                    if (user == null || villaInfo == null) {
//                        showToast("회원정보가 없거나 정보입력이 잘못되었습니다.")
//                        return@runOnUiThread
//                    } else {
////                        roomNumber = tenantInfo?.roomNumber.toString()
////                        address = MyApplication.prefs.getString("villaAddress", "")
////                        roadAddress = MyApplication.prefs.getString("roadAddress", "")
//                        binding.hUserName.setText(user.userName)
//
//                    }
//                }
//            }).start()
//        }
//        //---------------------------------------------------------------------------------------------

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


