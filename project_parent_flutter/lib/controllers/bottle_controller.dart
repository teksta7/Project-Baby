import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:intl/intl.dart';

class BottleController with ChangeNotifier {
  final SharedPreferences prefs;

  BottleController(this.prefs);

  int get bottlesTakenToday =>
      prefs.getInt('projectparent.bottles.${_getDayOfYear(DateTime.now())}') ?? 0;

  set bottlesTakenToday(int value) {
    prefs.setInt('projectparent.bottles.${_getDayOfYear(DateTime.now())}', value);
    notifyListeners();
  }

  int get yesterdayCount =>
      prefs.getInt('projectparent.bottles.${_getDayOfYear(DateTime.now().subtract(Duration(days: 1)))}') ?? 0;

  set yesterdayCount(int value) {
    prefs.setInt('projectparent.bottles.${_getDayOfYear(DateTime.now().subtract(Duration(days: 1)))}', value);
    notifyListeners();
  }

  double get averageBottleDuration => prefs.getDouble('projectparent.averagebottleduration') ?? 0.0;

  set averageBottleDuration(double value) {
    prefs.setDouble('projectparent.averagebottleduration', value);
    notifyListeners();
  }

  double get totalBottleDuration => prefs.getDouble('projectparent.totalbottleduration') ?? 0.0;

  set totalBottleDuration(double value) {
    prefs.setDouble('projectparent.totalbottleduration', value);
    notifyListeners();
  }

  double get totalBottles => prefs.getDouble('projectparent.totalbottles') ?? 0.0;

  set totalBottles(double value) {
    prefs.setDouble('projectparent.totalbottles', value);
    notifyListeners();
  }

  String get nextBottleNotificationDateTime =>
      prefs.getString('projectparent.nextBottleNotificationDateTime') ?? 'N/A';

  set nextBottleNotificationDateTime(String value) {
    prefs.setString('projectparent.nextBottleNotificationDateTime', value);
    notifyListeners();
  }

  void addBottleToTodayCount() {
    bottlesTakenToday += 1;
  }

  void removeBottleFromTodayCount() {
    bottlesTakenToday -= 1;
  }

  void removeBottleFromYesterdayCount() {
    var bottlesTakenYesterday = yesterdayCount;
    bottlesTakenYesterday -= 1;
    yesterdayCount = bottlesTakenYesterday;
  }

  void calculateAverageBottleDuration() {
    averageBottleDuration = totalBottleDuration / totalBottles;
  }

  void setTimeUntilNextBottle(String timeString) {
    nextBottleNotificationDateTime = timeString;
  }

  String _getDayOfYear(DateTime date) {
    return DateFormat('D').format(date);
  }
}
