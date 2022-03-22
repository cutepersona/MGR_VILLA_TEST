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

    // onClickListener와 OnDialogClickListener와 연결
    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }
    // onClicked Interface
    // 이 다이얼로그에서 선택한 값을 가져오기 위해 선언
    interface OnDialogClickListener {
        fun onClicked(bank: String)
    }

    fun showDialog(BankListAdapter: BankDialogAdapter) {
        dialog.setContentView(R.layout.mgr_addaccount)

        dialog.setCanceledOnTouchOutside(false)
        dialog.rv_banks.adapter = BankListAdapter
        dialog.rv_banks.layoutManager = LinearLayoutManager(dialog.context)
        dialog.show()

        // BankDialogAdapter의 OnItemClickListener 구현
        BankListAdapter.setItemClickListener(object : BankDialogAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                bank = BankListAdapter.bankList[position]
            }
        })
        // 취소 버튼
        dialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        // 확인 버튼  클릭시 선택한 값을 onClicked에 담는다.
        dialog.finishButton.setOnClickListener {
            onClickListener.onClicked(bank)
            dialog.dismiss()
        }

    }

    fun DisMiss() {
        dialog.dismiss()
    }


    private fun showToast(message: String) {
        Toast.makeText(dialog.context, message, Toast.LENGTH_SHORT).show()
    }


}