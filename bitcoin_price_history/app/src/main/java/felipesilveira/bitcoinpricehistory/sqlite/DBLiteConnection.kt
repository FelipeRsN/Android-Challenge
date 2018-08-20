package felipesilveira.bitcoinpricehistory.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase

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

    val hasItemCached: Boolean
        get() {
            val columns = arrayOf("cachedItems")
            val cursor = db?.query("cryptoCache", columns, null, null, null, null, null)
            return if(cursor != null && cursor.moveToFirst()){
                cursor.close()
                true
            }else{
                cursor?.close()
                false
            }
        }
}