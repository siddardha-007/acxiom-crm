import { useEffect, useState } from "react";
import { Plus } from "lucide-react";
import PageHeader from "../components/PageHeader";
import DataTable from "../components/DataTable";
import Modal from "../components/Modal";
import FormField from "../components/FormField";
import { userApi } from "../services/api";
import { errorMessage, unwrap, titleCase } from "../utils/helpers";

export default function UsersPage() {
  const [rows, setRows] = useState([]),
    [modal, setModal] = useState(false),
    [editing, setEditing] = useState(null),
    [form, setForm] = useState({}),
    [error, setError] = useState("");
  const load = async () => {
    try {
      const r = await userApi.all();
      setRows(unwrap(r.data));
    } catch (e) {
      setError(errorMessage(e));
    }
  };
  useEffect(() => {
    load();
  }, []);
  const change = (e) =>
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));
  const save = async (e) => {
    e.preventDefault();
    try {
      if (editing) await userApi.update(editing.id, form);
      else await userApi.create(form);
      setModal(false);
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };
  const action = async (fn, id) => {
    try {
      await fn(id);
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };
  const cols = ["id", "name", "email", "role", "active"].map((k) => ({
    key: k,
    label: titleCase(k),
  }));
  return (
    <>
      <PageHeader
        title="User Management"
        description="Admin user administration."
        action={
          <button
            onClick={() => {
              setEditing(null);
              setForm({});
              setModal(true);
            }}
            className="flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white"
          >
            <Plus size={18} />
            Create User
          </button>
        }
      />
      {error && (
        <div className="mb-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">
          {error}
        </div>
      )}
      <DataTable
        data={rows}
        columns={cols}
        onEdit={(r) => {
          setEditing(r);
          setForm({ name: r.name, email: r.email, role: r.role });
          setModal(true);
        }}
      />
      <div className="mt-4 rounded-2xl border bg-white p-4 text-sm text-slate-500">
        Use the edit action for profile changes. Activate, deactivate, reset
        password and unlock actions are available below each record.
      </div>
      <div className="mt-4 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        {rows.map((r) => (
          <div key={r.id} className="rounded-2xl border bg-white p-4">
            <div className="font-semibold">{r.name}</div>
            <div className="mt-1 text-xs text-slate-500">{r.email}</div>
            <div className="mt-3 flex flex-wrap gap-2">
              <button
                onClick={() => action(userApi.activate, r.id)}
                className="rounded-lg bg-emerald-50 px-3 py-1.5 text-xs text-emerald-700"
              >
                Activate
              </button>
              <button
                onClick={() => action(userApi.deactivate, r.id)}
                className="rounded-lg bg-red-50 px-3 py-1.5 text-xs text-red-700"
              >
                Deactivate
              </button>
              <button
                onClick={() => action(userApi.unlock, r.id)}
                className="rounded-lg bg-amber-50 px-3 py-1.5 text-xs text-amber-700"
              >
                Unlock
              </button>
              <button
                onClick={async () => {
                  const p = prompt("New password");
                  if (p)
                    try {
                      await userApi.resetPassword(r.id, { newPassword: p });
                      alert("Password reset");
                    } catch (e) {
                      setError(errorMessage(e));
                    }
                }}
                className="rounded-lg bg-indigo-50 px-3 py-1.5 text-xs text-indigo-700"
              >
                Reset Password
              </button>
            </div>
          </div>
        ))}
      </div>
      <Modal
        open={modal}
        onClose={() => setModal(false)}
        title={editing ? "Edit User" : "Create User"}
      >
        <form onSubmit={save} className="space-y-4">
          <FormField
            label="Name"
            name="name"
            value={form.name}
            onChange={change}
          />
          <FormField
            label="Email"
            name="email"
            value={form.email}
            onChange={change}
            type="email"
          />
          {!editing && (
            <FormField
              label="Password"
              name="password"
              value={form.password}
              onChange={change}
              type="password"
            />
          )}
          <FormField
            label="Role"
            name="role"
            value={form.role}
            onChange={change}
            options={["ADMIN", "SALES_EXECUTIVE", "SALES_MANAGER"]}
          />
          <div className="flex justify-end">
            <button className="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white">
              Save
            </button>
          </div>
        </form>
      </Modal>
    </>
  );
}
