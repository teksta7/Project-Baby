import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ProfileController with ChangeNotifier {
  final SharedPreferences prefs;

  ProfileController({SharedPreferences? prefs})
      : prefs = prefs ?? SharedPreferences.getInstance() as SharedPreferences;

  String get localProfileString => prefs.getString('profileString') ?? 'Profile';

  set localProfileString(String value) {
    prefs.setString('profileString', value);
    notifyListeners();
  }
}
