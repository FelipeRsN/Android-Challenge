package felipesilveira.bitcoinpricehistory.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*

class App : Application(){

    companion object STATIC_ITENS {
        const val CONNECTION_BASE_URL = "https://api.coindesk.com/v1/bpi/historical/close.json"
        const val DATE_INTERVAL_DAYS: Int = -14

        const val SELECTED_CURRENCY = "BRL"

        //Detected if network is available
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun getTwoWeeksAgoDate(): String{
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_WEEK, DATE_INTERVAL_DAYS)
            return returnYYYYMDDD(calendar)
        }

        fun returnYYYYMDDD(calendar: Calendar): String{
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(calendar.time)
        }

        fun addOneDayToDate(date: String): String{
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            calendar.time = format.parse(date)
            calendar.add(Calendar.DAY_OF_WEEK, 1)

            return returnYYYYMDDD(calendar)
        }

    }
}