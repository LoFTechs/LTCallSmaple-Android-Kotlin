package com.loftechs.sample.main

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Bundle
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sample.common.IntentKey
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber

class MainPresenter : MainContract.Presenter<MainContract.View> {

    private var mView: MainContract.View? = null

    private lateinit var mReceiverID: String
    private val mFragmentTypeList: ArrayList<MainItemType> by lazy {
        arrayListOf(
            MainItemType.CALL
        )
    }

    override val tabItemCount: Int
        get() = 1

    companion object {
        private val TAG = MainPresenter::class.java.simpleName
    }

    override fun create() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
    }

    override fun bindView(view: MainContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
    }

    override fun logout() {
        LTSDKManager.resetSDK()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Boolean) {
                    Timber.tag(TAG).d("logoff status : $result")
                }

                override fun onError(e: Throwable) {
                    Timber.tag(TAG).e("logoff error e : $e")
                }

                override fun onComplete() {
                    (SampleApp.context.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
                        .clearApplicationUserData()
                }
            })
    }

    override fun onFabClick(currentItem: Int) {
        val mainItemType = mFragmentTypeList[currentItem]
        mainItemType.onFabClick(mView)
    }

    override fun getTabStringResourceID(position: Int): Int {
        return R.string.main_tab_item_call
    }

    override fun getFabIconResource(position: Int): Int {
        return when (position) {
            0 -> {
                R.drawable.ic_action_new_call
            }

            else -> {
                0
            }
        }
    }

    override fun getTabItemType(position: Int): MainItemType {
        return mFragmentTypeList[position]
    }

    override fun secureCheck() {
    }
}