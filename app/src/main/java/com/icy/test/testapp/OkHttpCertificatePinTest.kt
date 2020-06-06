package com.icy.test.testapp

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request

object OkHttpCertificatePinTest {
    private val okHttpClient =
        OkHttpClient.Builder()
            .certificatePinner(
                CertificatePinner.Builder()
                    // wrong sha256-pin
//                    .add("v.qq.com", "sha256/bbN43MZRG2jht5n8F80PlYuHDAMgkJTRu3jR9PYjrr8=")
                    .add("v.qq.com", "sha256/nrN43MZRG2jht5n8F80PlYuHDAMgkJTRu3jR9PYjrr8=")
                    .build())
            .build()


    fun run() {
        val request1 = Request.Builder().url("https://v.qq.com").build()
        okHttpClient.newCall(request1).execute().use {
            println("OkHttpCertificatePinTest call for v.qq : ${it.code()}")
        }

        val request2 = Request.Builder().url("https://www.baidu.com").build()
        okHttpClient.newCall(request2).execute().use {
            println("OkHttpCertificatePinTest call for baidu : ${it.code()}")
        }
    }
}