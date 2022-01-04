package fastcampus.aop.part2.mgr_villa.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import fastcampus.aop.part2.mgr_villa.NoticeActivity
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.VillaHomeActivity
import fastcampus.aop.part2.mgr_villa.databinding.MgrHomeFragmentBinding

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

        initFragOnClick()

    }

    private fun initFragOnClick(){
        binding.villaTenantCountFragmentArea.setOnClickListener {

            val NoticeActivity = Intent(context, NoticeActivity::class.java)
            startActivity(NoticeActivity)

        }
    }



    private fun showToast(message: String) {
        Toast.makeText( activity
            , message, Toast.LENGTH_SHORT).show()
    }




}