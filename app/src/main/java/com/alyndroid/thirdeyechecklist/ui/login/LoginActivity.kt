package com.alyndroid.thirdeyechecklist.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.databinding.ActivityLoginBinding
import com.alyndroid.thirdeyechecklist.ui.MainActivity
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.loginButton.attachTextChangeAnimator()


        binding.loginButton.setOnClickListener {
            if (checkInput()) {
                viewModel.login(
                    binding.phoneNoEt.text.toString(),
                    binding.passwordEt.text.toString()
                )
            }
        }
        var token: String? = null

        if (SharedPreference(this).getValueString("fireBaseToken")== null || SharedPreference(this).getValueString("fireBaseToken")!!.isEmpty()){
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("SplashActivity", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                token = task.result

                // Log and toast
                val msg =  token
                Log.d("SplashActivity", msg)
                SharedPreference(this).save("fireBaseToken", token!!)
            })} else {
            token = SharedPreference(this).getValueString("fireBaseToken")
        }
        viewModel.response.observe(this, Observer {
            if (it.success) {
                SharedPreference(this).save("name", it.data.name)
                SharedPreference(this).save("phone", binding.phoneNoEt.text.toString())
                SharedPreference(this).save("userID", it.data.id)
                SharedPreference(this).save("password", binding.passwordEt.editableText.toString())

                viewModel.saveFirebaseToken(it.data.id, token!!)
            }
        })

        viewModel.saveFirebaseResponse.observe(this, Observer {
            if (it.status){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                binding.loginButton.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
            } else {
                //todo solve this
                binding.loginButton.hideProgress(R.string.login)
            }
        })

    }

    private fun checkInput(): Boolean {
        binding.phoneNoIl.error = null
        binding.passwordIl.error = null
        var flag = true
        if (binding.phoneNoEt.text.toString().isEmpty()) {
            binding.phoneNoIl.error = getString(R.string.must_enter_phone)
            flag = false
        }
        if (binding.phoneNoEt.text.toString().startsWith("01") && binding.phoneNoEt.text.toString().length != 11) {
            binding.phoneNoIl.error = getString(R.string.phone_is_not_correct)
            flag = false
        }
        if (binding.passwordEt.text.toString().isEmpty()) {
            binding.passwordIl.error = getString(R.string.must_enter_password)
            flag = false
        }

        return flag
    }
}
