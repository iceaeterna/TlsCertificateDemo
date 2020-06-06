package com.icy.test.testapp

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request

object SecurityConfigCustomTest {
    private val okHttpClient = OkHttpClient.Builder()
            // 与OkHttp的固定证书一起使用
//        .certificatePinner(
//            CertificatePinner.Builder()
                // wrong sha256-pin
//                .add("www.jd.com", "sha256/aafW7XcXs6Hz6KQXpF0pT4Nby0EZ/L97t5hWP2G+Xgg=")
//                .add("www.jd.com", "sha256/yNfW7XcXs6Hz6KQXpF0pT4Nby0EZ/L97t5hWP2G+Xgg=")
//                .build())
        .build()

    fun run() {
        val request = Request.Builder().url("https://www.jd.com").build()
        okHttpClient.newCall(request).execute().use {
            println("SecurityConfigCustomTest call for jd : ${it.code()}")
        }

        val request2 = Request.Builder().url("https://www.meituan.com").build()
        okHttpClient.newCall(request2).execute().use {
            println("SecurityConfigCustomTest call for meituan : ${it.code()}")
        }
    }

}