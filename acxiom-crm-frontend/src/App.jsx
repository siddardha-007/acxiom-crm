import { Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import EntityPage from "./pages/EntityPage";
import UsersPage from "./pages/UsersPage";
import AuditPage from "./pages/AuditPage";
import ReportsPage from "./pages/ReportsPage";

function Protected() {
  return localStorage.getItem("jwt_token") ? (
    <AppLayout />
  ) : (
    <Navigate to="/login" replace />
  );
}
export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route element={<Protected />}>
        <Route path="/" element={<Dashboard />} />
        <Route path="/customers" element={<EntityPage type="customers" />} />
        <Route path="/leads" element={<EntityPage type="leads" />} />
        <Route
          path="/opportunities"
          element={<EntityPage type="opportunities" />}
        />
        <Route path="/followups" element={<EntityPage type="followups" />} />
        <Route path="/activities" element={<EntityPage type="activities" />} />
        <Route path="/users" element={<UsersPage />} />
        <Route path="/audit" element={<AuditPage />} />
        <Route path="/reports" element={<ReportsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
