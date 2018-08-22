package felipesilveira.bitcoinpricehistorical.model

data class CurrentPriceModel(
	val bpi: Bpi? = null,
	val time: Time? = null,
	val disclaimer: String? = null
)

data class Bpi(
		val USD: USD? = null,
		val BRL: BRL? = null
)

data class BRL(
		val rate_float: Double = 0.0,
		val code: String? = null,
		val rate: String? = null,
		val description: String? = null
)

data class USD(
		val rateFloat: Double = 0.0,
		val code: String? = null,
		val rate: String? = null,
		val description: String? = null
)

data class Time(
		val updateduk: String = "",
		val updatedISO: String = "",
		val updated: String = ""
)
