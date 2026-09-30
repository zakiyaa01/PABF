import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { LayoutDashboard, CheckSquare, FileText, Users, Building, Shield } from 'lucide-react';

const Sidebar = ({ isOpen, setIsOpen }) => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const activeTab = location.pathname.split('/')[1] || 'dashboard';

  const handleNav = (path) => {
    navigate(path);
    if (setIsOpen) setIsOpen(false);
  };

  return (
    <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
      <div className="sidebar-brand">
        <Shield size={24} color="var(--accent)" />
        <span>ASRAMA UNAND</span>
      </div>

      <ul className="sidebar-menu">
        <li 
          className={`sidebar-item ${activeTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => handleNav('/dashboard')}
        >
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </li>

        {user?.role === 'Penghuni' && (
          <>
            <li 
              className={`sidebar-item ${activeTab === 'presensi' ? 'active' : ''}`}
              onClick={() => handleNav('/presensi')}
            >
              <CheckSquare size={18} />
              <span>Presensi Harian</span>
            </li>
            <li 
              className={`sidebar-item ${activeTab === 'izin' ? 'active' : ''}`}
              onClick={() => handleNav('/izin')}
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
              onClick={() => handleNav('/verifikasi')}
            >
              <FileText size={18} />
              <span>Verifikasi Izin</span>
            </li>
            <li 
              className={`sidebar-item ${activeTab === 'rekap' ? 'active' : ''}`}
              onClick={() => handleNav('/rekap')}
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
              onClick={() => handleNav('/users')}
            >
              <Users size={18} />
              <span>Manajemen User</span>
            </li>
            <li 
              className={`sidebar-item ${activeTab === 'gedung' ? 'active' : ''}`}
              onClick={() => handleNav('/gedung')}
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
