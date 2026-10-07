import { useEffect, useState } from "react";
import {
  Plus,
  UserCheck,
  UserX,
  Unlock,
  KeyRound,
  Shield,
  Loader2,
  AlertCircle,
  MoreVertical,
  Edit2,
} from "lucide-react";
import PageHeader from "../components/PageHeader";
import DataTable from "../components/DataTable";
import Modal from "../components/Modal";
import FormField from "../components/FormField";
import { userApi } from "../services/api";
import { errorMessage, unwrap, titleCase } from "../utils/helpers";

export default function UsersPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [actionLoading, setActionLoading] = useState(null);
  const [modal, setModal] = useState(false);
  const [resetModal, setResetModal] = useState(null);
  const [newPassword, setNewPassword] = useState("");
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({});
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const r = await userApi.all();
      setRows(unwrap(r.data));
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const change = (e) =>
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      if (editing) {
        await userApi.update(editing.id, form);
        setSuccessMsg("User details updated successfully.");
      } else {
        await userApi.create(form);
        setSuccessMsg("User created successfully.");
      }
      setModal(false);
      await load();
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const handleAction = async (actionFn, id, actionLabel) => {
    setActionLoading(id);
    setError("");
    try {
      await actionFn(id);
      setSuccessMsg(`User successfully ${actionLabel}.`);
      await load();
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setActionLoading(null);
    }
  };

  const handlePasswordResetSubmit = async (e) => {
    e.preventDefault();
    if (!newPassword || !resetModal) return;
    setSaving(true);
    setError("");
    try {
      await userApi.resetPassword(resetModal.id, { newPassword });
      setSuccessMsg(`Password reset successfully for ${resetModal.name}.`);
      setResetModal(null);
      setNewPassword("");
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  // Define Table Columns with Action Buttons
  const cols = [
    {
      key: "id",
      label: "ID",
      render: (val) => <span className="font-mono text-xs text-slate-500">#{val}</span>,
    },
    {
      key: "name",
      label: "User",
      render: (val, row) => (
        <div className="flex flex-col">
          <span className="font-semibold text-slate-900">{val || "Unnamed User"}</span>
          <span className="text-xs text-slate-400">{row.email}</span>
        </div>
      ),
    },
    {
      key: "role",
      label: "Role",
      render: (val) => {
        const role = String(val || "").toUpperCase();
        const isManager = role.includes("MANAGER");
        const isAdmin = role.includes("ADMIN");
        return (
          <span
            className={`inline-flex items-center gap-1.5 rounded-lg border px-2.5 py-1 text-xs font-semibold ${
              isAdmin
                ? "border-indigo-200 bg-indigo-50 text-indigo-700"
                : isManager
                ? "border-purple-200 bg-purple-50 text-purple-700"
                : "border-slate-200 bg-slate-50 text-slate-700"
            }`}
          >
            <Shield size={12} />
            {titleCase(role || "USER")}
          </span>
        );
      },
    },
    {
      key: "active",
      label: "Status",
      render: (val, row) => {
        const isActive = val ?? row.status === "ACTIVE";
        return (
          <span
            className={`inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-medium ${
              isActive
                ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                : "bg-rose-50 text-rose-700 border border-rose-200"
            }`}
          >
            <span
              className={`h-1.5 w-1.5 rounded-full ${
                isActive ? "bg-emerald-500" : "bg-rose-500"
              }`}
            />
            {isActive ? "Active" : "Inactive"}
          </span>
        );
      },
    },
    {
      key: "actions",
      label: "Account Actions",
      render: (_, row) => {
        const isActive = row.active ?? row.status === "ACTIVE";
        const isCurrentLoading = actionLoading === row.id;

        return (
          <div className="flex items-center gap-1.5">
            {isActive ? (
              <button
                onClick={() => handleAction(userApi.deactivate, row.id, "deactivated")}
                disabled={isCurrentLoading}
                title="Deactivate Account"
                className="inline-flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-2 py-1 text-xs font-medium text-slate-700 transition-colors hover:bg-rose-50 hover:text-rose-700"
              >
                <UserX size={13} />
                Deactivate
              </button>
            ) : (
              <button
                onClick={() => handleAction(userApi.activate, row.id, "activated")}
                disabled={isCurrentLoading}
                title="Activate Account"
                className="inline-flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-2 py-1 text-xs font-medium text-slate-700 transition-colors hover:bg-emerald-50 hover:text-emerald-700"
              >
                <UserCheck size={13} />
                Activate
              </button>
            )}

            <button
              onClick={() => handleAction(userApi.unlock, row.id, "unlocked")}
              disabled={isCurrentLoading}
              title="Unlock Account"
              className="inline-flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-2 py-1 text-xs font-medium text-slate-700 transition-colors hover:bg-amber-50 hover:text-amber-700"
            >
              <Unlock size={13} />
              Unlock
            </button>

            <button
              onClick={() => {
                setResetModal(row);
                setNewPassword("");
              }}
              title="Reset Password"
              className="inline-flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-2 py-1 text-xs font-medium text-slate-700 transition-colors hover:bg-indigo-50 hover:text-indigo-700"
            >
              <KeyRound size={13} />
              Reset Pass
            </button>
          </div>
        );
      },
    },
  ];

  return (
    <div className="space-y-6">
      <PageHeader
        title="User Management"
        description="Manage system users, assign roles, reset passwords, and control access."
        action={
          <button
            onClick={() => {
              setEditing(null);
              setForm({ role: "SALES_EXECUTIVE" });
              setModal(true);
            }}
            className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-indigo-700"
          >
            <Plus size={18} />
            Create User
          </button>
        }
      />

      {error && (
        <div className="flex items-center gap-2 rounded-2xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-800">
          <AlertCircle size={18} className="text-rose-600" />
          <span>{error}</span>
        </div>
      )}

      {successMsg && (
        <div className="flex items-center justify-between rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-800">
          <span>{successMsg}</span>
          <button
            onClick={() => setSuccessMsg("")}
            className="text-xs font-semibold text-emerald-700 hover:underline"
          >
            Dismiss
          </button>
        </div>
      )}

      {loading ? (
        <div className="flex flex-col items-center justify-center py-16 text-slate-400">
          <Loader2 size={32} className="animate-spin text-indigo-600 mb-2" />
          <p className="text-sm">Loading users list...</p>
        </div>
      ) : (
        <DataTable
          data={rows}
          columns={cols}
          onEdit={(r) => {
            setEditing(r);
            setForm({ name: r.name, email: r.email, role: r.role });
            setModal(true);
          }}
        />
      )}

      {/* User Create / Edit Modal */}
      <Modal
        open={modal}
        onClose={() => setModal(false)}
        title={editing ? "Edit User Profile" : "Create New User"}
      >
        <form onSubmit={save} className="space-y-4">
          <FormField
            label="Full Name"
            name="name"
            value={form.name ?? ""}
            onChange={change}
            required
          />
          <FormField
            label="Email Address"
            name="email"
            value={form.email ?? ""}
            onChange={change}
            type="email"
            required
          />
          {!editing && (
            <FormField
              label="Password"
              name="password"
              value={form.password ?? ""}
              onChange={change}
              type="password"
              required
            />
          )}
          <FormField
            label="Role"
            name="role"
            value={form.role ?? "SALES_EXECUTIVE"}
            onChange={change}
            type="select"
            options={["ADMIN", "SALES_MANAGER", "SALES_EXECUTIVE"]}
          />
          <div className="mt-6 flex justify-end gap-3 border-t border-slate-100 pt-4">
            <button
              type="button"
              onClick={() => setModal(false)}
              className="rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-indigo-700 disabled:opacity-60"
            >
              {saving && <Loader2 size={16} className="animate-spin" />}
              {saving ? "Saving..." : "Save User"}
            </button>
          </div>
        </form>
      </Modal>

      {/* Reset Password Dedicated Modal */}
      <Modal
        open={Boolean(resetModal)}
        onClose={() => setResetModal(null)}
        title={`Reset Password for ${resetModal?.name || "User"}`}
      >
        <form onSubmit={handlePasswordResetSubmit} className="space-y-4">
          <p className="text-xs text-slate-500">
            Enter a new password for <strong className="text-slate-800">{resetModal?.email}</strong>.
          </p>
          <FormField
            label="New Password"
            name="newPassword"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            type="password"
            required
          />
          <div className="mt-6 flex justify-end gap-3 border-t border-slate-100 pt-4">
            <button
              type="button"
              onClick={() => setResetModal(null)}
              className="rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving || !newPassword}
              className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-indigo-700 disabled:opacity-60"
            >
              {saving && <Loader2 size={16} className="animate-spin" />}
              {saving ? "Updating..." : "Update Password"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}