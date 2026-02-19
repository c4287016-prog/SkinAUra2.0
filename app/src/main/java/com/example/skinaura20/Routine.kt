package com.example.skinaura20.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.example.skinaura20.utils.Notification
import java.util.*

class Routine : Fragment() {

    private lateinit var tvMorningTime: TextView
    private lateinit var tvNightTime: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_routine, container, false)

        tvMorningTime = view.findViewById(R.id.tvMorningTime)
        tvNightTime = view.findViewById(R.id.tvNightTime)
        val btnMorning = view.findViewById<Button>(R.id.btnSetMorning)
        val btnNight = view.findViewById<Button>(R.id.btnSetNight)

        btnMorning.setOnClickListener {
            showTimePicker("Morning", tvMorningTime, "Morning Glow ✨", "Apna morning skincare kar lijiye!", 101)
        }

        btnNight.setOnClickListener {
            showTimePicker("Night", tvNightTime, "Night Recovery 🌙", "Sone se pehle night routine zaroori hai!", 102)
        }

        return view
    }

    private fun showTimePicker(type: String, textView: TextView, title: String, message: String, requestCode: Int) {
        val c = Calendar.getInstance()
        val timePicker = TimePickerDialog(requireContext(), { _, hour, minute ->
            val timeString = String.format("%02d:%02d", hour, minute)
            textView.text = "$type: $timeString"

            setReminder(hour, minute, title, message, requestCode)

        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false)
        timePicker.show()
    }

    private fun setReminder(hour: Int, minute: Int, title: String, message: String, requestCode: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), Notification::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // 🔥 Updated Logic for Always-On Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                // Permission maangne ke liye settings kholna
                val intentSettings = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intentSettings)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        Toast.makeText(context, "Reminder set for ${String.format("%02d:%02d", hour, minute)} ✅", Toast.LENGTH_SHORT).show()
    }
}