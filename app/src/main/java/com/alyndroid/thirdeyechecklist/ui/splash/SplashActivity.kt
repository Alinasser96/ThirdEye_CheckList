package com.alyndroid.thirdeyechecklist.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.ui.MainActivity
import com.alyndroid.thirdeyechecklist.ui.login.LoginActivity
import com.alyndroid.thirdeyechecklist.ui.login.LoginViewModel
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (SharedPreference(this).getValueString("fireBaseToken")== null || SharedPreference(this).getValueString("fireBaseToken")!!.isEmpty()){
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("SplashActivity", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg =  token
                Log.d("SplashActivity", msg)
                SharedPreference(this).save("fireBaseToken", token!!)
            })

        }
        login()
        viewModel.response.observe(this, Observer {
            if (it.success) {
                viewModel.saveFirebaseToken(it.data.id, SharedPreference(this).getValueString("fireBaseToken")!!)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.saveFirebaseResponse.observe(this, Observer {
            if (it.status){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun login() {
        if (SharedPreference(this).getValueString("name") != null) {
            viewModel.login(
                SharedPreference(this).getValueString("phone")!!,
                SharedPreference(this).getValueString("password")!!
            )
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
