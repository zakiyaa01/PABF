import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { LogOut, User as UserIcon, Menu } from 'lucide-react';

const Navbar = ({ onMenuClick }) => {
  const { user, logoutUser } = useAuth();

  return (
    <header className="header">
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <button 
          className="btn" 
          style={{ padding: '0.5rem', backgroundColor: 'transparent', border: 'none' }}
          onClick={onMenuClick}
        >
          <Menu size={24} color="#1e293b" />
        </button>
        <h2 style={{ fontSize: '1.1rem', color: '#1e293b', margin: 0 }}>
          Sistem Absensi Asrama UNAND
        </h2>
      </div>
      {user && (
        <div className="user-profile">
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{user.nama}</div>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
              {user.nim ? `NIM: ${user.nim}` : user.email}
            </div>
          </div>
          <span className={`badge-role role-${user.role}`}>{user.role}</span>
          <button 
            onClick={logoutUser} 
            className="btn btn-danger" 
            style={{ padding: '0.4rem 0.75rem', fontSize: '0.8rem' }}
          >
            <LogOut size={14} /> Logout
          </button>
        </div>
      )}
    </header>
  );
};

export default Navbar;
