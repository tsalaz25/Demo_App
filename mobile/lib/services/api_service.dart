import 'dart:convert';
import 'package:http/http.dart' as http;

class _TokenStore {
  static String? _token;
  static String? get token => _token;
  static void save(String token) => _token = token;
  static void clear() => _token = null;
}

class ApiService {
  static const String baseUrl = 'http://localhost:8080/api';

  // --- Token management ---
  static Future<String?> getToken() async {
    return _TokenStore.token;
  }

  static Future<void> saveToken(String token) async {
    _TokenStore.save(token);
  }

  static Future<void> clearToken() async {
    _TokenStore.clear();
  }

  static Future<Map<String, String>> _authHeaders() async {
    final token = await getToken();
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    };
  }

  // --- Auth ---
  static Future<Map<String, dynamic>> register(
      String email, String password, String displayName) async {
    final res = await http.post(
      Uri.parse('$baseUrl/auth/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'email': email,
        'password': password,
        'displayName': displayName,
      }),
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> login(
      String email, String password) async {
    print('LOGIN CALLED: email=$email password=$password');
    final res = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );
    print('LOGIN STATUS: ${res.statusCode}');
    print('LOGIN BODY: ${res.body}');
    return jsonDecode(res.body);
  }

  // --- Diet ---
  static Future<List<dynamic>> searchFoods(String query) async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse(
          '$baseUrl/diet/foods/search?query=${Uri.encodeComponent(query)}'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> createMeal(
      String mealDate, String mealName) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/diet/meals'),
      headers: headers,
      body: jsonEncode({'mealDate': mealDate, 'mealName': mealName}),
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> getDailySummary(String date) async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/diet/summary?date=$date'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> logFood(
      Map<String, dynamic> foodLog) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/diet/food-logs'),
      headers: headers,
      body: jsonEncode(foodLog),
    );
    return jsonDecode(res.body);
  }

  // --- Workout ---
  static Future<List<dynamic>> searchExercises(String name) async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse(
          '$baseUrl/workout/exercises/search?name=${Uri.encodeComponent(name)}'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  static Future<List<dynamic>> getExercisesByBodyPart(String bodyPart) async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/workout/exercises/bodypart/$bodyPart'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  static Future<List<dynamic>> getSplits() async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/workout/splits'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> createSplit(
      String name, String? description) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/workout/splits'),
      headers: headers,
      body: jsonEncode({'splitName': name, 'description': description}),
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> createSplitDay(
      String splitId, String onDay, String workoutName) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/workout/split-days'),
      headers: headers,
      body: jsonEncode({
        'splitId': splitId,
        'onDay': onDay,
        'workoutName': workoutName,
      }),
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> addExerciseToDay(
      Map<String, dynamic> exercise) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/workout/split-days/exercises'),
      headers: headers,
      body: jsonEncode(exercise),
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> createWorkoutLog(
      String splitDayId, String performedOn, int? durationMin) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/workout/logs'),
      headers: headers,
      body: jsonEncode({
        'splitDayId': splitDayId,
        'performedOn': performedOn,
        'durationMin': durationMin,
      }),
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>> logSet(
      Map<String, dynamic> set) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/workout/logs/sets'),
      headers: headers,
      body: jsonEncode(set),
    );
    return jsonDecode(res.body);
  }

  static Future<List<dynamic>> getWorkoutHistory(String splitDayId) async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/workout/logs/history/$splitDayId'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  static Future<List<dynamic>> getWorkoutLogs(String date) async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/workout/logs?date=$date'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  // --- Progress ---
  static Future<Map<String, dynamic>> logWeight(
      double weightLbs, String onDate, String? notes) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/progress/weight'),
      headers: headers,
      body: jsonEncode({
        'weightLbs': weightLbs,
        'onDate': onDate,
        'notes': notes,
      }),
    );
    return jsonDecode(res.body);
  }

  static Future<List<dynamic>> getWeightHistory() async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/progress/weight'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  static Future<List<dynamic>> getWeeklySummaries() async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/progress/weekly'),
      headers: headers,
    );
    return jsonDecode(res.body);
  }

  // --- Goals ---
  static Future<Map<String, dynamic>> createGoal(
      Map<String, dynamic> goal) async {
    final headers = await _authHeaders();
    final res = await http.post(
      Uri.parse('$baseUrl/goals'),
      headers: headers,
      body: jsonEncode(goal),
    );
    return jsonDecode(res.body);
  }

  static Future<Map<String, dynamic>?> getActiveGoal() async {
    final headers = await _authHeaders();
    final res = await http.get(
      Uri.parse('$baseUrl/goals/active'),
      headers: headers,
    );
    if (res.statusCode == 404) return null;
    return jsonDecode(res.body);
  }
}