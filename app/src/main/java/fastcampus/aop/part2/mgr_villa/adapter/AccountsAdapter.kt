package fastcampus.aop.part2.mgr_villa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.AccountLayout
import fastcampus.aop.part2.mgr_villa.model.TenantLayout

class AccountsAdapter(val accountList: ArrayList<AccountLayout>): RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsAdapter.AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_accounts, parent, false)
        return AccountViewHolder(view)
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.accountId.text = accountList[position].accountId.toString()
        holder.accountBankName.text = accountList[position].bankName
        holder.accountHolder.text = accountList[position].accountHolder
        holder.accountNumber.text = accountList[position].accountNumber
        holder.favValue.text = accountList[position].favorite

        if(selectedPosition == position){
            holder.favButton.setImageResource(R.drawable.ic_circle_fav)
        }else{
            holder.favButton.setImageResource(R.drawable.ic_circle_nonfav)
        }

        if(!accountList[position].favorite.equals("")){
            holder.favButton.setImageResource(R.drawable.ic_circle_fav)
        }
        else{
            holder.favButton.setImageResource(R.drawable.ic_circle_nonfav)
        }


//        holder.itemView.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }

        holder.accountUpdate.setOnClickListener {
            slideButtonClickListener.onSlideButtonClick(it, holder.accountUpdate, position)
        }

        holder.accountDelete.setOnClickListener {
            slideButtonClickListener.onSlideButtonClick(it, holder.accountDelete, position)
        }

        holder.favButton.setOnClickListener {
            selectedPosition = position
            itemClickListener.onClick(it, holder.favButton, position)
        }

    }

    class AccountViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val accountId: TextView = itemView.findViewById(R.id.AccountId)
        val accountBankName: TextView = itemView.findViewById(R.id.BankName)
        val accountHolder: TextView = itemView.findViewById(R.id.BankAccountHolder)
        val accountNumber: TextView = itemView.findViewById(R.id.BankAccountNumber)

        val accountUpdate: ImageView = itemView.findViewById(R.id.AccountUpdate)
        val accountDelete: ImageView = itemView.findViewById(R.id.AccountDelete)
        val favButton: ImageView = itemView.findViewById(R.id.favButton)
        val favValue: TextView = itemView.findViewById(R.id.favValue)

    }


    interface OnItemClickListener {
        fun onClick(v: View, imageView: ImageView, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    interface OnSlideButtonClickListener {
        fun onSlideButtonClick(v: View, imageView: ImageView, position: Int)
    }

    fun setSlideButtonClickListener(onSlideButtonClickListener: OnSlideButtonClickListener){
        this.slideButtonClickListener = onSlideButtonClickListener
    }

    private lateinit var slideButtonClickListener : OnSlideButtonClickListener


}