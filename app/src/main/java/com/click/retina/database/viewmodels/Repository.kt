package com.click.retina.database.viewmodels

import android.util.Log
import com.click.retina.database.models.DataModel
import com.click.retina.network.SSLConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DataRepository {
    private val urlString = "https://jsonkeeper.com/b/6HBE"
    private val TAG = "DataRepository"

    init {
        SSLConfiguration.setupUntrustedSSL()
    }

    suspend fun getData(): DataModel? {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            var dataModel: DataModel? = null
            try {
                val url = URL(urlString)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 10000
                    readTimeout = 10000
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                    Log.d(TAG, "Response: $response")

                    dataModel = parseResponse(response)
                } else {
                    Log.e(TAG, "Error response code: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data: ${e.localizedMessage}", e)
            } finally {
                connection?.disconnect()
            }

            dataModel
        }
    }

    private fun parseResponse(response: String): DataModel? {
        return try {
            val jsonObject = JSONObject(response)
            val choicesArray = jsonObject.getJSONArray("choices")
            val messageContent = choicesArray.getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
            val contentJson = JSONObject(messageContent)

            val titlesArray = contentJson.getJSONArray("titles")
            val titles = (0 until titlesArray.length()).joinToString("\n") {
                titlesArray.getString(it)
            }

            val description = contentJson.getString("description")
            DataModel(titles, description)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response: ${e.localizedMessage}", e)
            null
        }
    }
}
