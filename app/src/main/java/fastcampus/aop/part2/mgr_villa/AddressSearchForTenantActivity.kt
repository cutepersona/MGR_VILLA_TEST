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
import fastcampus.aop.part2.mgr_villa.Object.KakaoApiRetrofitClient
import fastcampus.aop.part2.mgr_villa.adapter.KakaoApiAdapter
import fastcampus.aop.part2.mgr_villa.adapter.KakaoApiTenantAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityAddrSearchTenantBinding
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoApi
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoApi.Companion.API_KEY
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoApi.Companion.BASE_URL
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoData
import fastcampus.aop.part2.mgr_villa.kakaodata.KakaoKeywordData
import fastcampus.aop.part2.mgr_villa.model.AddrTenantLayout
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddressSearchForTenantActivity : AppCompatActivity() {

    private val binding: ActivityAddrSearchTenantBinding by lazy {
        ActivityAddrSearchTenantBinding.inflate(
            layoutInflater
        )
    }

    private val addrTenantListItems = arrayListOf<AddrTenantLayout>()                   // 리싸이클러 뷰 아이템
    private val addrListAdapter = KakaoApiTenantAdapter(addrTenantListItems)            // 리싸이클러 뷰 어댑터

    private val kakaoApi = KakaoApiRetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvAddrsTenant.adapter = addrListAdapter

        if(intent.hasExtra("email")){
            binding.emailHiddenTenant.setText(intent.getStringExtra("email"))
        }
//        initUsers()
        binding.addrSearchTenantButton.setOnClickListener {
            if (binding.AddressTenantEditText.text.toString().contains("동")
                ||binding.AddressTenantEditText.text.toString().contains("길")){
                callKakaoAddress(binding.AddressTenantEditText.text.toString())
            }else{
                callKakaoKeyword(binding.AddressTenantEditText.text.toString())
            }

        }


        // 리스트 주소 클릭
        addrListAdapter.setItemClickListener(object : KakaoApiTenantAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                Thread(Runnable {

                    val userdb = VillaNoticeHelper.getInstance(applicationContext)

                    val isRequestVilla = userdb!!.VillaNoticeDao().isRequestVilla(
                        addrTenantListItems[position].address_name
                    )

                    runOnUiThread {
                        if(isRequestVilla < 1){
                            showToast("해당 주소지가 등록되어 있지 않습니다. 관리자에게 문의바랍니다.")
                        } else {
                            val ToRequestAddr = Intent(v.context, TenantListForRequestActivity::class.java)
                            ToRequestAddr.putExtra("requestAddress",addrTenantListItems[position].address_name)
                            startActivity(ToRequestAddr)
                        }
                    }
                }).start()

//                val addVillaInfoActivity = Intent(v.context, VillaInfoActivity::class.java)
//                addVillaInfoActivity.putExtra("address", addrListItems[position].address_name)
//                if (!addrListItems[position].villa_name.isNullOrEmpty()){
//                    addVillaInfoActivity.putExtra("villa_name", addrListItems[position].villa_name)
//                }
//                addVillaInfoActivity.putExtra("email", binding.emailHiddenTenant.text.toString().trim())
//                startActivity(addVillaInfoActivity)

            }

        })

        initToolBar()
        initAddrSearchEditText()


    }

    private fun initRequestTenant() {
        Thread(Runnable {

            val userdb = VillaNoticeHelper.getInstance(applicationContext)

            val userInfo = userdb!!.VillaNoticeDao().getUser(
                MyApplication.prefs.getString("email","")
            )

            runOnUiThread {

            }
        }).start()
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.addrSearchTenantToolbar)
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


    private fun initAddrSearchEditText(){
        binding.AddressTenantEditText.setOnKeyListener { v, keyCode, event ->
//            if (event.action == KeyEvent.KEYCODE_BACK){
                binding.AddressTenantEditText.setText("")
                addrTenantListItems.clear()
                addrListAdapter.notifyDataSetChanged()
//            }

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

            addrTenantListItems.clear()

            for(document in searchResult!!.documents){
                // 결과를 리싸이클러 뷰에 추가
                var item = AddrTenantLayout(document.road_address.address_name
                    ,document.address.address_name
                    ,""
                )
                addrTenantListItems.add(item)
            }

            addrListAdapter.notifyDataSetChanged()


        } else {
            showToast("검색 결과가 없습니다.")
        }

    }

    private fun addItemsKeywordAddrs(searchResult: KakaoKeywordData?){
        if (!searchResult?.documents.isNullOrEmpty()){

            addrTenantListItems.clear()

            for(document in searchResult!!.documents){
                // 결과를 리싸이클러 뷰에 추가
                var item = AddrTenantLayout(document.road_address_name
                                    ,document.address_name
                                    ,document.place_name
                )
                addrTenantListItems.add(item)
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