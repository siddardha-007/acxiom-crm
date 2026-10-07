import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("jwt_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("jwt_token");
      localStorage.removeItem("crm_user");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);

export const authApi = {
  register: (data) => api.post("/api/auth/register", data),
  login: (data) => api.post("/api/auth/login", data),
};

const crud = (base) => ({
  all: () => api.get(`/api/${base}`),
  one: (id) => api.get(`/api/${base}/${id}`),
  create: (data) => api.post(`/api/${base}`, data),
  update: (id, data) => api.put(`/api/${base}/${id}`, data),
  remove: (id) => api.delete(`/api/${base}/${id}`),
  page: (params) => api.get(`/api/${base}/page`, { params }),
});

export const customerApi = crud("customers");
export const leadApi = {
  ...crud("leads"),
  convert: (id, data) => api.post(`/api/leads/${id}/convert`, data),
};
export const opportunityApi = crud("opportunities");
export const followupApi = crud("followups");
export const activityApi = crud("activities");

export const dashboardApi = {
  get: (params) => api.get("/api/dashboard", { params }),
};

export const userApi = {
  all: () => api.get("/api/users"),
  one: (id) => api.get(`/api/users/${id}`),
  create: (data) => api.post("/api/users", data),
  update: (id, data) => api.put(`/api/users/${id}`, data),
  activate: (id) => api.patch(`/api/users/${id}/activate`),
  deactivate: (id) => api.patch(`/api/users/${id}/deactivate`),
  resetPassword: (id, data) =>
    api.patch(`/api/users/${id}/reset-password`, data),
  unlock: (id) => api.patch(`/api/users/${id}/unlock`),
};

export const auditApi = { all: (params) => api.get("/api/audit", { params }) };
export const reportsApi = {
  customers: () => api.get("/api/reports/customers"),
  leads: () => api.get("/api/reports/leads"),
  opportunities: () => api.get("/api/reports/opportunities"),
  followups: () => api.get("/api/reports/followups"),
  pipeline: () => api.get("/api/reports/pipeline"),
  conversion: () => api.get("/api/reports/conversion"),
  users: () => api.get("/api/reports/users"),
  audit: () => api.get("/api/reports/audit"),
};

export default api;
