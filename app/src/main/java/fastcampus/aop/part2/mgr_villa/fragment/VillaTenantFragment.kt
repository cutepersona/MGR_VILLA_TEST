package fastcampus.aop.part2.mgr_villa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.databinding.VillaTenantFragmentInHomeBinding
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_searchidcomplete.*
import kotlinx.android.synthetic.main.mgr_home_fragment.*

class VillaTenantFragment: Fragment() {

    private lateinit var binding: VillaTenantFragmentInHomeBinding

    val firestoreDB = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = VillaTenantFragmentInHomeBinding.inflate(inflater, container, false)


        return binding.root
//        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.tenantCurrentCount.text = arguments?.getString("currentTenantCount")
        binding.tenantTotalCount.text = arguments?.getString("totalTenantCount")

//        showToast(arguments?.getString("currentTenantCount").toString())
    }


    private fun showToast(message: String) {
        Toast.makeText( activity
            , message, Toast.LENGTH_SHORT).show()
    }
//
//    fun setText(string: String) {
//        binding.tenantCurrentCount.setText(string)
//    }


}