import React, { useState } from 'react';
import { authService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { Shield, Lock, User as UserIcon } from 'lucide-react';

const LoginPage = () => {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login(identifier, password);
      if (response.success) {
        loginUser(response.data);
      } else {
        setError(response.message || 'Login gagal!');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Gagal terhubung ke server backend! Pastikan backend Spring Boot aktif.');
    } finally {
      setLoading(false);
    }
  };

  const setPresetCredentials = (presetIdentifier, presetPassword) => {
    setIdentifier(presetIdentifier);
    setPassword(presetPassword);
    setError('');
  };

  return (
    <div className="login-split">
      {/* Left Visual Area */}
      <div className="login-visual">
        <Shield size={80} color="#fff" style={{ marginBottom: '1.5rem' }} />
        <h1 style={{ fontSize: '2rem', fontWeight: 700, marginBottom: '0.5rem', textAlign: 'center' }}>
          Sistem Absensi Terpadu
        </h1>
        <p style={{ fontSize: '1rem', color: '#e2e8f0', textAlign: 'center', maxWidth: '400px' }}>
          Kelola kehadiran, pengajuan izin, dan pemantauan asrama Universitas Andalas dengan mudah, cepat, dan transparan.
        </p>
      </div>

      {/* Right Form Area */}
      <div className="login-form-container">
        <div className="card" style={{ width: '100%', maxWidth: '420px', padding: '2.5rem', border: 'none', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{
            width: '60px',
            height: '60px',
            backgroundColor: 'var(--primary)',
            borderRadius: '50%',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1rem'
          }}>
            <Shield size={32} color="#fff" />
          </div>
          <h1 style={{ fontSize: '1.4rem', color: '#0f172a', fontWeight: 700 }}>
            Masuk ke Akun Anda
          </h1>
          <p style={{ color: '#64748b', fontSize: '0.85rem', marginTop: '0.25rem' }}>
            Masukkan identitas penghuni atau pengelola
          </p>
        </div>

        {error && (
          <div style={{
            backgroundColor: '#fef2f2',
            color: '#991b1b',
            padding: '0.75rem 1rem',
            borderRadius: '8px',
            fontSize: '0.85rem',
            marginBottom: '1.25rem',
            border: '1px solid #fecaca'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleLogin}>
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              NIM / Email
            </label>
            <div style={{ position: 'relative' }}>
              <UserIcon size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              <input
                type="text"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                placeholder="Masukkan NIM atau Email"
                required
                style={{
                  width: '100%',
                  padding: '0.65rem 0.65rem 0.65rem 2.5rem',
                  border: '1px solid #cbd5e1',
                  borderRadius: '8px',
                  fontSize: '0.9rem'
                }}
              />
            </div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              Password
            </label>
            <div style={{ position: 'relative' }}>
              <Lock size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Masukkan Password"
                required
                style={{
                  width: '100%',
                  padding: '0.65rem 0.65rem 0.65rem 2.5rem',
                  border: '1px solid #cbd5e1',
                  borderRadius: '8px',
                  fontSize: '0.9rem'
                }}
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
            style={{ width: '100%', justifyContent: 'center', padding: '0.75rem' }}
          >
            {loading ? 'Memproses Login...' : 'Masuk ke Aplikasi'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', paddingTop: '1.25rem', borderTop: '1px solid #e2e8f0' }}>
          <div style={{ fontSize: '0.75rem', color: '#64748b', textAlign: 'center', marginBottom: '0.75rem' }}>
            ⚡ Akun Pengujian Demo Uji Coba:
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '0.5rem' }}>
            <button
              onClick={() => setPresetCredentials('penghuni@unand.ac.id', 'penghuni123')}
              className="btn"
              style={{ backgroundColor: '#e0f2fe', color: '#0369a1', fontSize: '0.75rem', padding: '0.4rem' }}
            >
              Penghuni
            </button>
            <button
              onClick={() => setPresetCredentials('fasil@unand.ac.id', 'fasil123')}
              className="btn"
              style={{ backgroundColor: '#fef3c7', color: '#b45309', fontSize: '0.75rem', padding: '0.4rem' }}
            >
              Fasil
            </button>
            <button
              onClick={() => setPresetCredentials('admin@unand.ac.id', 'admin123')}
              className="btn"
              style={{ backgroundColor: '#fce7f3', color: '#be185d', fontSize: '0.75rem', padding: '0.4rem' }}
            >
              Admin
            </button>
          </div>
        </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
