import 'package:flutter/material.dart';

class HomeCard {
  final String id;
  final Color color;
  final String viewString;
  final String presentedString;
  final String imageToDisplay;
  final bool toTrack;

  HomeCard({
    required this.id,
    required this.color,
    required this.viewString,
    required this.presentedString,
    required this.imageToDisplay,
    required this.toTrack,
  });
}

class HomeCardStore with ChangeNotifier {
  List<HomeCard> _homeCards = [];

  List<HomeCard> get homeCards => _homeCards;

  HomeCardStore() {
    _initializeHomeCards();
  }

  void _initializeHomeCards() {
    _homeCards = [
      HomeCard(
        id: '1',
        color: Colors.teal, // Using standard Flutter color
        viewString: 'PROFILE',
        presentedString: 'Profile',
        imageToDisplay: 'figure.child',
        toTrack: true,
      ),
      HomeCard(
        id: '2',
        color: Colors.green,
        viewString: 'BOTTLES',
        presentedString: 'Bottles',
        imageToDisplay: 'waterbottle',
        toTrack: true,
      ),
      HomeCard(
        id: '3',
        color: Colors.indigo,
        viewString: 'SLEEP',
        presentedString: 'Sleep',
        imageToDisplay: 'powersleep',
        toTrack: false,
      ),
      HomeCard(
        id: '4',
        color: Colors.yellow,
        viewString: 'FOOD',
        presentedString: 'Food',
        imageToDisplay: 'carrot',
        toTrack: false,
      ),
      HomeCard(
        id: '5',
        color: Colors.red,
        viewString: 'MEDS',
        presentedString: 'Medicene',
        imageToDisplay: 'pill',
        toTrack: false,
      ),
      HomeCard(
        id: '6',
        color: Colors.blue,
        viewString: 'WIND',
        presentedString: 'Wind',
        imageToDisplay: 'wind',
        toTrack: false,
      ),
      HomeCard(
        id: '7',
        color: Colors.brown,
        viewString: 'POO',
        presentedString: 'Nappies',
        imageToDisplay: 'toilet',
        toTrack: false,
      ),
      HomeCard(
        id: '8',
        color: Colors.grey,
        viewString: 'SETTINGS',
        presentedString: 'Settings',
        imageToDisplay: 'gear',
        toTrack: true,
      ),
      HomeCard(
        id: '9',
        color: Colors.purple,
        viewString: 'TEST',
        presentedString: 'Test',
        imageToDisplay: 'testtube.2',
        toTrack: true,
      ),
    ];
  }
}
