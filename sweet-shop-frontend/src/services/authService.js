import api from './api';

export const authService = {
  register: async (userData) => {
    const response = await api.post('/auth/register', userData);
    if (response.data.success && response.data.data.token) {
      localStorage.setItem('token', response.data.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.data));
    }
    return response.data;
  },

  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    if (response.data.success && response.data.data.token) {
      localStorage.setItem('token', response.data.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.data));
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },

  isAdmin: () => {
    const user = authService.getCurrentUser();
    return user?.roles?.includes('ADMIN') || false;
  },
};

export const sweetService = {
  getAllSweets: async () => {
    const response = await api.get('/sweets');
    return response.data;
  },

  getSweetById: async (id) => {
    const response = await api.get(`/sweets/${id}`);
    return response.data;
  },

  searchSweets: async (params) => {
    const response = await api.get('/sweets/search', { params });
    return response.data;
  },

  createSweet: async (sweetData) => {
    const response = await api.post('/sweets', sweetData);
    return response.data;
  },

  updateSweet: async (id, sweetData) => {
    const response = await api.put(`/sweets/${id}`, sweetData);
    return response.data;
  },

  deleteSweet: async (id) => {
    const response = await api.delete(`/sweets/${id}`);
    return response.data;
  },

  purchaseSweet: async (id, quantity) => {
    const response = await api.post(`/sweets/${id}/purchase`, { quantity });
    return response.data;
  },

  restockSweet: async (id, quantity) => {
    const response = await api.post(`/sweets/${id}/restock`, { quantity });
    return response.data;
  },
};