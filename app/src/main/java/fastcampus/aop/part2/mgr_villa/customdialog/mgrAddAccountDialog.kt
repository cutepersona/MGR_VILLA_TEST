package fastcampus.aop.part2.mgr_villa.customdialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.adapter.BankDialogAdapter
import kotlinx.android.synthetic.main.activity_account.view.*
import kotlinx.android.synthetic.main.mgr_addaccount.*
import kotlinx.android.synthetic.main.mgr_check.cancelButton
import kotlinx.android.synthetic.main.mgr_check.finishButton
import kotlinx.android.synthetic.main.recycleview_banks.*
import kotlinx.android.synthetic.main.recycleview_banks.view.*
import kotlinx.coroutines.selects.select

class mgrAddAccountDialog(context: Context) {


    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    private var bank: String = ""

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog(BankListAdapter: BankDialogAdapter) {
        dialog.setContentView(R.layout.mgr_addaccount)

//        dialog.setTitle("은행선택")
//        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
//        dialog.setCancelable(true)
        dialog.rv_banks.adapter = BankListAdapter
        dialog.rv_banks.layoutManager = LinearLayoutManager(dialog.context)
//        dialog.rv_banks.addItemDecoration(DividerItemDecoration(dialog.context, LinearLayoutManager.VERTICAL))
        dialog.show()


        BankListAdapter.setItemClickListener(object : BankDialogAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                bank = BankListAdapter.bankList[position]
            }
        })


        dialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.finishButton.setOnClickListener {
            onClickListener.onClicked(bank)
            dialog.dismiss()
        }


    }

    fun DisMiss() {
        dialog.dismiss()
    }

    interface OnDialogClickListener {
        fun onClicked(bank: String)
    }

    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}