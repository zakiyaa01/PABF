import React from 'react';
import { Shield, Building, Users, CheckSquare } from 'lucide-react';

const DashboardAdmin = ({ activeTab = 'dashboard' }) => {
  return (
    <div>
      <div className="card" style={{ marginBottom: '1.5rem', backgroundColor: '#ecfdf5', borderLeft: '4px solid var(--primary)' }}>
        <h2 style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--primary)' }}>Dashboard Administrator Asrama UNAND 🏛️</h2>
        <p style={{ fontSize: '0.85rem', color: '#065f46', marginTop: '0.25rem' }}>
          Kelola master data gedung, kamar, akun penghuni, fasil, dan monitoring rekapitulasi presensi global.
        </p>
      </div>

      {/* Stats Cards */}
      {activeTab === 'dashboard' && (
      <div className="grid-3" style={{ marginBottom: '1.5rem' }}>
        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ backgroundColor: '#eff6ff', padding: '1rem', borderRadius: '12px' }}>
            <Users size={28} color="#2563eb" />
          </div>
          <div>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>Total Penghuni Asrama</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700 }}>2 Akun Demo</div>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ backgroundColor: '#fef3c7', padding: '1rem', borderRadius: '12px' }}>
            <Shield size={28} color="#b45309" />
          </div>
          <div>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>Fasilitator (Fasil)</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700 }}>1 Pembina</div>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ backgroundColor: '#ecfdf5', padding: '1rem', borderRadius: '12px' }}>
            <Building size={28} color="#059669" />
          </div>
          <div>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>Gedung & Kamar</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700 }}>2 Gedung / 2 Kamar</div>
          </div>
        </div>
      </div>
      )}

      {/* Info Gedung & Master Data Overview */}
      {(activeTab === 'dashboard' || activeTab === 'gedung') && (
      <div className="card">
        <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '1rem' }}>Informasi Gedung Asrama Terdaftar</h3>
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>No</th>
                <th>Nama Gedung</th>
                <th>Jumlah Kamar</th>
                <th>Kapasitas Utama</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>1</td>
                <td><strong>Gedung Asrama Putra A</strong></td>
                <td>1 Kamar (A.101)</td>
                <td>4 Orang / Kamar</td>
                <td><span className="badge-role" style={{ backgroundColor: '#ecfdf5', color: '#047857' }}>AKTIF</span></td>
              </tr>
              <tr>
                <td>2</td>
                <td><strong>Gedung Asrama Putri B</strong></td>
                <td>1 Kamar (B.201)</td>
                <td>4 Orang / Kamar</td>
                <td><span className="badge-role" style={{ backgroundColor: '#ecfdf5', color: '#047857' }}>AKTIF</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      )}
    </div>
  );
};

export default DashboardAdmin;
