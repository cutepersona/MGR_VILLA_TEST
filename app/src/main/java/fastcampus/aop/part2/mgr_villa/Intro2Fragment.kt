package fastcampus.aop.part2.mgr_villa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import fastcampus.aop.part2.mgr_villa.databinding.FragmentIntro2Binding

class Intro2Fragment: Fragment() {

    private val binding : FragmentIntro2Binding by lazy { FragmentIntro2Binding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.introViewPager)

        viewPager?.currentItem = 1
//
//    binding.txtNext.setOnClickListener {
//        viewPager?.currentItem = 1
//    }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = Intro1Fragment()
    }

}