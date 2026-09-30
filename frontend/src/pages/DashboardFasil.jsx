import React, { useState, useEffect } from 'react';
import { presensiService, izinService } from '../services/api';
import { FileText, CheckCircle, XCircle, Users } from 'lucide-react';

const DashboardFasil = ({ activeTab = 'dashboard' }) => {
  const [izinList, setIzinList] = useState([]);
  const [presensiList, setPresensiList] = useState([]);
  const [message, setMessage] = useState('');

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

  const handleDecision = async (id, status) => {
    try {
      const res = await izinService.updateStatus(id, status);
      if (res.success) {
        setMessage(`Pengajuan izin berhasil diubah menjadi: ${status}`);
        loadData();
      }
    } catch (err) {
      alert('Gagal memproses keputusan izin!');
    }
  };

  return (
    <div>
      <div className="card" style={{ marginBottom: '1.5rem', backgroundColor: '#fef3c7', borderLeft: '4px solid #b45309' }}>
        <h2 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#92400e' }}>Dashboard Fasil (Fasilitator Asrama) 📋</h2>
        <p style={{ fontSize: '0.85rem', color: '#b45309', marginTop: '0.25rem' }}>
          Pantau kehadiran penghuni asrama binaan dan lakukan verifikasi pengajuan izin secara real-time.
        </p>
      </div>

      {message && (
        <div style={{ backgroundColor: '#ecfdf5', color: '#065f46', padding: '0.75rem', borderRadius: '8px', marginBottom: '1.5rem', border: '1px solid #a7f3d0' }}>
          {message}
        </div>
      )}

      {/* Verification Izin Card */}
      {(activeTab === 'dashboard' || activeTab === 'verifikasi') && (
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <FileText size={18} color="#2563eb" /> Pengajuan Izin Menunggu Verifikasi
        </h3>

        {izinList.length === 0 ? (
          <p style={{ fontSize: '0.85rem', color: '#94a3b8' }}>Tidak ada pengajuan izin saat ini.</p>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>No</th>
                  <th>Penghuni</th>
                  <th>NIM</th>
                  <th>Tgl Mulai</th>
                  <th>Tgl Selesai</th>
                  <th>Alasan</th>
                  <th>Status</th>
                  <th>Aksi Keputusan</th>
                </tr>
              </thead>
              <tbody>
                {izinList.map((item, idx) => (
                  <tr key={item.id}>
                    <td>{idx + 1}</td>
                    <td><strong>{item.user?.nama || 'Penghuni'}</strong></td>
                    <td>{item.user?.nim || '-'}</td>
                    <td>{item.tglMulai}</td>
                    <td>{item.tglSelesai}</td>
                    <td>{item.alasan}</td>
                    <td>
                      <span className="badge-role" style={{
                        backgroundColor: item.status === 'Disetujui' ? '#ecfdf5' : item.status === 'Ditolak' ? '#fef2f2' : '#fef3c7',
                        color: item.status === 'Disetujui' ? '#047857' : item.status === 'Ditolak' ? '#b91c1c' : '#b45309'
                      }}>
                        {item.status}
                      </span>
                    </td>
                    <td>
                      {item.status === 'Menunggu' ? (
                        <div style={{ display: 'flex', gap: '0.35rem' }}>
                          <button onClick={() => handleDecision(item.id, 'Disetujui')} className="btn btn-success" style={{ padding: '0.3rem 0.5rem', fontSize: '0.75rem' }}>
                            <CheckCircle size={12} /> Setujui
                          </button>
                          <button onClick={() => handleDecision(item.id, 'Ditolak')} className="btn btn-danger" style={{ padding: '0.3rem 0.5rem', fontSize: '0.75rem' }}>
                            <XCircle size={12} /> Tolak
                          </button>
                        </div>
                      ) : (
                        <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Selesai</span>
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

      {/* Monitoring Rekap Presensi */}
      {(activeTab === 'dashboard' || activeTab === 'rekap') && (
      <div className="card">
        <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Users size={18} color="#1e3a8a" /> Rekapitulasi Presensi Seluruh Penghuni
        </h3>
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>No</th>
                <th>Nama Penghuni</th>
                <th>NIM</th>
                <th>Tanggal</th>
                <th>Waktu</th>
                <th>Sesi / Keterangan</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {presensiList.length === 0 ? (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', color: '#94a3b8' }}>Belum ada data presensi penghuni.</td>
                </tr>
              ) : (
                presensiList.map((p, idx) => (
                  <tr key={p.id}>
                    <td>{idx + 1}</td>
                    <td><strong>{p.user?.nama}</strong></td>
                    <td>{p.user?.nim}</td>
                    <td>{p.tanggal}</td>
                    <td>{new Date(p.waktuAbsen).toLocaleTimeString('id-ID')} WIB</td>
                    <td>{p.keterangan || '-'}</td>
                    <td><span className="badge-role" style={{ backgroundColor: '#ecfdf5', color: '#047857' }}>{p.status}</span></td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
      )}
    </div>
  );
};

export default DashboardFasil;
