package com.example.bluetoothlechat.ui

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.base.BaseCallback
import com.example.bluetoothlechat.base.BaseFragment
import com.example.bluetoothlechat.base.BaseItem
import com.example.bluetoothlechat.databinding.FragmentSelectDeviceBinding
import com.example.bluetoothlechat.databinding.ItemDeviceNewBinding
import com.example.bluetoothlechat.models.DeviceModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


/**
 * Property of Techfathers, Inc @ 2022 All Rights Reserved.
 */

class SelectDeviceFragment : BaseFragment<FragmentSelectDeviceBinding>() {

    /**
     * Member fields
     */
    private lateinit var mBtAdapter: BluetoothAdapter

    private var mDevicesAdapter = GroupAdapter<GroupieViewHolder>()

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_select_device
    }

    override fun onFragmentCreateView(savedInstanceState: Bundle?) {
        super.onFragmentCreateView(savedInstanceState)
        initView()
    }

    private fun initView() {
        setBaseCallback(baseCallback)
        binding.toolbar.tvTitle.text = getString(R.string.select_device)
        binding.rvDevices.adapter = mDevicesAdapter

        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        // Get a set of currently paired devices
        val pairedDevices = mBtAdapter.bondedDevices

        // If there are paired devices, add each one to the ArrayAdapter

        val listOfPairedDevice = mutableListOf<DeviceModel>()
        if (pairedDevices.size > 0) {
            binding.rvDevices.visibility = View.VISIBLE
            binding.tvNoDataFound.visibility = View.GONE
            for (device in pairedDevices) {
                val deviceModel = DeviceModel()
                deviceModel.name = device.name
                deviceModel.address = device.address
                listOfPairedDevice.add(deviceModel)
            }
            mDevicesAdapter.addAll(listOfPairedDevice.toDeviceItems())
        } else {
            binding.rvDevices.visibility = View.GONE
            binding.tvNoDataFound.visibility = View.VISIBLE
        }
    }

    private val baseCallback = BaseCallback {
        when (it.id) {
            R.id.img_back -> goBack()
        }
    }

    private fun List<DeviceModel>.toDeviceItems(): List<BaseItem<DeviceModel, ItemDeviceNewBinding>> {
        return this.map {
            BaseItem(
                R.layout.item_device_new,
                it,
                object : BaseItem.OnItemClickListener<DeviceModel> {
                    override fun onItemClick(view: View, model: DeviceModel, position: Int) {
                        findNavController().navigate(
                            SelectDeviceFragmentDirections.actionDeviceListFragmentToAdminHomeFragment(
                                model
                            )
                        )
                    }
                }
            )
        }
    }
}