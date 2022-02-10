package fastcampus.aop.part2.mgr_villa.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.CostTenantLayout
import fastcampus.aop.part2.mgr_villa.model.VillaTenant
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class CostTenantAdapter(val CostTenantList: ArrayList<VillaTenant>): RecyclerView.Adapter<CostTenantAdapter.CostTenantViewHolder>() {

    val firestoreDB = Firebase.firestore

    init {
        firestoreDB?.collection("VillaTenant")
            .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
            .orderBy("roomNumber", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                CostTenantList.clear()

                if (e != null){
//                    showToast(e.message.toString())
                    Log.d("VillaTenant/CostTenantAdapter------------------>", e.message.toString())
                    return@addSnapshotListener
                }

                for (snapshot in querySnapshot!!.documents){
                    val item = snapshot.toObject(VillaTenant::class.java)
                    item!!.roomId = snapshot.id
                    CostTenantList.add(item!!)
                }
                notifyDataSetChanged()
            }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostTenantAdapter.CostTenantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_costtenants, parent, false)
        return CostTenantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return CostTenantList.size
    }

    override fun onBindViewHolder(holder: CostTenantViewHolder, position: Int) {
        holder.CostTenantRoomNumber.text = CostTenantList[position].roomNumber
        holder.CostTenantContractDate.text = CostTenantList[position].tenantContractDate
        holder.CostTenantLeaveDate.text = CostTenantList[position].tenantLeaveDate
        holder.CostTenantRoomId.text = CostTenantList[position].roomId

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