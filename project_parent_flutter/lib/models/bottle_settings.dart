import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class BottleSettings with ChangeNotifier {
  final SharedPreferences prefs;

  BottleSettings(this.prefs);

  bool get enableBottleNotification => prefs.getBool('EnableBottleNotification') ?? false;
  set enableBottleNotification(bool value) {
    prefs.setBool('EnableBottleNotification', value);
    notifyListeners();
  }

  bool get isBottleFeedLiveActivityOn => prefs.getBool('isBottleFeedLiveActivityOn') ?? false;
  set isBottleFeedLiveActivityOn(bool value) {
    prefs.setBool('isBottleFeedLiveActivityOn', value);
    notifyListeners();
  }

  bool get toggleInAppAds => prefs.getBool('toggleInAppAds') ?? true;
  set toggleInAppAds(bool value) {
    prefs.setBool('toggleInAppAds', value);
    notifyListeners();
  }

  String get setDefaultBottleNote => prefs.getString('setDefaultBottleNote') ?? 'None';
  set setDefaultBottleNote(String value) {
    prefs.setString('setDefaultBottleNote', value);
    notifyListeners();
  }

  int get setBottleNotificationCountDownMinutes => prefs.getInt('setBottleNotificationCountDownMinutes') ?? 0;
  set setBottleNotificationCountDownMinutes(int value) {
    prefs.setInt('setBottleNotificationCountDownMinutes', value);
    notifyListeners();
  }

  double get setDefaultOunces => prefs.getDouble('setDefaultOunces') ?? 0.0;
  set setDefaultOunces(double value) {
    prefs.setDouble('setDefaultOunces', value);
    notifyListeners();
  }
}
