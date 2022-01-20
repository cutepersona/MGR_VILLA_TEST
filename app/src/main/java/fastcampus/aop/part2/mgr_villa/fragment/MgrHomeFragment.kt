package fastcampus.aop.part2.mgr_villa.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import fastcampus.aop.part2.mgr_villa.*
import fastcampus.aop.part2.mgr_villa.databinding.MgrHomeFragmentBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class MgrHomeFragment:Fragment(){

    private lateinit var binding: MgrHomeFragmentBinding

    private var roomNumber: String = ""
    private var roadAddress: String = ""
    private var address: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        roomNumber = (context as VillaHomeActivity).getRoomNumber()
//        roadAddress = (context as VillaHomeActivity).getRoadAddress()
//        address = (context as VillaHomeActivity).getAddress()

//        if(address.isNullOrEmpty()){
//            address = MyApplication.prefs.getString("villaAddress","")
//        }
//        if(roadAddress.isNullOrEmpty()){
//            roadAddress = MyApplication.prefs.getString("roadAddress","")
//        }
//        if(roomNumber.isNullOrEmpty()){
//            roomNumber = MyApplication.prefs.getString("roomNumber","")
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MgrHomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hAddress.text = arguments?.getString("address")
        binding.hRoomNumber.text = arguments?.getString("roomNumber")
        binding.hRoadAddress.text = arguments?.getString("roadAddress")
        initFragOnClick()

    }

    private fun initFragOnClick(){
        binding.villaTenantCountFragmentArea.setOnClickListener {
            val TenantListActivity = Intent(context, TenantListActivity::class.java)
            startActivity(TenantListActivity)
        }

        binding.villaNoticeFragmentArea.setOnClickListener {
            val NoticeActivity = Intent(context, NoticeListActivity::class.java)
            startActivity(NoticeActivity)
        }

        binding.villaMgrCostFragmentArea.setOnClickListener {
            if (MyApplication.prefs.getString("userType","").equals("MGR")){
                val mgrCostDivActivity = Intent(context, MgrCostDivActivity::class.java)
                startActivity(mgrCostDivActivity)
            }
        }

    }



    private fun showToast(message: String) {
        Toast.makeText( activity
            , message, Toast.LENGTH_SHORT).show()
    }




}