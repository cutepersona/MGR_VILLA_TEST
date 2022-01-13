package fastcampus.aop.part2.mgr_villa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R

class BankDialogAdapter(val bankList: Array<String>): RecyclerView.Adapter<BankDialogAdapter.bankViewHolder>() {

    private var checkPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankDialogAdapter.bankViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_banks, parent, false)
        return bankViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bankList.size
    }

    override fun onBindViewHolder(holder: bankViewHolder, position: Int) {

        holder.bankRadioButton.isChecked = position == checkPosition
        holder.bankName.text = bankList[position]
        holder.bankRadioButton.setOnClickListener {
            checkPosition = position
            itemClickListener.onClick(it, position)
            notifyDataSetChanged()
        }
//
//        holder.bankName.setOnClickListener {
//            checkPosition = position
//            notifyDataSetChanged()
//        }

        holder.itemView.setOnClickListener {
            checkPosition = position
            itemClickListener.onClick(it, position)
            notifyDataSetChanged()
        }

    }

    class bankViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val bankRadioButton: RadioButton = itemView.findViewById(R.id.radio_bank)
        val bankName: TextView = itemView.findViewById(R.id.bankName)

    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

}