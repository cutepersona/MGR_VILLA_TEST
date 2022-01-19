package fastcampus.aop.part2.mgr_villa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.RequestTenantLayout

class RequestTenantDialogAdapter(val RequestTenantList: Array<RequestTenantLayout>): RecyclerView.Adapter<RequestTenantDialogAdapter.RequestTenantViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestTenantDialogAdapter.RequestTenantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_requesttenants, parent, false)
        return RequestTenantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return RequestTenantList.size
    }

    override fun onBindViewHolder(holder: RequestTenantViewHolder, position: Int) {

        holder.RequestTenantEmail.text = RequestTenantList[position].RequestTenantEmail
        holder.RequestTenantName.text = RequestTenantList[position].RequestTenantName
        holder.RequestTenantPhone.text = RequestTenantList[position].RequestTenantPhone

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
            notifyDataSetChanged()
        }

    }

    class RequestTenantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val RequestTenantEmail: RadioButton = itemView.findViewById(R.id.RequestTenantEmail)
        val RequestTenantName: TextView = itemView.findViewById(R.id.RequestTenantName)
        val RequestTenantPhone: TextView = itemView.findViewById(R.id.RequestTenantPhone)

    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

}