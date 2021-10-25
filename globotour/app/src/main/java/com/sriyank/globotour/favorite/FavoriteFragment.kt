package com.sriyank.globotour.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sriyank.globotour.R
import com.sriyank.globotour.city.City
import com.sriyank.globotour.city.CityAdapter
import com.sriyank.globotour.city.VacationSpots


class FavoriteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        setupFavoriteList(view)

        return view
    }

    private fun setupFavoriteList(view: View?) {

        val context = requireContext()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.favorite_recycler_view)

        val adapter = CityAdapter(context, cityList = VacationSpots.favoriteCityList as ArrayList<City>, true)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
    }
}
