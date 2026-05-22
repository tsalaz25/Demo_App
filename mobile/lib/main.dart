import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:go_router/go_router.dart';
import 'services/auth_provider.dart';
import 'screens/auth/login_screen.dart';
import 'screens/auth/register_screen.dart';
import 'screens/diet/diet_screen.dart';
import 'screens/workout/workout_screen.dart';
import 'screens/progress/progress_screen.dart';
import 'screens/profile/profile_screen.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => AuthProvider()..checkAuth(),
      child: const GymApp(),
    ),
  );
}

class GymApp extends StatelessWidget {
  const GymApp({super.key});

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();

    final router = GoRouter(
      initialLocation: auth.isLoggedIn ? '/diet' : '/login',
      routes: [
        GoRoute(path: '/login', builder: (_, __) => const LoginScreen()),
        GoRoute(path: '/register', builder: (_, __) => const RegisterScreen()),
        ShellRoute(
          builder: (context, state, child) => MainShell(child: child),
          routes: [
            GoRoute(path: '/diet', builder: (_, __) => const DietScreen()),
            GoRoute(path: '/workout', builder: (_, __) => const WorkoutScreen()),
            GoRoute(path: '/progress', builder: (_, __) => const ProgressScreen()),
            GoRoute(path: '/profile', builder: (_, __) => const ProfileScreen()),
          ],
        ),
      ],
    );

    return MaterialApp.router(
      title: 'GymApp',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF6C63FF),
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ),
      routerConfig: router,
    );
  }
}

class MainShell extends StatelessWidget {
  final Widget child;
  const MainShell({super.key, required this.child});

  int _selectedIndex(BuildContext context) {
    final location = GoRouterState.of(context).uri.toString();
    if (location.startsWith('/diet')) return 0;
    if (location.startsWith('/workout')) return 1;
    if (location.startsWith('/progress')) return 2;
    if (location.startsWith('/profile')) return 3;
    return 0;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _selectedIndex(context),
        onDestinationSelected: (index) {
          switch (index) {
            case 0: context.go('/diet');
            case 1: context.go('/workout');
            case 2: context.go('/progress');
            case 3: context.go('/profile');
          }
        },
        destinations: const [
          NavigationDestination(icon: Icon(Icons.restaurant_menu), label: 'Diet'),
          NavigationDestination(icon: Icon(Icons.fitness_center), label: 'Workout'),
          NavigationDestination(icon: Icon(Icons.show_chart), label: 'Progress'),
          NavigationDestination(icon: Icon(Icons.person), label: 'Profile'),
        ],
      ),
    );
  }
}