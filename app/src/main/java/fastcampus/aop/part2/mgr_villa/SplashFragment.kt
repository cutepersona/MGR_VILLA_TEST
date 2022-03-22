package fastcampus.aop.part2.mgr_villa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import fastcampus.aop.part2.mgr_villa.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private val binding : FragmentSplashBinding by lazy { FragmentSplashBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Intro 화면 확인 여부에 따라 구분
        // Intro화면 못봤을 경우 IntroOnBoardingFragment이동
        // Intro화면 본 경우 1500 millisecond만큼 Delay이후 Main으로 이동
        //
        Handler(Looper.getMainLooper()).postDelayed({
            if (isOnBoardingFinished()) {
                val toMain = Intent(context, MainActivity::class.java)
                startActivity(toMain)
            } else {
                findNavController().navigate(R.id.action_SplashFragment_to_onBoardingFragment)
            }
        }, 1500)
    }

    // Intro 화면 완료(확인) 여부 체크
    private fun isOnBoardingFinished(): Boolean {
        val prefs = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return prefs.getBoolean("finished", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun showToast(message: String) {
        Toast.makeText( activity
            , message, Toast.LENGTH_SHORT).show()
    }


}