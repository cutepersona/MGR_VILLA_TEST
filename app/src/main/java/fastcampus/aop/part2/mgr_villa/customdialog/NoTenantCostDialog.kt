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

class NoTenantCostDialog(context: Context) :AppCompatActivity() {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.tenant_no_cost)
        dialog.setCanceledOnTouchOutside(true)
        dialog.requestTile.setText("해당 월의 관리비 정보가 없습니다.")
        dialog.show()

        dialog.finishButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener {
        fun onClicked(context: Context, requestResult:String, roomId: Long)
    }

    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}