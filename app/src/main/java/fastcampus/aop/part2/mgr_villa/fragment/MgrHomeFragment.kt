package fastcampus.aop.part2.mgr_villa.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import fastcampus.aop.part2.mgr_villa.NoticeListActivity
import fastcampus.aop.part2.mgr_villa.VillaHomeActivity
import fastcampus.aop.part2.mgr_villa.databinding.MgrHomeFragmentBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class MgrHomeFragment:Fragment(){

    private lateinit var binding: MgrHomeFragmentBinding

    private var villaDetailAddress: String = ""
    private var address: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        address = (context as VillaHomeActivity).getAddress()
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
        binding.hAddress.text = address
        binding.hRoomNumber.text = villaDetailAddress

        MyApplication.prefs.setString("villaAddress", address.trim())

        initFragOnClick()

    }

    private fun initFragOnClick(){
        binding.villaNoticeFragmentArea.setOnClickListener {

            val NoticeActivity = Intent(context, NoticeListActivity::class.java)
            startActivity(NoticeActivity)

        }
    }



    private fun showToast(message: String) {
        Toast.makeText( activity
            , message, Toast.LENGTH_SHORT).show()
    }




}