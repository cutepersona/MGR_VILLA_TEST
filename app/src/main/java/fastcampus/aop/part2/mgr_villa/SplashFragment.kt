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

        //1
        Handler(Looper.getMainLooper()).postDelayed({
            if (isOnBoardingFinished()) {

                val toMain = Intent(context, MainActivity::class.java)
                startActivity(toMain)

//                findNavController().navigateUp()
//                findNavController().navigate(R.id.action_onBoardingFragment_to_mainActivity)
            } else {
                findNavController().navigate(R.id.action_SplashFragment_to_onBoardingFragment)
            }
        }, 1500)
    }

    //2
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