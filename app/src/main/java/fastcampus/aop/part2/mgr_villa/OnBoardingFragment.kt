package fastcampus.aop.part2.mgr_villa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import fastcampus.aop.part2.mgr_villa.adapter.IntroViewPagerAdapter
import fastcampus.aop.part2.mgr_villa.databinding.FragmentOnBoardingBinding
import kotlinx.android.synthetic.main.fragment_on_boarding.*

class OnBoardingFragment : Fragment() {

    private val binding : FragmentOnBoardingBinding by lazy { FragmentOnBoardingBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //1
        setupViewPager()

    }

    private fun setupViewPager() {
        val fragmentList = arrayListOf(
            Intro1Fragment(),
            Intro2Fragment(),
            Intro3Fragment(),
            Intro4Fragment()
        )

        val adapter = IntroViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.introViewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.introViewPager)
        //2
        // 사용자가 버튼눌러서 넘어가게끔 함 이거는 추후 수정필요
//        binding.introViewPager.isUserInputEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun showToast(message: String) {
        Toast.makeText( activity
            , message, Toast.LENGTH_SHORT).show()
    }

}