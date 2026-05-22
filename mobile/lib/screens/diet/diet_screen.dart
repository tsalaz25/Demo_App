import 'package:flutter/material.dart';
import '../../services/api_service.dart';

class DietScreen extends StatefulWidget {
  const DietScreen({super.key});
  @override
  State<DietScreen> createState() => _DietScreenState();
}

class _DietScreenState extends State<DietScreen> {
  Map<String, dynamic>? _summary;
  bool _loading = true;
  DateTime _selectedDate = DateTime.now();

  @override
  void initState() {
    super.initState();
    _loadSummary();
  }

  String get _dateStr =>
      '${_selectedDate.year}-${_selectedDate.month.toString().padLeft(2, '0')}-${_selectedDate.day.toString().padLeft(2, '0')}';

  Future<void> _loadSummary() async {
    setState(() => _loading = true);
    try {
      final summary = await ApiService.getDailySummary(_dateStr);
      setState(() { _summary = summary; _loading = false; });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Diet'),
        actions: [
          IconButton(
            icon: const Icon(Icons.calendar_today),
            onPressed: _pickDate,
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _addMeal,
        icon: const Icon(Icons.add),
        label: const Text('Add Meal'),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadSummary,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  // Date header
                  Text(_dateStr,
                      style: Theme.of(context).textTheme.titleMedium),
                  const SizedBox(height: 16),
                  // Macro summary card
                  if (_summary != null) ...[
                    _MacroCard(summary: _summary!),
                    const SizedBox(height: 16),
                    // Meals list
                    ...(_summary!['meals'] as List).map((meal) =>
                        _MealCard(meal: meal, onFoodAdded: _loadSummary)),
                  ],
                ],
              ),
            ),
    );
  }

  Future<void> _pickDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate,
      firstDate: DateTime(2024),
      lastDate: DateTime.now(),
    );
    if (picked != null) {
      setState(() => _selectedDate = picked);
      _loadSummary();
    }
  }

  Future<void> _addMeal() async {
    final nameController = TextEditingController();
    await showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('New Meal'),
        content: TextField(
          controller: nameController,
          decoration: const InputDecoration(
            labelText: 'Meal name (e.g. Breakfast)',
            border: OutlineInputBorder(),
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          FilledButton(
            onPressed: () async {
              await ApiService.createMeal(_dateStr, nameController.text.trim());
              if (ctx.mounted) Navigator.pop(ctx);
              _loadSummary();
            },
            child: const Text('Create'),
          ),
        ],
      ),
    );
  }
}

class _MacroCard extends StatelessWidget {
  final Map<String, dynamic> summary;
  const _MacroCard({required this.summary});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Daily Totals',
                style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _MacroChip('Calories', '${summary['totalCalories']?.toStringAsFixed(0) ?? 0}', Colors.orange),
                _MacroChip('Protein', '${summary['totalProtein']?.toStringAsFixed(1) ?? 0}g', Colors.red),
                _MacroChip('Carbs', '${summary['totalCarbs']?.toStringAsFixed(1) ?? 0}g', Colors.blue),
                _MacroChip('Fat', '${summary['totalFat']?.toStringAsFixed(1) ?? 0}g', Colors.yellow),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _MacroChip extends StatelessWidget {
  final String label;
  final String value;
  final Color color;
  const _MacroChip(this.label, this.value, this.color);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(value, style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: color)),
        Text(label, style: const TextStyle(fontSize: 12)),
      ],
    );
  }
}

class _MealCard extends StatelessWidget {
  final Map<String, dynamic> meal;
  final VoidCallback onFoodAdded;
  const _MealCard({required this.meal, required this.onFoodAdded});

  @override
  Widget build(BuildContext context) {
    final logs = meal['foodLogs'] as List;
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ExpansionTile(
        title: Text(meal['mealName']),
        subtitle: Text('${logs.length} items'),
        trailing: IconButton(
          icon: const Icon(Icons.search),
          onPressed: () => _searchFood(context),
        ),
        children: [
          ...logs.map((log) => ListTile(
            title: Text(log['foodName']),
            subtitle: Text('${log['servingQuantity']} serving'),
            trailing: Text('${log['calories']?.toStringAsFixed(0)} cal'),
          )),
        ],
      ),
    );
  }

  Future<void> _searchFood(BuildContext context) async {
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
                  labelText: 'Search food',
                  border: const OutlineInputBorder(),
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.search),
                    onPressed: () async {
                      final r = await ApiService.searchFoods(queryController.text);
                      setModalState(() => results = r);
                    },
                  ),
                ),
                onSubmitted: (_) async {
                  final r = await ApiService.searchFoods(queryController.text);
                  setModalState(() => results = r);
                },
              ),
              const SizedBox(height: 8),
              SizedBox(
                height: 300,
                child: ListView.builder(
                  itemCount: results.length,
                  itemBuilder: (_, i) {
                    final food = results[i];
                    return ListTile(
                      title: Text(food['foodName']),
                      subtitle: Text('${food['calories']} cal | ${food['servingDescription']}'),
                      onTap: () async {
                        await ApiService.logFood({
                          'mealId': meal['id'],
                          'foodIdApi': food['foodId'],
                          'foodName': food['foodName'],
                          'servingQuantity': 1.0,
                          'calories': food['calories'],
                          'proteinGrams': food['protein'],
                          'carbGrams': food['carbs'],
                          'fatGrams': food['fat'],
                          'logDate': meal['mealDate'],
                        });
                        if (ctx.mounted) Navigator.pop(ctx);
                        onFoodAdded();
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
}