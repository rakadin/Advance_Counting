package com.example.advancecounting.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import com.example.advancecounting.R
import com.example.advancecounting.service.CountingService

class MainActivity : AppCompatActivity() {
    private lateinit var boundModeButton: TextView
    private lateinit var foreModeButton: TextView
    private lateinit var backModeButton: TextView
    private lateinit var count_value_text : TextView
    private lateinit var serviceIntent : Intent
    private var isUsingBound = false
    private var isUsingForeGround = false
    private var isUsingBackground = false

    private val countUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "COUNT_UPDATED") {
                Log.v("check_status","get count update")

                val count = intent.getIntExtra("count", 0)
                // Update UI with the count value
                updateUI(count)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        serviceIntent = Intent(this, CountingService::class.java)
        getIDs()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the count update receiver
        unregisterReceiver(countUpdateReceiver)
    }
    fun getIDs(){
        boundModeButton = findViewById(R.id.bound_service_mode)
        foreModeButton = findViewById(R.id.foreground_service_mode)
        backModeButton = findViewById(R.id.background_service_mode)
        count_value_text = findViewById(R.id.total_count_in)
    }
    fun StartCountingFunc(view: View) {
        Log.v("check_status","start counting")
        // Register the count update receiver
        Log.v("check_status_but","check before but: $isUsingForeGround")
        Log.v("check_status_but","check before but: $isUsingBackground")
        Log.v("check_status_but","check before but: $isUsingBound")

        registerReceiver(countUpdateReceiver, IntentFilter("COUNT_UPDATED"))

        // send button ch
        serviceIntent.putExtra("bound_called", isUsingBound)
        serviceIntent.putExtra("fore_called", isUsingForeGround)
        serviceIntent.putExtra("back_called", isUsingBackground)

        // Create and start the service
        startService(serviceIntent)

    }
    fun StopCountingFunc(view: View) {
        Log.v("check_status","stop service")
        stopService(serviceIntent)
    }

    fun useBoundServiceFunc(view: View) {
        setBackGroundClickButton(boundModeButton,foreModeButton,backModeButton)
        isUsingBound = true
        isUsingBackground = false
        isUsingForeGround = false
    }
    fun useForegroundServiceFunc(view: View) {
        setBackGroundClickButton(foreModeButton,boundModeButton,backModeButton)
        isUsingBound = false
        isUsingBackground = false
        isUsingForeGround = true
    }
    fun useBackgroundServiceFunc(view: View) {
        setBackGroundClickButton(backModeButton ,foreModeButton,boundModeButton)
        isUsingBound = false
        isUsingBackground = true
        isUsingForeGround = false
    }
    fun setBackGroundClickButton(clickedBut : TextView, otherBut : TextView,otherBut2 : TextView){
        clickedBut.setBackgroundResource(R.drawable.gradient_negative_button)
        otherBut.setBackgroundResource(R.drawable.gradient_possitive_button)
        otherBut2.setBackgroundResource(R.drawable.gradient_possitive_button)

    }
    private fun updateUI(count: Int) {
        // Update your UI elements with the received count value
        count_value_text.text = "$count"
    }
}