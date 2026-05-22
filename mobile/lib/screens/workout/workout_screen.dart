import 'package:flutter/material.dart';
import '../../services/api_service.dart';

class WorkoutScreen extends StatefulWidget {
  const WorkoutScreen({super.key});
  @override
  State<WorkoutScreen> createState() => _WorkoutScreenState();
}

class _WorkoutScreenState extends State<WorkoutScreen> {
  List<dynamic> _splits = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadSplits();
  }

  Future<void> _loadSplits() async {
    setState(() => _loading = true);
    try {
      final splits = await ApiService.getSplits();
      setState(() { _splits = splits; _loading = false; });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Workout')),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _createSplit,
        icon: const Icon(Icons.add),
        label: const Text('New Split'),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadSplits,
              child: _splits.isEmpty
                  ? const Center(child: Text('No splits yet. Create one!'))
                  : ListView.builder(
                      padding: const EdgeInsets.all(16),
                      itemCount: _splits.length,
                      itemBuilder: (_, i) => _SplitCard(
                        split: _splits[i],
                        onRefresh: _loadSplits,
                      ),
                    ),
            ),
    );
  }

  Future<void> _createSplit() async {
    final nameController = TextEditingController();
    final descController = TextEditingController();
    await showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('New Split'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: nameController,
              decoration: const InputDecoration(
                labelText: 'Split name (e.g. Push Pull Legs)',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: descController,
              decoration: const InputDecoration(
                labelText: 'Description (optional)',
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          FilledButton(
            onPressed: () async {
              await ApiService.createSplit(
                nameController.text.trim(),
                descController.text.trim().isEmpty ? null : descController.text.trim(),
              );
              if (ctx.mounted) Navigator.pop(ctx);
              _loadSplits();
            },
            child: const Text('Create'),
          ),
        ],
      ),
    );
  }
}

class _SplitCard extends StatelessWidget {
  final Map<String, dynamic> split;
  final VoidCallback onRefresh;
  const _SplitCard({required this.split, required this.onRefresh});

  @override
  Widget build(BuildContext context) {
    final days = split['days'] as List;
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ExpansionTile(
        title: Text(split['splitName'],
            style: const TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Text('${days.length} days'),
        trailing: IconButton(
          icon: const Icon(Icons.add_circle_outline),
          onPressed: () => _addDay(context),
        ),
        children: [
          ...days.map((day) => _DayTile(day: day, onRefresh: onRefresh)),
        ],
      ),
    );
  }

  Future<void> _addDay(BuildContext context) async {
    final nameController = TextEditingController();
    final days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    String selectedDay = days[0];

    await showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setState) => AlertDialog(
          title: const Text('Add Day'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              DropdownButton<String>(
                value: selectedDay,
                isExpanded: true,
                items: days.map((d) => DropdownMenuItem(value: d, child: Text(d))).toList(),
                onChanged: (v) => setState(() => selectedDay = v!),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: nameController,
                decoration: const InputDecoration(
                  labelText: 'Session name (e.g. Push Day)',
                  border: OutlineInputBorder(),
                ),
              ),
            ],
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
            FilledButton(
              onPressed: () async {
                await ApiService.createSplitDay(
                  split['id'], selectedDay, nameController.text.trim(),
                );
                if (ctx.mounted) Navigator.pop(ctx);
                onRefresh();
              },
              child: const Text('Add'),
            ),
          ],
        ),
      ),
    );
  }
}

class _DayTile extends StatelessWidget {
  final Map<String, dynamic> day;
  final VoidCallback onRefresh;
  const _DayTile({required this.day, required this.onRefresh});

  @override
  Widget build(BuildContext context) {
    final exercises = day['exercises'] as List;
    return ExpansionTile(
      title: Text('${day['onDay']} — ${day['workoutName']}'),
      subtitle: Text('${exercises.length} exercises'),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () => _searchExercise(context),
          ),
          IconButton(
            icon: const Icon(Icons.play_arrow),
            onPressed: () => _startWorkout(context),
          ),
        ],
      ),
      children: exercises.map((ex) => ListTile(
        leading: const Icon(Icons.fitness_center),
        title: Text(ex['exerciseName']),
        subtitle: Text(ex['muscleGroup'] ?? ''),
      )).toList(),
    );
  }

  Future<void> _searchExercise(BuildContext context) async {
    final queryController = TextEditingController();
    List<dynamic> results = [];

    await showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setModalState) => Padding(
          padding: EdgeInsets.only(
            bottom: MediaQuery.of(ctx).viewInsets.bottom,
            left: 16, right: 16, top: 16,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: queryController,
                decoration: InputDecoration(
                  labelText: 'Search exercises',
                  border: const OutlineInputBorder(),
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.search),
                    onPressed: () async {
                      final r = await ApiService.searchExercises(queryController.text);
                      setModalState(() => results = r);
                    },
                  ),
                ),
              ),
              const SizedBox(height: 8),
              SizedBox(
                height: 300,
                child: ListView.builder(
                  itemCount: results.length,
                  itemBuilder: (_, i) {
                    final ex = results[i];
                    return ListTile(
                      title: Text(ex['name']),
                      subtitle: Text((ex['bodyParts'] as List).join(', ')),
                      onTap: () async {
                        await ApiService.addExerciseToDay({
                          'splitDayId': day['id'],
                          'workoutIdApi': ex['exerciseId'],
                          'exerciseName': ex['name'],
                          'muscleGroup': (ex['bodyParts'] as List).isNotEmpty
                              ? ex['bodyParts'][0] : null,
                          'dispOrder': (day['exercises'] as List).length + 1,
                        });
                        if (ctx.mounted) Navigator.pop(ctx);
                        onRefresh();
                      },
                    );
                  },
                ),
              ),
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _startWorkout(BuildContext context) async {
    final today = DateTime.now();
    final dateStr = '${today.year}-${today.month.toString().padLeft(2, '0')}-${today.day.toString().padLeft(2, '0')}';
    final log = await ApiService.createWorkoutLog(day['id'], dateStr, null);
    final exercises = day['exercises'] as List;

    if (!context.mounted) return;
    await showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => _WorkoutSessionSheet(
        log: log,
        exercises: exercises,
        dayName: day['workoutName'],
      ),
    );
  }
}

class _WorkoutSessionSheet extends StatefulWidget {
  final Map<String, dynamic> log;
  final List<dynamic> exercises;
  final String dayName;
  const _WorkoutSessionSheet({
    required this.log, required this.exercises, required this.dayName,
  });
  @override
  State<_WorkoutSessionSheet> createState() => _WorkoutSessionSheetState();
}

class _WorkoutSessionSheetState extends State<_WorkoutSessionSheet> {
  final Map<String, List<Map<String, dynamic>>> _sets = {};

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      expand: false,
      initialChildSize: 0.85,
      builder: (_, controller) => Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Text(widget.dayName,
                style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 16),
            Expanded(
              child: ListView.builder(
                controller: controller,
                itemCount: widget.exercises.length,
                itemBuilder: (_, i) {
                  final ex = widget.exercises[i];
                  final exId = ex['workoutIdApi'] as String;
                  final sets = _sets[exId] ?? [];
                  return Card(
                    child: Padding(
                      padding: const EdgeInsets.all(12),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(ex['exerciseName'],
                              style: const TextStyle(fontWeight: FontWeight.bold)),
                          ...sets.asMap().entries.map((entry) => Padding(
                            padding: const EdgeInsets.symmetric(vertical: 4),
                            child: Text(
                              'Set ${entry.key + 1}: ${entry.value['weight']} lbs × ${entry.value['reps']} reps',
                            ),
                          )),
                          TextButton.icon(
                            icon: const Icon(Icons.add),
                            label: const Text('Add Set'),
                            onPressed: () => _addSet(ex, exId, sets.length + 1),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
            FilledButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Finish Workout'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _addSet(
      Map<String, dynamic> ex, String exId, int setNum) async {
    final weightController = TextEditingController();
    final repsController = TextEditingController();

    await showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Set $setNum'),
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
              controller: repsController,
              decoration: const InputDecoration(
                labelText: 'Reps',
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
              final weight = double.tryParse(weightController.text) ?? 0;
              final reps = int.tryParse(repsController.text) ?? 0;
              await ApiService.logSet({
                'workoutLogId': widget.log['id'],
                'workoutIdApi': exId,
                'setNumber': setNum,
                'reps': reps,
                'weightForSetLbs': weight,
                'completed': true,
              });
              setState(() {
                _sets[exId] = [...(_sets[exId] ?? []),
                  {'weight': weight, 'reps': reps}];
              });
              if (ctx.mounted) Navigator.pop(ctx);
            },
            child: const Text('Log Set'),
          ),
        ],
      ),
    );
  }
}