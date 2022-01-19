package fastcampus.aop.part2.mgr_villa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.AddrLayout
import fastcampus.aop.part2.mgr_villa.model.AddrTenantLayout
import org.w3c.dom.Text

class KakaoApiTenantAdapter(val itemList: ArrayList<AddrTenantLayout>): RecyclerView.Adapter<KakaoApiTenantAdapter.AddrTenantViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KakaoApiTenantAdapter.AddrTenantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_address, parent, false)
        return AddrTenantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: AddrTenantViewHolder, position: Int) {
        holder.road_address_name.text = itemList[position].road_address_name
        holder.address_name.text = itemList[position].address_name
        if (!itemList[position].villa_name.isNullOrEmpty()) {
            holder.villa_name.text = itemList[position].villa_name
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class AddrTenantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val road_address_name: TextView = itemView.findViewById(R.id.road_address_name)
        val address_name: TextView = itemView.findViewById(R.id.address_name)
        val villa_name: TextView = itemView.findViewById(R.id.villa_name)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

}
