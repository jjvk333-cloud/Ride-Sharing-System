/**
 * VELTO Central REST API Client
 * Interfaces directly with the Spring Boot 3 backend at http://localhost:8080/api
 */
const BASE_URL = (window.location.origin && window.location.origin.startsWith('http')) ? `${window.location.origin}/api` : 'http://localhost:8080/api';

const API = {
  // Generic fetch wrapper with error parsing
  async request(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    };

    if (typeof Auth !== 'undefined' && Auth.getToken && Auth.getToken()) {
      headers['Authorization'] = `Basic ${Auth.getToken()}`;
    }

    try {
      const response = await fetch(url, { ...options, headers });
      const data = await response.json().catch(() => null);

      if (!response.ok) {
        const errorMsg = data && (data.message || data.error) ? (data.message || data.error) : `HTTP ${response.status}: Request failed`;
        throw new Error(errorMsg);
      }
      return data;
    } catch (err) {
      console.error(`API Error on [${options.method || 'GET'} ${endpoint}]:`, err.message);
      throw err;
    }
  },

  // Health
  health: () => API.request('/health'),

  // Auth (Factory Method)
  auth: {
    login: (email, password) => API.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    }),
    register: (userData) => API.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData)
    })
  },

  // Users
  users: {
    getAll: (role) => API.request(role ? `/users?role=${role}` : '/users'),
    getById: (id) => API.request(`/users/${id}`)
  },

  // Rides (Builder Pattern)
  rides: {
    getAll: (pickup = '', destination = '') => {
      const params = new URLSearchParams();
      if (pickup) params.append('pickup', pickup);
      if (destination) params.append('destination', destination);
      const q = params.toString();
      return API.request(q ? `/rides?${q}` : '/rides');
    },
    getById: (id) => API.request(`/rides/${id}`),
    getByDriver: (driverId) => API.request(`/rides/driver/${driverId}`),
    create: (rideData) => API.request('/rides', {
      method: 'POST',
      body: JSON.stringify(rideData)
    }),
    // State Pattern endpoint
    updateStatus: (rideId, status) => API.request(`/rides/${rideId}/status?status=${status}`, {
      method: 'PATCH'
    }),
    // Strategy Pattern endpoint
    calculatePrice: (rideId, seats, pricingType = 'STANDARD') => 
      API.request(`/rides/${rideId}/calculate-price?seats=${seats}&pricingType=${pricingType}`)
  },

  // Bookings (Facade Pattern)
  bookings: {
    create: (bookingData) => API.request('/bookings', {
      method: 'POST',
      body: JSON.stringify(bookingData)
    }),
    getAll: () => API.request('/bookings'),
    getById: (id) => API.request(`/bookings/${id}`),
    getByUser: (userId) => API.request(`/bookings/user/${userId}`),
    cancel: (bookingId) => API.request(`/bookings/${bookingId}/cancel`, {
      method: 'PATCH'
    })
  },

  // Notifications (Observer Pattern)
  notifications: {
    getByUser: (userId) => API.request(`/notifications/${userId}`),
    markAsRead: (notificationId) => API.request(`/notifications/${notificationId}/read`, {
      method: 'PATCH'
    })
  },

  // Payments (Adapter Pattern)
  payments: {
    process: (paymentData) => API.request('/payments', {
      method: 'POST',
      body: JSON.stringify(paymentData)
    }),
    getById: (id) => API.request(`/payments/${id}`),
    getByBooking: (bookingId) => API.request(`/payments/booking/${bookingId}`),
    getByPassenger: (passengerId) => API.request(`/payments/passenger/${passengerId}`)
  },

  // System Configuration (Singleton Pattern)
  config: {
    get: () => API.request('/config'),
    update: (configData) => API.request('/config', {
      method: 'PATCH',
      body: JSON.stringify(configData)
    }),
    reset: () => API.request('/config/reset', {
      method: 'POST'
    })
  }
};
