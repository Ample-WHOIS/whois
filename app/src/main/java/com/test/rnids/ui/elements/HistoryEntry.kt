package com.test.rnids.ui.elements

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.test.rnids.databinding.HistoryEntryBinding

class HistoryEntry(context: Context, attrs: AttributeSet?) : MaterialCardView(context, attrs) {
    constructor(context: Context) : this(context, null)

    private var _binding: HistoryEntryBinding? = null

    private val binding get() = _binding!!

    private var title: TextView
    private var timestamp: TextView
    private var expiry: TextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addView(inflater.inflate(com.test.rnids.R.layout.history_entry, this, false))

        title = findViewById(com.test.rnids.R.id.cardtitle)
        timestamp = findViewById(com.test.rnids.R.id.timestamp)
        expiry = findViewById(com.test.rnids.R.id.expiry)
    }

    fun setTitle(str: String)
    {
        title.text = str
    }

    fun setTimestamp(str: String)
    {
        timestamp.text = str
    }

    fun setExpiry(str: String)
    {
        expiry.text = str
    }
}