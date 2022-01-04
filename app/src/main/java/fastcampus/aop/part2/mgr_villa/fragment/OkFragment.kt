package fastcampus.aop.part2.mgr_villa.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import fastcampus.aop.part2.mgr_villa.MainActivity
import fastcampus.aop.part2.mgr_villa.SignUpActivity
import fastcampus.aop.part2.mgr_villa.databinding.OkPopupBinding

class OkFragment: DialogFragment() {

    private lateinit var binding: OkPopupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OkPopupBinding.inflate(inflater, container, false)


//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.SignComplete.setOnClickListener {
            dismiss()
            val CallMainIntent = Intent(getActivity(), MainActivity::class.java)
            startActivity(CallMainIntent)
        }

        return binding.root
    }

}