package felipesilveira.bitcoinpricehistorical.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import felipesilveira.bitcoinpricehistorical.R
import felipesilveira.bitcoinpricehistorical.model.BitcoinHistorical

class BitcoinListAdapter(private val currentPrice: String, private val lastUdated: String, private val data: ArrayList<BitcoinHistorical>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    class ViewHolderHeader(val view: View) : RecyclerView.ViewHolder(view){
        val price = view.findViewById<TextView>(R.id.price)
        val lastUpdate = view.findViewById<TextView>(R.id.lastUpdate)
    }

    class ViewHolderItem(val view: View) : RecyclerView.ViewHolder(view){
        val price = view.findViewById<TextView>(R.id.price)
        val date = view.findViewById<TextView>(R.id.date)
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) TYPE_HEADER
        else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_HEADER){
            val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_bitcoinprice, parent, false)
            return ViewHolderHeader(layoutView)
        }else{
            val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_historical, parent, false)
            return ViewHolderItem(layoutView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when (holder.itemViewType){
           TYPE_HEADER ->{
               (holder as ViewHolderHeader).price.text = currentPrice
               holder.lastUpdate.text = lastUdated
           }
           else ->{
               (holder as ViewHolderItem).price.text = data[position].price
               holder.date.text = data[position].date
           }
       }

    }

    override fun getItemCount() = data.size
}