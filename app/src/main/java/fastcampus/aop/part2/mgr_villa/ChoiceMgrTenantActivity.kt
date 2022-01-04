package fastcampus.aop.part2.mgr_villa

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fastcampus.aop.part2.mgr_villa.customdialog.mgrCheckDialog
import fastcampus.aop.part2.mgr_villa.database.VillaUsersHelper
import fastcampus.aop.part2.mgr_villa.databinding.ActivityChoiceMgrTenantBinding

class ChoiceMgrTenantActivity: AppCompatActivity() {

    private val binding: ActivityChoiceMgrTenantBinding by lazy { ActivityChoiceMgrTenantBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initTenantButton()
        initMgrButton()
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.UserTypeToolbar)
        setSupportActionBar(toolbar)

        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
    }

    // 툴바 백버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // tenant 버튼 클릭
    private fun initTenantButton() {
        binding.TenantButton.setOnClickListener {
            val tenantSignUpActivity = Intent(this, SignUpActivity::class.java)
            tenantSignUpActivity.putExtra("tenant","TENANT")
            startActivity(tenantSignUpActivity)

        }
    }

    // mgr 버튼 클릭
    private fun initMgrButton() {
        binding.MgrButton.setOnClickListener {

            val mgrCheckDialog = mgrCheckDialog(this)
            mgrCheckDialog.showDialog()
            mgrCheckDialog.setOnClickListener(object : mgrCheckDialog.OnDialogClickListener {
                override fun onClicked(mgrPassword: String) {
                    if (mgrPassword.equals("cjtel")){
                        callMgrSighUpActivity()
                    } else {
                        showToast("관리자 비밀번호가 일치하지 않습니다.")
                    }

                }

            })

        }
    }

    private fun callMgrSighUpActivity(){
        val callSignUpActivity = Intent(this, SignUpActivity::class.java)
        callSignUpActivity.putExtra("mgr","MGR")
        startActivity(callSignUpActivity)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}