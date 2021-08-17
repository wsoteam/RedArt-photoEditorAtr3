package com.artstudio.photoeditor.activity.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.artstudio.photoeditor.R
import com.artstudio.photoeditor.Utils.Analytics
import com.artstudio.photoeditor.activity.EditActivity
import com.artstudio.photoeditor.activity.bl.BlActivity
import com.google.firebase.database.FirebaseDatabase

class SplashAct : AppCompatActivity(R.layout.activity_splash) {


    private lateinit var vm : SplashVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       //startWhiteScreen()


        Analytics.open()

        vm = ViewModelProviders.of(this).get(SplashVM::class.java)
        vm.getStatusLD().observe(this, Observer {
            when (it) {
                SplashVM.BLACK -> startBlackScreen()
                SplashVM.WHITE -> startWhiteScreen()
            }
        })
    }

    private fun createNewDirectory() {
        FirebaseDatabase
                .getInstance("")
                .reference
                .child("")
                .setValue("")
                .addOnSuccessListener {
                    Log.e("LOL", "addOnSuccessListener")
                }
    }


    fun startWhiteScreen() {
        Analytics.openWhite()
        var intentStartChoiseAct = Intent(this@SplashAct, EditActivity::class.java)
        startActivity(intentStartChoiseAct)
        finish()
    }

    fun startBlackScreen() {
        Analytics.openBlack()
        var intentBlack = Intent(this@SplashAct, BlActivity::class.java)
        startActivity(intentBlack)
        finish()
    }
}