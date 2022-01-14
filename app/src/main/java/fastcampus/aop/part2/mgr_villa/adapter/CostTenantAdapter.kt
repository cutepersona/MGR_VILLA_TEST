package fastcampus.aop.part2.mgr_villa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.CostTenantLayout

class CostTenantAdapter(val CostTenantList: ArrayList<CostTenantLayout>): RecyclerView.Adapter<CostTenantAdapter.CostTenantViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostTenantAdapter.CostTenantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_costtenants, parent, false)
        return CostTenantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return CostTenantList.size
    }

    override fun onBindViewHolder(holder: CostTenantViewHolder, position: Int) {
        holder.CostTenantRoomNumber.text = CostTenantList[position].CostTenantRoomNumber
        holder.CostTenantContractDate.text = CostTenantList[position].CostTenantContractDate
        holder.CostTenantLeaveDate.text = CostTenantList[position].CostTenantLeaveDate
        holder.CostTenantRoomId.text = CostTenantList[position].CostTenantRoomId.toString()

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class CostTenantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val CostTenantRoomNumber: TextView = itemView.findViewById(R.id.CostTenantTitle)
        val CostTenantContractDate: TextView = itemView.findViewById(R.id.CostContractDate)
        val CostTenantLeaveDate: TextView = itemView.findViewById(R.id.CostLeaveDate)
        val CostTenantRoomId: TextView = itemView.findViewById(R.id.CostTenantRoomId)

    }


    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
//
//    interface OnSlideButtonClickListener {
//        fun onSlideButtonClick(v: View, imageView: ImageView, position: Int)
//    }
//
//    fun setSlideButtonClickListener(onSlideButtonClickListener: OnSlideButtonClickListener){
//        this.slideButtonClickListener = onSlideButtonClickListener
//    }
//
//    private lateinit var slideButtonClickListener : OnSlideButtonClickListener


}