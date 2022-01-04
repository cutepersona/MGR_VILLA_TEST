package fastcampus.aop.part2.mgr_villa.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.adapter.PagerHomeAdapter
import fastcampus.aop.part2.mgr_villa.databinding.AddFragmentBinding

class AddFragment: Fragment() {

//    private val binding:AddFragmentBinding by lazy { AddFragmentBinding.inflate(layoutInflater)}

    private var _binding: AddFragmentBinding? = null

    private val binding get() = _binding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var context = container?.context

        val binding = AddFragmentBinding.inflate(inflater, container, false)

        var contents = mutableListOf<Fragment>()

        binding.addFragmentImage.setOnClickListener {



        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showToast(message: String) {
        Toast.makeText( activity
, message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        fun newInstance(frgN: String): AddFragment {
            val args = Bundle()
            args.putString("addFrag",frgN)
            val fragment = AddFragment()
            fragment.arguments = args
            return fragment
        }
    }
}