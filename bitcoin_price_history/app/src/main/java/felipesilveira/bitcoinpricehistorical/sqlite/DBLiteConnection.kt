package felipesilveira.bitcoinpricehistorical.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import felipesilveira.bitcoinpricehistorical.model.BitcoinHistorical
import java.util.*
import kotlin.collections.ArrayList

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
            val columns = arrayOf("cachedPrice")
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
            val cursor = db?.query("bitcoinCurrentValue", columns, null, null, null, null, null)
            return if(cursor != null && cursor.moveToFirst()){
                cursor.getString(0)
            }else ""
        }

    val getHistorical: ArrayList<BitcoinHistorical>?
        get() {
            val columns = arrayOf("cachedPrice, cachedDate")
            val cursor = db?.query("bitcoinCache", columns, null, null, null, null, null)
            val array = ArrayList<BitcoinHistorical>()
            if(cursor != null && cursor.moveToFirst()){
                while(!cursor.isAfterLast){
                    array.add(BitcoinHistorical(cursor.getString(0), cursor.getString(1)))
                    cursor.moveToNext()
                }
                cursor.close()
                return array
            }else{
                return null
            }
        }

    /////////////////////////////////////////////////////////////
    // insert
    fun insertCurrentPriceToCache(lastPrice: String, lastUpdated: String) {
        val values = ContentValues()
        values.put("currentPrice", lastPrice)
        values.put("lastModifiedDate", lastUpdated)
        db?.insert("bitcoinCurrentValue", null, values)
    }

    fun insertHistoricalToCache(price: String, date: String) {
        val values = ContentValues()
        values.put("cachedPrice", price)
        values.put("cachedDate", date)
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