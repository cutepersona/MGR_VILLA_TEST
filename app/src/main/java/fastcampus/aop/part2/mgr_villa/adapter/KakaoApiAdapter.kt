package fastcampus.aop.part2.mgr_villa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.AddrLayout
import org.w3c.dom.Text

class KakaoApiAdapter(val itemList: ArrayList<AddrLayout>): RecyclerView.Adapter<KakaoApiAdapter.AddrViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KakaoApiAdapter.AddrViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_address, parent, false)
        return AddrViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: AddrViewHolder, position: Int) {
        holder.road_address_name.text = itemList[position].road_address_name
        holder.address_name.text = itemList[position].address_name
        if (!itemList[position].villa_name.isNullOrEmpty()) {
            holder.villa_name.text = itemList[position].villa_name
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class AddrViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
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
