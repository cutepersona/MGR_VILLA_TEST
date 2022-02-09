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

class TenantOutDialog(context: Context) :AppCompatActivity() {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    private var context: Context = context

    private var requestDelete: String = ""

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog(roomNumber:String, roomId:String) {
        dialog.setContentView(R.layout.tenant_request)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestTile.setText(roomNumber + "의 세입자를 퇴거하시겠습니까? \n (세입자 요청을 다시받아야 합니다.)")
        dialog.show()

        dialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.finishButton.setOnClickListener {
            requestDelete = "D"
            onClickListener.onClicked(context,requestDelete, roomId)
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener {
        fun onClicked(context: Context, requestResult:String, roomId: String)
    }

    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}