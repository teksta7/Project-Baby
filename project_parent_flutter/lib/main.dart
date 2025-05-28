import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as p;
import 'package:sqflite/sqflite.dart';

import 'models/bottle_settings.dart';
import 'models/home_card.dart';
import 'views/home_view.dart';
import 'controllers/core_data_controller.dart';
import 'controllers/bottle_controller.dart';
import 'controllers/profile_controller.dart';
import 'views/initial_onboarding_view.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize shared preferences
  final prefs = await SharedPreferences.getInstance();

  // Initialize database
  final database = await initDatabase();

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => BottleSettings(prefs)),
        ChangeNotifierProvider(create: (_) => HomeCardStore()),
        ChangeNotifierProvider(create: (_) => CoreDataController(database)),
        ChangeNotifierProvider(create: (_) => BottleController(prefs)),
        ChangeNotifierProvider(create: (_) => ProfileController()),
      ],
      child: const ProjectParentApp(),
    ),
  );
}

Future<Database> initDatabase() async {
  final directory = await getApplicationDocumentsDirectory();
  final path = p.join(directory.path, 'project_parent.db');
  return openDatabase(
    path,
    onCreate: (db, version) {
      // Create tables here
      return db.execute(
        "CREATE TABLE bottles(id INTEGER PRIMARY KEY, start_time TEXT, duration REAL)",
      );
    },
    version: 1,
  );
}

class ProjectParentApp extends StatelessWidget {
  const ProjectParentApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Project Parent',
      theme: ThemeData.dark(),
      home: const InitialOnboardingView(),
    );
  }
}
