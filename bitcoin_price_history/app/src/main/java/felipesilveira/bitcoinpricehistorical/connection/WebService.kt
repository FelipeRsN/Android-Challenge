package felipesilveira.bitcoinpricehistorical.connection

import felipesilveira.bitcoinpricehistorical.model.CurrentPriceModel
import felipesilveira.bitcoinpricehistorical.utils.App
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface WebService {

    @GET(App.CONNECTION_CURRENTPRICE_ENDPOINT)
    fun getCurrentPrice(): Call<CurrentPriceModel>

    @GET(App.CONNECTION_HISTORICAL_ENDPOINT)
    fun getBitcoinHistorical(@Query("startDate") startDate: String, @Query("endDate") endDate: String, @Query("currency") currency: String = App.SELECTED_CURRENCY): Call<ResponseBody>

}