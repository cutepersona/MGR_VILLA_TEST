package fastcampus.aop.part2.mgr_villa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.model.TenantLayout
import fastcampus.aop.part2.mgr_villa.model.VillaNotice

class TenantAdapter(val tenantList: ArrayList<TenantLayout>): RecyclerView.Adapter<TenantAdapter.TenantViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantAdapter.TenantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_tenants, parent, false)
        return TenantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tenantList.size
    }

    override fun onBindViewHolder(holder: TenantViewHolder, position: Int) {
        holder.tenantRoomNumber.text = tenantList[position].tenantRoomNumber
        holder.tenantContractDate.text = tenantList[position].tenantContractDate
        holder.tenantLeaveDate.text = tenantList[position].tenantLeaveDate

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class TenantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tenantRoomNumber: TextView = itemView.findViewById(R.id.tenantTitle)
        val tenantContractDate: TextView = itemView.findViewById(R.id.ContractDate)
        val tenantLeaveDate: TextView = itemView.findViewById(R.id.LeaveDate)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

}