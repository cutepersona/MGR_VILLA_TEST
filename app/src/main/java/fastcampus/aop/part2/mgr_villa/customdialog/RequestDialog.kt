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

class RequestDialog(context: Context) :AppCompatActivity() {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener


    private var requestResult: String = ""

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog(TenantRequestAdapter:TenantRequestAdapter, requestAddress:String, roomId: Long, roomNumber:String) {
        dialog.setContentView(R.layout.tenant_request)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestTile.setText(roomNumber + "으로 전입요청 하시겠습니까?")
        dialog.show()

        dialog.cancelButton.setOnClickListener {
            requestResult = "Cancel"
            onClickListener.onClicked(requestResult)
            dialog.dismiss()
        }

        dialog.finishButton.setOnClickListener {
            requestResult = "Request"
            onClickListener.onClicked(requestResult)
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener {
        fun onClicked(requestResult:String)
    }

    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}