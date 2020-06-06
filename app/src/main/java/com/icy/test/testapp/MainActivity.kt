package com.icy.test.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val executors = Executors.newCachedThreadPool()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        executors.execute {
            // 需要去掉network-security-config配置
//            OkHttpCustomTrust.run()
//            SecurityConfigPinTest.run()
//            SecurityConfigCustomTest.run()
//            OkHttpCertificatePinTest.run()
            OkHttpCompactTest.run()
        }
    }
}


