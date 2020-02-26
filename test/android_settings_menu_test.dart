import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:android_settings_menu/android_settings_menu.dart';

void main() {
  const MethodChannel channel = MethodChannel('android_settings_menu');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AndroidSettingsMenu.platformVersion, '42');
  });
}
