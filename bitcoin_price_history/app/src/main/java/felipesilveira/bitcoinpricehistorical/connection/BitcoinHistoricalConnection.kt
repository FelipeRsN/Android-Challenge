package felipesilveira.bitcoinpricehistorical.connection

import android.os.AsyncTask
import felipesilveira.bitcoinpricehistorical.utils.App
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class BitcoinHistoricalConnection : AsyncTask<String, String, JSONObject>() {

    override fun doInBackground(vararg params: String): JSONObject? {
        val data: JSONObject?

        val fullUrl = App.CONNECTION_BASE_URL+App.CONNECTION_HISTORICAL_ENDPOINT+params[0]

        val url = URL(fullUrl)
        val urlConnection = url.openConnection() as HttpURLConnection

        val stream = BufferedInputStream(urlConnection.inputStream)
        val bufferedReader = BufferedReader(InputStreamReader(stream))
        val builder = StringBuilder()

        val inputString: String? = bufferedReader.readLine()

        if(inputString != null) builder.append(inputString)

        data = JSONObject(builder.toString())
        urlConnection.disconnect()

        return data
    }
}