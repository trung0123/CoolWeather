package com.example.coolweather.ui.area


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.coolweather.R
import com.example.coolweather.databinding.FragmentChooseAreaBindingImpl
import com.example.coolweather.ui.MainActivity
import com.example.coolweather.ui.weather.WeatherActivity
import com.example.coolweather.util.InjectorUtil
import kotlinx.android.synthetic.main.fragment_choose_area.*

/**
 * A simple [Fragment] subclass.
 */
class ChooseAreaFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(
            this, InjectorUtil.getChooseAreaModelFactory()
        ).get(ChooseAreaViewModel::class.java)
    }
    private var progressDialog: AlertDialog? = null
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_choose_area, container, false)
        val binding = DataBindingUtil.bind<FragmentChooseAreaBindingImpl>(view)
        binding?.viewModel = viewModel
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = ChooseAreaAdapter(context!!, R.layout.simple_item, viewModel.dataList)
        listView.adapter = adapter
        observe()
    }

    private fun observe() {
        viewModel.currentLevel.observe(this, Observer { level ->
            when (level) {
                LEVEL_PROVINCE -> {
                    titleText.text = "中国"
                    backButton.visibility = View.GONE
                }
                LEVEL_CITY -> {
                    titleText.text = viewModel.selectedProvince?.provinceName
                    backButton.visibility = View.VISIBLE
                }
                LEVEL_COUNTY -> {
                    titleText.text = viewModel.selectedCity?.cityName
                    backButton.visibility = View.VISIBLE
                }
            }
        })
        viewModel.dataChanged.observe(this, Observer {
            adapter.notifyDataSetChanged()
            listView.setSelection(0)
            closeProgressDialog()
        })
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) showProgressDialog()
            else closeProgressDialog()
        })
        viewModel.areaSelected.observe(this, Observer { selected ->
            if (selected && viewModel.selectedCounty != null) {
                if (activity is MainActivity) {
                    val intent = Intent(activity, WeatherActivity::class.java)
                    intent.putExtra("weather_id", viewModel.selectedCounty!!.weatherId)
                    startActivity(intent)
                    activity?.finish()
                } else if (activity is WeatherActivity) {
//                    val weatherActivity = activity as WeatherActivity
//                    weatherActivity.drawerLayout.closeDrawers()
//                    weatherActivity.viewModel.weatherId = viewModel.selectedCounty!!.weatherId
//                    weatherActivity.viewModel.refreshWeather()
                }
                viewModel.areaSelected.value = false
            }
        })
        if (viewModel.dataList.isEmpty()) {
            viewModel.getProvinces()
        }
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = AlertDialog.Builder(requireContext()).setMessage("正在加载...").create()
        }
        progressDialog?.show()
    }

    /**
     * 关闭进度对话框
     */
    private fun closeProgressDialog() {
        progressDialog?.hide()
    }

    companion object {
        const val LEVEL_PROVINCE = 0
        const val LEVEL_CITY = 1
        const val LEVEL_COUNTY = 2
    }
}
