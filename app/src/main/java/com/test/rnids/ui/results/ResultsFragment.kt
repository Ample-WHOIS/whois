package com.test.rnids.ui.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.test.rnids.MainActivity
import com.test.rnids.databinding.ExpandableInfoboxBinding
import com.test.rnids.databinding.FragmentResultsBinding
import com.test.rnids.parsers.BasicParser
import com.test.rnids.ui.elements.ExpandableInfobox
import java.net.IDN

class ResultsFragment : Fragment() {

    private lateinit var resultsViewModel: ResultsViewModel
    private var _binding: FragmentResultsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var parser: BasicParser
    private val boxes: MutableList<ExpandableInfobox> = mutableListOf()

    private var doneWork = false
    private var rawMode = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        resultsViewModel =
            ViewModelProvider(this).get(ResultsViewModel::class.java)

        _binding = FragmentResultsBinding.inflate(inflater, container, false)

        parser = BasicParser(requireContext())

        MainActivity.whoisClientWrapper.result.observe(viewLifecycleOwner, {
            parser.processRaw(it)
            rebuildView()
            parser.addObserver { _, _ ->
                rebuildView()
            }
            _binding!!.emptytext.visibility = GONE
        })

        MainActivity.whoisClientWrapper.lastDomain.observe(viewLifecycleOwner, {
            if (it.isNotEmpty())
            {
                _binding!!.sitename.text = IDN.toUnicode(it)
                _binding!!.favBtn.visibility = VISIBLE
                _binding!!.alarmBtn.visibility = VISIBLE
                _binding!!.emptytext.visibility = GONE
            }
        })

        _binding!!.rawModeBtn.setOnClickListener { rawButtonListener() }
        _binding!!.favBtn.setOnClickListener { favButtonListener() }
        _binding!!.alarmBtn.setOnClickListener { alarmButtonListener() }

        return binding.root
    }

    private fun rawButtonListener()
    {
        rawMode = !rawMode

        val id: Int
        if (rawMode)
        {
            id = com.test.rnids.R.drawable.raw_off
        }
        else
        {
            id = com.test.rnids.R.drawable.raw_on
        }

        binding.rawModeBtn.setImageResource(id)

        rebuildView()
    }

    private fun favButtonListener()
    {

    }

    private fun alarmButtonListener()
    {

    }

    private fun rebuildView()
    {
        _binding!!.rescontainer.scrollTo(0, 0)

        val llayout = _binding!!.prettylayout
        val rawcontainer = _binding!!.rawcontainer

        if (!rawMode)
        {
            rawcontainer.visibility = GONE

            for (section in parser.getSectionsOrdered())
            {
                if (section.isEmpty())
                {
                    continue
                }

                val box = ExpandableInfobox(requireContext())

                box.setTitle(section.title)
                box.setText(section.toString())

                llayout.addView(box)
                boxes.add(box)
            }
        }
        else
        {
            for (box in boxes)
            {
                (llayout as ViewGroup).removeView(box)
            }
            boxes.clear()

            rawcontainer.visibility = VISIBLE
        }

        rawcontainer.text = parser.getRaw()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
