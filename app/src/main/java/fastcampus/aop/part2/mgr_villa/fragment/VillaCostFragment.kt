package fastcampus.aop.part2.mgr_villa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fastcampus.aop.part2.mgr_villa.databinding.VillaMgrcostFragmentInHomeBinding
import fastcampus.aop.part2.mgr_villa.databinding.VillaNoticeFragmentInHomeBinding
import fastcampus.aop.part2.mgr_villa.databinding.VillaTenantFragmentInHomeBinding

class VillaCostFragment: Fragment() {

    private lateinit var binding: VillaMgrcostFragmentInHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = VillaMgrcostFragmentInHomeBinding.inflate(inflater, container, false)


        return binding.root
//        return super.onCreateView(inflater, container, savedInstanceState)
    }


}