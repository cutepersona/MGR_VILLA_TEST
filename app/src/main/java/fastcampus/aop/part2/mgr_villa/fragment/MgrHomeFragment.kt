package fastcampus.aop.part2.mgr_villa.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import fastcampus.aop.part2.mgr_villa.*
import fastcampus.aop.part2.mgr_villa.databinding.MgrHomeFragmentBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.mgr_home_fragment.*

class MgrHomeFragment() :Fragment(){

    private lateinit var binding: MgrHomeFragmentBinding

    private val tenantFragment = VillaTenantFragment()
    private val noticeFragment = VillaNoticeFragment()
    private val costFragment = VillaCostFragment()

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

//        (activity as VillaHomeActivity).createSubFragment()

//        val bundle = Bundle()
//        bundle.putString("currentTenantCount", arguments?.getString("currentTenantCount"))
//
//        tenantFragment.arguments = bundle
//

        val subFragment = childFragmentManager.beginTransaction()
        subFragment.add(R.id.villaTenantCountFragmentArea, tenantFragment)
        subFragment.add(R.id.villaNoticeFragmentArea, noticeFragment)
        subFragment.add(R.id.villaMgrCostFragmentArea, costFragment)
        subFragment.commit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hAddress.text = arguments?.getString("address")
        binding.hRoomNumber.text = arguments?.getString("roomNumber")
        binding.hRoadAddress.text = arguments?.getString("roadAddress")

        val bundle = Bundle()
        bundle.putString("currentTenantCount", arguments?.getString("currentTenantCount"))
        bundle.putString("totalTenantCount", arguments?.getString("totalTenantCount"))

        tenantFragment.arguments = bundle

//             showToast(arguments?.getString("currentTenantCount").toString())


        val tenantTransaction = childFragmentManager.beginTransaction()
        tenantTransaction.replace(R.id.villaTenantCountFragmentArea, tenantFragment)
        tenantTransaction.commit()


        if (MyApplication.prefs.getString("userType","").equals("TENANT")){
            binding.villaTenantCountFragmentArea.isVisible = false
        }

        initFragOnClick()
//        (activity as VillaHomeActivity).changeText("test")
//        (activity as VillaHomeActivity).createSubFragment()

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
            } else {
                val ToTenantCost = Intent(context, TenantRoomCostForTenantActivity::class.java)
                startActivity(ToTenantCost)
            }
        }

    }



    private fun showToast(message: String) {
        Toast.makeText( activity
            , message, Toast.LENGTH_SHORT).show()
    }




}