package fastcampus.aop.part2.mgr_villa.customdialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.adapter.BankDialogAdapter
import fastcampus.aop.part2.mgr_villa.adapter.TenantRequestAdapter
import fastcampus.aop.part2.mgr_villa.database.VillaNoticeHelper
import fastcampus.aop.part2.mgr_villa.model.TenantRequestLayout
import kotlinx.android.synthetic.main.mgr_check.cancelButton
import kotlinx.android.synthetic.main.mgr_check.finishButton
import kotlinx.android.synthetic.main.tenant_request.*

class WelcomeDialog(context: Context) :AppCompatActivity() {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    private var context: Context = context

    private var done: String = ""

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.welcome)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestTile.setText("환영합니다. \n mgr_villa 회원가입이 완료되었습니다.")
        dialog.show()

//        dialog.cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }

        dialog.finishButton.setOnClickListener {
            done = "Done"
            onClickListener.onClicked(context,done)
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener {
        fun onClicked(context: Context, done:String)
    }

    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}