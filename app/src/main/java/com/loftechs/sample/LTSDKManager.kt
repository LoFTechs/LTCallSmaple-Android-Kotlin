package com.loftechs.sample

import com.loftechs.sample.model.AccountHelper
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.LTSDKOptions
import com.loftechs.sdk.call.LTCallCenterManager
import com.loftechs.sdk.extension.rx.cleanData
import com.loftechs.sdk.extension.rx.deletePrimaryUser
import com.loftechs.sdk.extension.rx.init
import com.loftechs.sdk.http.response.LTResponse
import com.loftechs.sdk.permission.LTPermissionManager
import com.loftechs.sdk.storage.LTStorageManager
import com.loftechs.sdk.utils.LTLog
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

object LTSDKManager {
    private val TAG = LTSDKManager::class.java.simpleName
    private var mSDK: LTSDK? = null
    private var userDataReady = false

    fun getStorageManager(receiverID: String): Observable<LTStorageManager> {
        return sdkObservable
            .map { sdk: LTSDK -> sdk.getStorageManager(receiverID) }
    }

    fun getCallCenterManager(): Observable<LTCallCenterManager> {
        return sdkObservable
            .map { sdk: LTSDK -> sdk.getCallManager() }
    }

    fun getPermissionManager(): Observable<LTPermissionManager> {
        return sdkObservable
            .map {
                it.getPermissionManager()
            }
    }

    fun resetSDK(): Observable<Boolean> {
        return LTSDK.cleanData()
    }

    val sdkObservable: Observable<LTSDK>
        get() {
            if (mSDK == null || !userDataReady) {
                AccountHelper.firstAccount?.let {
                    val options = LTSDKOptions.builder()
                        .context(SampleApp.context)
                        .url(BuildConfig.Auth_API)
                        .licenseKey(BuildConfig.License_Key)
                        .userID(it.userID)
                        .uuid(it.uuid)
                        .build()
                    return LTSDK.init(options)
                        .subscribeOn(Schedulers.newThread())
                        .map { aBoolean: Boolean ->
                            LTLog.i(TAG, "getLTSDK init: $aBoolean")
                            if (aBoolean && it.uuid.isNotEmpty()) {
                                userDataReady = true
                            }
                            mSDK = LTSDK
                            mSDK
                        }
                } ?: return Observable.error(Throwable("$TAG error: no user in sample app"))
            }
            return Observable.just(mSDK)
        }

    fun deletePrimaryUser(): Observable<LTResponse> {
        return LTSDK.deletePrimaryUser()
            .doOnNext {
                Timber.tag(TAG).d("deletePrimaryUser ++ success: $it")
            }
            .doOnError {
                Timber.tag(TAG).e("deletePrimaryUser ++ error: $it")
            }
    }
}