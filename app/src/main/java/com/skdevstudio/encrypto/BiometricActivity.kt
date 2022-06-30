package com.skdevstudio.encrypto

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.skdevstudio.encrypto.databinding.ActivityBiometricBinding
import java.util.concurrent.Executor

class BiometricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricBinding
    private lateinit var biometricPrompt: BiometricPrompt

    private lateinit var executor: Executor
    private lateinit var promptInfo : BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Biometric"

        val intent = Intent(this, MainActivity::class.java)

//        ************* Biometric *********************

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this@BiometricActivity,executor, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                toast("Authentication Failed!")
                finishAffinity()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                toast("Authentication Failed!")
//                finishAffinity()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                toast("Success!")
                binding.fpDone.visibility = View.VISIBLE
                binding.fpimg.visibility = View.GONE
                binding.bioWarn.visibility = View.GONE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        binding.fpDone.visibility = View.GONE
                        intent.putExtra("FpStatus","YES")
                        startActivity(intent)
                        finish()
                    },
                    1200 // value in milliseconds
                )
            }

        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Encrypto")
            .setSubtitle("Use Your FingerPrint to Authenticate...!")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
        biometricPrompt.authenticate(promptInfo)

//        *********************** Biometric ends *************************


    }

    public fun toast(msg : String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }

}