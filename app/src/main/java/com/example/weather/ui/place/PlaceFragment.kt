package com.example.weather.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.MainActivity
import com.example.weather.R
import com.example.weather.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

/**
 * A simple [Fragment] subclass.
 */
class PlaceFragment : Fragment() {
    private lateinit var placeAdapter: PlaceAdapter
    val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PlaceViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //只有当PlaceFragment被嵌入到MainActivity当中，并且之前已经存在选中的城市
        //此时才会直接跳转到WeatherActivity
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        //配置适配器
        val manager = LinearLayoutManager(activity)
        recyclerView.layoutManager = manager
        placeAdapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = placeAdapter
        //对输入内容进行判断
        searchPlaceEdit.addTextChangedListener {
            val context = it.toString()
            if (context.isNotEmpty()) {
                viewModel.searchPlaces(context)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                placeAdapter.notifyDataSetChanged()
            }
        }
        //对查询结果进行判断
        viewModel.placeLiveData.observe(requireActivity(), Observer {
            val place = it.getOrNull()
            if (place != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(place)
                placeAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}
