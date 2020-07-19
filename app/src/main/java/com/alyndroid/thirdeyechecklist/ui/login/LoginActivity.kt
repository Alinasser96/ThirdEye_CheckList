package com.alyndroid.thirdeyechecklist.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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

        viewModel.response.observe(this, Observer {
            if (it.success) {
                SharedPreference(this).save("name", it.data.name)
                SharedPreference(this).save("phone", binding.phoneNoEt.text.toString())
                SharedPreference(this).save("userID", it.data.id)
                SharedPreference(this).save("password", binding.passwordEt.editableText.toString())
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
