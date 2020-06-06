package com.icy.test.testapp

import okhttp3.OkHttpClient
import okhttp3.Request

object SecurityConfigPinTest {
    private val okHttpClient = OkHttpClient.Builder().build()

    fun run() {
        val request = Request.Builder().url("https://www.taobao.com").build()
        okHttpClient.newCall(request).execute().use {
            println("SecurityConfigPinTest call for taobao : ${it.code()}")
        }

        val request2 = Request.Builder().url("https://www.meituan.com").build()
        okHttpClient.newCall(request2).execute().use {
            println("SecurityConfigPinTest call for meituan : ${it.code()}")
        }
    }

}