package felipesilveira.bitcoinpricehistorical.view

import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import felipesilveira.bitcoinpricehistorical.R
import felipesilveira.bitcoinpricehistorical.adapter.BitcoinListAdapter
import felipesilveira.bitcoinpricehistorical.connection.BitcoinCurrentPriceConnection
import felipesilveira.bitcoinpricehistorical.connection.BitcoinHistoricalConnection
import felipesilveira.bitcoinpricehistorical.model.BitcoinHistorical
import felipesilveira.bitcoinpricehistorical.sqlite.DBLiteConnection
import felipesilveira.bitcoinpricehistorical.utils.App
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private val LOG_ONLINE_REQUEST:String = "ONLINE_REQUEST"
    private val SAVED_INSTANCE_LIST:String = "SAVED_INSTANCE_LIST"
    private val SAVED_INSTANCE_PRICE:String = "SAVED_INSTANCE_PRICE"
    private val SAVED_INSTANCE_LAST_UPDATED:String = "SAVED_INSTANCE_LAST_UPDATED"
    private var dbl: DBLiteConnection? = null
    private lateinit var adapter: BitcoinListAdapter
    private var array: ArrayList<BitcoinHistorical> = ArrayList()
    private var lastPrice: String = ""
    private var lastUpdated: String  = ""
    private var snackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null){
            array = savedInstanceState.getParcelableArrayList(SAVED_INSTANCE_LIST)
            lastPrice = savedInstanceState.getString(SAVED_INSTANCE_PRICE)
            lastUpdated = savedInstanceState.getString(SAVED_INSTANCE_LAST_UPDATED)
        }

        initVariablesAndSetupListeners()
        setupRecyclerViewAndGetData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(array.isNotEmpty()){
            outState.putParcelableArrayList(SAVED_INSTANCE_LIST, array)
            outState.putString(SAVED_INSTANCE_PRICE, lastPrice)
            outState.putString(SAVED_INSTANCE_LAST_UPDATED, lastUpdated)
        }
    }

    //Initialize variables and set RefreshListener to swipeToRefresh
    private fun initVariablesAndSetupListeners(){
        if(context != null) dbl = DBLiteConnection.getInstance(context!!)

        swipeToRefresh.setOnRefreshListener {
            checkInternetAndGetData()
        }
    }

    private fun setupRecyclerViewAndGetData(){
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun checkInternetAndGetData(){
        //detect if array has already populated if user change orientation for example
        if(array.isNotEmpty()){
            populateAdapter()
        }else {
            showSwipeRefresh()
            //Current Price
            //verify internet connection
            if (context != null && App.isNetworkAvailable(context!!)) {
                //has internet connection. get updated price
                getOnlineBitcoinCurrentPrice()
                dismissSnackBar()
            } else {
                //No internet connection, dismiss swipeRefresh and show snackbar
                dismissSwipeRefresh()
                showSnackBar()

                //check if has cached lastPrice to show
                if (dbl != null && dbl!!.hasCurrentPriceCached) {
                    getLocalBitcoinCurrentPrice()
                }
            }
        }
    }

    private fun showSnackBar(){
        snackbar = Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.noNetworkAvailable), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.tryAgain)) {
                    checkInternetAndGetData()
                }

        snackbar?.show()
    }

    private fun dismissSnackBar(){
        if(snackbar != null && snackbar!!.isShown) snackbar?.dismiss()
    }

    private fun showSwipeRefresh(){
        if(!swipeToRefresh.isRefreshing) swipeToRefresh?.isRefreshing = true
    }

    private fun dismissSwipeRefresh(){
        if(swipeToRefresh.isRefreshing) swipeToRefresh?.isRefreshing = false
    }

    //get current price of bitcoin from api
    private fun getOnlineBitcoinCurrentPrice(){
        //result
        val result = BitcoinCurrentPriceConnection().execute().get()
        Log.d(LOG_ONLINE_REQUEST, "Result: "+result.toString())

        if(result != null){
            //parse result and get values for the header
            val bpi = result.getJSONObject("bpi")
            val time = result.getJSONObject("time")
            val currency = bpi.getJSONObject(App.SELECTED_CURRENCY)
            val value: Double = currency.get("rate_float") as Double
            val updatedIso:String = time.get("updatedISO").toString()

            lastUpdated = getString(R.string.lastUpdateAt)+" "+App.getLastUpdateStringFormated(updatedIso)
            lastPrice = App.SELECTED_CURRENCY+" "+String.format("%.2f", value)

            Log.d(LOG_ONLINE_REQUEST, "lastUpdated: $lastUpdated")
            Log.d(LOG_ONLINE_REQUEST, "Value: $lastPrice")

            dbl?.clearCurrentCache()
            dbl?.insertCurrentPriceToCache(lastPrice, lastUpdated)

            //detect if has historical in cache
            if(hasHistoricalCached()) {
                //use cached itens
                getLocalBitcoinHistoricalList()
            }else{
                //get historical data
                getOnlineBitcoinHistoricalList()
            }
        }else{
            //Error getting data, show snackbar to retry
            showSnackBar()
        }
    }

    //Get historical of bitcoin price from api
    private fun getOnlineBitcoinHistoricalList(){
        //start and end date range
        val endDate = App.returnYYYYMDDD(Calendar.getInstance())
        val startDate = App.getTwoWeeksAgoDate()
        Log.d(LOG_ONLINE_REQUEST, "endDate: $endDate")
        Log.d(LOG_ONLINE_REQUEST, "startDate: $startDate")

        //Complete url from api
        val completeUrl = "?start="+startDate+"&end="+endDate+"&currency="+App.SELECTED_CURRENCY
        Log.d(LOG_ONLINE_REQUEST, "completeURL: $completeUrl")

        //result
        val result = BitcoinHistoricalConnection().execute(completeUrl).get()
        Log.d(LOG_ONLINE_REQUEST, "Result: "+result.toString())

        if(result != null){
            //parse result and get price and date from each day
            val bpi = result.getJSONObject("bpi")
            var key = endDate
            array = ArrayList()
            array.add(BitcoinHistorical("",""))

            dbl?.clearHistoricalCache()
            while(key != startDate){
                Log.d(LOG_ONLINE_REQUEST, "Get FROM: $key")

                if(bpi.has(key)) {
                    val value: Double = bpi.get(key) as Double
                    array.add(BitcoinHistorical(App.SELECTED_CURRENCY + " " + String.format("%.2f", value), App.formatDateToHistorical(key)))
                    dbl?.insertHistoricalToCache(App.SELECTED_CURRENCY + " " + String.format("%.2f", value), App.formatDateToHistorical(key))
                }

                key = App.removeOneDayToDate(key)
            }

            Log.d(LOG_ONLINE_REQUEST, "historical list: $array")

            //dismiss SwipeRefresh and populate the recyclerView with the list
            dismissSnackBar()
            populateAdapter()
        }else{
            //Error getting data, show snackbar to retry
            if(hasHistoricalCached()) getLocalBitcoinHistoricalList()
            showSnackBar()
        }

    }

    private fun hasHistoricalCached():Boolean{
        return dbl != null && dbl!!.hasHistoricalCached
    }

    //Populate adapter on RecyclerView
    private fun populateAdapter(){
        adapter = BitcoinListAdapter(lastPrice, lastUpdated, array)
        recyclerView.adapter = adapter
        dismissSwipeRefresh()
    }

    //get price from sqlite to populate list
    private fun getLocalBitcoinCurrentPrice(){
        lastPrice = dbl!!.getLastCurrentPrice
        lastUpdated = dbl!!.getLastModifiedDate

        if(hasHistoricalCached()){
            getLocalBitcoinHistoricalList()
        }else{
            array = ArrayList()
            array.add(BitcoinHistorical("",""))
            populateAdapter()
        }

    }

    //get historical from sqlite to populate list
    private fun getLocalBitcoinHistoricalList(){
        val historical = dbl?.getHistorical
        if(historical != null && historical.isNotEmpty()){
            array = ArrayList(historical)
            array.add(0, BitcoinHistorical("", ""))
            populateAdapter()
        }else{
            //Error getting data, show snackbar to retry
            showSnackBar()
        }
    }

    override fun onResume() {
        super.onResume()
        checkInternetAndGetData()
    }

}
