import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { presensiService, izinService } from '../services/api';
import {
  FileText, CheckCircle, XCircle, Users, Edit3, ShieldAlert,
  Printer, Download, Eye, X, History, Filter, AlertTriangle,
  ArrowRight, Search, CheckSquare, Clock, ChevronRight
} from 'lucide-react';

const DashboardFasil = ({ activeTab = 'dashboard' }) => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [izinList, setIzinList] = useState([]);
  const [presensiList, setPresensiList] = useState([]);
  const [message, setMessage] = useState('');

  // Filter states
  const [izinFilterStatus, setIzinFilterStatus] = useState('Semua');
  const [presensiSearch, setPresensiSearch] = useState('');
  const [presensiFilterSesi, setPresensiFilterSesi] = useState('Semua');
  const [presensiFilterStatus, setPresensiFilterStatus] = useState('Semua');

  // Koreksi Presensi Modal
  const [showKoreksiModal, setShowKoreksiModal] = useState(false);
  const [selectedPresensi, setSelectedPresensi] = useState(null);
  const [statusBaru, setStatusBaru] = useState('Hadir');
  const [alasanKoreksi, setAlasanKoreksi] = useState('');

  // Audit Logs Modal
  const [showAuditModal, setShowAuditModal] = useState(false);
  const [auditLogs, setAuditLogs] = useState([]);

  // Izin Approval Modal (with Mandatory Catatan Fasil & Lightbox Viewer)
  const [showIzinModal, setShowIzinModal] = useState(false);
  const [selectedIzin, setSelectedIzin] = useState(null);
  const [izinDecisionStatus, setIzinDecisionStatus] = useState('Disetujui');
  const [catatanFasil, setCatatanFasil] = useState('');
  const [lightboxImage, setLightboxImage] = useState(null);

  const loadData = async () => {
    try {
      const izinRes = await izinService.getAllIzin();
      if (izinRes.success) setIzinList(izinRes.data);

      const presRes = await presensiService.getAllPresensi();
      if (presRes.success) setPresensiList(presRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Open Izin Review Modal
  const handleOpenIzinReview = (item, decision) => {
    setSelectedIzin(item);
    setIzinDecisionStatus(decision);
    setCatatanFasil('');
    setShowIzinModal(true);
  };

  // Submit Izin Review
  const handleSubmitIzinReview = async (e) => {
    e.preventDefault();
    if (!catatanFasil.trim()) {
      alert('Catatan Fasilitator wajib diisi!');
      return;
    }

    try {
      const res = await izinService.reviewIzin(
        selectedIzin.id,
        izinDecisionStatus,
        catatanFasil,
        user?.id || 2
      );
      if (res.success) {
        setMessage(`Pengajuan izin ${selectedIzin.user?.nama} telah diperbarui: ${izinDecisionStatus}`);
        setShowIzinModal(false);
        loadData();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Gagal memproses keputusan izin');
    }
  };

  // Open Koreksi Presensi Modal
  const handleOpenKoreksi = (presensi) => {
    setSelectedPresensi(presensi);
    setStatusBaru('Hadir');
    setAlasanKoreksi('');
    setShowKoreksiModal(true);
  };

  // Submit Koreksi Presensi
  const handleSubmitKoreksi = async (e) => {
    e.preventDefault();
    if (!alasanKoreksi.trim()) {
      alert('Alasan koreksi presensi wajib diisi demi audit integritas!');
      return;
    }

    try {
      const res = await presensiService.koreksiPresensi(
        selectedPresensi.id,
        statusBaru,
        alasanKoreksi,
        user?.id || 2,
        user?.nama || 'Fasilitator Utama'
      );
      if (res.success) {
        setMessage(`Koreksi presensi berhasil dicatat ke Immutable Audit Trail Log!`);
        setShowKoreksiModal(false);
        loadData();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Gagal mengoreksi presensi');
    }
  };

  // Open Audit Trail Log
  const handleViewAuditLogs = async (presensiId) => {
    try {
      const res = await presensiService.getAuditLogs(presensiId);
      if (res.success) {
        setAuditLogs(res.data);
        setShowAuditModal(true);
      }
    } catch (err) {
      alert('Gagal mengambil audit log');
    }
  };

  // Stats calculation
  const totalBinaan = presensiList.length;
  const hadirCount = presensiList.filter((p) => p.status === 'Hadir' || p.status === 'Terlambat').length;
  const izinCount = presensiList.filter((p) => p.status === 'Izin').length;
  const alphaCount = presensiList.filter((p) => p.status === 'Alpha').length;
  const attendanceRate = totalBinaan > 0 ? Math.round((hadirCount / totalBinaan) * 100) : 0;
  const currentTab = (activeTab === 'verifikasi' || activeTab === 'rekap') ? activeTab : 'dashboard';

  return (
    <div>
      {/* Banner */}
      <div className="card" style={{ marginBottom: '1.5rem', background: 'linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%)', color: '#fff', border: 'none', borderRadius: '16px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <span style={{ background: '#f59e0b', color: '#000', fontSize: '0.75rem', fontWeight: 800, padding: '2px 8px', borderRadius: '4px' }}>
              PORTAL FASILITATOR ASRAMA
            </span>
            <h1 style={{ fontSize: '1.5rem', fontWeight: 800, margin: '0.5rem 0 0.25rem' }}>
              {currentTab === 'verifikasi' ? 'Verifikasi Perizinan Santri Asrama' :
               currentTab === 'rekap' ? 'Rekapitulasi Presensi & Koreksi Audit' :
               'Monitoring & Ringkasan Presensi Binaan'}
            </h1>
            <p style={{ fontSize: '0.85rem', color: '#cbd5e1', margin: 0 }}>
              Wilayah Binaan: <strong>{user?.lorongBinaan || 'Lorong 1'} • Gedung Asrama Putra A</strong>
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              onClick={() => window.print()}
              className="btn btn-secondary"
              style={{ background: 'rgba(255,255,255,0.15)', color: '#fff', border: '1px solid rgba(255,255,255,0.2)', padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px' }}
            >
              <Printer size={16} /> Cetak Laporan
            </button>
            <a
              href={presensiService.exportPresensiExcelUrl()}
              target="_blank"
              rel="noreferrer"
              className="btn btn-secondary"
              style={{ background: '#059669', color: '#fff', border: 'none', padding: '0.5rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '6px', textDecoration: 'none' }}
            >
              <Download size={16} /> Ekspor Excel
            </a>
          </div>
        </div>
      </div>

      {message && (
        <div style={{ backgroundColor: '#ecfdf5', color: '#065f46', padding: '0.85rem 1.25rem', borderRadius: '8px', marginBottom: '1.5rem', border: '1px solid #a7f3d0' }}>
          ✓ {message}
        </div>
      )}

      {/* ============================================================== */}
      {/* 1. DASHBOARD OVERVIEW TAB (currentTab === 'dashboard')          */}
      {/* ============================================================== */}
      {currentTab === 'dashboard' && (
        <>
          {/* Visual Analytics Widgets */}
          <div className="grid-3" style={{ marginBottom: '1.5rem' }}>
            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', borderLeft: '4px solid #10b981' }}>
              <div style={{ backgroundColor: '#ecfdf5', padding: '0.85rem', borderRadius: '12px' }}>
                <CheckCircle size={28} color="#059669" />
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Tingkat Kehadiran Binaan</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#0f172a' }}>{attendanceRate}%</div>
                <div style={{ fontSize: '0.75rem', color: '#10b981' }}>{hadirCount} Hadir dari {totalBinaan} Presensi</div>
              </div>
            </div>

            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', borderLeft: '4px solid #f59e0b' }}>
              <div style={{ backgroundColor: '#fef3c7', padding: '0.85rem', borderRadius: '12px' }}>
                <FileText size={28} color="#d97706" />
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Izin Menunggu Verifikasi</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#0f172a' }}>
                  {izinList.filter((i) => i.status === 'Menunggu').length}
                </div>
                <div style={{ fontSize: '0.75rem', color: '#d97706' }}>Membutuhkan persetujuan Fasil</div>
              </div>
            </div>

            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', borderLeft: '4px solid #ef4444' }}>
              <div style={{ backgroundColor: '#fee2e2', padding: '0.85rem', borderRadius: '12px' }}>
                <AlertTriangle size={28} color="#dc2626" />
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Total Alpha / Tidak Hadir</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#0f172a' }}>{alphaCount}</div>
                <div style={{ fontSize: '0.75rem', color: '#ef4444' }}>Dapat dikoreksi bila ada kendala</div>
              </div>
            </div>
          </div>

          {/* Quick Nav Shortcut Cards */}
          <div className="grid-2" style={{ marginBottom: '1.5rem' }}>
            <div className="card" style={{ borderRadius: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <FileText size={20} color="#2563eb" />
                  <strong>Verifikasi Perizinan Santri</strong>
                </div>
                <span className="badge-role" style={{ background: '#fef3c7', color: '#b45309', fontWeight: 700 }}>
                  {izinList.filter((i) => i.status === 'Menunggu').length} Menunggu
                </span>
              </div>
              <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '0 0 1rem 0' }}>
                Tinjau surat sakit, izin pulang libur, atau dispensasi kampus santri binaan.
              </p>
              <button
                onClick={() => navigate('/verifikasi')}
                className="btn btn-secondary"
                style={{ width: '100%', padding: '0.6rem', fontSize: '0.85rem', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}
              >
                <span>Buka Panel Verifikasi Izin</span> <ArrowRight size={14} />
              </button>
            </div>

            <div className="card" style={{ borderRadius: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <Users size={20} color="#059669" />
                  <strong>Rekapitulasi & Koreksi Presensi</strong>
                </div>
                <span className="badge-role" style={{ background: '#ecfdf5', color: '#047857', fontWeight: 700 }}>
                  Live Sync
                </span>
              </div>
              <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '0 0 1rem 0' }}>
                Pantau kehadiran subuh & malam serta lakukan koreksi manual ber-audit trail.
              </p>
              <button
                onClick={() => navigate('/rekap')}
                className="btn btn-secondary"
                style={{ width: '100%', padding: '0.6rem', fontSize: '0.85rem', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}
              >
                <span>Buka Panel Rekap Presensi</span> <ArrowRight size={14} />
              </button>
            </div>
          </div>

          {/* Izin Prioritas Menunggu */}
          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Clock size={18} color="#d97706" /> Pengajuan Izin Menunggu Persetujuan
              </h3>
              <button
                onClick={() => navigate('/verifikasi')}
                className="btn btn-secondary"
                style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '4px' }}
              >
                Lihat Semua Izin ({izinList.length}) <ChevronRight size={14} />
              </button>
            </div>

            {izinList.filter((i) => i.status === 'Menunggu').length === 0 ? (
              <p style={{ fontSize: '0.85rem', color: '#059669', padding: '1rem 0', margin: 0 }}>
                ✓ Semua pengajuan izin santri binaan sudah selesai ditinjau.
              </p>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Penghuni</th>
                      <th>NIM</th>
                      <th>Kategori</th>
                      <th>Periode</th>
                      <th>Alasan</th>
                      <th>Bukti</th>
                      <th>Aksi</th>
                    </tr>
                  </thead>
                  <tbody>
                    {izinList.filter((i) => i.status === 'Menunggu').slice(0, 5).map((item) => (
                      <tr key={item.id}>
                        <td><strong>{item.user?.nama || 'Penghuni'}</strong></td>
                        <td>{item.user?.nim || '-'}</td>
                        <td>
                          <span className="badge-role" style={{ background: '#f1f5f9', color: '#1e293b' }}>
                            {item.kategori}
                          </span>
                        </td>
                        <td style={{ fontSize: '0.8rem' }}>{item.tglMulai} s/d {item.tglSelesai}</td>
                        <td style={{ fontSize: '0.8rem' }}>{item.alasan}</td>
                        <td>
                          {item.fileBukti ? (
                            <button
                              onClick={() => {
                                const url = item.fileBukti.startsWith('http') ? item.fileBukti : `http://localhost:8080${item.fileBukti.startsWith('/') ? '' : '/'}${item.fileBukti}`;
                                setLightboxImage(url);
                              }}
                              className="btn btn-secondary"
                              style={{ padding: '0.2rem 0.5rem', fontSize: '0.7rem' }}
                            >
                              <Eye size={12} /> Bukti
                            </button>
                          ) : '-'}
                        </td>
                        <td>
                          <div style={{ display: 'flex', gap: '0.35rem' }}>
                            <button
                              onClick={() => handleOpenIzinReview(item, 'Disetujui')}
                              className="btn btn-success"
                              style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                            >
                              <CheckCircle size={12} /> Setujui
                            </button>
                            <button
                              onClick={() => handleOpenIzinReview(item, 'Ditolak')}
                              className="btn btn-danger"
                              style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                            >
                              <XCircle size={12} /> Tolak
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}

      {/* ============================================================== */}
      {/* 2. VERIFIKASI IZIN TAB (currentTab === 'verifikasi')            */}
      {/* ============================================================== */}
      {currentTab === 'verifikasi' && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.75rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <FileText size={18} color="#2563eb" /> Pengajuan Izin Digital Penghuni Binaan
            </h3>

            {/* Status Filter */}
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              {['Semua', 'Menunggu', 'Disetujui', 'Ditolak'].map((st) => (
                <button
                  key={st}
                  onClick={() => setIzinFilterStatus(st)}
                  className={`btn ${izinFilterStatus === st ? 'btn-primary' : 'btn-secondary'}`}
                  style={{ padding: '0.35rem 0.75rem', fontSize: '0.75rem', borderRadius: '6px' }}
                >
                  {st} {st !== 'Semua' && `(${izinList.filter((i) => i.status === st).length})`}
                </button>
              ))}
            </div>
          </div>

          {izinList.filter((item) => izinFilterStatus === 'Semua' || item.status === izinFilterStatus).length === 0 ? (
            <p style={{ fontSize: '0.85rem', color: '#94a3b8', padding: '1.5rem', textAlign: 'center' }}>
              Tidak ada pengajuan izin dengan status "{izinFilterStatus}".
            </p>
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>No</th>
                    <th>Penghuni</th>
                    <th>NIM</th>
                    <th>Kategori</th>
                    <th>Periode Izin</th>
                    <th>Alasan</th>
                    <th>Bukti Dokumen</th>
                    <th>Status</th>
                    <th>Aksi Keputusan</th>
                  </tr>
                </thead>
                <tbody>
                  {izinList
                    .filter((item) => izinFilterStatus === 'Semua' || item.status === izinFilterStatus)
                    .map((item, idx) => (
                    <tr key={item.id}>
                      <td>{idx + 1}</td>
                      <td><strong>{item.user?.nama || 'Penghuni'}</strong></td>
                      <td>{item.user?.nim || '-'}</td>
                      <td>
                        <span className="badge-role" style={{ background: '#f1f5f9', color: '#1e293b' }}>
                          {item.kategori || 'Sakit'}
                        </span>
                      </td>
                      <td style={{ fontSize: '0.8rem' }}>{item.tglMulai} s/d {item.tglSelesai}</td>
                      <td style={{ fontSize: '0.8rem' }}>{item.alasan}</td>
                      <td>
                        {item.fileBukti ? (
                          <button
                            onClick={() => {
                              const url = item.fileBukti.startsWith('http') ? item.fileBukti : `http://localhost:8080${item.fileBukti.startsWith('/') ? '' : '/'}${item.fileBukti}`;
                              setLightboxImage(url);
                            }}
                            className="btn btn-secondary"
                            style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                          >
                            <Eye size={12} /> Lightbox
                          </button>
                        ) : (
                          <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Tidak ada</span>
                        )}
                      </td>
                      <td>
                        <span className="badge-role" style={{
                          backgroundColor: item.status === 'Disetujui' ? '#ecfdf5' : item.status === 'Ditolak' ? '#fee2e2' : '#fef3c7',
                          color: item.status === 'Disetujui' ? '#047857' : item.status === 'Ditolak' ? '#b91c1c' : '#b45309'
                        }}>
                          {item.status}
                        </span>
                      </td>
                      <td>
                        {item.status === 'Menunggu' ? (
                          <div style={{ display: 'flex', gap: '0.35rem' }}>
                            <button
                              onClick={() => handleOpenIzinReview(item, 'Disetujui')}
                              className="btn btn-success"
                              style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                            >
                              <CheckCircle size={12} /> Setujui
                            </button>
                            <button
                              onClick={() => handleOpenIzinReview(item, 'Ditolak')}
                              className="btn btn-danger"
                              style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                            >
                              <XCircle size={12} /> Tolak
                            </button>
                          </div>
                        ) : (
                          <span style={{ fontSize: '0.75rem', color: '#64748b' }}>
                            Catatan: {item.catatanFasil || '-'}
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* ============================================================== */}
      {/* 3. REKAP PRESENSI TAB (currentTab === 'rekap')                  */}
      {/* ============================================================== */}
      {currentTab === 'rekap' && (
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.75rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Users size={18} color="#1e3a8a" /> Rekapitulasi Presensi & Koreksi Manual Ber-Audit
              </h3>
              <span style={{ fontSize: '0.75rem', color: '#047857', background: '#ecfdf5', padding: '3px 8px', borderRadius: '10px', fontWeight: 700 }}>
                ● LIVE SYNC
              </span>
            </div>

            {/* Filter & Search Toolbar */}
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              <div style={{ position: 'relative' }}>
                <Search size={14} color="#94a3b8" style={{ position: 'absolute', left: '8px', top: '9px' }} />
                <input
                  type="text"
                  placeholder="Cari Nama / NIM..."
                  value={presensiSearch}
                  onChange={(e) => setPresensiSearch(e.target.value)}
                  style={{ padding: '0.4rem 0.5rem 0.4rem 1.8rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.8rem' }}
                />
              </div>

              <select
                value={presensiFilterSesi}
                onChange={(e) => setPresensiFilterSesi(e.target.value)}
                style={{ padding: '0.4rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.8rem' }}
              >
                <option value="Semua">Semua Sesi</option>
                <option value="SUBUH">Subuh</option>
                <option value="MALAM">Malam</option>
              </select>

              <select
                value={presensiFilterStatus}
                onChange={(e) => setPresensiFilterStatus(e.target.value)}
                style={{ padding: '0.4rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.8rem' }}
              >
                <option value="Semua">Semua Status</option>
                <option value="Hadir">Hadir</option>
                <option value="Terlambat">Terlambat</option>
                <option value="Izin">Izin</option>
                <option value="Alpha">Alpha</option>
              </select>
            </div>
          </div>

          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>No</th>
                  <th>Nama Penghuni</th>
                  <th>NIM</th>
                  <th>Tanggal</th>
                  <th>Sesi</th>
                  <th>Status Presensi</th>
                  <th>Jarak Geofence</th>
                  <th>Foto Geotag</th>
                  <th>Aksi Koreksi</th>
                </tr>
              </thead>
              <tbody>
                {presensiList
                  .filter((p) => {
                    if (presensiFilterSesi !== 'Semua' && p.jenisPresensi !== presensiFilterSesi) return false;
                    if (presensiFilterStatus !== 'Semua' && p.status !== presensiFilterStatus) return false;
                    if (presensiSearch.trim()) {
                      const q = presensiSearch.toLowerCase();
                      const matchNama = p.user?.nama?.toLowerCase().includes(q);
                      const matchNim = p.user?.nim?.toLowerCase().includes(q);
                      if (!matchNama && !matchNim) return false;
                    }
                    return true;
                  })
                  .length === 0 ? (
                  <tr>
                    <td colSpan="9" style={{ textAlign: 'center', color: '#94a3b8', padding: '2rem' }}>
                      Tidak ditemukan rekaman presensi yang sesuai kriteria pencarian.
                    </td>
                  </tr>
                ) : (
                  presensiList
                    .filter((p) => {
                      if (presensiFilterSesi !== 'Semua' && p.jenisPresensi !== presensiFilterSesi) return false;
                      if (presensiFilterStatus !== 'Semua' && p.status !== presensiFilterStatus) return false;
                      if (presensiSearch.trim()) {
                        const q = presensiSearch.toLowerCase();
                        const matchNama = p.user?.nama?.toLowerCase().includes(q);
                        const matchNim = p.user?.nim?.toLowerCase().includes(q);
                        if (!matchNama && !matchNim) return false;
                      }
                      return true;
                    })
                    .map((p, idx) => (
                    <tr key={p.id}>
                      <td>{idx + 1}</td>
                      <td><strong>{p.user?.nama}</strong></td>
                      <td>{p.user?.nim}</td>
                      <td>{p.tanggal}</td>
                      <td>
                        <span className="badge-role" style={{ background: '#f8fafc', color: '#334155' }}>
                          {p.jenisPresensi}
                        </span>
                      </td>
                      <td>
                        <span className="badge-role" style={{
                          backgroundColor: p.status === 'Hadir' ? '#ecfdf5' : p.status === 'Terlambat' ? '#fef3c7' : p.status === 'Izin' ? '#e0e7ff' : '#fee2e2',
                          color: p.status === 'Hadir' ? '#047857' : p.status === 'Terlambat' ? '#b45309' : p.status === 'Izin' ? '#4338ca' : '#b91c1c'
                        }}>
                          {p.status}
                        </span>
                      </td>
                      <td style={{ fontSize: '0.75rem' }}>
                        {p.distanceMeters ? `${Math.round(p.distanceMeters)}m` : '0m'}
                      </td>
                      <td>
                        {p.fotoWajah ? (
                          <button
                            onClick={() => {
                              const url = p.fotoWajah.startsWith('http') ? p.fotoWajah : `http://localhost:8080${p.fotoWajah.startsWith('/') ? '' : '/'}${p.fotoWajah}`;
                              setLightboxImage(url);
                            }}
                            className="btn btn-secondary"
                            style={{ padding: '0.2rem 0.5rem', fontSize: '0.7rem' }}
                          >
                            Lihat
                          </button>
                        ) : '-'}
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.35rem' }}>
                          <button
                            onClick={() => handleOpenKoreksi(p)}
                            className="btn btn-secondary"
                            style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                            title="Koreksi presensi dengan catatan audit"
                          >
                            <Edit3 size={12} /> Koreksi
                          </button>
                          <button
                            onClick={() => handleViewAuditLogs(p.id)}
                            className="btn btn-secondary"
                            style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                            title="Lihat riwayat audit"
                          >
                            <History size={12} /> Audit Log
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

      {/* --- MODAL MANUAL ADJUSTMENT PRESENSI WITH AUDIT LOG --- */}
      {showKoreksiModal && selectedPresensi && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(15, 23, 42, 0.75)',
          backdropFilter: 'blur(4px)',
          zIndex: 1000,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '1rem'
        }}>
          <div style={{ background: '#fff', borderRadius: '16px', maxWidth: '520px', width: '100%', overflow: 'hidden' }}>
            <div style={{ background: '#0f172a', color: '#fff', padding: '1rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>
                ⚖️ Koreksi Presensi (Immutable Audit Trail)
              </h3>
              <button onClick={() => setShowKoreksiModal(false)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmitKoreksi} style={{ padding: '1.5rem' }}>
              <div style={{ marginBottom: '1rem', background: '#f8fafc', padding: '0.75rem', borderRadius: '8px', fontSize: '0.85rem' }}>
                <div><strong>Mahasiswa:</strong> {selectedPresensi.user?.nama} ({selectedPresensi.user?.nim})</div>
                <div><strong>Tanggal / Sesi:</strong> {selectedPresensi.tanggal} - {selectedPresensi.jenisPresensi}</div>
                <div><strong>Status Saat Ini:</strong> <span style={{ color: '#dc2626', fontWeight: 700 }}>{selectedPresensi.status}</span></div>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem' }}>
                  Ubah Status Menjadi
                </label>
                <select
                  value={statusBaru}
                  onChange={(e) => setStatusBaru(e.target.value)}
                  style={{ width: '100%', padding: '0.65rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
                >
                  <option value="Hadir">Hadir</option>
                  <option value="Terlambat">Terlambat</option>
                  <option value="Izin">Izin</option>
                  <option value="Sakit">Sakit</option>
                  <option value="Alpha">Alpha</option>
                </select>
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem', color: '#991b1b' }}>
                  * Alasan Koreksi (Wajib Diisi untuk Audit Trail):
                </label>
                <textarea
                  rows="3"
                  value={alasanKoreksi}
                  onChange={(e) => setAlasanKoreksi(e.target.value)}
                  placeholder="Misal: Mahasiswa hadir fisik namun terjadi gangguan listrik/jaringan di lorong A..."
                  required
                  style={{ width: '100%', padding: '0.65rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button type="button" onClick={() => setShowKoreksiModal(false)} className="btn btn-secondary">
                  Batal
                </button>
                <button type="submit" className="btn btn-primary" style={{ background: '#2563eb', fontWeight: 700 }}>
                  Simpan Koreksi & Catat Audit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- AUDIT TRAIL LOG MODAL --- */}
      {showAuditModal && (
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
          <div style={{ background: '#fff', borderRadius: '16px', maxWidth: '640px', width: '100%', overflow: 'hidden' }}>
            <div style={{ background: '#0f172a', color: '#fff', padding: '1rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>
                📜 Immutable Audit Trail Log
              </h3>
              <button onClick={() => setShowAuditModal(false)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <div style={{ padding: '1.5rem', maxHeight: '400px', overflowY: 'auto' }}>
              {auditLogs.length === 0 ? (
                <p style={{ textAlign: 'center', color: '#94a3b8' }}>Belum ada catatan koreksi untuk data presensi ini.</p>
              ) : (
                auditLogs.map((log) => (
                  <div key={log.id} style={{
                    padding: '0.85rem',
                    borderRadius: '8px',
                    border: '1px solid #e2e8f0',
                    marginBottom: '0.75rem',
                    background: '#f8fafc'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', color: '#64748b', marginBottom: '0.35rem' }}>
                      <span>Oleh: <strong>{log.fasilNama}</strong> (IP: {log.ipAddress || '127.0.0.1'})</span>
                      <span>{new Date(log.timestamp).toLocaleString('id-ID')}</span>
                    </div>
                    <div style={{ fontSize: '0.85rem', marginBottom: '0.35rem' }}>
                      Perubahan: <span style={{ color: '#dc2626', textDecoration: 'line-through' }}>{log.statusLama}</span> ➔ <strong style={{ color: '#059669' }}>{log.statusBaru}</strong>
                    </div>
                    <div style={{ fontSize: '0.8rem', color: '#334155', fontStyle: 'italic' }}>
                      " {log.alasan} "
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}

      {/* --- LIGHTBOX VIEWER FOR PROOF / WATERMARKED PHOTOS --- */}
      {lightboxImage && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.92)',
          zIndex: 1200,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '1.5rem'
        }}>
          <div style={{ position: 'relative', maxWidth: '850px', width: '100%', textAlign: 'center' }}>
            <button
              onClick={() => setLightboxImage(null)}
              style={{
                position: 'absolute',
                top: '-45px',
                right: '0',
                background: '#fff',
                border: 'none',
                borderRadius: '50%',
                width: '36px',
                height: '36px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <X size={20} />
            </button>
            <img
              src={lightboxImage}
              alt="Lightbox View"
              style={{ maxHeight: '82vh', maxWidth: '100%', borderRadius: '12px', border: '2px solid #f59e0b', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.5)' }}
            />
          </div>
        </div>
      )}

      {/* --- MODAL DECISION IZIN WITH MANDATORY CATATAN FASIL --- */}
      {showIzinModal && selectedIzin && (
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
          <div style={{ background: '#fff', borderRadius: '16px', maxWidth: '520px', width: '100%', overflow: 'hidden' }}>
            <div style={{
              background: izinDecisionStatus === 'Disetujui' ? '#047857' : '#b91c1c',
              color: '#fff',
              padding: '1rem 1.5rem',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>
                {izinDecisionStatus === 'Disetujui' ? '✓ Setujui Pengajuan Izin' : '✕ Tolak Pengajuan Izin'}
              </h3>
              <button onClick={() => setShowIzinModal(false)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmitIzinReview} style={{ padding: '1.5rem' }}>
              <div style={{ marginBottom: '1rem', background: '#f8fafc', padding: '0.75rem', borderRadius: '8px', fontSize: '0.85rem' }}>
                <div><strong>Mahasiswa:</strong> {selectedIzin.user?.nama} ({selectedIzin.user?.nim})</div>
                <div><strong>Kategori:</strong> {selectedIzin.kategori}</div>
                <div><strong>Periode:</strong> {selectedIzin.tglMulai} s/d {selectedIzin.tglSelesai}</div>
                <div><strong>Alasan:</strong> {selectedIzin.alasan}</div>
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem', color: '#1e293b' }}>
                  * Catatan Fasilitator (Wajib Diisi untuk Notifikasi Mahasiswa):
                </label>
                <textarea
                  rows="3"
                  value={catatanFasil}
                  onChange={(e) => setCatatanFasil(e.target.value)}
                  placeholder="Tuliskan catatan, rekomendasi tugas pengganti, atau alasan keputusan..."
                  required
                  style={{ width: '100%', padding: '0.65rem', borderRadius: '8px', border: '1px solid #cbd5e1' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button type="button" onClick={() => setShowIzinModal(false)} className="btn btn-secondary">
                  Batal
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  style={{ background: izinDecisionStatus === 'Disetujui' ? '#047857' : '#b91c1c', fontWeight: 700 }}
                >
                  Konfirmasi {izinDecisionStatus}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DashboardFasil;
