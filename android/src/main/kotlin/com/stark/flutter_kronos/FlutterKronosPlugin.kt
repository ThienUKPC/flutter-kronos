package com.stark.flutter_kronos

import androidx.annotation.NonNull
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

import android.util.Log
import android.content.Context

/** FlutterKronosPlugin */
class FlutterKronosPlugin : MethodCallHandler, FlutterPlugin, ActivityAware {

    private var kronosClock: KronosClock? = null
    private var channel: MethodChannel? = null

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "SYNC" -> {
                kronosClock?.syncInBackground()
            }
            "GET_CURRENT_TIME_MS" -> {
                result.success(kronosClock?.getCurrentTimeMs())
            }
            "GET_CURRENT_NTP_TIME_MS" -> {
                result.success(kronosClock?.getCurrentNtpTimeMs())
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        val messenger = binding.binaryMessenger
        channel = MethodChannel(messenger, "flutter_kronos")
        channel?.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        kronosClock = AndroidClockFactory.createKronosClock(binding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        kronosClock = null
    }
}
