package com.example.bluetoothlechat.ui.user

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.base.BaseCallback
import com.example.bluetoothlechat.base.BaseFragment
import com.example.bluetoothlechat.base.BaseItem
import com.example.bluetoothlechat.bluetooth.BluetoothChatService
import com.example.bluetoothlechat.databinding.FragmentVoteDetailsBinding
import com.example.bluetoothlechat.databinding.ItemVoteOptionBinding
import com.example.bluetoothlechat.models.UserModel
import com.example.bluetoothlechat.models.VoteModel
import com.example.bluetoothlechat.models.VoteOptionModel
import com.example.bluetoothlechat.ui.admin.CreateVoteFragmentArgs
import com.example.bluetoothlechat.utils.ConstantsJava
import com.example.bluetoothlechat.utils.PreferenceProvider
import com.example.bluetoothlechat.utils.showMessageDialog
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class VoteDetailsFragment : BaseFragment<FragmentVoteDetailsBinding>(), KodeinAware {

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()

    private lateinit var mVoteModel: VoteModel

    /**
     * Member object for the chat services
     */
    private var mChatService: BluetoothChatService? = null

    private var mVoteOptionsAdapter = GroupAdapter<GroupieViewHolder>()

    override fun getFragmentLayout(): Int = R.layout.fragment_vote_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.vote_details)
        setBaseCallback(baseCallback)
        binding.rvVotes.adapter = mVoteOptionsAdapter

        val args = arguments?.let { CreateVoteFragmentArgs.fromBundle(it) }
        args?.let { it ->
            it.voteModel?.let {
                mVoteModel = it

                binding.etTitle.setText(it.vote_title)
                binding.etQuestion.setText(it.description)
                binding.etTitle.isEnabled = false
                binding.etQuestion.isEnabled = false

                mVoteOptionsAdapter.addAll(it.options.toVoteOptionItems())
            }
        }
        mChatService = BluetoothChatService(activity, mHandler)
    }

    private fun List<VoteOptionModel>.toVoteOptionItems(): List<BaseItem<VoteOptionModel, ItemVoteOptionBinding>> {
        return this.map {
            BaseItem(
                R.layout.item_vote_option,
                it,
                object : BaseItem.OnItemClickListener<VoteOptionModel> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onItemClick(view: View, model: VoteOptionModel, position: Int) {
                        if (mVoteModel.voted_by_list.contains(mPrefProvider.getUser()!!.id)) {
                            requireContext().showMessageDialog("You have already voted.")
                        } else {
                            for (index in 0 until mVoteOptionsAdapter.itemCount) {
                                val optionItem =
                                    mVoteOptionsAdapter.getItem(index) as BaseItem<*, *>
                                (optionItem.model as VoteOptionModel).selected = false
                            }
                            model.selected = true
                            mVoteOptionsAdapter.notifyDataSetChanged()
                        }
                    }
                }
            )
        }
    }

    private val baseCallback = BaseCallback {
        when (it.id) {
            R.id.img_back -> goBack()
            R.id.tv_submit -> {
                mPrefProvider.getUser()?.let { userModel ->
                    if (mVoteModel.voted_by_list.contains(userModel.id)) {
                        requireContext().showMessageDialog("You have already voted.")
                    } else {
                        if (validate()) {
                            mVoteModel.voted_by_list.add(userModel.id)
                            val modifiedOptionsList = mutableListOf<VoteOptionModel>()
                            for (index in 0 until mVoteOptionsAdapter.itemCount) {
                                val optionItem =
                                    mVoteOptionsAdapter.getItem(index) as BaseItem<*, *>
                                val voteOptionModel = optionItem.model as VoteOptionModel
                                if (voteOptionModel.selected) {
                                    if (!voteOptionModel.selected_users_id.contains(mPrefProvider.getUser()!!.id)) {
                                        voteOptionModel.selected_users_id.add(mPrefProvider.getUser()!!.id)
                                    }
                                }
                                modifiedOptionsList.add(voteOptionModel)
                            }
                            mVoteModel.options = modifiedOptionsList
                            UserHomeFragment.mInstance?.sendMessage(Gson().toJson(mVoteModel))

                            voteSubmittedSuccessfully(userModel)
                        }
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        for (index in 0 until mVoteOptionsAdapter.itemCount) {
            val optionItem = mVoteOptionsAdapter.getItem(index) as BaseItem<*, *>
            if ((optionItem.model as VoteOptionModel).selected) {
                return true
            }
        }

        return false
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            val activity = activity
            when (msg.what) {
                ConstantsJava.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
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

    private fun voteSubmittedSuccessfully(userModel: UserModel) {

        val listOfStoredVotes = mPrefProvider.getUserVotesList()
        listOfStoredVotes?.let {
            for (voteModel in listOfStoredVotes.withIndex()) {
                if (voteModel.value.id == mVoteModel.id) {
                    listOfStoredVotes[voteModel.index].voted_by_list.add(userModel.id)
                }
            }
            mPrefProvider.setUserVotesList(listOfStoredVotes)
        }
        binding.rlWholeView.visibility = View.GONE
        binding.rlViewLottie.visibility = View.VISIBLE

        binding.lottieAnimation.speed = 0.5F
        binding.lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                Timber.e("Animation:", "start")
            }

            override fun onAnimationEnd(animation: Animator?) {
                binding.tvMessage.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
                Timber.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator?) {
                Timber.e("Animation:", "repeat")
            }
        })
        binding.lottieAnimation.playAnimation()
    }

}