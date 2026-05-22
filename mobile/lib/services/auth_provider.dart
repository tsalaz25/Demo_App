import 'package:flutter/material.dart';
import 'api_service.dart';

class AuthProvider extends ChangeNotifier {
  bool _isLoggedIn = false;
  String? _userId;
  String? _displayName;
  String? _email;

  bool get isLoggedIn => _isLoggedIn;
  String? get userId => _userId;
  String? get displayName => _displayName;
  String? get email => _email;

  Future<void> checkAuth() async {
    final token = await ApiService.getToken();
    _isLoggedIn = token != null;
    notifyListeners();
  }

   Future<bool> login(String email, String password) async {
    try {
      final res = await ApiService.login(email, password);
      print('AUTH PROVIDER RES: $res');
      print('HAS TOKEN: ${res.containsKey('token')}');
    if (res.containsKey('token')) {
      await ApiService.saveToken(res['token']);
      _isLoggedIn = true;
      _userId = res['userId'];
      _displayName = res['displayName'];
      _email = res['email'];
      notifyListeners();
      return true;
    }
    return false;
  } catch (e) {
    print('AUTH PROVIDER ERROR: $e');
    return false;
  }
}

  Future<bool> register(
      String email, String password, String displayName) async {
    try {
      final res = await ApiService.register(email, password, displayName);
      if (res.containsKey('token')) {
        await ApiService.saveToken(res['token']);
        _isLoggedIn = true;
        _userId = res['userId'];
        _displayName = res['displayName'];
        _email = res['email'];
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      print('REGISTER ERROR: $e'); // ADD THIS
      return false;
    }
  }

  Future<void> logout() async {
    await ApiService.clearToken();
    _isLoggedIn = false;
    _userId = null;
    _displayName = null;
    _email = null;
    notifyListeners();
  }
}