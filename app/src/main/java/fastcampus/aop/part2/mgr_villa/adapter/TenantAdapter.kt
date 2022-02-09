package fastcampus.aop.part2.mgr_villa.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.model.TenantLayout
import fastcampus.aop.part2.mgr_villa.model.VillaTenant
import fastcampus.aop.part2.mgr_villa.sharedPreferences.MyApplication

class TenantAdapter(val tenantList: ArrayList<VillaTenant>): RecyclerView.Adapter<TenantAdapter.TenantViewHolder>() {

    val firestoreDB = Firebase.firestore
    
    init {
        firestoreDB?.collection("VillaTenant")
            .whereEqualTo("villaAddr", MyApplication.prefs.getString("villaAddress", "").trim())
            .orderBy("roomNumber", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                tenantList.clear()

                if (e != null){
//                    showToast(e.message.toString())
                    Log.d("VillaTenant/TenantAdapter------------------>", e.message.toString())
                    return@addSnapshotListener
                }

                for (snapshot in querySnapshot!!.documents){
                    val item = snapshot.toObject(VillaTenant::class.java)
                    item!!.roomId = snapshot.id
                    tenantList.add(item!!)
                }
                notifyDataSetChanged()
            }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantAdapter.TenantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_tenants, parent, false)
        return TenantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tenantList.size
    }

    override fun onBindViewHolder(holder: TenantViewHolder, position: Int) {
        holder.tenantRoomNumber.text = tenantList[position].roomNumber
        holder.tenantContractDate.text = tenantList[position].tenantContractDate
        holder.tenantLeaveDate.text = tenantList[position].tenantLeaveDate
        holder.tenantRoomId.text = tenantList[position].roomId

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }

        holder.tenantUpdate.setOnClickListener {
            slideButtonClickListener.onSlideButtonClick(it, holder.tenantUpdate, position)
        }

        holder.tenantDelete.setOnClickListener {
            slideButtonClickListener.onSlideButtonClick(it, holder.tenantDelete, position)
        }


    }

    class TenantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val tenantRoomNumber: TextView = itemView.findViewById(R.id.tenantTitle)
        val tenantContractDate: TextView = itemView.findViewById(R.id.ContractDate)
        val tenantLeaveDate: TextView = itemView.findViewById(R.id.LeaveDate)
        val tenantRoomId: TextView = itemView.findViewById(R.id.tenantRoomId)

        val tenantUpdate: ImageView = itemView.findViewById(R.id.tenantUpdate)
        val tenantDelete: ImageView = itemView.findViewById(R.id.tenantDelete)

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