package felipesilveira.bitcoinpricehistorical.connection

import android.util.Log
import felipesilveira.bitcoinpricehistorical.model.CurrentPriceModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback


class BitcoinRepository(val webService: WebService): BitcoinDataSource {
    override fun listHistorical(success: (ResponseBody) -> Unit, failure: () -> Unit, startDate: String, endDate: String) {
        val call = webService.getBitcoinHistorical(startDate, endDate)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                if (response.isSuccessful) {
                    success(response.body()!!)
                } else {
                    failure()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                Log.i("RESPONSE", t.toString())
                failure()
            }
        })

    }

    override fun listCurrentPrice(success: (CurrentPriceModel) -> Unit, failure: () -> Unit) {
        val call = webService.getCurrentPrice()
        call.enqueue(object : Callback<CurrentPriceModel> {
            override fun onResponse(call: Call<CurrentPriceModel>, response: retrofit2.Response<CurrentPriceModel>) {
                if (response.isSuccessful) {
                    success(response.body()!!)
                } else {
                    failure()
                }
            }

            override fun onFailure(call: Call<CurrentPriceModel>, t: Throwable?) {
                Log.i("RESPONSE", t.toString())
                failure()
            }
        })
    }
}