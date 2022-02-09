package fastcampus.aop.part2.mgr_villa.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.NoticeLayout
import fastcampus.aop.part2.mgr_villa.model.VillaNotice
import fastcampus.aop.part2.mgr_villa.model.VillaTenant
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class NoticeAdapter(val noticeList: ArrayList<VillaNotice>): RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    val firestoreDB = Firebase.firestore

    init {
        firestoreDB?.collection("VillaNotice")
            .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
            .orderBy("noticeDatetime", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, e ->
                noticeList.clear()

                if (e != null){
//                    showToast(e.message.toString())
                    Log.d("VillaNotice/NoticeAdapter------------------>", e.message.toString())
                    return@addSnapshotListener
                }

                for (snapshot in querySnapshot!!.documents){
                    val item = snapshot.toObject(VillaNotice::class.java)
                    item!!.noticeNo = snapshot.id
                    noticeList.add(item!!)
                }
                notifyDataSetChanged()
            }

    }

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