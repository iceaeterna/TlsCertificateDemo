package com.icy.test.testapp

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyStore
import javax.net.ssl.*


object OkHttpCompactTest {
    // 5.0以下不支持TLSv1.3，也没有必要添加了
    private val TLS_COMPACT_VER =
        arrayOf("TLSv1.1", "TLSv1.2")
    private val okHttpClient: OkHttpClient
    init {
        val trustManager = defaultTrustManager()
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustManager), null)
        }
        okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(Tls12SocketFactory(sslContext.socketFactory), trustManager)
            .build()
    }

    fun run() {
        val request = Request.Builder().url("https://github.com").build()
        okHttpClient.newCall(request).execute().use {
            println("OkHttpCertificatePinTest call for github: ${it.code()}")
        }
    }

    private fun defaultTrustManager(): X509TrustManager {
        return TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()).apply {
            // 传入null就可以获取默认的TrustManager
            init(null as KeyStore?)
        }.trustManagers?.find {
            it is X509TrustManager
        }?.let {
            it as X509TrustManager
        }?: throw Exception("can not get default trust manager")
    }

    private class Tls12SocketFactory(val delegate: SSLSocketFactory) :
        SSLSocketFactory() {

        override fun getDefaultCipherSuites(): Array<String> {
            return delegate.defaultCipherSuites
        }

        override fun getSupportedCipherSuites(): Array<String> {
            return delegate.supportedCipherSuites
        }

        @Throws(IOException::class)
        override fun createSocket(
            s: Socket?,
            host: String?,
            port: Int,
            autoClose: Boolean
        ): Socket {
            return patch(delegate.createSocket(s, host, port, autoClose))
        }

        override fun createSocket(host: String?, port: Int): Socket {
            return delegate.createSocket(host, port)
        }

        override fun createSocket(
            host: String?,
            port: Int,
            localHost: InetAddress?,
            localPort: Int
        ): Socket {
            return delegate.createSocket(host, port, localHost, localPort)
        }

        override fun createSocket(host: InetAddress?, port: Int): Socket {
            return delegate.createSocket(host, port)
        }

        override fun createSocket(
            address: InetAddress?,
            port: Int,
            localAddress: InetAddress?,
            localPort: Int
        ): Socket {
            return delegate.createSocket(address, port, localAddress, localPort)
        }

        private fun patch(s: Socket): Socket {
            if (s is SSLSocket) {
                s.enabledProtocols = TLS_COMPACT_VER
            }
            return s
        }
    }
}