package felipesilveira.bitcoinpricehistorical.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class BitcoinHistorical(val price: String, val date: String): Parcelable