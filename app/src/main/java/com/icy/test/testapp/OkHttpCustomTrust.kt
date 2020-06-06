package com.icy.test.testapp

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

object OkHttpCustomTrust {
    private val client: OkHttpClient
    init {
        val trustManager = CompositeTrustManager(
            defaultTrustManager(),
            arrayOf(
                trustManagerOnlyForCustomCertificates(trustedCertificatesInputStream())
            )
        )
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf<TrustManager>(trustManager), null)
        }
        client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .build()
    }

    @Throws(Exception::class)
    fun run() {
        val request1 = Request.Builder()
            .url("https://publicobject.com/helloworld.txt")
            .build()
        client.newCall(request1).execute().use {
            println("OkHttpCustomTrust call for publicobject : ${it.code()}")
        }

        val request2 = Request.Builder().url("https://www.baidu.com").build()
        client.newCall(request2).execute().use {
            println("OkHttpCustomTrust call for baidu : ${it.code()}")
        }
    }

    /**
     * Returns an input stream containing one or more certificate PEM files. This implementation just
     * embeds the PEM files in Java strings; most applications will instead read this from a resource
     * file that gets bundled with the application.
     */
    private fun trustedCertificatesInputStream(): InputStream {
        // PEM files for root certificates of Comodo and Entrust. These two CAs are sufficient to view
        // https://publicobject.com (Comodo) and https://squareup.com (Entrust). But they aren't
        // sufficient to connect to most HTTPS sites including https://godaddy.com and https://visa.com.
        // Typically developers will need to get a PEM file from their organization's TLS administrator.
        val comodoRsaCertificationAuthority = "-----BEGIN CERTIFICATE-----\n" +
                "MIIFmTCCBIGgAwIBAgISA4BLKcqJq50IMBtH1f0Rq/4FMA0GCSqGSIb3DQEBCwUAMEoxCzAJBgNV\n" +
                "BAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMwIQYDVQQDExpMZXQncyBFbmNyeXB0IEF1\n" +
                "dGhvcml0eSBYMzAeFw0yMDA0MTAyMzQzNDNaFw0yMDA3MDkyMzQzNDNaMBwxGjAYBgNVBAMTEXBy\n" +
                "aXZhdGVvYmplY3QuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0RHpBIwsRzBV\n" +
                "aheOc3CxLwI7ZRxXDzzKCQkKJBf+CtvT1bosZwdHxOhuzB/9LdsRO1F2mqvOLsS7YA7sDAwzlHCl\n" +
                "R9YT/Z00OaJz/57uBEu4zESOxs6U/wD+6SHyw6AXlH2pc23HSJgNdixIfwJWN5UNMADqCeG/S0NJ\n" +
                "0ZlV/YLI0zNory2UF1MRtb5SScLbChp6Y0Qg2vomee1C0FjpvzDPIRLasUD9H0VdN8Q0ljcOji17\n" +
                "no+kRWyXVhXfooDiC/piCE6kiyuvKQQi7qfbadpa5tUZjF4oJGhF10ic8zaIBgOJva7uBaoyx7EH\n" +
                "r4c5OxwN8Si9RHIojtBP1WR+UwIDAQABo4ICpTCCAqEwDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQW\n" +
                "MBQGCCsGAQUFBwMBBggrBgEFBQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTYudiNHtFoEmae\n" +
                "1zX+55hpnkVhsTAfBgNVHSMEGDAWgBSoSmpjBH3duubRObemRWXv86jsoTBvBggrBgEFBQcBAQRj\n" +
                "MGEwLgYIKwYBBQUHMAGGImh0dHA6Ly9vY3NwLmludC14My5sZXRzZW5jcnlwdC5vcmcwLwYIKwYB\n" +
                "BQUHMAKGI2h0dHA6Ly9jZXJ0LmludC14My5sZXRzZW5jcnlwdC5vcmcvMFsGA1UdEQRUMFKCFWJs\n" +
                "b2cucHVibGljb2JqZWN0LmNvbYIRcHJpdmF0ZW9iamVjdC5jb22CEHB1YmxpY29iamVjdC5jb22C\n" +
                "FHd3dy5wdWJsaWNvYmplY3QuY29tMEwGA1UdIARFMEMwCAYGZ4EMAQIBMDcGCysGAQQBgt8TAQEB\n" +
                "MCgwJgYIKwYBBQUHAgEWGmh0dHA6Ly9jcHMubGV0c2VuY3J5cHQub3JnMIIBBAYKKwYBBAHWeQIE\n" +
                "AgSB9QSB8gDwAHcA5xLysDd+GmL7jskMYYTx6ns3y1YdESZb8+DzS/JBVG4AAAFxZrE9BwAABAMA\n" +
                "SDBGAiEAu1q9TBCuk8/NuF+NIqLkI6OQJOG15hIec8u9bgLsrrQCIQDIhSiv/eR35tGPCgLCZCgI\n" +
                "2U8vmMRJPqxZgp5XKzdQ5gB1ALIeBcyLos2KIE6HZvkruYolIGdr2vpw57JJUy3vi5BeAAABcWax\n" +
                "POIAAAQDAEYwRAIgZTpeFdYihySsOg4B081HBNPpk54/XvJsXbgkt+9U2V4CID0qYiliY/EKsfjX\n" +
                "NuE1jAJGo76xGK7H1GkxzuLlECvcMA0GCSqGSIb3DQEBCwUAA4IBAQB99ZHwjYKaakB9DJoCsnbN\n" +
                "P0Wmk2Yq73KnPsSwNzigQ4IrHF29GBe1qhJUUAgczSl7HahqGyCuykgpR7I0WHra0D76FXxTmYTe\n" +
                "/HEpuZZg4XKRbjFrCCR8x6h4N3dhVp9F2K4HhVAX6z8Wbs5OnDhuvkOj40tqcdU71tZP243h31Hi\n" +
                "dhIiOvZzd3R1Dv42q3cQsnmbWMcghuT7Ypi7PrWZeL1PkMhH1UQ5WF0k6yAIszm+AG0BSlxyB5PB\n" +
                "DfqzJqUzKhIFJVTafrcN8QKX4aAHv04ymvnFK5D+HMJtN3cIqhWjr0g5rnsmxxGvQ6We6eSmZ97k\n" +
                "QoPf88fwx7HRSOXX\n" +
                "-----END CERTIFICATE-----\n"
        val entrustRootCertificateAuthority = (""
                + "-----BEGIN CERTIFICATE-----\n"
                + "MIIEkTCCA3mgAwIBAgIERWtQVDANBgkqhkiG9w0BAQUFADCBsDELMAkGA1UEBhMC\n"
                + "VVMxFjAUBgNVBAoTDUVudHJ1c3QsIEluYy4xOTA3BgNVBAsTMHd3dy5lbnRydXN0\n"
                + "Lm5ldC9DUFMgaXMgaW5jb3Jwb3JhdGVkIGJ5IHJlZmVyZW5jZTEfMB0GA1UECxMW\n"
                + "KGMpIDIwMDYgRW50cnVzdCwgSW5jLjEtMCsGA1UEAxMkRW50cnVzdCBSb290IENl\n"
                + "cnRpZmljYXRpb24gQXV0aG9yaXR5MB4XDTA2MTEyNzIwMjM0MloXDTI2MTEyNzIw\n"
                + "NTM0MlowgbAxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1FbnRydXN0LCBJbmMuMTkw\n"
                + "NwYDVQQLEzB3d3cuZW50cnVzdC5uZXQvQ1BTIGlzIGluY29ycG9yYXRlZCBieSBy\n"
                + "ZWZlcmVuY2UxHzAdBgNVBAsTFihjKSAyMDA2IEVudHJ1c3QsIEluYy4xLTArBgNV\n"
                + "BAMTJEVudHJ1c3QgUm9vdCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTCCASIwDQYJ\n"
                + "KoZIhvcNAQEBBQADggEPADCCAQoCggEBALaVtkNC+sZtKm9I35RMOVcF7sN5EUFo\n"
                + "Nu3s/poBj6E4KPz3EEZmLk0eGrEaTsbRwJWIsMn/MYszA9u3g3s+IIRe7bJWKKf4\n"
                + "4LlAcTfFy0cOlypowCKVYhXbR9n10Cv/gkvJrT7eTNuQgFA/CYqEAOwwCj0Yzfv9\n"
                + "KlmaI5UXLEWeH25DeW0MXJj+SKfFI0dcXv1u5x609mhF0YaDW6KKjbHjKYD+JXGI\n"
                + "rb68j6xSlkuqUY3kEzEZ6E5Nn9uss2rVvDlUccp6en+Q3X0dgNmBu1kmwhH+5pPi\n"
                + "94DkZfs0Nw4pgHBNrziGLp5/V6+eF67rHMsoIV+2HNjnogQi+dPa2MsCAwEAAaOB\n"
                + "sDCBrTAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/BAUwAwEB/zArBgNVHRAEJDAi\n"
                + "gA8yMDA2MTEyNzIwMjM0MlqBDzIwMjYxMTI3MjA1MzQyWjAfBgNVHSMEGDAWgBRo\n"
                + "kORnpKZTgMeGZqTx90tD+4S9bTAdBgNVHQ4EFgQUaJDkZ6SmU4DHhmak8fdLQ/uE\n"
                + "vW0wHQYJKoZIhvZ9B0EABBAwDhsIVjcuMTo0LjADAgSQMA0GCSqGSIb3DQEBBQUA\n"
                + "A4IBAQCT1DCw1wMgKtD5Y+iRDAUgqV8ZyntyTtSx29CW+1RaGSwMCPeyvIWonX9t\n"
                + "O1KzKtvn1ISMY/YPyyYBkVBs9F8U4pN0wBOeMDpQ47RgxRzwIkSNcUesyBrJ6Zua\n"
                + "AGAT/3B+XxFNSRuzFVJ7yVTav52Vr2ua2J7p8eRDjeIRRDq/r72DQnNSi6q7pynP\n"
                + "9WQcCk3RvKqsnyrQ/39/2n3qse0wJcGE2jTSW3iDVuycNsMm4hH2Z0kdkquM++v/\n"
                + "eu6FSqdQgPCnXEqULl8FmTxSQeDNtGPPAUO6nIPcj2A781q0tHuu2guQOHXvgR1m\n"
                + "0vdXcDazv/wor3ElhVsT/h5/WrQ8\n"
                + "-----END CERTIFICATE-----\n")
        return Buffer()
            .writeUtf8(comodoRsaCertificationAuthority)
            .writeUtf8(entrustRootCertificateAuthority)
            .inputStream()
    }

    /**
     * Returns a trust manager that trusts `certificates` and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a `SSLHandshakeException`.
     *
     *
     * This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     *
     * See also [CertificatePinner], which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     *
     * Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    @Throws(GeneralSecurityException::class)
    private fun trustManagerOnlyForCustomCertificates(`in`: InputStream): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(`in`)
        require(!certificates.isEmpty()) { "expected non-empty set of trusted certificates" }
        // Put the certificates a key store.
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        for ((index, certificate) in certificates.withIndex()) {
            keyStore.setCertificateEntry((index).toString(), certificate)
        }
        // Use it to build an X509 trust manager.
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            .init(keyStore, password)

        val trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            .apply {
                init(keyStore)
            }.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            ("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }

    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        return KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, password)
        }
    }

    private fun defaultTrustManager(): X509TrustManager? {
        return TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()).apply {
            // 传入null就可以获取默认的TrustManager
            init(null as KeyStore?)
        }.trustManagers?.find {
            it is X509TrustManager
        }?.let {
            it as X509TrustManager
        }
    }

    private class CompositeTrustManager(val defaultTrustManager: X509TrustManager?,
                                        val trustManagerList: Array<X509TrustManager>?)
        : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            defaultTrustManager?.checkClientTrusted(chain, authType)
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            var accept = false
            trustManagerList?.forEach {
                try {
                    it.checkServerTrusted(chain, authType)
                    accept = true
                } catch (e: CertificateException) {
                    println("failed in $it")
                }
            }

            if (!accept) {
                println("fallback to default trust manager")
                defaultTrustManager?.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return defaultTrustManager?.acceptedIssuers ?: emptyArray()
        }
    }
}