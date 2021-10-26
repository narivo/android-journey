package com.sriyank.globotour.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sriyank.globotour.R
import com.sriyank.globotour.city.City
import com.sriyank.globotour.city.CityAdapter
import com.sriyank.globotour.city.VacationSpots
import java.util.*
import kotlin.collections.AbstractList
import kotlin.collections.ArrayList


class FavoriteFragment : Fragment() {

    private lateinit var adapter: CityAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteCityList: ArrayList<City>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        setupFavoriteList(view)

        return view
    }

    private fun setupFavoriteList(view: View?) {

        val context = requireContext()

        recyclerView = view?.findViewById(R.id.favorite_recycler_view)!!
        favoriteCityList = VacationSpots.favoriteCityList as ArrayList<City>
        adapter = CityAdapter(context, cityList = favoriteCityList, true)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            Collections.swap(favoriteCityList, fromPosition, toPosition)

            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val deletedCity = favoriteCityList[position]

            deleteItem(position)
            updateCityList(deletedCity, false)

            Snackbar.make(recyclerView, "Deleted ${deletedCity.name}", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    undoDelete(position, deletedCity)
                    updateCityList(deletedCity, true)
                    recyclerView.scrollToPosition(position)
                }.show()
        }

    })

    private fun deleteItem(position: Int) {
        favoriteCityList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, favoriteCityList.size)
    }

    private fun updateCityList(city: City, isFavorite: Boolean) {
        val cityList = VacationSpots.cityList!!
        val position = cityList.indexOf(city)
        cityList[position].isFavorite = isFavorite
    }

    private fun undoDelete(position: Int, city: City){
        favoriteCityList.add(position, city)
        adapter.notifyItemInserted(position)
        adapter.notifyItemRangeChanged(position, favoriteCityList.size)
    }
}
