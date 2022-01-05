package fastcampus.aop.part2.mgr_villa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.model.VillaNotice

class NoticeAdapter(val noticeList: ArrayList<NoticeLayout>): RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeAdapter.NoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_notices, parent, false)
        return NoticeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.noticeNo.text = noticeList[position].noticeNo.toString()
        holder.NoticeTitle.text = noticeList[position].noticeTitle
        holder.NoticeTime.text = noticeList[position].noticeDatetime

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class NoticeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val noticeNo: TextView = itemView.findViewById(R.id.noticeNo)
        val NoticeTitle: TextView = itemView.findViewById(R.id.NoticeTitle)
        val NoticeTime: TextView = itemView.findViewById(R.id.NoticeTime)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

}