package com.test.rnids.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class HistoryAdapter(private val context: Context,
                    private val historyEntryModelArrayList: MutableList<HistoryEntryModel>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(com.test.rnids.R.layout.history_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: HistoryEntryModel = historyEntryModelArrayList[position]
        holder.title.text = model.domainName
        holder.timestamp.text = Date(model.timestamp).toString()
        holder.expiry.text = Date(model.expiry).toString()
    }

    override fun getItemCount(): Int {
        return historyEntryModelArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val timestamp: TextView
        val expiry: TextView

        init {
            title = itemView.findViewById(com.test.rnids.R.id.cardtitle)
            timestamp = itemView.findViewById(com.test.rnids.R.id.timestamp)
            expiry = itemView.findViewById(com.test.rnids.R.id.expiry)
        }
    }

}
