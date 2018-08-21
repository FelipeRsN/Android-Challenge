package felipesilveira.bitcoinpricehistorical.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import felipesilveira.bitcoinpricehistorical.model.BitcoinHistorical

class DBLiteConnection private constructor(context: Context) {
    private val db: SQLiteDatabase?

    companion object {
        private var instance: DBLiteConnection? = null

        fun getInstance(context: Context): DBLiteConnection? {
            if (instance == null) instance = DBLiteConnection(context)
            return instance
        }
    }

    init {
        val dbcore = DBCore.getInstance(context)
        db = dbcore?.writableDatabase
    }

    /////////////////////////////////////////////////////////////
    // detect if has cached items
    val hasHistoricalCached: Boolean
        get() {
            val columns = arrayOf("cachedItems")
            val cursor = db?.query("bitcoinCache", columns, null, null, null, null, null)
            return if(cursor != null && cursor.moveToFirst()){
                cursor.close()
                true
            }else{
                cursor?.close()
                false
            }
        }

    val hasCurrentPriceCached: Boolean
        get() {
            val columns = arrayOf("currentPrice")
            val cursor = db?.query("bitcoinCurrentValue", columns, null, null, null, null, null)
            return if(cursor != null && cursor.moveToFirst()){
                cursor.close()
                true
            }else{
                cursor?.close()
                false
            }
        }

    /////////////////////////////////////////////////////////////
    // select
    val getLastCurrentPrice: String
        get() {
            val columns = arrayOf("currentPrice")
            val cursor = db?.query("bitcoinCurrentValue", columns, null, null, null, null, null)
            return if(cursor != null && cursor.moveToFirst()){
                cursor.getString(0)
            }else ""
        }

    val getLastModifiedDate: String
        get() {
            val columns = arrayOf("lastModifiedDate")
            val cursor = db?.query("cryptoCache", columns, null, null, null, null, null)
            return if(cursor != null && cursor.moveToFirst()){
                cursor.getString(0)
            }else ""
        }

    /////////////////////////////////////////////////////////////
    // insert
    fun insertCurrentPriceToCache(lastPrice: String, lastUpdated: String) {
        val values = ContentValues()
        values.put("currentPrice", lastPrice)
        values.put("lastModifiedDate", lastUpdated)
        db?.insert("bitcoinCurrentValue", null, values)
    }

    fun insertHistoricalToCache(array: ArrayList<BitcoinHistorical>) {
        val values = ContentValues()
        values.put("cachedItems", array.toString())
        db?.insert("bitcoinCache", null, values)
    }

    /////////////////////////////////////////////////////////////
    // delete data
    fun clearCurrentCache() {
        db?.delete("bitcoinCurrentValue", null, null)
    }

    fun clearHistoricalCache() {
        db?.delete("bitcoinCache", null, null)
    }
}