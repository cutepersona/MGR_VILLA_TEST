package fastcampus.aop.part2.mgr_villa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fastcampus.aop.part2.mgr_villa.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {

    private val binding : ActivityLoadingBinding by lazy { ActivityLoadingBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}