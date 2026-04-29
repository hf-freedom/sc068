import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const chemicalApi = {
  getAll: () => api.get('/chemicals'),
  getById: (id) => api.get(`/chemicals/${id}`),
  create: (data) => api.post('/chemicals', data),
  update: (id, data) => api.put(`/chemicals/${id}`, data),
  delete: (id) => api.delete(`/chemicals/${id}`),
};

export const labApi = {
  getAll: () => api.get('/labs'),
  getById: (id) => api.get(`/labs/${id}`),
  create: (data) => api.post('/labs', data),
  update: (id, data) => api.put(`/labs/${id}`, data),
  delete: (id) => api.delete(`/labs/${id}`),
  resumeAccess: (labId, operatorId, reason) => 
    api.post(`/safety-checks/labs/${labId}/resume?operatorId=${operatorId}&reason=${reason}`),
};

export const userApi = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  create: (data) => api.post('/users', data),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
};

export const purchaseApi = {
  getAll: () => api.get('/purchases'),
  getById: (id) => api.get(`/purchases/${id}`),
  create: (data) => api.post('/purchases', data),
  approve: (id, approverId, approved, remark) => {
    const params = new URLSearchParams();
    params.append('approverId', approverId);
    params.append('approved', approved.toString());
    if (remark) params.append('remark', remark);
    return api.post(`/purchases/${id}/approve?${params.toString()}`);
  },
};

export const stockApi = {
  getAll: () => api.get('/stock'),
  getById: (id) => api.get(`/stock/${id}`),
  stockIn: (data) => api.post('/stock/in', data),
};

export const usageApi = {
  getAll: () => api.get('/usages'),
  getById: (id) => api.get(`/usages/${id}`),
  create: (data) => api.post('/usages', data),
  approve: (id, approverId, approved, remark) => {
    const params = new URLSearchParams();
    params.append('approverId', approverId);
    params.append('approved', approved.toString());
    if (remark) params.append('remark', remark);
    return api.post(`/usages/${id}/approve?${params.toString()}`);
  },
};

export const wasteApi = {
  getAllRecords: () => api.get('/waste/records'),
  createRecord: (data) => api.post('/waste/record', data),
  getAllRecoveryTasks: () => api.get('/waste/recovery-tasks'),
  approveRecoveryTask: (id, approverId, approved, remark) => {
    const params = new URLSearchParams();
    params.append('approverId', approverId);
    params.append('approved', approved.toString());
    if (remark) params.append('remark', remark);
    return api.post(`/waste/recovery-tasks/${id}/approve?${params.toString()}`);
  },
};

export const safetyCheckApi = {
  getAll: () => api.get('/safety-checks'),
  getById: (id) => api.get(`/safety-checks/${id}`),
  create: (data) => api.post('/safety-checks', data),
};

export const taskApi = {
  getAllScrapTasks: () => api.get('/tasks/scrap'),
  approveScrapTask: (id, approverId, approved, remark) => {
    const params = new URLSearchParams();
    params.append('approverId', approverId);
    params.append('approved', approved.toString());
    if (remark) params.append('remark', remark);
    return api.post(`/tasks/scrap/${id}/approve?${params.toString()}`);
  },
  checkExpired: () => api.post('/tasks/scrap/check-expired'),
};

export const auditLogApi = {
  getAll: () => api.get('/audit-logs'),
};

export default api;
