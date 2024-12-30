package com.example.bluetoothlechat.ui.admin

import android.animation.Animator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.base.BaseCallback
import com.example.bluetoothlechat.base.BaseCustomDialog
import com.example.bluetoothlechat.base.BaseFragment
import com.example.bluetoothlechat.base.BaseItem
import com.example.bluetoothlechat.bluetooth.BluetoothChatService
import com.example.bluetoothlechat.databinding.DialogDeleteVoteBinding
import com.example.bluetoothlechat.databinding.FragmentCreateVoteBinding
import com.example.bluetoothlechat.databinding.ItemVoteOptionBinding
import com.example.bluetoothlechat.models.AdminAllVotesModel
import com.example.bluetoothlechat.models.VoteModel
import com.example.bluetoothlechat.models.VoteOptionModel
import com.example.bluetoothlechat.utils.FieldsValidator
import com.example.bluetoothlechat.utils.PreferenceProvider
import com.example.bluetoothlechat.utils.showMessageDialog
import com.example.bluetoothlechat.utils.showToast
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber
import java.util.*

class CreateVoteFragment : BaseFragment<FragmentCreateVoteBinding>(), KodeinAware {

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()

    private val mFieldsValidator: FieldsValidator by instance()

    private var mVoteOptionsAdapter = GroupAdapter<GroupieViewHolder>()

    private var mDeleteVoteWarningDialog: BaseCustomDialog<DialogDeleteVoteBinding>? = null

    private var mVoteModel: VoteModel? = null

    /**
     * Member object for the chat services
     */
    private var mChatService: BluetoothChatService? = null

    override fun getFragmentLayout(): Int = R.layout.fragment_create_vote

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
        binding.toolbar.tvTitle.text = getString(R.string.create_vote)
        setBaseCallback(baseCallback)
        binding.rvVotes.adapter = mVoteOptionsAdapter

        val args = arguments?.let { CreateVoteFragmentArgs.fromBundle(it) }
        args?.let { it ->
            it.voteModel?.let {
                mVoteModel = it
                binding.toolbar.tvTitle.text = getString(R.string.vote_results)
                binding.etTitle.setText(it.vote_title)
                binding.etTitle.isEnabled = false
                binding.etQuestion.setText(it.description)
                binding.etQuestion.isEnabled = false
                binding.tvLabelAddOption.visibility = View.GONE
                binding.fbCreateOption.visibility = View.GONE
                binding.tvAction.visibility = View.GONE
                binding.tvDeleteVote.visibility = View.VISIBLE
                for (optionModel in it.options) {
                    optionModel.need_to_view_count = true
                }
                mVoteOptionsAdapter.addAll(it.options.toVoteOptionItems())
            }
        }
        /*mChatService = BluetoothChatService(activity, mHandler)*/
    }

    private fun VoteOptionModel.toVoteOptionItem(): BaseItem<VoteOptionModel, ItemVoteOptionBinding> {
        return BaseItem(
            R.layout.item_vote_option,
            this,
            object : BaseItem.OnItemClickListener<VoteOptionModel> {
                override fun onItemClick(view: View, model: VoteOptionModel, position: Int) {

                }
            }
        )
    }

    private fun List<VoteOptionModel>.toVoteOptionItems(): List<BaseItem<VoteOptionModel, ItemVoteOptionBinding>> {
        return this.map {
            BaseItem(
                R.layout.item_vote_option,
                it,
                object : BaseItem.OnItemClickListener<VoteOptionModel> {
                    override fun onItemClick(view: View, model: VoteOptionModel, position: Int) {

                    }
                }
            )
        }
    }

    private val baseCallback = BaseCallback {
        when (it.id) {
            R.id.img_back -> goBack()
            R.id.fb_create_option -> {
                binding.fbCreateOption.visibility = View.GONE
                binding.llAddOption.visibility = View.VISIBLE
            }
            R.id.btn_add_option -> {
                if (mFieldsValidator.hasText(binding.etOption)) {
                    val voteOptionModel = VoteOptionModel(
                        name = binding.etOption.text.toString()
                    )
                    mVoteOptionsAdapter.add(voteOptionModel.toVoteOptionItem())
                    binding.etOption.setText("")
                    binding.fbCreateOption.visibility = View.VISIBLE
                    binding.llAddOption.visibility = View.GONE
                }
            }
            R.id.tv_action -> {
                if (validateFields()) {
                    val model = VoteModel()
                    model.id = (1..1000).random()
                    model.vote_title = binding.etTitle.text.toString()
                    model.description = binding.etQuestion.text.toString()

                    val listOfOptions = mutableListOf<VoteOptionModel>()
                    for (option in 0 until mVoteOptionsAdapter.itemCount) {
                        val optionItem = mVoteOptionsAdapter.getItem(option) as BaseItem<*, *>
                        listOfOptions.add(optionItem.model as VoteOptionModel)
                    }
                    model.options = listOfOptions

                    if (mPrefProvider.getAdminVotesList() != null) {
                        mPrefProvider.getAdminVotesList()?.let { listOfVotes ->
                            listOfVotes.add(model)
                            mPrefProvider.setAdminVotesList(listOfVotes)
                        }
                    } else {
                        val listOfVotes = mutableListOf<VoteModel>()
                        listOfVotes.add(model)
                        mPrefProvider.setAdminVotesList(listOfVotes)
                    }

                    AdminHomeFragment.mInstance?.sendMessage(
                        Gson().toJson(model)
                    )

                    voteSubmittedSuccessfully()

                }
            }
            R.id.tv_delete_vote -> showAlertDialog()
        }
    }

    private fun validateFields(): Boolean {
        if (mVoteOptionsAdapter.itemCount == 0) {
            requireContext().showMessageDialog("Please add options")
            return false
        }

        return mFieldsValidator.hasText(binding.etTitle) &&
                mFieldsValidator.hasText(binding.etQuestion)
    }

    private fun voteSubmittedSuccessfully() {
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

    private fun showAlertDialog() {
        mDeleteVoteWarningDialog = BaseCustomDialog(
            requireContext(),
            R.layout.dialog_delete_vote,
            object : BaseCustomDialog.DialogListener {
                override fun onViewClick(view: View?) {
                    view?.let {
                        when (it.id) {
                            R.id.btn_cancel -> mDeleteVoteWarningDialog?.dismiss()
                            R.id.btn_ok -> {
                                mDeleteVoteWarningDialog?.dismiss()
                                deleteVoteFromLocalSavedList()
                            }
                            else -> mDeleteVoteWarningDialog?.dismiss()
                        }
                    }
                }
            })

        Objects.requireNonNull<Window>(mDeleteVoteWarningDialog?.window).setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        mDeleteVoteWarningDialog?.setCancelable(true)
        mDeleteVoteWarningDialog?.show()
    }

    private fun deleteVoteFromLocalSavedList() {
        mVoteModel?.let { currentVoteModel ->
            val finalLocalVotes = mPrefProvider.getAdminVotesList()
            finalLocalVotes?.let {
                val listOfLocalVotes = mPrefProvider.getAdminVotesList()
                listOfLocalVotes?.let {
                    for (voteModel in listOfLocalVotes.withIndex()) {
                        if (voteModel.value.id == currentVoteModel.id) {
                            finalLocalVotes.removeAt(voteModel.index)
                        }
                    }
                }

                mPrefProvider.setAdminVotesList(finalLocalVotes)
                val adminAllVotesModel = AdminAllVotesModel()
                adminAllVotesModel.admin_al_votes = finalLocalVotes
                AdminHomeFragment.mInstance?.sendMessage(Gson().toJson(adminAllVotesModel))
                requireContext().showToast("Vote delete successfully")
                goBack()
            }
        }
    }

}