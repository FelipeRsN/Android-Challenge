package felipesilveira.bitcoinpricehistorical.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBCore private constructor(ctx: Context) : SQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {

    companion object {
        private var db: DBCore? = null
        private val DB_NAME = "BitcoinPriceHistorical"
        private val DB_VERSION = 1

        fun getInstance(ctx: Context): DBCore? {
            if (db == null) db = DBCore(ctx)
            return db
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table bitcoinCache(cachedPrice text, cachedDate text)")
        db.execSQL("create table bitcoinCurrentValue(currentPrice text, lastModifiedDate date)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table IF EXISTS bitcoinCache;")
        db.execSQL("drop table IF EXISTS bitcoinCurrentValue;")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table IF EXISTS bitcoinCache;")
        db.execSQL("drop table IF EXISTS bitcoinCurrentValue;")
        onCreate(db)
    }
}