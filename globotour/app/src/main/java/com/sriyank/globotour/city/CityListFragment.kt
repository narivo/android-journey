package com.sriyank.globotour.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sriyank.globotour.R


class CityListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_city_list, container, false)

        setupCityList(view)

        return view
    }

    private fun setupCityList(view: View?) {

        val context = requireContext()
        val adapter = CityAdapter(context, cityList = VacationSpots.cityList!!, false)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.city_recycler_view)

        val layoutManager = LinearLayoutManager(context)
        //val layoutManager = GridLayoutManager(context, spanCount = 2)
        //val layoutManager = StaggeredGridLayoutManager(spanCount = 2, GridLayout.VERTICAL)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView?.setHasFixedSize(true)

        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter

    }
}