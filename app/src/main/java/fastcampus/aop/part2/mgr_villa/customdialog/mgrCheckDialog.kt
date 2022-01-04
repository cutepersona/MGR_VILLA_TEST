package fastcampus.aop.part2.mgr_villa.customdialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import fastcampus.aop.part2.mgr_villa.R
import kotlinx.android.synthetic.main.mgr_check.*

class mgrCheckDialog(context: Context)  {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener


    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog()
    {
        dialog.setContentView(R.layout.mgr_check)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val mgrPw = dialog.findViewById<EditText>(R.id.mgrPassword)

        dialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.finishButton.setOnClickListener {
            onClickListener.onClicked(mgrPw.text.toString())
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener
    {
        fun onClicked(mgrPassword: String)
    }

}