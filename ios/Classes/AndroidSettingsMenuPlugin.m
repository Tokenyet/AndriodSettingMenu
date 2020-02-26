#import "AndroidSettingsMenuPlugin.h"
#import <android_settings_menu/android_settings_menu-Swift.h>

@implementation AndroidSettingsMenuPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAndroidSettingsMenuPlugin registerWithRegistrar:registrar];
}
@end
