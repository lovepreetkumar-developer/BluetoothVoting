package com.example.bluetoothlechat.ui.admin

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.base.*
import com.example.bluetoothlechat.bluetooth.BluetoothChatService
import com.example.bluetoothlechat.databinding.DialogAlertBinding
import com.example.bluetoothlechat.databinding.FragmentAdminHomeBinding
import com.example.bluetoothlechat.databinding.ItemVoteBinding
import com.example.bluetoothlechat.models.AdminAllVotesModel
import com.example.bluetoothlechat.models.DeviceModel
import com.example.bluetoothlechat.models.VoteModel
import com.example.bluetoothlechat.utils.ConstantsJava.*
import com.example.bluetoothlechat.utils.PreferenceProvider
import com.example.bluetoothlechat.utils.showToast
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*


/**
 * Property of Techfathers, Inc @ 2022 All Rights Reserved.
 */

class AdminHomeFragment : BaseFragmentAdvance<FragmentAdminHomeBinding>(), KodeinAware {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mInstance: AdminHomeFragment? = null
    }

    init {
        mInstance = this
    }

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()

    private var mLogoutWarningDialog: BaseCustomDialog<DialogAlertBinding>? = null

    private var mVotesAdapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var mDeviceModel: DeviceModel

    /**
     * Name of the connected device
     */
    private var mConnectedDeviceName: String? = null

    /**
     * Local Bluetooth adapter
     */
    private lateinit var mBluetoothAdapter: BluetoothAdapter

    /**
     * Member object for the chat services
     */
    private var mChatService: BluetoothChatService? = null

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_admin_home
    }

    override fun onDestroy() {
        super.onDestroy()
        mChatService?.stop()
    }

    override fun onResume() {
        super.onResume()

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService!!.state === BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService!!.start()
            }
        }
    }

    override fun onFragmentCreateView(savedInstanceState: Bundle?) {
        super.onFragmentCreateView(savedInstanceState)
        binding?.let {
            mainBinding = it
            initView()
        }
    }

    private fun initView() {
        setBaseCallback(baseCallback)

        setLatestData()

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mChatService = BluetoothChatService(activity, mHandler)

        val args = arguments?.let { AdminHomeFragmentArgs.fromBundle(it) }
        args?.let {
            mDeviceModel = args.deviceModel
            connectDevice(true)
        }
    }

    private val baseCallback = BaseCallback {
        when (it.id) {
            R.id.img_logout -> showAlertDialog()
            R.id.fb_create_vote -> findNavController().navigate(AdminHomeFragmentDirections.actionAdminHomeFragmentToCreateVoteFragment())
            R.id.img_connect -> connectDevice(true)
        }
    }

    private fun List<VoteModel>.toVoteItems(): List<BaseItem<VoteModel, ItemVoteBinding>> {
        return this.map {
            BaseItem(
                R.layout.item_vote,
                it,
                object : BaseItem.OnItemClickListener<VoteModel> {
                    override fun onItemClick(view: View, model: VoteModel, position: Int) {
                        findNavController().navigate(
                            AdminHomeFragmentDirections.actionAdminHomeFragmentToCreateVoteFragment(
                                model
                            )
                        )
                    }
                }
            )
        }
    }

    private fun showAlertDialog() {
        mLogoutWarningDialog = BaseCustomDialog(
            requireContext(),
            R.layout.dialog_alert,
            object : BaseCustomDialog.DialogListener {
                override fun onViewClick(view: View?) {
                    view?.let {
                        when (it.id) {
                            R.id.btn_cancel -> mLogoutWarningDialog?.dismiss()
                            R.id.btn_ok -> {
                                mLogoutWarningDialog?.dismiss()
                                mPrefProvider.setUser(null)
                                goToMainScreen()
                            }
                            else -> mLogoutWarningDialog?.dismiss()
                        }
                    }
                }
            })

        Objects.requireNonNull<Window>(mLogoutWarningDialog?.window).setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        mLogoutWarningDialog?.getBinding()?.tvMessage?.text =
            getString(R.string.are_you_sure_to_logout)
        mLogoutWarningDialog?.setCancelable(true)
        mLogoutWarningDialog?.show()
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            val activity = activity
            when (msg.what) {
                MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName))
                        mainBinding.tvConnectionStatus.text =
                            "Connection : Connected (${mConnectedDeviceName})"
                        mPrefProvider.getAdminVotesList()?.let { votesList ->
                            val adminAllVotesModel = AdminAllVotesModel()
                            adminAllVotesModel.admin_al_votes = votesList
                            sendMessage(Gson().toJson(adminAllVotesModel))
                        }
                    }
                    BluetoothChatService.STATE_CONNECTING -> {
                        //setStatus(R.string.title_connecting)
                        mainBinding.tvConnectionStatus.text = "Connection : Connecting"
                    }
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                        //setStatus(R.string.title_not_connected)
                        mainBinding.tvConnectionStatus.text = "Connection : Disconnected"
                    }
                }
                MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    setLatestData()
                }
                MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    updateVoteInLocalDB(readMessage)
                }
                MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(DEVICE_NAME)
                    if (null != activity) {
                        Toast.makeText(
                            activity, "Connected to "
                                    + mConnectedDeviceName, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                MESSAGE_TOAST -> if (null != activity) {
                    Toast.makeText(
                        activity, msg.data.getString(TOAST),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setLatestData() {
        mPrefProvider.getAdminVotesList()?.let { allVotesList ->
            if (allVotesList.isNotEmpty()) {
                mVotesAdapter.clear()
                mainBinding.tvNoDataFound.visibility = View.GONE
                mainBinding.rvVotes.visibility = View.VISIBLE
                mainBinding.rvVotes.adapter = mVotesAdapter
                mVotesAdapter.addAll(allVotesList.toVoteItems())
            } else {
                mainBinding.tvNoDataFound.visibility = View.VISIBLE
                mainBinding.rvVotes.visibility = View.GONE
            }
        }
    }

    private fun updateVoteInLocalDB(readMessage: String) {
        val voteModel = Gson().fromJson(readMessage, VoteModel::class.java)
        voteModel?.let { finalVoteModel ->
            requireContext().showToast("Vote has been done by some user for ${finalVoteModel.vote_title}")

            val listOfStoredVotes = mPrefProvider.getAdminVotesList()
            listOfStoredVotes?.let {
                for (voteModelInLocal in listOfStoredVotes.withIndex()) {
                    if (voteModelInLocal.value.id == finalVoteModel.id) {
                        listOfStoredVotes[voteModelInLocal.index] = finalVoteModel
                        break
                    }
                }
                mPrefProvider.setAdminVotesList(listOfStoredVotes)
                setLatestData()
            }
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    fun sendMessage(message: String) {
        // Check that we're actually connected before trying anything
        if (mChatService!!.state !== BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(activity, R.string.not_connected, Toast.LENGTH_SHORT).show()
            return
        }
        if (message.isNotEmpty()) {
            val send = message.toByteArray()
            mChatService?.write(send)
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An [Intent] with [DeviceListActivity.EXTRA_DEVICE_ADDRESS] extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private fun connectDevice(secure: Boolean) {
        val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceModel.address)
        mChatService?.connect(device, secure)
    }
}