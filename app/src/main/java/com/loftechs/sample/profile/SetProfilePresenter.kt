package com.loftechs.sample.profile

import android.content.Intent
import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.common.IntentKey.EXTRA_IS_FROM_SETTING_PAGE
import com.loftechs.sample.common.IntentKey.EXTRA_NICKNAME
import com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.model.AccountHelper
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sample.utils.FileUtil
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class SetProfilePresenter : SetProfileContract.Presenter<SetProfileContract.View> {

    private var mView: SetProfileContract.View? = null

    private var mReceiverID: String = ""
    private var mNickname: String? = null
    private var bIsFromSettingPage: Boolean = false

    private val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    companion object {
        private val TAG = SetProfilePresenter::class.java.simpleName
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(EXTRA_RECEIVER_ID, "")
        mNickname = arguments.getString(EXTRA_NICKNAME)
        bIsFromSettingPage = arguments.getBoolean(EXTRA_IS_FROM_SETTING_PAGE, false)
        arguments.remove(EXTRA_NICKNAME)
        arguments.remove(EXTRA_IS_FROM_SETTING_PAGE)
    }

    override fun create() {
    }

    override fun resume() {
        loadAvatar()
        mNickname?.let {
            mView?.setNicknameText(it)
        }
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: SetProfileContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun setNickname(nickname: String) {
        if (nickname.isEmpty()) {
            mView?.showErrorDialog(R.string.set_profile_set_nickname_empty)
            return
        }
        AccountHelper.setSelfNickname(nickname)
        val profileInfoEntity =
            ProfileInfoEntity(mReceiverID, nickname, "", null, System.currentTimeMillis())
        ProfileInfoManager.updateProfileInfo(profileInfoEntity)
        if (bIsFromSettingPage) {
            mView?.dismissFragment()
        } else {
            mView?.gotoMainFragment()
        }
    }

    override fun setProfileImage(data: Intent?) {
        data?.data?.let { uri ->
            logDebug("setProfileImage uri : $uri")
        }
    }

    private fun loadAvatar() {
        mView?.loadAvatar(if (hasAvatar()) getSelfAvatarFile() else null)
    }

    private fun getSelfAvatarFile(): File {
        return FileUtil.getProfileFile("$mReceiverID.jpg", false)
    }

    override fun hasAvatar(): Boolean {
        return getSelfAvatarFile().length() > 0
    }

    override fun deleteAvatar() {
        logDebug("deleteAvatar")
    }
}