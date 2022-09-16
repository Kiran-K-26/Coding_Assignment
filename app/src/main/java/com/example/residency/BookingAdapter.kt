package com.example.residency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_booking_adapter.view.*

class BookingAdapter(private var facility: ArrayList<Facility>?) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.layout_booking_adapter,parent,false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_type.text = facility?.get(position)?.type
        holder.itemView.tv_select_date.text = facility?.get(position)?.date
        holder.itemView.tv_select_time.text = "${facility?.get(position)?.startTime} to ${facility?.get(position)?.endTime}"
        holder.itemView.tv_total_price.text = "Total: Rs. ${facility?.get(position)?.total}"
    }

    override fun getItemCount(): Int = facility?.size!!

    fun refreshLists(facilityList: ArrayList<Facility>) {
        facility = facilityList
        notifyDataSetChanged()
    }
}