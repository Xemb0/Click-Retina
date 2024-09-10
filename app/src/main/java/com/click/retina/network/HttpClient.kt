package com.click.retina.network

import android.annotation.SuppressLint
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection

object SSLConfiguration {
    fun setupUntrustedSSL() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            })

            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, trustAllCerts, java.security.SecureRandom())
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)

            val hostnameVerifier = HostnameVerifier { _, _ -> true }
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
