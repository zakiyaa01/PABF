import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { presensiService, izinService } from '../services/api';
import { Sun, Moon, Calendar, Clock, CheckCircle2, FilePlus, AlertCircle } from 'lucide-react';

const DashboardPenghuni = ({ activeTab = 'dashboard' }) => {
  const { user } = useAuth();
  const [presensiList, setPresensiList] = useState([]);
  const [izinList, setIzinList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  // Izin Form state
  const [showIzinModal, setShowIzinModal] = useState(false);
  const [tglMulai, setTglMulai] = useState('');
  const [tglSelesai, setTglSelesai] = useState('');
  const [alasan, setAlasan] = useState('');

  // Clock
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const fetchData = async () => {
    if (!user?.id) return;
    setLoading(true);
    try {
      const presRes = await presensiService.getUserPresensi(user.id);
      if (presRes.success) setPresensiList(presRes.data);

      const izinRes = await izinService.getUserIzin(user.id);
      if (izinRes.success) setIzinList(izinRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [user]);

  const handlePresensi = async (jenisPresensi) => {
    setMessage({ type: '', text: '' });
    try {
      const res = await presensiService.submitPresensi(user.id, jenisPresensi, null, null, `Presensi ${jenisPresensi} via Web`);
      if (res.success) {
        setMessage({ type: 'success', text: `Berhasil! ${res.message}` });
        fetchData();
      }
    } catch (err) {
      setMessage({ type: 'danger', text: err.response?.data?.message || 'Gagal melakukan presensi!' });
    }
  };

  const handleIzinSubmit = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });
    try {
      const res = await izinService.submitIzin(user.id, tglMulai, tglSelesai, alasan);
      if (res.success) {
        setMessage({ type: 'success', text: 'Pengajuan izin berhasil dikirim!' });
        setShowIzinModal(false);
        setTglMulai('');
        setTglSelesai('');
        setAlasan('');
        fetchData();
      }
    } catch (err) {
      setMessage({ type: 'danger', text: err.response?.data?.message || 'Gagal mengajukan izin!' });
    }
  };

  // Jam Server Check
  const currentHour = time.getHours();
  const currentMinute = time.getMinutes();
  const timeInMinutes = currentHour * 60 + currentMinute;

  // Subuh: 04:00 (240m) - 06:00 (360m)
  const isSubuhTime = timeInMinutes >= 240 && timeInMinutes <= 360;
  // Malam: 18:00 (1080m) - 20:30 (1230m)
  const isMalamTime = timeInMinutes >= 1080 && timeInMinutes <= 1230;

  const formatCountdown = (endMinute) => {
    const diff = endMinute - timeInMinutes;
    if (diff <= 0) return '0j 0m';
    const h = Math.floor(diff / 60);
    const m = diff % 60;
    return `${h}j ${m}m tersisa`;
  };

  return (
    <div>
      {/* Welcome Banner */}
      {activeTab === 'dashboard' && (
        <div className="card" style={{ marginBottom: '1.5rem', background: 'linear-gradient(135deg, var(--primary) 0%, var(--accent) 100%)', color: '#fff', border: 'none', boxShadow: '0 10px 15px -3px rgba(30, 81, 40, 0.3)' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 700, letterSpacing: '-0.025em' }}>Selamat Datang, {user?.nama}! 👋</h2>
        <p style={{ fontSize: '0.9rem', color: '#d1fae5', marginTop: '0.4rem', opacity: 0.9 }}>
          Penghuni {user?.namaGedung || 'Asrama UNAND'} - Kamar {user?.noKamar || '-'}
        </p>
        <div style={{ marginTop: '1.25rem', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '1.25rem', backgroundColor: 'rgba(255,255,255,0.15)', padding: '0.6rem 1rem', borderRadius: '8px', display: 'inline-flex' }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><Calendar size={16} /> {time.toLocaleDateString('id-ID', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><Clock size={16} /> {time.toLocaleTimeString('id-ID')} WIB</span>
        </div>
        </div>
      )}

      {message.text && (
        <div style={{
          backgroundColor: message.type === 'success' ? '#ecfdf5' : '#fef2f2',
          color: message.type === 'success' ? '#065f46' : '#991b1b',
          padding: '0.85rem 1rem',
          borderRadius: '8px',
          marginBottom: '1.5rem',
          border: `1px solid ${message.type === 'success' ? '#a7f3d0' : '#fecaca'}`
        }}>
          {message.text}
        </div>
      )}

      {/* Cards Session Presensi */}
      {(activeTab === 'dashboard' || activeTab === 'presensi') && (
        <>
          <div className="grid-2" style={{ marginBottom: '1.5rem' }}>
        {/* Card Subuh */}
        <div className="card" style={{ borderLeft: isSubuhTime ? '4px solid var(--success)' : '4px solid #cbd5e1', position: 'relative', overflow: 'hidden' }}>
          {isSubuhTime && <div style={{ position: 'absolute', top: 0, right: 0, backgroundColor: 'var(--success)', color: 'white', fontSize: '0.7rem', padding: '0.25rem 0.75rem', borderBottomLeftRadius: '10px', fontWeight: 'bold' }}>Sedang Berlangsung</div>}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Sun size={24} color={isSubuhTime ? 'var(--success)' : '#94a3b8'} />
              <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Presensi Subuh</h3>
            </div>
            <span style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 500, backgroundColor: '#f1f5f9', padding: '0.25rem 0.5rem', borderRadius: '4px' }}>04:00 - 06:00 WIB</span>
          </div>
          <p style={{ fontSize: '0.85rem', color: '#475569', marginBottom: '1.25rem', minHeight: '40px' }}>
            {isSubuhTime ? 
              <span style={{ color: 'var(--success)', fontWeight: 600 }}>🟢 Sesi Aktif • {formatCountdown(360)}</span> : 
              '🟡 Di luar jadwal sesi Subuh'}
          </p>
          <button
            onClick={() => handlePresensi('Subuh')}
            className={`btn ${isSubuhTime ? 'btn-primary btn-pulse' : 'btn'}`}
            style={{ width: '100%', backgroundColor: isSubuhTime ? '' : '#e2e8f0', color: isSubuhTime ? '' : '#64748b', cursor: isSubuhTime ? 'pointer' : 'not-allowed' }}
            disabled={!isSubuhTime}
          >
            <CheckCircle2 size={16} /> Presensi Subuh
          </button>
        </div>

        {/* Card Malam */}
        <div className="card" style={{ borderLeft: isMalamTime ? '4px solid var(--primary)' : '4px solid #cbd5e1', position: 'relative', overflow: 'hidden' }}>
          {isMalamTime && <div style={{ position: 'absolute', top: 0, right: 0, backgroundColor: 'var(--primary)', color: 'white', fontSize: '0.7rem', padding: '0.25rem 0.75rem', borderBottomLeftRadius: '10px', fontWeight: 'bold' }}>Sedang Berlangsung</div>}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Moon size={24} color={isMalamTime ? 'var(--primary)' : '#94a3b8'} />
              <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Presensi Malam</h3>
            </div>
            <span style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 500, backgroundColor: '#f1f5f9', padding: '0.25rem 0.5rem', borderRadius: '4px' }}>18:00 - 20:30 WIB</span>
          </div>
          <p style={{ fontSize: '0.85rem', color: '#475569', marginBottom: '1.25rem', minHeight: '40px' }}>
            {isMalamTime ? 
              <span style={{ color: 'var(--primary)', fontWeight: 600 }}>🟢 Sesi Aktif • {formatCountdown(1230)}</span> : 
              '🟡 Di luar jadwal sesi Malam'}
          </p>
          <button
            onClick={() => handlePresensi('Malam')}
            className={`btn ${isMalamTime ? 'btn-primary btn-pulse' : 'btn'}`}
            style={{ width: '100%', backgroundColor: isMalamTime ? '' : '#e2e8f0', color: isMalamTime ? '' : '#64748b', cursor: isMalamTime ? 'pointer' : 'not-allowed' }}
            disabled={!isMalamTime}
          >
            <CheckCircle2 size={16} /> Presensi Malam
          </button>
        </div>
      </div>
        </>
      )}

      {/* Button Ajukan Izin */}
      {(activeTab === 'dashboard' || activeTab === 'izin') && (
        <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'flex-end' }}>
          <button onClick={() => setShowIzinModal(true)} className="btn btn-success">
            <FilePlus size={16} /> Form Pengajuan Izin
          </button>
        </div>
      )}

      {/* Modal Izin */}
      {showIzinModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100
        }}>
          <div className="card" style={{ width: '100%', maxWidth: '480px' }}>
            <h3 style={{ marginBottom: '1rem', fontSize: '1.1rem' }}>Formulir Pengajuan Izin</h3>
            <form onSubmit={handleIzinSubmit}>
              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Tanggal Mulai</label>
                <input type="date" value={tglMulai} onChange={(e) => setTglMulai(e.target.value)} required style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              </div>
              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Tanggal Selesai</label>
                <input type="date" value={tglSelesai} onChange={(e) => setTglSelesai(e.target.value)} required style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
              </div>
              <div style={{ marginBottom: '1.25rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Alasan Izin</label>
                <textarea value={alasan} onChange={(e) => setAlasan(e.target.value)} required rows={3} style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} placeholder="Jelaskan alasan pengajuan izin..." />
              </div>
              <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                <button type="button" onClick={() => setShowIzinModal(false)} className="btn" style={{ backgroundColor: '#e2e8f0', color: '#475569' }}>Batal</button>
                <button type="submit" className="btn btn-primary">Kirim Izin</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Tabel Riwayat Presensi */}
      {(activeTab === 'dashboard' || activeTab === 'presensi') && (
        <div className="card" style={{ marginTop: '1.5rem' }}>
        <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '1rem' }}>Riwayat Presensi Personal</h3>
        {presensiList.length === 0 ? (
          <p style={{ fontSize: '0.85rem', color: '#94a3b8', fontStyle: 'italic' }}>Belum ada data presensi.</p>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>No</th>
                  <th>Tanggal</th>
                  <th>Waktu Absen</th>
                  <th>Keterangan / Jenis</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {presensiList.map((item, idx) => (
                  <tr key={item.id}>
                    <td>{idx + 1}</td>
                    <td>{item.tanggal}</td>
                    <td>{new Date(item.waktuAbsen).toLocaleTimeString('id-ID')} WIB</td>
                    <td>{item.keterangan || '-'}</td>
                    <td>
                      <span className="badge-role" style={{ backgroundColor: '#ecfdf5', color: '#047857' }}>
                        {item.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
      )}
    </div>
  );
};

export default DashboardPenghuni;
