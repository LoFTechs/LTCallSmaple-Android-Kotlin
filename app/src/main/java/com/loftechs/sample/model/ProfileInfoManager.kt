package com.loftechs.sample.model

import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.model.data.ProfileInfoEntity
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object ProfileInfoManager {
    private const val EXPIRE_DAY = 1L

    fun getProfileInfoByUserID(
        receiverID: String,
        userID: String,
        number: String
    ): Observable<ProfileInfoEntity> {
        return Observable.just(
            getLocalProfile(userID) ?: ProfileInfoEntity(
                userID,
                number,
                "",
                null,
                System.currentTimeMillis()
            )
        )
    }

    fun getLocalAllProfileInfo(): ArrayList<ProfileInfoEntity> {
        return getAllLocalProfile()
    }

    fun cleanProfileInfoByID(id: String) {
        ProfileHelper.cleanProfileEntity(id)
    }

    fun updateProfileInfo(profileInfoEntity: ProfileInfoEntity) {
        ProfileHelper.setProfileEntity(profileInfoEntity)
    }

    private fun getLocalProfile(userIDorChatID: String): ProfileInfoEntity? {
        val profileInfoEntity = ProfileHelper.getProfileEntity(userIDorChatID)
        return (profileInfoEntity?.let {
            if (!isExpire(it.updateTime)) {
                logDebug("[local]getLocalProfile $userIDorChatID : $it")
                it
            } else {
                null
            }
        } ?: run {
            null
        })
    }

    private fun getAllLocalProfile(): ArrayList<ProfileInfoEntity> {
        return ProfileHelper.getAllProfileEntity()
    }

    private fun isExpire(time: Long): Boolean {
        return System.currentTimeMillis() > (TimeUnit.DAYS.toMillis(EXPIRE_DAY) + time)
    }
}