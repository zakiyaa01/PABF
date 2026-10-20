import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import LoginPage from './pages/LoginPage';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import DashboardPenghuni from './pages/DashboardPenghuni';
import DashboardFasil from './pages/DashboardFasil';
import DashboardAdmin from './pages/DashboardAdmin';

const ProtectedRoute = ({ children }) => {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return children;
};

const AppLayout = ({ children }) => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = React.useState(false);

  return (
    <div className="app-container">
      <div 
        className={`mobile-overlay ${isMobileMenuOpen ? 'open' : ''}`}
        onClick={() => setIsMobileMenuOpen(false)}
      ></div>
      <Sidebar isOpen={isMobileMenuOpen} setIsOpen={setIsMobileMenuOpen} />
      <div className="main-content">
        <Navbar onMenuClick={() => setIsMobileMenuOpen(true)} />
        <main className="content-body">
          {children}
        </main>
      </div>
    </div>
  );
};

const DashboardRouter = () => {
  const { user } = useAuth();
  const location = useLocation();
  const path = location.pathname.split('/')[1] || 'dashboard';

  if (user?.role === 'Penghuni') {
    return <DashboardPenghuni activeTab={path} />;
  }
  if (user?.role === 'Fasil') {
    return <DashboardFasil activeTab={path} />;
  }
  if (user?.role === 'Admin') {
    if (path === 'verifikasi' || path === 'rekap') {
      return <DashboardFasil activeTab={path} />;
    }
    return <DashboardAdmin activeTab={path} />;
  }
  return <DashboardPenghuni activeTab={path} />;
};

const AppRoutes = () => {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/dashboard" replace /> : <LoginPage />} />
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/*" element={
        <ProtectedRoute>
          <AppLayout>
            <DashboardRouter />
          </AppLayout>
        </ProtectedRoute>
      } />
    </Routes>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;
