package fastcampus.aop.part2.mgr_villa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import com.nhn.android.naverlogin.OAuthLogin
import fastcampus.aop.part2.mgr_villa.Object.KakaoApiRetrofitClient
import fastcampus.aop.part2.mgr_villa.adapter.KakaoApiAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityAddrSearchBinding
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoApi
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoApi.Companion.API_KEY
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoApi.Companion.BASE_URL
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoData
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoKeywordData
import fastcampus.aop.part2.mgr_villa.model.AddrLayout
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddressSearchActivity : AppCompatActivity() {

    private val binding: ActivityAddrSearchBinding by lazy {
        ActivityAddrSearchBinding.inflate(
            layoutInflater
        )
    }

    lateinit var mOAuthLoginInstance : OAuthLogin

    private val addrListItems = arrayListOf<AddrLayout>()                   // 리싸이클러 뷰 아이템
    private val addrListAdapter = KakaoApiAdapter(addrListItems)            // 리싸이클러 뷰 어댑터

    private val kakaoApi = KakaoApiRetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvAddrs.adapter = addrListAdapter

        mOAuthLoginInstance = OAuthLogin.getInstance()

        if(intent.hasExtra("email")){
            binding.emailHidden.setText(intent.getStringExtra("email"))
        }
//        initUsers()
        binding.addrSearchButton.setOnClickListener {
            if (binding.AddressEditText.text.toString().contains("-")
                && !binding.AddressEditText.text.toString().equals("길동")
                && !binding.AddressEditText.text.toString().equals("동길")
            ){
                callKakaoAddress(binding.AddressEditText.text.toString())
            }else{
                callKakaoKeyword(binding.AddressEditText.text.toString())
            }

        }


        // 리스트 주소 클릭
        addrListAdapter.setItemClickListener(object : KakaoApiAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val addVillaInfoActivity = Intent(v.context, VillaInfoActivity::class.java)
                addVillaInfoActivity.putExtra("address", addrListItems[position].address_name)
                addVillaInfoActivity.putExtra("roadAddress", addrListItems[position].road_address_name)
                if (!addrListItems[position].villa_name.isNullOrEmpty()){
                    addVillaInfoActivity.putExtra("villa_name", addrListItems[position].villa_name)
                }
                addVillaInfoActivity.putExtra("email", binding.emailHidden.text.toString().trim())
                startActivity(addVillaInfoActivity)

            }

        })

        initToolBar()
        initAddrSearchEditText()


    }

//    // 유저정보 가져오기
//    private fun initUsers() {
//        Thread(Runnable {
//
//            val userdb = VillaNoticeHelper.getInstance(applicationContext)
//
//            val userInfo = userdb!!.VillaNoticeDao().getUser(
//                MyApplication.prefs.getString("email","")
//            )
//
//            runOnUiThread {
//                    MyApplication.prefs.setString("userType",userInfo.userType)
//            }
//        }).start()
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        mOAuthLoginInstance.logout(applicationContext)
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.addrSearchToolbar)
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
//
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


    private fun initAddrSearchEditText(){
        binding.AddressEditText.setOnKeyListener { v, keyCode, event ->
            if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_DEL){
                binding.AddressEditText.setText("")
                addrListItems.clear()
                addrListAdapter.notifyDataSetChanged()
            }

            if((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (binding.AddressEditText.text.toString().contains("-")
                    && !binding.AddressEditText.text.toString().equals("길동")
                    && !binding.AddressEditText.text.toString().equals("동길")
                ){
                    callKakaoAddress(binding.AddressEditText.text.toString())
                }else{
                    callKakaoKeyword(binding.AddressEditText.text.toString())
                }
            }
            true
        }

    }

    // 주소검색
    private fun callKakaoAddress(address: String) {
        val kakao = MutableLiveData<KakaoData>()

        kakaoApi.getKakaoAddress(KakaoApi.API_KEY, address = address)
            .enqueue(object : retrofit2.Callback<KakaoData> {
                override fun onResponse(call: Call<KakaoData>, response: Response<KakaoData>) {
                    addItemsAddrs(response.body())

//                    kakao.value = response.body()
//                    showToast("${kakao.value!!.documents[1].road_address_name}")
//                    showToast("${kakao.value!!.documents[0].address_name}")
                }

                override fun onFailure(call: Call<KakaoData>, t: Throwable) {
                    t.printStackTrace()
                }

            })

    }

    // 키워드검색
    fun callKakaoKeyword(keyword: String) {
        val kakao = MutableLiveData<KakaoKeywordData>()

        kakaoApi.getKakaoKeyword(KakaoApi.API_KEY, keyword = keyword)
            .enqueue(object : retrofit2.Callback<KakaoKeywordData> {
                override fun onResponse(
                    call: Call<KakaoKeywordData>,
                    response: Response<KakaoKeywordData>
                ) {

                    addItemsKeywordAddrs(response.body())
//                    kakao.value = response.body()
//                    showToast("${kakao.value!!.documents[0].address_name}")
                }

                override fun onFailure(call: Call<KakaoKeywordData>, t: Throwable) {
                    t.printStackTrace()
                }

            })

    }

    private fun addItemsAddrs(searchResult: KakaoData?){
        if (!searchResult?.documents.isNullOrEmpty()){

            addrListItems.clear()

            for(document in searchResult!!.documents){
                // 결과를 리싸이클러 뷰에 추가
                val item = AddrLayout(document.road_address.address_name
                    ,document.address.address_name
                    ,""
                )
                addrListItems.add(item)
                addrListItems.distinct()
            }
            addrListAdapter.notifyDataSetChanged()
        } else {
            showToast("검색 결과가 없습니다.")
        }

    }

    private fun addItemsKeywordAddrs(searchResult: KakaoKeywordData?){
        if (!searchResult?.documents.isNullOrEmpty()){

            addrListItems.clear()
        
            for(document in searchResult!!.documents){
                // 결과를 리싸이클러 뷰에 추가
                val item = AddrLayout(document.road_address_name
                                    ,document.address_name
                                    ,document.place_name
                )
                addrListItems.add(item)
                addrListItems.distinct()
            }
            addrListAdapter.notifyDataSetChanged()
        } else {
            showToast("검색 결과가 없습니다.")
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}