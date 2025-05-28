import 'package:flutter/foundation.dart';
import 'package:sqflite/sqflite.dart';

class CoreDataController with ChangeNotifier {
  final Database database;

  CoreDataController(this.database);

  Future<void> addBottle(Map<String, dynamic> bottle) async {
    await database.insert(
      'bottles',
      bottle,
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
    notifyListeners();
  }

  Future<List<Map<String, dynamic>>> getBottles() async {
    return await database.query('bottles');
  }

  Future<void> deleteBottle(int id) async {
    await database.delete(
      'bottles',
      where: 'id = ?',
      whereArgs: [id],
    );
    notifyListeners();
  }
}
