package fastcampus.aop.part2.mgr_villa.customdialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhn.android.naverlogin.OAuthLogin
import fastcampus.aop.part2.mgr_villa.LoginActivity
import fastcampus.aop.part2.mgr_villa.MainActivity
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.adapter.BankDialogAdapter
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.android.synthetic.main.activity_account.view.*
import kotlinx.android.synthetic.main.mgr_addaccount.*
import kotlinx.android.synthetic.main.mgr_check.cancelButton
import kotlinx.android.synthetic.main.mgr_check.finishButton
import kotlinx.android.synthetic.main.recycleview_banks.*
import kotlinx.android.synthetic.main.recycleview_banks.view.*
import kotlinx.coroutines.selects.select

class LogOutDialog(context: Context) {


    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    lateinit var mOAuthLoginInstance : OAuthLogin

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.logout)
        dialog.setCanceledOnTouchOutside(false)
        mOAuthLoginInstance = OAuthLogin.getInstance()
        dialog.show()


        dialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.finishButton.setOnClickListener {
            MyApplication.prefs.clear()
            mOAuthLoginInstance.logout(dialog.context)
            dialog.dismiss()
            val toMain = Intent(dialog.context, MainActivity::class.java)
            startActivity(dialog.context,toMain,null)
        }


    }

    interface OnDialogClickListener {
        fun onClicked(bank: String)
    }

    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}