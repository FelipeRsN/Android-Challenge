package felipesilveira.bitcoinpricehistorical.model

data class BitcoinList(var lastPrice: String, var lastUpdate: String, var historical: ArrayList<BitcoinHistorical>)