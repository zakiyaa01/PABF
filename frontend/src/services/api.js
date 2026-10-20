import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auto-attach JWT Bearer token
api.interceptors.request.use((config) => {
  const savedUser = localStorage.getItem('asrama_user');
  if (savedUser) {
    try {
      const parsed = JSON.parse(savedUser);
      if (parsed.token) {
        config.headers.Authorization = `Bearer ${parsed.token}`;
      }
    } catch (e) {
      console.error('Error parsing token', e);
    }
  }
  return config;
});

// Auto-logout on 401 Unauthorized / Token Expired
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('asrama_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (identifier, password, deviceId = null, isBiometric = false) => {
    const response = await api.post('/auth/login', {
      identifier,
      password,
      deviceId,
      isBiometric,
    });
    return response.data;
  },
  refreshToken: async (refreshToken) => {
    const response = await api.post('/auth/refresh', { refreshToken });
    return response.data;
  },
  logout: async () => {
    try {
      await api.post('/auth/logout');
    } finally {
      localStorage.removeItem('asrama_user');
    }
  },
};

export const presensiService = {
  submitPresensi: async (payload, bypassTimeGate = false) => {
    const response = await api.post(`/presensi?bypassTimeGate=${bypassTimeGate}`, payload);
    return response.data;
  },
  getStatusHariIni: async (userId) => {
    const response = await api.get(`/presensi/status-hari-ini/${userId}`);
    return response.data;
  },
  getUserPresensi: async (userId) => {
    const response = await api.get(`/presensi/user/${userId}`);
    return response.data;
  },
  getPresensiFasilBinaan: async (fasilId, tanggal = null) => {
    const url = tanggal ? `/presensi/fasil/binaan/${fasilId}?tanggal=${tanggal}` : `/presensi/fasil/binaan/${fasilId}`;
    const response = await api.get(url);
    return response.data;
  },
  getAllPresensi: async (tanggal = null) => {
    const url = tanggal ? `/presensi?tanggal=${tanggal}` : '/presensi';
    const response = await api.get(url);
    return response.data;
  },
  koreksiPresensi: async (id, statusBaru, alasan, fasilId, fasilNama) => {
    const response = await api.patch(
      `/presensi/${id}/koreksi?fasilId=${fasilId}&fasilNama=${encodeURIComponent(fasilNama)}`,
      { statusBaru, alasan }
    );
    return response.data;
  },
  getAuditLogs: async (presensiId) => {
    const response = await api.get(`/presensi/${presensiId}/audit-logs`);
    return response.data;
  },
  getAllAuditLogs: async () => {
    const response = await api.get('/presensi/audit-logs');
    return response.data;
  },
  exportPresensiExcelUrl: (start, end) => {
    let url = `${API_BASE_URL}/presensi/export/excel`;
    const params = [];
    if (start) params.push(`start=${start}`);
    if (end) params.push(`end=${end}`);
    return params.length > 0 ? `${url}?${params.join('&')}` : url;
  },
};

export const izinService = {
  submitIzin: async (payload) => {
    const response = await api.post('/izin', payload);
    return response.data;
  },
  getUserIzin: async (userId) => {
    const response = await api.get(`/izin/user/${userId}`);
    return response.data;
  },
  getIzinFasil: async (fasilId, status = null) => {
    const url = status ? `/izin/fasil/${fasilId}?status=${status}` : `/izin/fasil/${fasilId}`;
    const response = await api.get(url);
    return response.data;
  },
  getAllIzin: async () => {
    const response = await api.get('/izin');
    return response.data;
  },
  reviewIzin: async (id, status, catatanFasil, fasilId) => {
    const response = await api.patch(`/izin/${id}/keputusan?fasilId=${fasilId}`, {
      status,
      catatanFasil,
    });
    return response.data;
  },
};

export const adminService = {
  getAllUsers: async (role = null) => {
    const url = role ? `/admin/users?role=${role}` : '/admin/users';
    const response = await api.get(url);
    return response.data;
  },
  createUser: async (userData) => {
    const response = await api.post('/admin/users', userData);
    return response.data;
  },
  updateUser: async (id, userData) => {
    const response = await api.put(`/admin/users/${id}`, userData);
    return response.data;
  },
  toggleActive: async (id) => {
    const response = await api.patch(`/admin/users/${id}/toggle-active`);
    return response.data;
  },
  resetPassword: async (id, newPassword) => {
    const response = await api.post(`/admin/users/${id}/reset-password`, { newPassword });
    return response.data;
  },
  assignFasilToLorong: async (fasilId, lorong) => {
    const response = await api.post('/admin/fasil/assign-lorong', { fasilId, lorong });
    return response.data;
  },
  getAnalytics: async () => {
    const response = await api.get('/admin/analytics');
    return response.data;
  },
  exportPenghuniExcelUrl: () => `${API_BASE_URL}/admin/penghuni/export-excel`,
  importPenghuniExcel: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post('/admin/penghuni/import-excel', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
  getAllGedung: async () => {
    const response = await api.get('/admin/gedung');
    return response.data;
  },
  createGedung: async (data) => {
    const response = await api.post('/admin/gedung', data);
    return response.data;
  },
  getAllKamar: async () => {
    const response = await api.get('/admin/kamar');
    return response.data;
  },
  createKamar: async (data) => {
    const response = await api.post('/admin/kamar', data);
    return response.data;
  },
};

export default api;
