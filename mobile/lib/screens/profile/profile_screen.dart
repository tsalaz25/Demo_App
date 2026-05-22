import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../services/auth_provider.dart';
import '../../services/api_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});
  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  Map<String, dynamic>? _goal;

  @override
  void initState() {
    super.initState();
    _loadGoal();
  }

  Future<void> _loadGoal() async {
    final goal = await ApiService.getActiveGoal();
    setState(() => _goal = goal);
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(auth.displayName ?? 'User',
                      style: Theme.of(context).textTheme.headlineSmall),
                  Text(auth.email ?? '',
                      style: Theme.of(context).textTheme.bodyMedium),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          ListTile(
            leading: const Icon(Icons.flag),
            title: const Text('Set New Goal'),
            trailing: const Icon(Icons.chevron_right),
            onTap: _setGoal,
          ),
          if (_goal != null) ...[
            const Divider(),
            ListTile(
              leading: const Icon(Icons.track_changes),
              title: const Text('Current Goal'),
              subtitle: Text(
                'Target: ${_goal!['targetWeightLbs']} lbs | '
                '${_goal!['targetDailyCalIntake']} cal/day',
              ),
            ),
          ],
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout),
            title: const Text('Logout'),
            onTap: () async {
              await auth.logout();
              if (context.mounted) context.go('/login');
            },
          ),
        ],
      ),
    );
  }

  Future<void> _setGoal() async {
    final targetWeightController = TextEditingController();
    final startWeightController = TextEditingController();
    final calController = TextEditingController();

    await showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Set Goal'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: startWeightController,
              decoration: const InputDecoration(
                labelText: 'Current weight (lbs)',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 12),
            TextField(
              controller: targetWeightController,
              decoration: const InputDecoration(
                labelText: 'Target weight (lbs)',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 12),
            TextField(
              controller: calController,
              decoration: const InputDecoration(
                labelText: 'Daily calorie target',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          FilledButton(
            onPressed: () async {
              final today = DateTime.now();
              final dateStr =
                  '${today.year}-${today.month.toString().padLeft(2, '0')}-${today.day.toString().padLeft(2, '0')}';
              await ApiService.createGoal({
                'targetWeightLbs': double.tryParse(targetWeightController.text) ?? 0,
                'startWeightLbs': double.tryParse(startWeightController.text) ?? 0,
                'targetDailyCalIntake': int.tryParse(calController.text) ?? 0,
                'status': 'active',
                'startDate': dateStr,
              });
              if (ctx.mounted) Navigator.pop(ctx);
              _loadGoal();
            },
            child: const Text('Save'),
          ),
        ],
      ),
    );
  }
}