package com.example.bluetoothlechat.ui.user

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Toast
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.databinding.DialogAlertBinding
import com.example.bluetoothlechat.databinding.FragmentUserHomeBinding
import com.example.bluetoothlechat.databinding.ItemVoteBinding
import com.example.bluetoothlechat.models.VoteModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.util.*
import androidx.navigation.fragment.findNavController
import com.example.bluetoothlechat.base.*
import com.example.bluetoothlechat.bluetooth.BluetoothChatService
import com.example.bluetoothlechat.models.AdminAllVotesModel
import com.example.bluetoothlechat.ui.admin.AdminHomeFragmentDirections
import com.example.bluetoothlechat.utils.ConstantsJava
import com.example.bluetoothlechat.utils.PreferenceProvider
import com.google.gson.Gson
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * Property of Techfathers, Inc @ 2022 All Rights Reserved.
 */

class UserHomeFragment : BaseFragmentAdvance<FragmentUserHomeBinding>(), KodeinAware {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mInstance: UserHomeFragment? = null
    }

    init {
        mInstance = this
    }

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()

    private var mLogoutWarningDialog: BaseCustomDialog<DialogAlertBinding>? = null

    private var mVotesAdapter = GroupAdapter<GroupieViewHolder>()

    /**
     * Name of the connected device
     */
    private var mConnectedDeviceName: String? = null

    /**
     * Member object for the chat services
     */
    private var mChatService: BluetoothChatService? = null

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_user_home
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
        mainBinding.rvVotes.adapter = mVotesAdapter
        setLatestData()
        mChatService = BluetoothChatService(activity, mHandler)
    }

    private fun setLatestData() {
        mPrefProvider.getUserVotesList()?.let { allVotesList ->
            if (allVotesList.isNotEmpty()) {
                val myVotesList = mutableListOf<VoteModel>()
                for (voteModel in allVotesList) {
                    if (voteModel.user_ids.contains(mPrefProvider.getUser()!!.id)) {
                        myVotesList.add(voteModel)
                    }
                }
                if (myVotesList.isNotEmpty()) {
                    setData(myVotesList)
                }
            }
        }
    }

    private fun setData(allVotesList: MutableList<VoteModel>) {
        mVotesAdapter.clear()
        mainBinding.tvNoDataFound.visibility = View.GONE
        mainBinding.rvVotes.visibility = View.VISIBLE
        mainBinding.rvVotes.adapter = mVotesAdapter
        mVotesAdapter.addAll(allVotesList.toVoteItems())
    }

    private fun setNoData() {
        mPrefProvider.setUserVotesList(mutableListOf())
        mVotesAdapter.clear()
        mainBinding.tvNoDataFound.visibility = View.VISIBLE
        mainBinding.rvVotes.visibility = View.GONE
    }

    private fun List<VoteModel>.toVoteItems(): List<BaseItem<VoteModel, ItemVoteBinding>> {
        return this.map {
            BaseItem(
                R.layout.item_vote,
                it,
                object : BaseItem.OnItemClickListener<VoteModel> {
                    override fun onItemClick(view: View, model: VoteModel, position: Int) {
                        findNavController().navigate(
                            UserHomeFragmentDirections.actionUserHomeFragmentToVoteDetailsFragment(
                                model
                            )
                        )
                    }
                }
            )
        }
    }

    private val baseCallback = BaseCallback {
        if (it.id == R.id.img_logout) {
            showAlertDialog()
        }
    }

    private fun VoteModel.toVoteItem(): BaseItem<VoteModel, ItemVoteBinding> {
        return BaseItem(
            R.layout.item_vote,
            this,
            object : BaseItem.OnItemClickListener<VoteModel> {
                override fun onItemClick(view: View, model: VoteModel, position: Int) {
                    findNavController().navigate(
                        UserHomeFragmentDirections.actionUserHomeFragmentToVoteDetailsFragment(
                            model
                        )
                    )
                }
            }
        )
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
                ConstantsJava.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName))
                        mainBinding.tvConnectionStatus.text =
                            "Connection : Connected (${mConnectedDeviceName})"
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
                ConstantsJava.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    setLatestData()
                }
                ConstantsJava.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    if (readMessage.isNotEmpty()) {
                        val voteModel = Gson().fromJson(readMessage, VoteModel::class.java)
                        if (voteModel.id != 0) {
                            insertNewVoteToAdapter(voteModel)
                        } else {
                            val adminAllVotesModel =
                                Gson().fromJson(readMessage, AdminAllVotesModel::class.java)
                            if (adminAllVotesModel != null && adminAllVotesModel.admin_al_votes.isNotEmpty()) {
                                for (adminVoteModel in adminAllVotesModel.admin_al_votes) {
                                    if (!adminVoteModel.user_ids.contains(mPrefProvider.getUser()!!.id)) {
                                        adminVoteModel.user_ids.add(mPrefProvider.getUser()!!.id)
                                    }
                                }
                                mPrefProvider.setUserVotesList(adminAllVotesModel.admin_al_votes)
                                setData(adminAllVotesModel.admin_al_votes)
                            } else {
                                setNoData()
                            }
                        }
                    }
                }
                ConstantsJava.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(ConstantsJava.DEVICE_NAME)
                    if (null != activity) {
                        Toast.makeText(
                            activity, "Connected to "
                                    + mConnectedDeviceName, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ConstantsJava.MESSAGE_TOAST -> if (null != activity) {
                    Toast.makeText(
                        activity, msg.data.getString(ConstantsJava.TOAST),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun insertNewVoteToAdapter(voteModel: VoteModel) {
        voteModel.let { finalVoteModel ->
            finalVoteModel.user_ids.add(mPrefProvider.getUser()!!.id)
            if (mPrefProvider.getUserVotesList() != null) {
                mPrefProvider.getUserVotesList()?.let { listOfVotes ->
                    listOfVotes.add(finalVoteModel)
                    mPrefProvider.setUserVotesList(listOfVotes)
                }
            } else {
                val listOfVotes = mutableListOf<VoteModel>()
                listOfVotes.add(finalVoteModel)
                mPrefProvider.setUserVotesList(listOfVotes)
            }
            setLatestData()
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
}