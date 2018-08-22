package felipesilveira.bitcoinpricehistorical.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import felipesilveira.bitcoinpricehistorical.R
import felipesilveira.bitcoinpricehistorical.adapter.BitcoinListAdapter
import felipesilveira.bitcoinpricehistorical.utils.Resource
import felipesilveira.bitcoinpricehistorical.viewmodel.MainFragmentViewModel
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MainFragmentViewModel
    private var snackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        viewModel.init()

        setupRecyclerView()

        viewModel.data.observe(this, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS ->{
                        recyclerView.adapter = BitcoinListAdapter(it.data!!.lastPrice, it.data.lastUpdate, it.data.historical)
                        dismissSwipeRefresh()
                        dismissSnackBar()
                    }
                    Resource.Status.LOADING ->{
                        showSwipeRefresh()
                    }
                    Resource.Status.ERROR ->{
                        dismissSwipeRefresh()
                        showSnackBar()
                    }
                }
            } })
    }

    private fun showSwipeRefresh(){
        if(!swipeToRefresh.isRefreshing) swipeToRefresh?.isRefreshing = true
    }

    private fun dismissSwipeRefresh(){
        if(swipeToRefresh.isRefreshing) swipeToRefresh?.isRefreshing = false
    }

    private fun showSnackBar(){
        snackbar = Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.noNetworkAvailable), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.tryAgain)) {
                    viewModel.getData()
                }

        snackbar?.show()
    }

    private fun dismissSnackBar() {
        if (snackbar != null && snackbar!!.isShown) snackbar?.dismiss()
    }

    private fun setupRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        swipeToRefresh.setOnRefreshListener {
            viewModel.getData()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

}
