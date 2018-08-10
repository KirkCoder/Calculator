package com.example.kirill.calculator.views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.example.kirill.calculator.R

class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private var fragment: InputCalculatorFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
    }

    override fun onStart() {
        setFragment()
        super.onStart()
    }

    override fun onStop() {
        fragment = null
        super.onStop()
    }

    private fun setFragment() {
        fragment = supportFragmentManager.findFragmentByTag(InputCalculatorFragment.TAG) as InputCalculatorFragment?
        if (fragment == null){
            val errorMsg = resources.getString(R.string.error_msg)
            fragment = newInputCalculatorFragment(errorMsg)
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment!!, InputCalculatorFragment.TAG)
                .commit()
    }

    private fun findViews() {
        container = findViewById(R.id.fragment_container)
    }
}
