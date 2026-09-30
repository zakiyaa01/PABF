import React, { useState } from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import LoginPage from './pages/LoginPage';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import DashboardPenghuni from './pages/DashboardPenghuni';
import DashboardFasil from './pages/DashboardFasil';
import DashboardAdmin from './pages/DashboardAdmin';

const MainApp = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');

  if (!user) {
    return <LoginPage />;
  }

  const renderRoleDashboard = () => {
    switch (user.role) {
      case 'Penghuni':
        return <DashboardPenghuni activeTab={activeTab} setActiveTab={setActiveTab} />;
      case 'Fasil':
        return <DashboardFasil activeTab={activeTab} />;
      case 'Admin':
        return <DashboardAdmin activeTab={activeTab} />;
      default:
        return <DashboardPenghuni activeTab={activeTab} setActiveTab={setActiveTab} />;
    }
  };

  return (
    <div className="app-container">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />
      <div className="main-content">
        <Navbar />
        <main className="content-body">
          {renderRoleDashboard()}
        </main>
      </div>
    </div>
  );
};

function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}

export default App;
