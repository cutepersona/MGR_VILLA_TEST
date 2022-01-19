package fastcampus.aop.part2.mgr_villa.customdialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.adapter.RequestTenantDialogAdapter
import kotlinx.android.synthetic.main.activity_account.view.*
import kotlinx.android.synthetic.main.mgr_addaccount.*
import kotlinx.android.synthetic.main.mgr_check.cancelButton
import kotlinx.android.synthetic.main.mgr_check.finishButton
import kotlinx.android.synthetic.main.recycleview_banks.*
import kotlinx.android.synthetic.main.recycleview_banks.view.*
import kotlinx.coroutines.selects.select

class RequestTenantDialog(context: Context) {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    private var tenantEmail: String = ""
    private var tenantName: String = ""
    private var tenantPhone: String = ""

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog(requestTenantDialogAdapter: RequestTenantDialogAdapter) {
        dialog.setContentView(R.layout.mgr_addaccount)

        dialog.setCanceledOnTouchOutside(false)
        dialog.rv_banks.adapter = requestTenantDialogAdapter
        dialog.rv_banks.layoutManager = LinearLayoutManager(dialog.context)
//        dialog.rv_banks.addItemDecoration(DividerItemDecoration(dialog.context, LinearLayoutManager.VERTICAL))
        dialog.show()

//
//        BankListAdapter.setItemClickListener(object : BankDialogAdapter.OnItemClickListener {
//            override fun onClick(v: View, position: Int) {
//                bank = BankListAdapter.bankList[position]
//            }
//        })


        dialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.finishButton.setOnClickListener {
            onClickListener.onClicked(tenantEmail, tenantName, tenantPhone)
            dialog.dismiss()
        }


    }

    interface OnDialogClickListener {
        fun onClicked(tenantEmail: String, tenantName:String, tenantPhone:String)
    }

    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}