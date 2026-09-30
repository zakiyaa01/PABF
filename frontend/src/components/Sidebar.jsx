import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { LayoutDashboard, CheckSquare, FileText, Users, Building, Shield } from 'lucide-react';

const Sidebar = ({ activeTab, setActiveTab }) => {
  const { user } = useAuth();

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <Shield size={24} color="var(--accent)" />
        <span>ASRAMA UNAND</span>
      </div>

      <ul className="sidebar-menu">
        <li 
          className={`sidebar-item ${activeTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setActiveTab('dashboard')}
        >
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </li>

        {user?.role === 'Penghuni' && (
          <>
            <li 
              className={`sidebar-item ${activeTab === 'presensi' ? 'active' : ''}`}
              onClick={() => setActiveTab('presensi')}
            >
              <CheckSquare size={18} />
              <span>Presensi Harian</span>
            </li>
            <li 
              className={`sidebar-item ${activeTab === 'izin' ? 'active' : ''}`}
              onClick={() => setActiveTab('izin')}
            >
              <FileText size={18} />
              <span>Pengajuan Izin</span>
            </li>
          </>
        )}

        {(user?.role === 'Fasil' || user?.role === 'Admin') && (
          <>
            <li 
              className={`sidebar-item ${activeTab === 'verifikasi' ? 'active' : ''}`}
              onClick={() => setActiveTab('verifikasi')}
            >
              <FileText size={18} />
              <span>Verifikasi Izin</span>
            </li>
            <li 
              className={`sidebar-item ${activeTab === 'rekap' ? 'active' : ''}`}
              onClick={() => setActiveTab('rekap')}
            >
              <CheckSquare size={18} />
              <span>Rekap Presensi</span>
            </li>
          </>
        )}

        {user?.role === 'Admin' && (
          <>
            <li 
              className={`sidebar-item ${activeTab === 'users' ? 'active' : ''}`}
              onClick={() => setActiveTab('users')}
            >
              <Users size={18} />
              <span>Manajemen User</span>
            </li>
            <li 
              className={`sidebar-item ${activeTab === 'gedung' ? 'active' : ''}`}
              onClick={() => setActiveTab('gedung')}
            >
              <Building size={18} />
              <span>Gedung & Kamar</span>
            </li>
          </>
        )}
      </ul>
    </aside>
  );
};

export default Sidebar;
