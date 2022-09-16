package com.example.residency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var facility: ArrayList<Facility>? = null
    var bookingAdapter: BookingAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        facility = ArrayList()

        if (facility?.size!! > 0){
            rv_booking_history.visibility = View.VISIBLE
            tv_no_booking.visibility = View.GONE
        }else{
            rv_booking_history.visibility = View.GONE
            tv_no_booking.visibility = View.VISIBLE
        }

        setupRecycler()
        clicks()
    }

    private fun clicks() {
        add_fab.setOnClickListener {
            val intent = Intent(this,BookingActivity::class.java)
            intent.putParcelableArrayListExtra("facility",facility)
            startActivityForResult(intent,1)
        }
    }

    private fun setupRecycler() {
        bookingAdapter = BookingAdapter(facility)
        rv_booking_history.adapter = bookingAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                facility = data?.getParcelableArrayListExtra<Facility>("facility")
                Log.e("FACILITY", "onActivityResult: ${facility}" )
                if (facility?.size!! > 0){
                    rv_booking_history.visibility = View.VISIBLE
                    tv_no_booking.visibility = View.GONE
                }else{
                    rv_booking_history.visibility = View.GONE
                    tv_no_booking.visibility = View.VISIBLE
                }
                facility?.let { bookingAdapter?.refreshLists(it) }
            }
        }
    }
}