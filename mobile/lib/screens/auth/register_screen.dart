import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../services/auth_provider.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});
  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _nameController = TextEditingController();
  bool _loading = false;
  String? _error;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Create Account')),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextField(
                controller: _nameController,
                decoration: const InputDecoration(
                  labelText: 'Display Name',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: _emailController,
                decoration: const InputDecoration(
                  labelText: 'Email',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 16),
              TextField(
                controller: _passwordController,
                decoration: const InputDecoration(
                  labelText: 'Password',
                  border: OutlineInputBorder(),
                ),
                obscureText: true,
              ),
              if (_error != null) ...[
                const SizedBox(height: 8),
                Text(_error!, style: const TextStyle(color: Colors.red)),
              ],
              const SizedBox(height: 24),
              FilledButton(
                onPressed: _loading ? null : _register,
                child: _loading
                    ? const CircularProgressIndicator()
                    : const Text('Register'),
              ),
              TextButton(
                onPressed: () => context.go('/login'),
                child: const Text('Already have an account? Login'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _register() async {
  setState(() { _loading = true; _error = null; });
  final auth = context.read<AuthProvider>();
  final success = await auth.register(
    _emailController.text.trim(),
    _passwordController.text.trim(),
    _nameController.text.trim(),
  );
  if (success && mounted) {
    context.go('/diet');
  } else if (mounted) {
    // Try logging in instead — user may have just been created
    final loginSuccess = await auth.login(
      _emailController.text.trim(),
      _passwordController.text.trim(),
    );
    if (loginSuccess && mounted) {
      context.go('/diet');
    } else {
      setState(() {
        _error = 'Registration failed. Email may already be in use.';
        _loading = false;
        });
      }
    }
  }
}