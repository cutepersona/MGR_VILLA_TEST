package fastcampus.aop.part2.mgr_villa.customdialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import fastcampus.aop.part2.mgr_villa.AddAccountActivity
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.databinding.MgrAddaccountBinding
import kotlinx.android.synthetic.main.mgr_addaccount.*
import kotlinx.android.synthetic.main.mgr_check.*
import kotlinx.android.synthetic.main.mgr_check.cancelButton
import kotlinx.android.synthetic.main.mgr_check.finishButton

class mgrAddAccountDialog(context: Context){

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    var bankList = ArrayList<String>()


    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog()
    {
        dialog.setContentView(R.layout.mgr_addaccount)
//        dialog.setTitle("은행선택")
//        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.setCancelable(true)
        dialog.show()
//
//        dialog.cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.finishButton.setOnClickListener {
//            onClickListener.onClicked(dialog.bankSpinner.selectedItem.toString())
//            dialog.dismiss()
//        }

    }

    interface OnDialogClickListener
    {
        fun onClicked(bank: String)
    }

}