package felipesilveira.bitcoinpricehistorical.connection

import felipesilveira.bitcoinpricehistorical.model.CurrentPriceModel
import okhttp3.ResponseBody

interface BitcoinDataSource {
    fun listHistorical(success : (ResponseBody) -> Unit, failure: () -> Unit, startDate: String, endDate: String)
    fun listCurrentPrice(success : (CurrentPriceModel) -> Unit, failure: () -> Unit)
}