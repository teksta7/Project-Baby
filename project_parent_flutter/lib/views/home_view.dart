import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/home_card.dart';

class HomeView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final homeCards = Provider.of<HomeCardStore>(context).homeCards;

    return Scaffold(
      appBar: AppBar(
        title: Text('Project Parent'),
      ),
      body: ListView.builder(
        itemCount: homeCards.length,
        itemBuilder: (context, index) {
          final card = homeCards[index];
          return ListTile(
            title: Text(card.presentedString),
            tileColor: card.color,
            onTap: () {
              // Navigate to the appropriate view based on card.viewString
              // This is where you'd implement navigation to different screens
            },
          );
        },
      ),
    );
  }
}
