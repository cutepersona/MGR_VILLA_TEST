package fastcampus.aop.part2.mgr_villa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.TenantLayout
import fastcampus.aop.part2.mgr_villa.model.TenantRequestLayout

class TenantRequestAdapter(val tenantList: ArrayList<TenantRequestLayout>): RecyclerView.Adapter<TenantRequestAdapter.TenantRequestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantRequestAdapter.TenantRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_tenantsrequest, parent, false)
        return TenantRequestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tenantList.size
    }

    override fun onBindViewHolder(holder: TenantRequestViewHolder, position: Int) {
        holder.requestRoomNumber.text = tenantList[position].tenantRoomNumber
        holder.requestRoomId.text = tenantList[position].tenantRoomId.toString()

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }

    }

    class TenantRequestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val requestRoomNumber: TextView = itemView.findViewById(R.id.RequestRoomNumber)
        val requestRoomId: TextView = itemView.findViewById(R.id.RequestRoomId)
    }


    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
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