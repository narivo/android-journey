package com.sriyank.globotour.city

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sriyank.globotour.R

class CityAdapter(val context: Context, val cityList: ArrayList<City>,
                  private val forFavoriteItems: Boolean) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var imageView: ImageView? = itemView.findViewById(R.id.imv_city)
        private var textView: TextView? = itemView.findViewById(R.id.txv_city_name)
        private var favoriteView: ImageView? = itemView.findViewById(R.id.imv_favorite)
        private var deleteView: ImageView? = itemView.findViewById(R.id.imv_delete)

        lateinit var currentCity: City
        private var currentPosition: Int = -1

        private val favoriteIconFill = ContextCompat.getDrawable(context, R.drawable.ic_favorite_filled)
        private val favoriteIconOutlined = ContextCompat.getDrawable(context, R.drawable.ic_favorite_bordered)

        fun bindTo(city: City, position: Int) {
            currentPosition = position
            currentCity = city

            if(forFavoriteItems == false) {

                if(currentCity.isFavorite) {
                    favoriteView?.setImageDrawable(favoriteIconFill)
                } else {
                    favoriteView?.setImageDrawable(favoriteIconOutlined)
                }


                favoriteView?.setOnClickListener(this@CityViewHolder)
                deleteView?.setOnClickListener(this@CityViewHolder)

            }

            val imageDrawable = ContextCompat.getDrawable(context, city.imageId)
            imageView?.setImageDrawable(imageDrawable)
            textView?.text = city.name
        }

        override fun onClick(v: View?) {
            when (v!!.id){
                R.id.imv_favorite -> addToFavorite()
                R.id.imv_delete -> deleteItem()
            }
        }

        private fun deleteItem() {
            val deletedCity = cityList[currentPosition]
            cityList.removeAt(currentPosition)

            val favoritePos = VacationSpots.favoriteCityList.indexOf(deletedCity)
            VacationSpots.favoriteCityList.removeAt(favoritePos)

            notifyItemRemoved(currentPosition)
            notifyItemRangeChanged(currentPosition, cityList.size)
        }

        private fun addToFavorite() {
            currentCity.isFavorite = !currentCity.isFavorite

            if(currentCity.isFavorite) {
                favoriteView?.setImageDrawable(favoriteIconFill)
                VacationSpots.favoriteCityList.add(currentCity)
            } else {
                favoriteView?.setImageDrawable(favoriteIconOutlined)
                VacationSpots.favoriteCityList.remove(currentCity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        var itemView: View?
        if(forFavoriteItems) {

            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_favorite, parent, false)
        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_city, parent, false)
            //itemView = LayoutInflater.from(context).inflate(R.layout.grid_item_city, parent, false)
        }
        return CityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bindTo(cityList[position], position)
    }

    override fun getItemCount(): Int = cityList.size
}
