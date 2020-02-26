package com.dowen.android_settings_menu

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import android.content.Context
import android.app.NotificationManager
import android.os.Build
import android.content.Context.POWER_SERVICE
import android.os.PowerManager
import android.net.Uri

import com.thelittlefireman.appkillermanager.managers.KillerManager;





class AndroidSettingsMenuPlugin(activity: Activity): MethodCallHandler {

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "com.dowen.android_settings_menu")
      if(registrar == null || registrar.activity() == null) // Prevent background service registrar (Alarm manager)
          return;
      channel.setMethodCallHandler(AndroidSettingsMenuPlugin(registrar.activity()))
    }
  }

  val _activity :Activity = activity;

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }
    else if(call.method == "setting.checkMenu")
    {
        val nameOfMenu = call.argument<String>("name")
        if(nameOfMenu == null)
            result.error("NAME", "Name is undefined.", null)
        android.util.Log.w("nameOfMenu", "${nameOfMenu}");
        val systemName = retrieveSettingsURI(nameOfMenu.toString())
        android.util.Log.w("systemName", "${systemName}");
        if(systemName == android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val mNotificationManager = _activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                // Check if the notification policy access has been granted for the app.
                return result.success(mNotificationManager.isNotificationPolicyAccessGranted)
            }
            return result.success(true)
        }
        else if(systemName == android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = _activity.getPackageName()
                val mPowerManager = _activity.getSystemService(POWER_SERVICE) as PowerManager
                // ---- If ignore go to settings, else request ----
                return result.success(mPowerManager.isIgnoringBatteryOptimizations(packageName))
            }
            return result.success(true)
        }
        else if(systemName == android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = _activity.getPackageName()
                val mPowerManager = _activity.getSystemService(POWER_SERVICE) as PowerManager
                // ---- If ignore go to settings, else request ----
                return result.success(android.provider.Settings.canDrawOverlays(_activity.applicationContext))
            }
            return result.success(true)
        }
        return result.success(true)
    }
    else if(call.method == "setting.openMenu")
    {
        val nameOfMenu = call.argument<String>("name")
        if(nameOfMenu == null || nameOfMenu == "")
            result.error("NAME", "Name is undefined.", null)
        val systemName = retrieveSettingsURI(nameOfMenu.toString())

        //android.util.Log.w("1", "1");
        if(systemName == android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        {
            //android.util.Log.w("2", "2");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //android.util.Log.w("3", "3");
                val mNotificationManager = _activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                // Check if the notification policy access has been granted for the app.
                if (!mNotificationManager.isNotificationPolicyAccessGranted) {
                    //android.util.Log.w("4", "4");
                    val intent = Intent(systemName)
                    _activity.startActivity(intent)
                    return result.success(true)
                }
            }
            return result.success(true)
        }
        else if(systemName == android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        {
            //android.util.Log.w("2", "2");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(systemName)
                _activity.startActivity(intent)
                return result.success(true)
            }
            return result.success(true)
        }
        else if(systemName == android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS)
        {
            //android.util.Log.w("2", "2");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(systemName)
                _activity.startActivity(intent)
                return result.success(true)
            }
            return result.success(true)
        }
        else if(systemName == android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + _activity.packageName)
            _activity.startActivity(intent)
        }
        else if(systemName == android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                /*val intent = Intent(systemName)
                intent.setData(Uri.parse("package:" + _activity.packageName));
                _activity.startActivity(intent)*/
                KillerManager.doActionPowerSaving(_activity.applicationContext);
                return result.success(true)
            }
            return result.success(true)
        }
        else if(systemName == android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        {
            //android.util.Log.w("2", "2");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(systemName)
                _activity.startActivity(intent)
                return result.success(true)
            }
            return result.success(true)
        }
        else {
            //android.util.Log.w("5", "5");
            val intent = Intent(systemName);
            _activity.startActivity(intent);
            result.success(true);
        }
    }
    else {
        result.error("NO IMPLEMENTED", "NO IMPLEMENTED", null)
    }
  }

  private fun retrieveSettingsURI(settingsName: String): String {
    var settingsURI = ""
    when (settingsName) {

      "ACTION_ACCESSIBILITY_SETTINGS" -> settingsURI = Settings.ACTION_ACCESSIBILITY_SETTINGS
      "ACTION_ADD_ACCOUNT" -> settingsURI = Settings.ACTION_ADD_ACCOUNT
      "ACTION_AIRPLANE_MODE_SETTINGS" -> settingsURI = Settings.ACTION_AIRPLANE_MODE_SETTINGS
      "ACTION_APN_SETTINGS" -> settingsURI = Settings.ACTION_APN_SETTINGS
      "ACTION_APPLICATION_DETAILS_SETTINGS" -> settingsURI = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
      "ACTION_APPLICATION_DEVELOPMENT_SETTINGS" -> settingsURI = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
      "ACTION_APPLICATION_SETTINGS" -> settingsURI = Settings.ACTION_APPLICATION_SETTINGS
      "ACTION_APP_NOTIFICATION_SETTINGS" -> settingsURI = Settings.ACTION_APP_NOTIFICATION_SETTINGS
      "ACTION_BATTERY_SAVER_SETTINGS" -> settingsURI = Settings.ACTION_BATTERY_SAVER_SETTINGS
      "ACTION_BLUETOOTH_SETTINGS" -> settingsURI = Settings.ACTION_BLUETOOTH_SETTINGS
      "ACTION_CAPTIONING_SETTINGS" -> settingsURI = Settings.ACTION_CAPTIONING_SETTINGS
      "ACTION_CAST_SETTINGS" -> settingsURI = Settings.ACTION_CAST_SETTINGS
      "ACTION_CHANNEL_NOTIFICATION_SETTINGS" -> settingsURI = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
      "ACTION_DATA_ROAMING_SETTINGS" -> settingsURI = Settings.ACTION_DATA_ROAMING_SETTINGS
      "ACTION_DATE_SETTINGS" -> settingsURI = Settings.ACTION_DATE_SETTINGS
      "ACTION_DEVICE_INFO_SETTINGS" -> settingsURI = Settings.ACTION_DEVICE_INFO_SETTINGS
      "ACTION_DISPLAY_SETTINGS" -> settingsURI = Settings.ACTION_DISPLAY_SETTINGS
      "ACTION_DREAM_SETTINGS" -> settingsURI = Settings.ACTION_DREAM_SETTINGS
      "ACTION_HARD_KEYBOARD_SETTINGS" -> settingsURI = Settings.ACTION_HARD_KEYBOARD_SETTINGS
      "ACTION_HOME_SETTINGS" -> settingsURI = Settings.ACTION_HOME_SETTINGS
      "ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS" -> settingsURI = Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS
      "ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS" -> settingsURI = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
      "ACTION_INPUT_METHOD_SETTINGS" -> settingsURI = Settings.ACTION_INPUT_METHOD_SETTINGS
      "ACTION_INPUT_METHOD_SUBTYPE_SETTINGS" -> settingsURI = Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS
      "ACTION_INTERNAL_STORAGE_SETTINGS" -> settingsURI = Settings.ACTION_INTERNAL_STORAGE_SETTINGS
      "ACTION_LOCALE_SETTINGS" -> settingsURI = Settings.ACTION_LOCALE_SETTINGS
      "ACTION_LOCATION_SOURCE_SETTINGS" -> settingsURI = Settings.ACTION_LOCATION_SOURCE_SETTINGS
      "ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS" -> settingsURI = Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS
      "ACTION_MANAGE_APPLICATIONS_SETTINGS" -> settingsURI = Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
      "ACTION_MANAGE_DEFAULT_APPS_SETTINGS" -> settingsURI = Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS
      "ACTION_MANAGE_OVERLAY_PERMISSION" -> settingsURI = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
      "ACTION_MANAGE_UNKNOWN_APP_SOURCES" -> settingsURI = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
      "ACTION_MANAGE_WRITE_SETTINGS" -> settingsURI = Settings.ACTION_MANAGE_WRITE_SETTINGS
      "ACTION_MEMORY_CARD_SETTINGS" -> settingsURI = Settings.ACTION_MEMORY_CARD_SETTINGS
      "ACTION_NETWORK_OPERATOR_SETTINGS" -> settingsURI = Settings.ACTION_NETWORK_OPERATOR_SETTINGS
      "ACTION_NFCSHARING_SETTINGS" -> settingsURI = Settings.ACTION_NFCSHARING_SETTINGS
      "ACTION_NFC_PAYMENT_SETTINGS" -> settingsURI = Settings.ACTION_NFC_PAYMENT_SETTINGS
      "ACTION_NFC_SETTINGS" -> settingsURI = Settings.ACTION_NFC_SETTINGS
      "ACTION_NIGHT_DISPLAY_SETTINGS" -> settingsURI = Settings.ACTION_NIGHT_DISPLAY_SETTINGS
      "ACTION_NOTIFICATION_LISTENER_SETTINGS" -> settingsURI = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
      "ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS" -> settingsURI = Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
      "ACTION_PRINT_SETTINGS" -> settingsURI = Settings.ACTION_PRINT_SETTINGS
      "ACTION_PRIVACY_SETTINGS" -> settingsURI = Settings.ACTION_PRIVACY_SETTINGS
      "ACTION_QUICK_LAUNCH_SETTINGS" -> settingsURI = Settings.ACTION_QUICK_LAUNCH_SETTINGS
      "ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" -> settingsURI = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
      "ACTION_REQUEST_SET_AUTOFILL_SERVICE" -> settingsURI = Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE
      "ACTION_SEARCH_SETTINGS" -> settingsURI = Settings.ACTION_SEARCH_SETTINGS
      "ACTION_SECURITY_SETTINGS" -> settingsURI = Settings.ACTION_SECURITY_SETTINGS
      "ACTION_SETTINGS" -> settingsURI = Settings.ACTION_SETTINGS
      "ACTION_SHOW_REGULATORY_INFO" -> settingsURI = Settings.ACTION_SHOW_REGULATORY_INFO
      "ACTION_SOUND_SETTINGS" -> settingsURI = Settings.ACTION_SOUND_SETTINGS
      "ACTION_SYNC_SETTINGS" -> settingsURI = Settings.ACTION_SYNC_SETTINGS
      "ACTION_USAGE_ACCESS_SETTINGS" -> settingsURI = Settings.ACTION_USAGE_ACCESS_SETTINGS
      "ACTION_USER_DICTIONARY_SETTINGS" -> settingsURI = Settings.ACTION_USER_DICTIONARY_SETTINGS
      "ACTION_VOICE_CONTROL_AIRPLANE_MODE" -> settingsURI = Settings.ACTION_VOICE_CONTROL_AIRPLANE_MODE
      "ACTION_VOICE_CONTROL_BATTERY_SAVER_MODE" -> settingsURI = Settings.ACTION_VOICE_CONTROL_BATTERY_SAVER_MODE
      "ACTION_VOICE_CONTROL_DO_NOT_DISTURB_MODE" -> settingsURI = Settings.ACTION_VOICE_CONTROL_DO_NOT_DISTURB_MODE
      "ACTION_VOICE_INPUT_SETTINGS" -> settingsURI = Settings.ACTION_VOICE_INPUT_SETTINGS
      "ACTION_VPN_SETTINGS" -> settingsURI = Settings.ACTION_VPN_SETTINGS
      "ACTION_VR_LISTENER_SETTINGS" -> settingsURI = Settings.ACTION_VR_LISTENER_SETTINGS
      "ACTION_WEBVIEW_SETTINGS" -> settingsURI = Settings.ACTION_WEBVIEW_SETTINGS
      "ACTION_WIFI_IP_SETTINGS" -> settingsURI = Settings.ACTION_WIFI_IP_SETTINGS
      "ACTION_WIFI_SETTINGS" -> settingsURI = Settings.ACTION_WIFI_SETTINGS
      "ACTION_WIRELESS_SETTINGS" -> settingsURI = Settings.ACTION_WIRELESS_SETTINGS
      "ACTION_ZEN_MODE_PRIORITY_SETTINGS" -> settingsURI = Settings.ACTION_ZEN_MODE_PRIORITY_SETTINGS


      else -> settingsURI = ""
    }

    return settingsURI
  }
}
