package felipesilveira.bitcoinpricehistorical.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import felipesilveira.bitcoinpricehistorical.R
import felipesilveira.bitcoinpricehistorical.connection.BitcoinRepository
import felipesilveira.bitcoinpricehistorical.model.BitcoinHistorical
import felipesilveira.bitcoinpricehistorical.model.BitcoinList
import felipesilveira.bitcoinpricehistorical.sqlite.DBLiteConnection
import felipesilveira.bitcoinpricehistorical.utils.App
import felipesilveira.bitcoinpricehistorical.utils.NetworkUtils
import felipesilveira.bitcoinpricehistorical.utils.Resource
import org.json.JSONObject
import java.util.*

class MainFragmentViewModel : AndroidViewModel {

    constructor(application: Application) : super(application)

    private val LOG_ONLINE_REQUEST:String = "ONLINE_REQUEST"
    private val SAVED_INSTANCE_LIST:String = "SAVED_INSTANCE_LIST"
    private val SAVED_INSTANCE_PRICE:String = "SAVED_INSTANCE_PRICE"
    private val SAVED_INSTANCE_LAST_UPDATED:String = "SAVED_INSTANCE_LAST_UPDATED"
    private lateinit var dbl: DBLiteConnection
    private lateinit var repository: BitcoinRepository

    private var lastPrice = ""
    private var lastUpdated = ""

    val data = MutableLiveData<Resource<BitcoinList>>()

    fun getData() {
         data.value = Resource.loading(null)

        if(App.isNetworkAvailable(getApplication())){
            repository.listCurrentPrice({
                //success
                lastUpdated = getApplication<Application>().getString(R.string.lastUpdateAt)+" "+App.getLastUpdateStringFormated(it.time?.updatedISO)
                val value: Double = it.bpi?.BRL?.rate_float as Double
                lastPrice = App.SELECTED_CURRENCY+" "+String.format("%.2f", value)

                dbl.clearCurrentCache()
                dbl.insertCurrentPriceToCache(lastPrice, lastUpdated)
                
                //get historical list
                getHistorical()
            },{
                //failure
                verifyCurrentInCache()
            })
        }else{
            verifyCurrentInCache()
        }
    }

    private fun getHistorical(){
        //verify if has item cached
        if(dbl.hasHistoricalCached){
            getHistoricalFromCache()
        }else{
            //get data From Internet
            val endDate = App.returnYYYYMDDD(Calendar.getInstance())
            val startDate = App.getTwoWeeksAgoDate()

            repository.listHistorical({ response ->
                //success

                val result = JSONObject(String(response.bytes()))

                val bpi = result.getJSONObject("bpi")
                var key = endDate
                val array = ArrayList<BitcoinHistorical>()
                array.add(BitcoinHistorical("",""))

                dbl.clearHistoricalCache()
                while(key != startDate){
                    Log.d(LOG_ONLINE_REQUEST, "Get FROM: $key")

                    if(bpi.has(key)) {
                        val value: Double = bpi.get(key) as Double
                        array.add(BitcoinHistorical(App.SELECTED_CURRENCY + " " + String.format("%.2f", value), App.formatDateToHistorical(key)))
                        dbl.insertHistoricalToCache(App.SELECTED_CURRENCY + " " + String.format("%.2f", value), App.formatDateToHistorical(key))
                    }

                    key = App.removeOneDayToDate(key)
                }

                data.value = Resource.success(BitcoinList(lastPrice, lastUpdated, array))
            },{
                //failure

                getHistoricalFromCache()
                data.value = Resource.error(null)
            }, startDate, endDate)
        }
    }

    private fun getHistoricalFromCache(){
        if(dbl.hasHistoricalCached){
            val array = dbl.getHistorical
            if(array != null && array.isNotEmpty()) data.value = Resource.success(BitcoinList(lastPrice, lastUpdated, array))
            else data.value = Resource.error(null)
        }
    }

    private fun verifyCurrentInCache(){
        if(dbl.hasCurrentPriceCached){
            lastPrice = dbl.getLastCurrentPrice
            lastUpdated = dbl.getLastModifiedDate
            
            getHistorical()
        }else{
            data.value = Resource.error(null)
        }
    }

    fun init(){
        dbl = DBLiteConnection.getInstance(getApplication())!!
        repository = BitcoinRepository(NetworkUtils.getRetrofit())
    }
}
