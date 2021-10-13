package io.github.fastkvdemo.application

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import com.tencent.mmkv.MMKV
import io.fastkv.FastKV
import io.fastkv.FastKVConfig
import io.github.fastkvdemo.account.UserData
import io.github.fastkvdemo.fastkv.FastKVLogger
import io.github.fastkvdemo.manager.PathManager.fastKVDir
import io.github.fastkvdemo.storage.CommonStoreV2
import io.github.fastkvdemo.util.ChannelExecutorService
import io.github.fastkvdemo.util.runBlock
import kotlinx.coroutines.channels.Channel

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalConfig.appContext = this

        // filter other processes,
        // in case files damaged in multiple processes mode
        if(GlobalConfig.APPLICATION_ID == getProcessName(this)) {
            init()
            initMMKV()
        }
    }

    private fun init() {
        FastKVConfig.setLogger(FastKVLogger)
        FastKVConfig.setExecutor(ChannelExecutorService(4))
        preload()
    }

    private fun initMMKV(){
        /*
        val dir = filesDir.absolutePath + "/mmkv"
        val rootDir = MMKV.initialize(this, dir, {
                libName -> ReLinker.loadLibrary(this@MyApplication, libName)
            }, MMKVLogLevel.LevelInfo
        )*/
        val rootDir = MMKV.initialize(this)
        Log.i("MMKV", "mmkv root: $rootDir")
    }

    private fun preload() {
        Channel<Any>(1).runBlock {
            CommonStoreV2.kv
            UserData.kv
            FastKV.Builder(fastKVDir, "fastkv").build()
        }
    }

    private fun getProcessName(context: Context): String? {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        am?.runningAppProcesses?.let {
            val curPid = Process.myPid()
            for (runningApp in it) {
                if (runningApp.pid == curPid) {
                    return runningApp.processName
                }
            }
        }
        Log.e("AppApplication", "Get ActivityManager service failed.")
        return GlobalConfig.APPLICATION_ID
    }
}

