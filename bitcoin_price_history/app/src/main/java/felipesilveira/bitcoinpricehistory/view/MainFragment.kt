package felipesilveira.bitcoinpricehistory.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import felipesilveira.bitcoinpricehistory.connection.BitcoinHistoricalConnection
import felipesilveira.bitcoinpricehistory.R
import felipesilveira.bitcoinpricehistory.sqlite.DBLiteConnection
import felipesilveira.bitcoinpricehistory.utils.App
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.*

class MainFragment : Fragment() {

    private var dbl: DBLiteConnection? = null

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbl = DBLiteConnection.getInstance(context!!)

        setupRecyclerView()
        checkInternetAndGetHistorical()
    }

    private fun setupRecyclerView(){
        swipeToRefresh.setOnRefreshListener {
            checkInternetAndGetHistorical()
        }
    }

    private fun checkInternetAndGetHistorical(){
        showSwipeRefresh()

        if(App.isNetworkAvailable(context!!)){
            getOnlineBitcoinHistoricalList()
        }else{
            if(dbl != null && dbl!!.hasItemCached){
                getLocalBitcoinHistoricalList()
            }else{
                dismissSwipeRefresh()
                Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.noNetworkAvailable), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.tryAgain)) {
                            checkInternetAndGetHistorical()
                        }.show()
            }
        }

    }

    private fun showSwipeRefresh(){
        if(!swipeToRefresh.isRefreshing) swipeToRefresh?.isRefreshing = true
    }

    private fun dismissSwipeRefresh(){
        if(swipeToRefresh.isRefreshing) swipeToRefresh?.isRefreshing = false
    }

    private fun getOnlineBitcoinHistoricalList(){
        val endDate = App.returnYYYYMDDD(Calendar.getInstance())
        val startDate = App.getTwoWeeksAgoDate()

        val result = BitcoinHistoricalConnection().execute("?start="+startDate+"&end="+endDate+"&currency="+App.SELECTED_CURRENCY).get()

    }

    private fun getLocalBitcoinHistoricalList(){

    }

}
