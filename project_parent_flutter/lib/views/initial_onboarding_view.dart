import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/home_card.dart';
import 'home_view.dart';

class InitialOnboardingView extends StatelessWidget {
  const InitialOnboardingView({super.key});

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: Future.delayed(Duration(seconds: 2)),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else {
          // After a short delay, navigate to the home view
          return HomeView();
        }
      },
    );
  }
}
