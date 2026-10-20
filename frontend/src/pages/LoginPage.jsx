import React, { useState } from 'react';
import { authService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { Shield, Lock, User as UserIcon, Smartphone, Fingerprint, CheckCircle2 } from 'lucide-react';

const LoginPage = () => {
  const [identifier, setIdentifier] = useState('2211521001');
  const [password, setPassword] = useState('penghuni123');
  const [deviceId, setDeviceId] = useState('ANDROID_ID_A1B2C3D4');
  const [isBiometric, setIsBiometric] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login(identifier, password, deviceId, isBiometric);
      if (response.success) {
        loginUser(response.data);
      } else {
        setError(response.message || 'Login gagal!');
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Gagal terhubung ke server backend! Pastikan backend Spring Boot aktif.'
      );
    } finally {
      setLoading(false);
    }
  };

  const setPresetCredentials = (presetIdentifier, presetPassword, presetDeviceId = 'ANDROID_ID_A1B2C3D4') => {
    setIdentifier(presetIdentifier);
    setPassword(presetPassword);
    setDeviceId(presetDeviceId);
    setIsBiometric(false);
    setError('');
  };

  return (
    <div className="login-split">
      {/* Left Visual Area */}
      <div className="login-visual" style={{ background: 'linear-gradient(145deg, #0f172a 0%, #1e3a8a 60%, #0d9488 100%)' }}>
        <div style={{ textAlign: 'center', maxWidth: '440px', padding: '2rem' }}>
          <div style={{
            width: '80px',
            height: '80px',
            background: 'rgba(255, 255, 255, 0.1)',
            borderRadius: '20px',
            backdropFilter: 'blur(10px)',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '1.5rem',
            border: '1px solid rgba(255, 255, 255, 0.2)'
          }}>
            <Shield size={44} color="#f59e0b" />
          </div>

          <span style={{
            display: 'inline-block',
            padding: '4px 12px',
            background: 'rgba(245, 158, 11, 0.2)',
            color: '#fbbf24',
            borderRadius: '20px',
            fontSize: '0.75rem',
            fontWeight: 700,
            letterSpacing: '1px',
            marginBottom: '1rem'
          }}>
            ENTERPRISE EDITION 2.0
          </span>

          <h1 style={{ fontSize: '2rem', fontWeight: 800, color: '#fff', marginBottom: '0.75rem', lineHeight: 1.2 }}>
            SIA-ASRAMA UNAND
          </h1>
          <p style={{ fontSize: '0.95rem', color: '#cbd5e1', lineHeight: 1.6, marginBottom: '2rem' }}>
            Sistem Informasi Absensi Berbasis Geofencing Haversine, Anti-Jam HP (NTP Time-Gating), dan Biometric Single-Device Lock.
          </p>

          <div style={{
            background: 'rgba(255, 255, 255, 0.07)',
            padding: '1rem',
            borderRadius: '12px',
            textAlign: 'left',
            border: '1px solid rgba(255, 255, 255, 0.12)'
          }}>
            <div style={{ fontSize: '0.75rem', color: '#94a3b8', fontWeight: 600, marginBottom: '0.5rem' }}>
              GUARDRAILS KEAMANAN AKTIF:
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.8rem', color: '#e2e8f0' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <CheckCircle2 size={14} color="#10b981" /> Single-Device Lock (Anti-Joki HP)
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <CheckCircle2 size={14} color="#10b981" /> Geofencing Haversine (&lt; 50 Meter)
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <CheckCircle2 size={14} color="#10b981" /> AI Liveness & Permanent Geotagging
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Right Form Area */}
      <div className="login-form-container">
        <div className="card" style={{ width: '100%', maxWidth: '440px', padding: '2.5rem', border: '1px solid #e2e8f0', boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.08)' }}>
          <div style={{ textAlign: 'center', marginBottom: '1.75rem' }}>
            <h2 style={{ fontSize: '1.4rem', color: '#0f172a', fontWeight: 800 }}>
              Masuk ke SIA-Asrama
            </h2>
            <p style={{ color: '#64748b', fontSize: '0.85rem', marginTop: '0.25rem' }}>
              Gunakan NIM (Penghuni) atau NIP/ID (Fasil/Admin)
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
              border: '1px solid #fecaca',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}>
              <span>⚠️</span>
              <div>{error}</div>
            </div>
          )}

          <form onSubmit={handleLogin}>
            <div style={{ marginBottom: '1.25rem' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem', color: '#334155' }}>
                NIM / NIP / Email
              </label>
              <div style={{ position: 'relative' }}>
                <UserIcon size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
                <input
                  type="text"
                  value={identifier}
                  onChange={(e) => setIdentifier(e.target.value)}
                  placeholder="Contoh: 2211521001 atau ADM001"
                  required
                  style={{
                    width: '100%',
                    padding: '0.75rem 0.75rem 0.75rem 2.4rem',
                    borderRadius: '8px',
                    border: '1px solid #cbd5e1',
                    fontSize: '0.9rem',
                    outline: 'none',
                    boxSizing: 'border-box'
                  }}
                />
              </div>
            </div>

            <div style={{ marginBottom: '1.25rem' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem', color: '#334155' }}>
                Kata Sandi
              </label>
              <div style={{ position: 'relative' }}>
                <Lock size={18} color="#94a3b8" style={{ position: 'absolute', left: '12px', top: '12px' }} />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Masukkan kata sandi akun"
                  required={!isBiometric}
                  style={{
                    width: '100%',
                    padding: '0.75rem 0.75rem 0.75rem 2.4rem',
                    borderRadius: '8px',
                    border: '1px solid #cbd5e1',
                    fontSize: '0.9rem',
                    outline: 'none',
                    boxSizing: 'border-box'
                  }}
                />
              </div>
            </div>

            {/* Single-Device Binding Emulation */}
            <div style={{
              background: '#f8fafc',
              border: '1px dashed #cbd5e1',
              borderRadius: '8px',
              padding: '0.75rem',
              marginBottom: '1.5rem'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.35rem' }}>
                <span style={{ fontSize: '0.75rem', fontWeight: 700, color: '#475569', display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <Smartphone size={14} color="#2563eb" /> Device ID Binding (Anti-Joki)
                </span>
                <span style={{ fontSize: '0.7rem', color: '#059669', fontWeight: 600 }}>Terkunci</span>
              </div>
              <input
                type="text"
                value={deviceId}
                onChange={(e) => setDeviceId(e.target.value)}
                style={{
                  width: '100%',
                  padding: '0.4rem 0.6rem',
                  fontSize: '0.75rem',
                  borderRadius: '4px',
                  border: '1px solid #e2e8f0',
                  background: '#fff',
                  fontFamily: 'monospace'
                }}
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary"
              style={{
                width: '100%',
                padding: '0.85rem',
                fontSize: '0.95rem',
                fontWeight: 700,
                borderRadius: '8px',
                background: '#2563eb',
                color: '#fff',
                border: 'none',
                cursor: loading ? 'not-allowed' : 'pointer'
              }}
            >
              {loading ? 'Memvalidasi...' : 'Masuk ke SIA-Asrama'}
            </button>
          </form>

          {/* Preset Buttons for Graders / Testers */}
          <div style={{ marginTop: '1.75rem', borderTop: '1px solid #e2e8f0', paddingTop: '1.25rem' }}>
            <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600, textAlign: 'center', marginBottom: '0.75rem' }}>
              AKSES CEPAT PENGUJIAN (DEMO ROLE):
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '0.5rem' }}>
              <button
                type="button"
                onClick={() => setPresetCredentials('2211521001', 'penghuni123', 'ANDROID_ID_A1B2C3D4')}
                style={{
                  padding: '0.5rem 0.25rem',
                  fontSize: '0.75rem',
                  fontWeight: 600,
                  borderRadius: '6px',
                  border: '1px solid #93c5fd',
                  backgroundColor: '#eff6ff',
                  color: '#1d4ed8',
                  cursor: 'pointer'
                }}
              >
                Penghuni (Farhan)
              </button>
              <button
                type="button"
                onClick={() => setPresetCredentials('FAS001', 'fasil123', 'DEVICE_FASIL_001')}
                style={{
                  padding: '0.5rem 0.25rem',
                  fontSize: '0.75rem',
                  fontWeight: 600,
                  borderRadius: '6px',
                  border: '1px solid #fde68a',
                  backgroundColor: '#fefce8',
                  color: '#854d0e',
                  cursor: 'pointer'
                }}
              >
                Fasilitator (Fasil)
              </button>
              <button
                type="button"
                onClick={() => setPresetCredentials('ADM001', 'admin123', 'DEVICE_ADMIN_ROOT')}
                style={{
                  padding: '0.5rem 0.25rem',
                  fontSize: '0.75rem',
                  fontWeight: 600,
                  borderRadius: '6px',
                  border: '1px solid #cbd5e1',
                  backgroundColor: '#f1f5f9',
                  color: '#334155',
                  cursor: 'pointer'
                }}
              >
                Administrator
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
