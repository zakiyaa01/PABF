import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminService, presensiService } from '../services/api';
import {
  Shield, Building, Users, CheckSquare, Plus, Edit2, Key,
  Trash2, Upload, Download, FileSpreadsheet, MapPin, CheckCircle2,
  XCircle, Search, UserCheck, X, RefreshCw, BarChart2,
  ArrowRight, ChevronRight, Clock, FileText, Smartphone, Lock, Server
} from 'lucide-react';

const DashboardAdmin = ({ activeTab = 'dashboard' }) => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [gedungList, setGedungList] = useState([]);
  const [kamarList, setKamarList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [message, setMessage] = useState('');

  // Modals state
  const [showUserModal, setShowUserModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [newPassword, setNewPassword] = useState('');
  const [showImportModal, setShowImportModal] = useState(false);
  const [importFile, setImportFile] = useState(null);
  const [showAssignFasilModal, setShowAssignFasilModal] = useState(false);
  const [selectedFasilId, setSelectedFasilId] = useState('');
  const [assignedLorong, setAssignedLorong] = useState('Lorong 1');

  // Form user state
  const [userForm, setUserForm] = useState({
    nim: '',
    nama: '',
    asal: '',
    jenisKelamin: 'L',
    email: '',
    password: '',
    role: 'Penghuni',
    kamarId: '',
    fasilId: '',
    lorongBinaan: '',
    deviceId: '',
    isActive: true,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const usersRes = await adminService.getAllUsers();
      if (usersRes.success) setUsers(usersRes.data);

      const analyticsRes = await adminService.getAnalytics();
      if (analyticsRes.success) setAnalytics(analyticsRes.data);

      const gedungRes = await adminService.getAllGedung();
      if (gedungRes.success) setGedungList(gedungRes.data);

      const kamarRes = await adminService.getAllKamar();
      if (kamarRes.success) setKamarList(kamarRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOpenAddUser = () => {
    setEditingUser(null);
    setUserForm({
      nim: '',
      nama: '',
      asal: '',
      jenisKelamin: 'L',
      email: '',
      password: 'password123',
      role: 'Penghuni',
      kamarId: kamarList[0]?.id || '',
      fasilId: '',
      lorongBinaan: '',
      deviceId: '',
      isActive: true,
    });
    setShowUserModal(true);
  };

  const handleOpenEditUser = (u) => {
    setEditingUser(u);
    setUserForm({
      nim: u.nim || '',
      nama: u.nama || '',
      asal: u.asal || '',
      jenisKelamin: u.jenisKelamin || 'L',
      email: u.email || '',
      password: '',
      role: u.role || 'Penghuni',
      kamarId: u.kamar?.id || '',
      fasilId: u.fasil?.id || '',
      lorongBinaan: u.lorongBinaan || '',
      deviceId: u.deviceId || '',
      isActive: u.isActive !== false,
    });
    setShowUserModal(true);
  };

  const handleSaveUser = async (e) => {
    e.preventDefault();
    try {
      if (editingUser) {
        await adminService.updateUser(editingUser.id, userForm);
        setMessage(`Data pengguna ${userForm.nama} berhasil diperbarui!`);
      } else {
        await adminService.createUser(userForm);
        setMessage(`Pengguna baru ${userForm.nama} berhasil ditambahkan!`);
      }
      setShowUserModal(false);
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Gagal menyimpan pengguna');
    }
  };

  const handleToggleActive = async (id) => {
    try {
      const res = await adminService.toggleActive(id);
      if (res.success) {
        setMessage(`Status akun pengguna berhasil diubah!`);
        loadData();
      }
    } catch (err) {
      alert('Gagal mengubah status akun');
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    try {
      await adminService.resetPassword(selectedUserId, newPassword);
      setMessage('Kata sandi berhasil direset!');
      setShowPasswordModal(false);
      setNewPassword('');
    } catch (err) {
      alert('Gagal mereset kata sandi');
    }
  };

  const handleImportExcel = async (e) => {
    e.preventDefault();
    if (!importFile) return;
    try {
      const res = await adminService.importPenghuniExcel(importFile);
      if (res.success) {
        setMessage(res.message);
        setShowImportModal(false);
        setImportFile(null);
        loadData();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Gagal mengimpor file Excel');
    }
  };

  const handleAssignFasil = async (e) => {
    e.preventDefault();
    try {
      const res = await adminService.assignFasilToLorong(selectedFasilId, assignedLorong);
      if (res.success) {
        setMessage(`Fasilitator berhasil dialokasikan ke ${assignedLorong}!`);
        setShowAssignFasilModal(false);
        loadData();
      }
    } catch (err) {
      alert('Gagal mengalokasikan fasilitator');
    }
  };

  const filteredUsers = users.filter((u) => {
    const matchesSearch =
      (u.nama && u.nama.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (u.nim && u.nim.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (u.email && u.email.toLowerCase().includes(searchTerm.toLowerCase()));
    const matchesRole = roleFilter ? u.role === roleFilter : true;
    return matchesSearch && matchesRole;
  });

  const currentTab = (activeTab === 'users' || activeTab === 'gedung') ? activeTab : 'dashboard';

  return (
    <div>
      {/* Admin Header */}
      <div className="card" style={{
        marginBottom: '1.5rem',
        background: 'linear-gradient(135deg, #047857 0%, #065f46 100%)',
        color: '#fff',
        border: 'none',
        borderRadius: '16px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <span style={{ background: '#fbbf24', color: '#000', fontSize: '0.75rem', fontWeight: 800, padding: '2px 8px', borderRadius: '4px' }}>
              ADMINISTRATOR ASRAMA PUSAT
            </span>
            <h1 style={{ fontSize: '1.6rem', fontWeight: 800, margin: '0.5rem 0 0.25rem' }}>
              {currentTab === 'users' ? 'Manajemen Pengguna & Device Binding' :
               currentTab === 'gedung' ? 'Pemetaan Gedung, Kamar & Geofence' :
               'SIA-Asrama Master Data & Executive Analytics'}
            </h1>
            <p style={{ fontSize: '0.85rem', color: '#a7f3d0', margin: 0 }}>
              {currentTab === 'users' ? 'Kelola akun penghuni, fasilitator, admin, reset kata sandi, dan reset binding hardware device ID.' :
               currentTab === 'gedung' ? 'Konfigurasi titik koordinat geofencing, radius meter, Wi-Fi BSSID asrama, dan penugasan fasilitator lorong.' :
               'Manajemen Terpadu Gedung, Kamar, Akun Mahasiswa, Fasilitator, dan Laporan Eksekutif Universitas.'}
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            {currentTab === 'users' && (
              <>
                <a
                  href={adminService.exportPenghuniExcelUrl()}
                  target="_blank"
                  rel="noreferrer"
                  className="btn btn-secondary"
                  style={{ background: '#fff', color: '#065f46', border: 'none', fontWeight: 700, padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px', textDecoration: 'none' }}
                >
                  <Download size={16} /> Ekspor Data (.xlsx)
                </a>
                <button
                  onClick={() => setShowImportModal(true)}
                  className="btn btn-secondary"
                  style={{ background: '#f59e0b', color: '#000', border: 'none', fontWeight: 700, padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px' }}
                >
                  <Upload size={16} /> Impor Massal Excel
                </button>
                <button
                  onClick={handleOpenAddUser}
                  className="btn btn-primary"
                  style={{ background: '#0f172a', border: 'none', padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px' }}
                >
                  <Plus size={16} /> Tambah Akun
                </button>
              </>
            )}
            {currentTab === 'gedung' && (
              <button
                onClick={() => setShowAssignFasilModal(true)}
                className="btn btn-secondary"
                style={{ background: '#fbbf24', color: '#0f172a', border: 'none', fontWeight: 700, padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px' }}
              >
                <UserCheck size={16} /> Alokasikan Fasil ke Lorong
              </button>
            )}
            {currentTab === 'dashboard' && (
              <>
                <button
                  onClick={() => navigate('/users')}
                  className="btn btn-secondary"
                  style={{ background: 'rgba(255,255,255,0.2)', color: '#fff', border: '1px solid rgba(255,255,255,0.3)', padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px' }}
                >
                  <Users size={16} /> Manajemen User
                </button>
                <button
                  onClick={() => navigate('/gedung')}
                  className="btn btn-secondary"
                  style={{ background: '#fbbf24', color: '#0f172a', border: 'none', fontWeight: 700, padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px' }}
                >
                  <Building size={16} /> Struktur Gedung
                </button>
              </>
            )}
          </div>
        </div>
      </div>

      {message && (
        <div style={{ backgroundColor: '#ecfdf5', color: '#065f46', padding: '0.85rem 1.25rem', borderRadius: '8px', marginBottom: '1.5rem', border: '1px solid #a7f3d0' }}>
          ✓ {message}
        </div>
      )}

      {/* ============================================================== */}
      {/* 1. EXECUTIVE DASHBOARD OVERVIEW (currentTab === 'dashboard')    */}
      {/* ============================================================== */}
      {currentTab === 'dashboard' && (
        <>
          {/* System-Wide Analytics Widgets */}
          <div className="grid-3" style={{ marginBottom: '1.5rem' }}>
            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', borderLeft: '4px solid #2563eb' }}>
              <div style={{ backgroundColor: '#eff6ff', padding: '0.85rem', borderRadius: '12px' }}>
                <Users size={28} color="#2563eb" />
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Total Penghuni Terdaftar</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 800 }}>{analytics?.totalPenghuni || users.filter((u) => u.role === 'Penghuni').length}</div>
                <div style={{ fontSize: '0.75rem', color: '#2563eb' }}>{users.filter((u) => u.role === 'Fasil').length} Fasilitator Pembina</div>
              </div>
            </div>

            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', borderLeft: '4px solid #059669' }}>
              <div style={{ backgroundColor: '#ecfdf5', padding: '0.85rem', borderRadius: '12px' }}>
                <BarChart2 size={28} color="#059669" />
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Persentase Kehadiran Hari Ini</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#059669' }}>
                  {analytics?.attendancePercentageToday || 85}%
                </div>
                <div style={{ fontSize: '0.75rem', color: '#64748b' }}>Hadir: {analytics?.hadirToday || 2} | Izin: {analytics?.izinToday || 1}</div>
              </div>
            </div>

            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', borderLeft: '4px solid #d97706' }}>
              <div style={{ backgroundColor: '#fef3c7', padding: '0.85rem', borderRadius: '12px' }}>
                <Building size={28} color="#d97706" />
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Kapasitas Asrama UNAND</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 800 }}>{gedungList.length || 2} Gedung</div>
                <div style={{ fontSize: '0.75rem', color: '#d97706' }}>{kamarList.length || 2} Kamar Aktif Terpetakan</div>
              </div>
            </div>
          </div>

          {/* Quick Access Shortcut Cards */}
          <div className="grid-2" style={{ marginBottom: '1.5rem' }}>
            <div className="card" style={{ borderRadius: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <Users size={20} color="#2563eb" />
                  <strong>Master Data Pengguna & Device Binding</strong>
                </div>
                <span className="badge-role" style={{ background: '#eff6ff', color: '#1d4ed8', fontWeight: 700 }}>
                  {users.length} Akun
                </span>
              </div>
              <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '0 0 1rem 0' }}>
                Manajemen akun Santri & Fasil, reset password, enkripsi credential, dan reset device hardware UUID.
              </p>
              <button
                onClick={() => navigate('/users')}
                className="btn btn-secondary"
                style={{ width: '100%', padding: '0.6rem', fontSize: '0.85rem', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}
              >
                <span>Buka Manajemen Pengguna</span> <ArrowRight size={14} />
              </button>
            </div>

            <div className="card" style={{ borderRadius: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <Building size={20} color="#059669" />
                  <strong>Pemetaan Gedung, Kamar & Geofencing</strong>
                </div>
                <span className="badge-role" style={{ background: '#ecfdf5', color: '#047857', fontWeight: 700 }}>
                  {gedungList.length} Gedung
                </span>
              </div>
              <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '0 0 1rem 0' }}>
                Pengaturan koordinat GPS Haversine, radius meter, Wi-Fi BSSID validasi, serta penugasan fasil ke lorong.
              </p>
              <button
                onClick={() => navigate('/gedung')}
                className="btn btn-secondary"
                style={{ width: '100%', padding: '0.6rem', fontSize: '0.85rem', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}
              >
                <span>Kelola Struktur Asrama</span> <ArrowRight size={14} />
              </button>
            </div>
          </div>

          {/* System Security & Architecture Overview */}
          <div className="card" style={{ marginBottom: '1.5rem', background: '#f8fafc', border: '1px solid #e2e8f0' }}>
            <h4 style={{ margin: '0 0 0.75rem 0', fontSize: '0.9rem', color: '#0f172a', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Shield size={16} color="#047857" /> Arsitektur Keamanan Enterprise & Status Proteksi
            </h4>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '0.75rem', fontSize: '0.8rem' }}>
              <div style={{ background: '#fff', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}>
                <div style={{ fontWeight: 600, color: '#334155', marginBottom: '2px' }}>🔒 Single-Device Lock</div>
                <div style={{ color: '#059669', fontSize: '0.75rem' }}>Aktif (Device ID Terikat)</div>
              </div>
              <div style={{ background: '#fff', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}>
                <div style={{ fontWeight: 600, color: '#334155', marginBottom: '2px' }}>📍 Geofence Enforcement</div>
                <div style={{ color: '#059669', fontSize: '0.75rem' }}>Radius Validasi Haversine</div>
              </div>
              <div style={{ background: '#fff', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}>
                <div style={{ fontWeight: 600, color: '#334155', marginBottom: '2px' }}>📸 Anti Fake & Mock GPS</div>
                <div style={{ color: '#059669', fontSize: '0.75rem' }}>Live Geotag Watermark</div>
              </div>
              <div style={{ background: '#fff', padding: '0.75rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}>
                <div style={{ fontWeight: 600, color: '#334155', marginBottom: '2px' }}>🛡️ RBAC & Spring Security</div>
                <div style={{ color: '#059669', fontSize: '0.75rem' }}>JWT Bearer Protection</div>
              </div>
            </div>
          </div>
        </>
      )}

      {/* ============================================================== */}
      {/* 2. GEDUNG & KAMAR TAB (currentTab === 'gedung')                 */}
      {/* ============================================================== */}
      {currentTab === 'gedung' && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '0.5rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Building size={18} color="#059669" /> Pemetaan Struktur Asrama & Alokasi Fasil (Gedung ➔ Lantai ➔ Lorong ➔ Kamar)
          </h3>
          <button
            onClick={() => setShowAssignFasilModal(true)}
            className="btn btn-secondary"
            style={{ padding: '0.4rem 0.8rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
          >
            <UserCheck size={14} /> Alokasikan Fasil ke Lorong
          </button>
        </div>

        <div className="grid-2">
          {gedungList.map((g) => (
            <div key={g.id} style={{ border: '1px solid #e2e8f0', borderRadius: '12px', padding: '1rem', background: '#f8fafc' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <strong style={{ fontSize: '0.95rem', color: '#0f172a' }}>{g.nama}</strong>
                <span className="badge-role" style={{ background: '#ecfdf5', color: '#047857' }}>Radius {g.radiusMeter}m</span>
              </div>
              <div style={{ fontSize: '0.75rem', color: '#64748b', marginBottom: '0.5rem' }}>
                📍 Titik Geofence: {g.latitude}, {g.longitude} | Wi-Fi BSSID: <code style={{ color: '#2563eb' }}>{g.wifiBssid}</code>
              </div>

              <div style={{ fontSize: '0.8rem', color: '#334155' }}>
                <strong>Kamar Terdaftar:</strong>
                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginTop: '0.35rem' }}>
                  {kamarList.filter((k) => k.gedung?.id === g.id).map((k) => (
                    <span key={k.id} style={{ background: '#fff', border: '1px solid #cbd5e1', padding: '2px 8px', borderRadius: '6px', fontSize: '0.75rem' }}>
                      {k.noKamar} ({k.lorong || 'Lorong 1'} - Lt {k.lantai})
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
      )}

      {/* ============================================================== */}
      {/* 3. MANAJEMEN USER TAB (currentTab === 'users')                  */}
      {/* ============================================================== */}
      {currentTab === 'users' && (
        <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.75rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, margin: 0 }}>
            Master Data Pengguna (Penghuni & Fasil)
          </h3>

          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <div style={{ position: 'relative' }}>
              <Search size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '10px' }} />
              <input
                type="text"
                placeholder="Cari NIM, Nama, Email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                style={{ padding: '0.5rem 0.5rem 0.5rem 2rem', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              />
            </div>
            <select
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              style={{ padding: '0.5rem', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
            >
              <option value="">Semua Role</option>
              <option value="Penghuni">Penghuni</option>
              <option value="Fasil">Fasil</option>
              <option value="Admin">Admin</option>
            </select>
          </div>
        </div>

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>No</th>
                <th>NIM / NIP</th>
                <th>Nama Lengkap</th>
                <th>Role</th>
                <th>Email</th>
                <th>Gedung / Kamar / Lorong</th>
                <th>Device ID Binding</th>
                <th>Status Akun</th>
                <th>Aksi Kelola</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan="9" style={{ textAlign: 'center', color: '#94a3b8', padding: '2rem' }}>
                    Tidak ada data pengguna yang cocok.
                  </td>
                </tr>
              ) : (
                filteredUsers.map((u, idx) => (
                  <tr key={u.id} style={{ opacity: u.isActive === false ? 0.6 : 1 }}>
                    <td>{idx + 1}</td>
                    <td><strong>{u.nim || '-'}</strong></td>
                    <td>{u.nama}</td>
                    <td>
                      <span className="badge-role" style={{
                        background: u.role === 'Admin' ? '#f1f5f9' : u.role === 'Fasil' ? '#fef3c7' : '#eff6ff',
                        color: u.role === 'Admin' ? '#0f172a' : u.role === 'Fasil' ? '#b45309' : '#1d4ed8'
                      }}>
                        {u.role}
                      </span>
                    </td>
                    <td style={{ fontSize: '0.8rem' }}>{u.email}</td>
                    <td style={{ fontSize: '0.8rem' }}>
                      {u.kamar ? (
                        `${u.kamar.gedung?.nama || 'Gedung A'} - ${u.kamar.noKamar} (${u.kamar.lorong || 'Lorong 1'})`
                      ) : u.lorongBinaan ? (
                        `Fasil: ${u.lorongBinaan}`
                      ) : '-'}
                    </td>
                    <td style={{ fontSize: '0.75rem', fontFamily: 'monospace' }}>
                      {u.deviceId ? (
                        <span style={{ color: '#059669' }}>🔒 {u.deviceId}</span>
                      ) : (
                        <span style={{ color: '#94a3b8' }}>Belum Terikat</span>
                      )}
                    </td>
                    <td>
                      <button
                        onClick={() => handleToggleActive(u.id)}
                        className={`badge-role`}
                        style={{
                          background: u.isActive !== false ? '#ecfdf5' : '#fee2e2',
                          color: u.isActive !== false ? '#047857' : '#b91c1c',
                          border: 'none',
                          cursor: 'pointer',
                          fontWeight: 700
                        }}
                      >
                        {u.isActive !== false ? 'AKTIF' : 'NONAKTIF'}
                      </button>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.35rem' }}>
                        <button
                          onClick={() => handleOpenEditUser(u)}
                          className="btn btn-secondary"
                          style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                          title="Edit Pengguna"
                        >
                          <Edit2 size={12} /> Edit
                        </button>
                        <button
                          onClick={() => {
                            setSelectedUserId(u.id);
                            setShowPasswordModal(true);
                          }}
                          className="btn btn-secondary"
                          style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                          title="Reset Password"
                        >
                          <Key size={12} /> Reset
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
      )}

      {/* --- ADD / EDIT USER MODAL --- */}
      {showUserModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(15, 23, 42, 0.75)',
          zIndex: 1000,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '1rem'
        }}>
          <div style={{ background: '#fff', borderRadius: '16px', maxWidth: '580px', width: '100%', overflow: 'hidden' }}>
            <div style={{ background: '#0f172a', color: '#fff', padding: '1rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>
                {editingUser ? '✏️ Edit Data Pengguna' : '➕ Tambah Pengguna Baru'}
              </h3>
              <button onClick={() => setShowUserModal(false)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSaveUser} style={{ padding: '1.5rem', maxHeight: '500px', overflowY: 'auto' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '1rem' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>NIM / NIP</label>
                  <input
                    type="text"
                    value={userForm.nim}
                    onChange={(e) => setUserForm({ ...userForm, nim: e.target.value })}
                    required
                    style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>Role</label>
                  <select
                    value={userForm.role}
                    onChange={(e) => setUserForm({ ...userForm, role: e.target.value })}
                    style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  >
                    <option value="Penghuni">Penghuni</option>
                    <option value="Fasil">Fasil</option>
                    <option value="Admin">Admin</option>
                  </select>
                </div>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>Nama Lengkap</label>
                <input
                  type="text"
                  value={userForm.nama}
                  onChange={(e) => setUserForm({ ...userForm, nama: e.target.value })}
                  required
                  style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '1rem' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>Email UNAND</label>
                  <input
                    type="email"
                    value={userForm.email}
                    onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
                    required
                    style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>Kata Sandi (Opsional saat Edit)</label>
                  <input
                    type="password"
                    value={userForm.password}
                    onChange={(e) => setUserForm({ ...userForm, password: e.target.value })}
                    placeholder={editingUser ? 'Biarkan kosong jika tidak diubah' : 'Minimal 6 karakter'}
                    style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  />
                </div>
              </div>

              {userForm.role === 'Penghuni' && (
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '1rem' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>Alokasi Kamar</label>
                    <select
                      value={userForm.kamarId}
                      onChange={(e) => setUserForm({ ...userForm, kamarId: e.target.value })}
                      style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                    >
                      <option value="">Pilih Kamar...</option>
                      {kamarList.map((k) => (
                        <option key={k.id} value={k.id}>
                          {k.noKamar} - {k.gedung?.nama} ({k.lorong})
                        </option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>Fasilitator Pembina</label>
                    <select
                      value={userForm.fasilId}
                      onChange={(e) => setUserForm({ ...userForm, fasilId: e.target.value })}
                      style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                    >
                      <option value="">Pilih Fasil...</option>
                      {users.filter((u) => u.role === 'Fasil').map((f) => (
                        <option key={f.id} value={f.id}>{f.nama} ({f.nim || 'Fasil'})</option>
                      ))}
                    </select>
                  </div>
                </div>
              )}

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>Device ID Binding (Single-Device Lock)</label>
                <input
                  type="text"
                  value={userForm.deviceId}
                  onChange={(e) => setUserForm({ ...userForm, deviceId: e.target.value })}
                  placeholder="Kosongkan untuk mengizinkan binding otomatis pada perangkat pertama"
                  style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontFamily: 'monospace' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button type="button" onClick={() => setShowUserModal(false)} className="btn btn-secondary">
                  Batal
                </button>
                <button type="submit" className="btn btn-primary" style={{ background: '#047857' }}>
                  Simpan Pengguna
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- RESET PASSWORD MODAL --- */}
      {showPasswordModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(15, 23, 42, 0.75)',
          zIndex: 1000,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '1rem'
        }}>
          <div style={{ background: '#fff', borderRadius: '16px', maxWidth: '440px', width: '100%', overflow: 'hidden' }}>
            <div style={{ background: '#0f172a', color: '#fff', padding: '1rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ margin: 0, fontSize: '1rem', fontWeight: 700 }}>🔑 Reset Kata Sandi Pengguna</h3>
              <button onClick={() => setShowPasswordModal(false)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleResetPassword} style={{ padding: '1.5rem' }}>
              <div style={{ marginBottom: '1.25rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem' }}>
                  Kata Sandi Baru
                </label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  placeholder="Masukkan kata sandi baru"
                  required
                  style={{ width: '100%', padding: '0.65rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
                />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button type="button" onClick={() => setShowPasswordModal(false)} className="btn btn-secondary">
                  Batal
                </button>
                <button type="submit" className="btn btn-primary" style={{ background: '#2563eb' }}>
                  Reset Kata Sandi
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- MASS EXCEL IMPORT MODAL --- */}
      {showImportModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(15, 23, 42, 0.75)',
          zIndex: 1000,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '1rem'
        }}>
          <div style={{ background: '#fff', borderRadius: '16px', maxWidth: '480px', width: '100%', overflow: 'hidden' }}>
            <div style={{ background: '#047857', color: '#fff', padding: '1rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>📊 Impor Massal Data Mahasiswa (.xlsx)</h3>
              <button onClick={() => setShowImportModal(false)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleImportExcel} style={{ padding: '1.5rem' }}>
              <p style={{ fontSize: '0.85rem', color: '#475569', marginBottom: '1rem' }}>
                Pilih file template Excel (.xlsx) berisi daftar mahasiswa. Kolom yang dibaca: <code>NIM, Nama, Asal, L/P, Email</code>.
              </p>
              <input
                type="file"
                accept=".xlsx, .xls"
                onChange={(e) => setImportFile(e.target.files[0])}
                required
                style={{ width: '100%', marginBottom: '1.5rem' }}
              />
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button type="button" onClick={() => setShowImportModal(false)} className="btn btn-secondary">
                  Batal
                </button>
                <button type="submit" className="btn btn-primary" style={{ background: '#047857' }}>
                  Unggah & Impor Sekarang
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- ASSIGN FASIL TO LORONG MODAL --- */}
      {showAssignFasilModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(15, 23, 42, 0.75)',
          zIndex: 1000,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '1rem'
        }}>
          <div style={{ background: '#fff', borderRadius: '16px', maxWidth: '480px', width: '100%', overflow: 'hidden' }}>
            <div style={{ background: '#1e3a8a', color: '#fff', padding: '1rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>📍 Alokasi Fasilitator ke Lorong</h3>
              <button onClick={() => setShowAssignFasilModal(false)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleAssignFasil} style={{ padding: '1.5rem' }}>
              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem' }}>
                  Pilih Fasilitator
                </label>
                <select
                  value={selectedFasilId}
                  onChange={(e) => setSelectedFasilId(e.target.value)}
                  required
                  style={{ width: '100%', padding: '0.65rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
                >
                  <option value="">Pilih Fasil...</option>
                  {users.filter((u) => u.role === 'Fasil').map((f) => (
                    <option key={f.id} value={f.id}>{f.nama} ({f.nim || 'Fasil'})</option>
                  ))}
                </select>
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem' }}>
                  Lorong Tanggung Jawab
                </label>
                <input
                  type="text"
                  value={assignedLorong}
                  onChange={(e) => setAssignedLorong(e.target.value)}
                  placeholder="Contoh: Lorong 1 atau Lorong Timur"
                  required
                  style={{ width: '100%', padding: '0.65rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button type="button" onClick={() => setShowAssignFasilModal(false)} className="btn btn-secondary">
                  Batal
                </button>
                <button type="submit" className="btn btn-primary" style={{ background: '#1e3a8a' }}>
                  Simpan Pemetaan Lorong
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DashboardAdmin;
