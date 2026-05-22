import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import '../../services/api_service.dart';

class ProgressScreen extends StatefulWidget {
  const ProgressScreen({super.key});
  @override
  State<ProgressScreen> createState() => _ProgressScreenState();
}

class _ProgressScreenState extends State<ProgressScreen> {
  List<dynamic> _weightLogs = [];
  List<dynamic> _weeklySummaries = [];
  Map<String, dynamic>? _activeGoal;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _loading = true);
    try {
      final results = await Future.wait([
        ApiService.getWeightHistory(),
        ApiService.getWeeklySummaries(),
        ApiService.getActiveGoal().then((g) => g ?? {}),
      ]);
      setState(() {
        _weightLogs = results[0] as List;
        _weeklySummaries = results[1] as List;
        _activeGoal = results[2] as Map<String, dynamic>;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Progress')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _logWeight,
        icon: const Icon(Icons.monitor_weight),
        label: const Text('Log Weight'),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadData,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  if (_activeGoal != null && _activeGoal!.isNotEmpty) ...[
                    _GoalCard(goal: _activeGoal!),
                    const SizedBox(height: 16),
                  ],
                  if (_weightLogs.isNotEmpty) ...[
                    _WeightChart(logs: _weightLogs),
                    const SizedBox(height: 16),
                  ],
                  if (_weeklySummaries.isNotEmpty) ...[
                    Text('Weekly Summaries',
                        style: Theme.of(context).textTheme.titleMedium),
                    const SizedBox(height: 8),
                    ..._weeklySummaries.map((s) => _WeeklySummaryCard(summary: s)),
                  ],
                ],
              ),
            ),
    );
  }

  Future<void> _logWeight() async {
    final weightController = TextEditingController();
    final notesController = TextEditingController();
    await showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Log Weight'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: weightController,
              decoration: const InputDecoration(
                labelText: 'Weight (lbs)',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 12),
            TextField(
              controller: notesController,
              decoration: const InputDecoration(
                labelText: 'Notes (optional)',
                border: OutlineInputBorder(),
              ),
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
              await ApiService.logWeight(
                double.tryParse(weightController.text) ?? 0,
                dateStr,
                notesController.text.isEmpty ? null : notesController.text,
              );
              if (ctx.mounted) Navigator.pop(ctx);
              _loadData();
            },
            child: const Text('Save'),
          ),
        ],
      ),
    );
  }
}

class _GoalCard extends StatelessWidget {
  final Map<String, dynamic> goal;
  const _GoalCard({required this.goal});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Active Goal', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text('Start: ${goal['startWeightLbs']} lbs'),
                  Text('Target: ${goal['targetWeightLbs']} lbs'),
                ]),
                Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
                  Text('Daily Cal Target:'),
                  Text('${goal['targetDailyCalIntake']} kcal',
                      style: const TextStyle(fontWeight: FontWeight.bold)),
                ]),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _WeightChart extends StatelessWidget {
  final List<dynamic> logs;
  const _WeightChart({required this.logs});

  @override
  Widget build(BuildContext context) {
    final reversed = logs.reversed.toList();
    final spots = reversed.asMap().entries.map((e) =>
        FlSpot(e.key.toDouble(), (e.value['weightLbs'] as num).toDouble())
    ).toList();

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Weight History',
                style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 16),
            SizedBox(
              height: 200,
              child: LineChart(
                LineChartData(
                  gridData: const FlGridData(show: false),
                  titlesData: const FlTitlesData(show: false),
                  borderData: FlBorderData(show: false),
                  lineBarsData: [
                    LineChartBarData(
                      spots: spots,
                      isCurved: true,
                      color: const Color(0xFF6C63FF),
                      barWidth: 3,
                      dotData: const FlDotData(show: false),
                      belowBarData: BarAreaData(
                        show: true,
                        color: const Color(0xFF6C63FF).withOpacity(0.1),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _WeeklySummaryCard extends StatelessWidget {
  final Map<String, dynamic> summary;
  const _WeeklySummaryCard({required this.summary});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        title: Text('${summary['weekStart']} — ${summary['weekEnd']}'),
        subtitle: Text(
          'Workouts: ${summary['workoutsCompleted']}  |  '
          'Avg Cal: ${summary['avgCals']?.toStringAsFixed(0) ?? 'N/A'}  |  '
          'Weight Δ: ${summary['weightDiff']?.toStringAsFixed(1) ?? 'N/A'} lbs',
        ),
      ),
    );
  }
}