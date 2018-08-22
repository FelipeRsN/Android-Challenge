package felipesilveira.bitcoinpricehistorical.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class App : Application(){

    companion object STATIC_ITENS {

        const val SELECTED_CURRENCY = "BRL"

        const val CONNECTION_BASE_URL = "https://api.coindesk.com/v1/bpi/"
        const val CONNECTION_HISTORICAL_ENDPOINT = "historical/close.json"
        const val CONNECTION_CURRENTPRICE_ENDPOINT = "currentprice/$SELECTED_CURRENCY.json"
        const val DATE_INTERVAL_DAYS: Int = -14

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

        fun removeOneDayToDate(date: String): String{
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            calendar.time = format.parse(date)
            calendar.add(Calendar.DAY_OF_WEEK, -1)

            return returnYYYYMDDD(calendar)
        }

        fun convertUTCDateToLocalTimeZone(date: String): String{
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",  Locale.US)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")

            val localDate = utcFormat.parse(date)

            val timeZoneFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            timeZoneFormat.timeZone = TimeZone.getDefault()

            return timeZoneFormat.format(localDate)
        }

        fun getLastUpdateStringFormated(date: String?): String{
            date.let {
                val convertedDate = convertUTCDateToLocalTimeZone(it!!)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(convertedDate)

                return DateFormat.getDateTimeInstance().format(date)
            }
        }

        fun formatDateToHistorical(date: String): String{
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(date)
            return DateFormat.getDateInstance().format(date)

        }

    }
}