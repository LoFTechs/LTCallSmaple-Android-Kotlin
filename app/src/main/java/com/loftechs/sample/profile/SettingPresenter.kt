package com.loftechs.sample.profile

import android.os.Bundle
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.model.PreferenceSetting
import com.loftechs.sample.profile.SettingItemType.ITEM_SETTING_DISPLAY_CONTENT
import com.loftechs.sample.profile.SettingItemType.ITEM_SETTING_DISPLAY_SENDER
import com.loftechs.sample.profile.SettingItemType.ITEM_SETTING_MUTE
import com.loftechs.sample.utils.FileUtil
import io.reactivex.disposables.CompositeDisposable

class SettingPresenter : SettingContract.Presenter<SettingContract.View> {

    private var mView: SettingContract.View? = null

    private lateinit var mReceiverID: String

    private val mSettingList: ArrayList<SettingItemType> by lazy {
        arrayListOf(ITEM_SETTING_MUTE, ITEM_SETTING_DISPLAY_SENDER, ITEM_SETTING_DISPLAY_CONTENT)
    }

    private val mDisposable by lazy {
        CompositeDisposable()
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
    }

    override fun create() {
    }

    override fun resume() {
        refreshAvatar()
        refreshNickname()
        refreshSetting()
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: SettingContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun getSetProfileBundle(bundle: Bundle): Bundle {
        return bundle.apply {
            putString(IntentKey.EXTRA_NICKNAME, PreferenceSetting.nickname)
            putBoolean(IntentKey.EXTRA_IS_FROM_SETTING_PAGE, true)
        }
    }

    private fun refreshAvatar() {
        val avatarFile = FileUtil.getProfileFile("$mReceiverID.jpg", false)
        if (avatarFile.length() == 0L) {
            mView?.setDefaultAvatarView()
        } else {
            mView?.setAvatarView(avatarFile)
        }
    }

    private fun refreshNickname() {
        mView?.setNicknameText(PreferenceSetting.nickname)
    }

    private fun refreshSetting() {
        for (settingItemType in mSettingList) {
            settingItemType.setTitleText(mView)
        }
    }

    override fun setMute(enable: Boolean) {
        logDebug("setMute")
    }

    override fun enableNotificationDisplay(showSender: Boolean, showContent: Boolean) {
        logDebug("enableNotificationDisplay")
    }
}