package com.example.advancecounting.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import com.example.advancecounting.R

class MainActivity : AppCompatActivity() {
    private lateinit var boundModeButton: TextView
    private lateinit var foreModeButton: TextView
    private lateinit var backModeButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        getIDs()
    }
    fun getIDs(){
        boundModeButton = findViewById(R.id.bound_service_mode)
        foreModeButton = findViewById(R.id.foreground_service_mode)
        backModeButton = findViewById(R.id.background_service_mode)
    }
    fun StartCountingFunc(view: View) {}
    fun useBoundServiceFunc(view: View) {
        setBackGroundClickButton(boundModeButton,foreModeButton,backModeButton)
    }
    fun useForegroundServiceFunc(view: View) {
        setBackGroundClickButton(foreModeButton,boundModeButton,backModeButton)

    }
    fun useBackgroundServiceFunc(view: View) {
        setBackGroundClickButton(backModeButton ,foreModeButton,boundModeButton)

    }
    fun setBackGroundClickButton(clickedBut : TextView, otherBut : TextView,otherBut2 : TextView){
        clickedBut.setBackgroundResource(R.drawable.gradient_negative_button)
        otherBut.setBackgroundResource(R.drawable.gradient_possitive_button)
        otherBut2.setBackgroundResource(R.drawable.gradient_possitive_button)

    }
}