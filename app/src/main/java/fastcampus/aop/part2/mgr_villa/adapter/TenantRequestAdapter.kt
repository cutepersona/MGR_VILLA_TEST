package fastcampus.aop.part2.mgr_villa.adapter

import android.annotation.SuppressLint
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
import fastcampus.aop.part2.mgr_villa.model.TenantLayout
import fastcampus.aop.part2.mgr_villa.model.TenantRequestLayout
import fastcampus.aop.part2.mgr_villa.model.VillaTenant
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class TenantRequestAdapter(val tenantList: ArrayList<VillaTenant>): RecyclerView.Adapter<TenantRequestAdapter.TenantRequestViewHolder>() {

    val firestoreDB = Firebase.firestore

    init {
        CoroutineScope(Dispatchers.Main).launch {
            firestoreDB?.collection("VillaTenant")
                .whereEqualTo("villaAddr", MyApplication.prefs.getString("requestAddress", "").trim())
                .orderBy("roomNumber", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, e ->
                    tenantList.clear()

                    if (e != null){
//                    showToast(e.message.toString())
                        Log.d("VillaTenant/TenantRequestAdapter------------------>", e.message.toString())
                        return@addSnapshotListener
                    }

                    for (snapshot in querySnapshot!!.documents){
                        val item = snapshot.toObject(VillaTenant::class.java)
                        item!!.roomId = snapshot.id
                        tenantList.add(item!!)
                    }
                    notifyDataSetChanged()
                }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantRequestAdapter.TenantRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_tenantsrequest, parent, false)
        return TenantRequestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tenantList.size
    }

    override fun onBindViewHolder(holder: TenantRequestViewHolder, position: Int) {
        holder.requestRoomNumber.text = tenantList[position].roomNumber
        holder.requestRoomId.text = tenantList[position].roomId

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



}