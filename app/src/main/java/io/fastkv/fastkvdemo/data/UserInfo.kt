package io.fastkv.fastkvdemo.data

import android.util.ArrayMap
import io.fastkv.fastkvdemo.account.AccountInfo
import io.fastkv.fastkvdemo.account.Gender
import io.fastkv.fastkvdemo.base.AppContext
import io.fastkv.fastkvdemo.fastkv.LongListEncoder
import io.fastkv.fastkvdemo.fastkv.kvbase.UserKV
import io.fastkv.interfaces.FastEncoder

/**
 * 用户信息
 */
class UserInfo(uid: Long): UserKV("user_info", uid) {
    companion object {
        private val map = ArrayMap<Long, UserInfo>()

        @Synchronized
        fun get(): UserInfo {
            return get(AppContext.uid)
        }

        @Synchronized
        fun get(uid: Long): UserInfo {
            return map.getOrPut(uid) {
                UserInfo(uid)
            }
        }
    }

    override fun encoders(): Array<FastEncoder<*>> {
        return arrayOf(AccountInfo.ENCODER, LongListEncoder)
    }

    var userAccount by obj("user_account", AccountInfo.ENCODER)
    var gender by intEnum("gender", Gender.CONVERTER)
    var isVip by boolean("is_vip")
    var fansCount by int("fans_count")
    var score by float("score")
    var loginTime by long("login_time")
    var balance by double("balance")
    var sign by string("sing")
    var lock by array("lock")
    var tags by stringSet("tags")
    var friendIdList by obj("favorite_channels", LongListEncoder)
    val favorites by string2Set("favorites")
    val config by combineKey("config")
}
