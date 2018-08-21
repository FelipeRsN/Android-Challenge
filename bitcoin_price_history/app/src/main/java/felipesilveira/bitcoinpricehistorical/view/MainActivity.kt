package felipesilveira.bitcoinpricehistorical.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import felipesilveira.bitcoinpricehistorical.R
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)
    }

}
