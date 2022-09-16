package com.example.residency

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_booking.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

class BookingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    val type = mutableListOf<String>("Clubhouse", "Tennis Court")
    var date = ""
    private var fromSelected = true
    var fromTime = ""
    var toTime = ""
    var selectedType = ""
    var fromTimeDiff = ""
    var toTimeDiff = ""
    var differenceHours = 0
    var differenceMinutes = 0
    var facility: ArrayList<Facility>? = null
    var futureTime = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        facility = ArrayList()
        facility = intent?.getParcelableArrayListExtra("facility")
        setUpSpinner()
        clicks()
        initCalender()
        initTime()
    }

    private fun initTime() {
        val timeFormat = SimpleDateFormat("HH:mm")
        val cal = Calendar.getInstance()

        tv_timeFrom.text = "${timeFormat.format(cal.time)}"
        fromTime = timeFormat.format(cal.time)

        tv_timeTo.text = "${timeFormat.format(cal.time)}"
        toTime = timeFormat.format(cal.time)
    }

    private fun initCalender() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val cal = Calendar.getInstance()

        tv_date.text = "${dateFormat.format(cal.time)}"
        date = dateFormat.format(cal.time)
    }

    private fun clicks() {
        tv_date.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, this, year, month, day).show()
        }

        tv_timeFrom.setOnClickListener {
            fromSelected = true
            val calendar: Calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this, this, hour, minute,
                DateFormat.is24HourFormat(this)
            )
//            ,
//            DateFormat.is24HourFormat(this)
            timePickerDialog.show()
        }

        tv_timeTo.setOnClickListener {
            fromSelected = false
            val calendar: Calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this, this, hour, minute,
            DateFormat.is24HourFormat(this)
            )
//            ,
//            DateFormat.is24HourFormat(this)
            timePickerDialog.show()
        }

        btn_booking.setOnClickListener {
            Log.e("facility", "clicks: $facility" )
            checkValidation()
        }
    }

    private fun checkValidation() {
        if (facility?.size!! > 0) {
            for (data in facility!!) {
                if (selectedType == data.type) {
                    if (date == data.date) {
                        if (compareTime(data.startTime, data.endTime)) {
//                            break
                            if (futureTime){
                                diffTime()
                                confirmDialog()
                            }else{
                                Snackbar.make(
                                    cl_parent,
                                    "Pick Actual time!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Snackbar.make(
                                cl_parent,
                                "Selected timeslot already booked!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }else{
                        if (futureTime){
                            diffTime()
                            confirmDialog()
                        }else{
                            Snackbar.make(
                                cl_parent,
                                "Pick Actual time!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
//                break
                }else{
                    if (futureTime){
                        diffTime()
                        confirmDialog()
                    }else{
                        Snackbar.make(
                            cl_parent,
                            "Pick Actual time!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
//            break
            }
//            diffTime()
////        facility!!.add(Facility(type = selectedType,date = date, startTime = fromTime, endTime = toTime))
//
//            confirmDialog()
        }else{
            if (futureTime){
                diffTime()
                confirmDialog()
            }else{
                Snackbar.make(
                    cl_parent,
                    "Pick Actual time!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun setUpSpinner() {
        if (type_spinner != null) {
            val spinnerAdapter = ArrayAdapter(
                this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                type
            )
            type_spinner.adapter = spinnerAdapter

            type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedType = type[p2]
                    if (selectedType == "Clubhouse"){
                        clubhouse_1.visibility = View.VISIBLE
                        clubhouse_2.visibility = View.VISIBLE
                        tennis.visibility = View.GONE
                    }else{
                        clubhouse_1.visibility = View.GONE
                        clubhouse_2.visibility = View.GONE
                        tennis.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        Log.e(" Date", "$p1 ${p2 + 1} $p3")
        date = "$p1-${p2 + 1}-$p3"
        tv_date.text = "$p1-${p2 + 1}-$p3"
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        Log.e("Time", "onTimeSet: $p1:$p2" )
        var AM_PM = ""
        val hour = if (p1 == 0) 12 else p1

        val datetime = Calendar.getInstance()
        val c = Calendar.getInstance()
        datetime[Calendar.HOUR_OF_DAY] = hour
        datetime[Calendar.MINUTE] = p2

        val c1 = Calendar.getInstance()
        val year = c1.get(Calendar.YEAR)
        val month = c1.get(Calendar.MONTH)
        val day = c1.get(Calendar.DAY_OF_MONTH)

        if (date == "") {
            date = "$year ${month + 1} $day"
        }
            if (date == "$year ${month + 1} $day"){
                Log.e("Date", "onTimeSet: $year ${month + 1} $day" )
                Log.e("Date", "onTimeSet: $year ${month + 1} $day" )

                if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                    futureTime = true
                    if (fromSelected){
                        AM_PM = if (p1 >=0 && p1 < 12){
                            "AM";
                        } else {
                            "PM";
                        }
                        fromTime = "$hour:$p2 $AM_PM"
//            fromTimeAMPM = "$hour:$p2 $AM_PM".toInt()
                        fromTimeDiff = "${if (hour.toString().length == 1) "0$hour" else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            fromTimeDiff = "${if (hour > 12) hour%12 else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            fromTimeDiff = "$hour:$p2"
                        tv_timeFrom.text = "$hour:$p2 $AM_PM"
                    }else{
                        AM_PM = if (p1 >=0 && p1 < 12){
                            "AM";
                        } else {
                            "PM";
                        }
                        toTime = "$hour:$p2 $AM_PM"
//            toTimeAMPM = "$hour:$p2 $AM_PM".toInt()
                        toTimeDiff = "${if (hour.toString().length == 1) "0$hour" else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            toTimeDiff = "${if (hour > 12) hour%12 else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            toTimeDiff = "$hour:$p2"
                        tv_timeTo.text = "$hour:$p2 $AM_PM"
                    }

                    if (fromTime == toTime) {
                        futureTime = false
                    }
                }else{
                    futureTime = false
                    Snackbar.make(
                        cl_parent,
                        "Pick Actual time!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }else{
                Log.e("Else", "onTimeSet: Date" )
                futureTime = true
                if (fromSelected){
                    AM_PM = if (p1 >=0 && p1 < 12){
                        "AM";
                    } else {
                        "PM";
                    }
                    fromTime = "$hour:$p2 $AM_PM"
//            fromTimeAMPM = "$hour:$p2 $AM_PM".toInt()
                    fromTimeDiff = "${if (hour.toString().length == 1) "0$hour" else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            fromTimeDiff = "${if (hour > 12) hour%12 else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            fromTimeDiff = "$hour:$p2"
                    tv_timeFrom.text = "$hour:$p2 $AM_PM"
                }else{
                    AM_PM = if (p1 >=0 && p1 < 12){
                        "AM";
                    } else {
                        "PM";
                    }
                    toTime = "$hour:$p2 $AM_PM"
//            toTimeAMPM = "$hour:$p2 $AM_PM".toInt()
                    toTimeDiff = "${if (hour.toString().length == 1) "0$hour" else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            toTimeDiff = "${if (hour > 12) hour%12 else hour}:${if (p2.toString().length == 1) "0$p2" else p2}"
//            toTimeDiff = "$hour:$p2"
                    tv_timeTo.text = "$hour:$p2 $AM_PM"
                }

                if (fromTime == toTime) {
                    futureTime = false
                }
            }

    }

    private fun compareTime(startTime: String?, endTime: String?): Boolean{
        try {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            val startime = simpleDateFormat.parse(startTime)
            val endtime = simpleDateFormat.parse(endTime)

            val selected_start_time = simpleDateFormat.parse(fromTime)
            val selected_end_time = simpleDateFormat.parse(toTime)
            return if (fromTime.equals(startTime) && toTime.equals(endTime)){
                println("No")
                false
            }
            else if (selected_start_time.after(startime) && selected_start_time.before(endtime)) {
                println("No")
                false
            }
                else if (selected_end_time.after(startime) && selected_end_time.before(endtime)){
                println("No")
                false
            }
//            else if ((selected_end_time.after(startime) && selected_end_time.after(endtime)) && (selected_end_time.after(startime) && selected_end_time.after(endtime))) {
//            else if (selected_start_time == selected_end_time) {
//                println("No")
//                false
//            }
            else {
                println("Yes")
                true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    private fun diffTime(){
        val dateFormat = SimpleDateFormat(
            "H:mm"
        )
        try {


            val startTime = dateFormat.parse(fromTimeDiff)
            val endTime = dateFormat.parse(toTimeDiff)
////            val diff = startTime.time - endTime.time
            var diff = abs(endTime.time - startTime.time)

            val differenceInHours: Long = (diff / (60 * 60 * 1000)
                    % 24)

            val differenceInMinutes: Long = diff / (60 * 1000) % 60

            val differenceInSeconds: Long = diff / 1000 % 60
//
//            val seconds = diff / 1000
//            val minutes = seconds / 60
//            val hours = minutes / 60
//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                 val formatter = DateTimeFormatter.ofPattern("HH:mm")
//                 val localDate1: LocalDateTime = LocalDate.parse(fromTimeDiff,formatter).atStartOfDay()
//                 val localDate2: LocalDateTime = LocalDate.parse(toTimeDiff,formatter).atStartOfDay()
//
//                 val hours = ChronoUnit.HOURS.between(localDate1,localDate2)
////                 val hours = ChronoUnit.HOURS.between(ZonedDateTime.of(LocalDateTime.parse( fromTimeDiff ),
////                     ZoneId.of( "America/Montreal" )),
////                     ZonedDateTime.of(LocalDateTime.parse( toTimeDiff ),
////                         ZoneId.of( "America/Montreal" ))
////                 )
//                 val min = ChronoUnit.MINUTES.between(localDate1,localDate2)
//
//                 Log.e(
//                     "Difference: ", " minutes: " + "min"
//                             + " hours: " + hours
//                 )
//
//            } else {
//
//            }



            differenceHours = differenceInHours.toInt()
            differenceMinutes = differenceInMinutes.toInt()

            Log.e(
                "Difference: ", " minutes: " + differenceInMinutes
                        + " hours: " + differenceInHours
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }


    private fun confirmDialog(){
        val builder = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.layout_confirm_order,null)
        builder.setView(view)
        val type = view.findViewById<TextView>(R.id.tv_type)
        val selectedDate = view.findViewById<TextView>(R.id.tv_date)
        val selectedTime = view.findViewById<TextView>(R.id.tv_time)
        val hours = view.findViewById<TextView>(R.id.tv_hours)
        val total = view.findViewById<TextView>(R.id.tv_total)
        val confirmBooking = view.findViewById<Button>(R.id.btn_confirm_order)

        var price = 0
        var grantTotal = 0.0

        type.text = selectedType
        selectedDate.text = "Selected Date: $date"
        selectedTime.text = "Selected Time: $fromTime to $toTime"
        hours.text = "Total Hours- $differenceHours : $differenceMinutes"

        if (selectedType == "Clubhouse"){
            if (fromTime.contains("PM") && toTime.contains("PM")){
                price = 500
                if (differenceHours > 0){
                    grantTotal = (price * differenceHours).toDouble()
                }
                if (differenceMinutes > 0){
                    val minPrice = 8.3
                    grantTotal = grantTotal.plus(minPrice * differenceMinutes)
                }
            }else{
                price = 100
                if (differenceHours > 0){
                    grantTotal = (price * differenceHours).toDouble()
                }
                if (differenceMinutes > 0){
                    val minPrice = 1.6
                    grantTotal = grantTotal.plus(minPrice * differenceMinutes)
                }
            }
        }else{
            price = 50
            if (differenceHours > 0){
                grantTotal = (price * differenceHours).toDouble()
            }
            if (differenceMinutes > 0){
                val minPrice = 0.8
                grantTotal = grantTotal.plus(minPrice * differenceMinutes)
            }
        }

        total.text = "Total: Rs. ${grantTotal.toString()}"

        confirmBooking.setOnClickListener {
            facility!!.add(Facility(type = selectedType,date = date, startTime = fromTime, endTime = toTime, total = grantTotal.toString()))
            Log.e("facility", "clicks: $facility" )
            val intent = Intent()
            intent.putParcelableArrayListExtra("facility",facility)
            setResult(RESULT_OK,intent)
            builder.dismiss()
            this.finish()
        }

        builder.show()
    }
}