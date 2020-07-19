package com.alyndroid.thirdeyechecklist.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.ui.MainActivity
import com.alyndroid.thirdeyechecklist.ui.login.LoginActivity
import com.alyndroid.thirdeyechecklist.ui.login.LoginViewModel
import com.alyndroid.thirdeyechecklist.util.Extras
import com.alyndroid.thirdeyechecklist.util.ExtrasKeys
import com.alyndroid.thirdeyechecklist.util.SharedPreference

class SplashActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        login()
        viewModel.response.observe(this, Observer {
            if (it.success) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                val intent = Intent(this, LoginActivity::class.java)
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
