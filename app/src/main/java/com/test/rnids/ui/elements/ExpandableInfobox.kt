package com.test.rnids.ui.elements

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.ms.square.android.expandabletextview.ExpandableTextView
import com.test.rnids.databinding.ExpandableInfoboxBinding

class ExpandableInfobox(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    private var _binding: ExpandableInfoboxBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var title: TextView
    private var expandableTextView: ExpandableTextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addView(inflater.inflate(com.test.rnids.R.layout.expandable_infobox, this, false))

        title = findViewById(com.test.rnids.R.id.textView2)
        expandableTextView = findViewById(com.test.rnids.R.id.expand_text_view)
    }

    fun setTitle(str: String)
    {
        title.text = str
    }

    fun setText(str: String)
    {
        expandableTextView.text = str
    }
}