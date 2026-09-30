import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const authService = {
  login: async (identifier, password) => {
    const response = await api.post('/auth/login', { identifier, password });
    return response.data;
  },
};

export const presensiService = {
  submitPresensi: async (userId, jenisPresensi, latitude = null, longitude = null, keterangan = '') => {
    const response = await api.post('/presensi', {
      userId,
      jenisPresensi,
      latitude,
      longitude,
      keterangan,
    });
    return response.data;
  },
  getUserPresensi: async (userId) => {
    const response = await api.get(`/presensi/user/${userId}`);
    return response.data;
  },
  getAllPresensi: async () => {
    const response = await api.get('/presensi');
    return response.data;
  },
};

export const izinService = {
  submitIzin: async (userId, tglMulai, tglSelesai, alasan) => {
    const response = await api.post('/izin', {
      userId,
      tglMulai,
      tglSelesai,
      alasan,
    });
    return response.data;
  },
  getUserIzin: async (userId) => {
    const response = await api.get(`/izin/user/${userId}`);
    return response.data;
  },
  getAllIzin: async () => {
    const response = await api.get('/izin');
    return response.data;
  },
  updateStatus: async (id, status) => {
    const response = await api.patch(`/izin/${id}/status?status=${status}`);
    return response.data;
  },
};

export default api;
