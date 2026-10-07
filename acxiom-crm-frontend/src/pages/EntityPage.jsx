import { useEffect, useMemo, useState, useCallback } from "react";
import { Plus, Search, X, Loader2, Trash2, Edit3, AlertCircle } from "lucide-react";
import PageHeader from "../components/PageHeader";
import DataTable from "../components/DataTable";
import Modal from "../components/Modal";
import FormField from "../components/FormField";
import {
  customerApi,
  leadApi,
  opportunityApi,
  followupApi,
  activityApi,
} from "../services/api";
import {
  errorMessage,
  titleCase,
  unwrap,
  money,
  dateTime,
} from "../utils/helpers";

const configs = {
  customers: {
    title: "Customers",
    api: customerApi,
    search: true,
    fields: [
      ["customerName", "Customer Name"],
      ["email", "Email", "email"],
      ["phone", "Phone"],
      ["companyName", "Company Name"],
      ["address", "Address"],
      ["city", "City"],
      ["state", "State"],
      ["status", "Status", "select", ["ACTIVE", "INACTIVE"]],
      ["assignedTo", "Assigned To", "number"],
    ],
    columns: ["id", "customerName", "email", "phone", "companyName", "status"],
  },
  leads: {
    title: "Leads",
    api: leadApi,
    search: true,
    fields: [
      ["leadName", "Lead Name"],
      ["email", "Email", "email"],
      ["phone", "Phone"],
      ["companyName", "Company Name"],
      [
        "source",
        "Source",
        "select",
        ["WEBSITE", "REFERRAL", "SOCIAL_MEDIA", "COLD_CALL"],
      ],
      [
        "status",
        "Status",
        "select",
        ["NEW", "CONTACTED", "QUALIFIED", "CONVERTED", "LOST"],
      ],
      ["priority", "Priority", "select", ["LOW", "MEDIUM", "HIGH"]],
      ["expectedValue", "Expected Value", "number"],
      ["assignedTo", "Assigned To", "number"],
    ],
    columns: [
      "id",
      "leadName",
      "email",
      "companyName",
      "source",
      "status",
      "priority",
      "expectedValue",
    ],
  },
  opportunities: {
    title: "Opportunities",
    api: opportunityApi,
    search: true,
    fields: [
      ["opportunityName", "Opportunity Name"],
      ["customerId", "Customer ID", "number"],
      ["leadId", "Lead ID", "number"],
      ["assignedTo", "Assigned To", "number"],
      ["amount", "Amount", "number"],
      [
        "stage",
        "Stage",
        "select",
        [
          "PROSPECTING",
          "QUALIFICATION",
          "PROPOSAL",
          "NEGOTIATION",
          "CLOSED_WON",
          "CLOSED_LOST",
        ],
      ],
      ["probability", "Probability (%)", "number"],
      ["expectedCloseDate", "Expected Close Date", "date"],
      ["status", "Status", "select", ["OPEN", "WON", "LOST"]],
      ["notes", "Notes"],
    ],
    columns: [
      "id",
      "opportunityName",
      "amount",
      "stage",
      "probability",
      "expectedCloseDate",
      "status",
    ],
  },
  followups: {
    title: "Follow-Ups",
    api: followupApi,
    search: true,
    fields: [
      ["customerId", "Customer ID", "number"],
      ["leadId", "Lead ID", "number"],
      ["followUpDate", "Follow-Up Date", "datetime-local"],
      ["followUpType", "Type", "select", ["CALL", "MEETING", "EMAIL"]],
      ["subject", "Subject"],
      ["remarks", "Remarks"],
      ["status", "Status", "select", ["PENDING", "COMPLETED", "CANCELLED"]],
      ["assignedTo", "Assigned To", "number"],
    ],
    columns: [
      "id",
      "subject",
      "followUpDate",
      "followUpType",
      "status",
      "assignedTo",
    ],
  },
  activities: {
    title: "Activities",
    api: activityApi,
    search: false,
    fields: [
      ["activityType", "Type", "select", ["CALL", "MEETING", "EMAIL", "TASK"]],
      ["subject", "Subject"],
      ["description", "Description"],
      ["activityDate", "Activity Date", "datetime-local"],
      ["customerId", "Customer ID", "number"],
      ["leadId", "Lead ID", "number"],
      ["status", "Status", "select", ["PENDING", "COMPLETED", "CANCELLED"]],
      ["assignedTo", "Assigned To", "number"],
    ],
    columns: [
      "id",
      "activityType",
      "subject",
      "activityDate",
      "status",
      "assignedTo",
    ],
  },
};

export default function EntityPage({ type }) {
  const c = configs[type] || configs.customers;
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [modal, setModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({});
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");

  const load = useCallback(async (query = search) => {
    setLoading(true);
    setError("");
    try {
      const r =
        query && c.search
          ? await c.api.page({ page: 0, size: 25, search: query })
          : await c.api.all();
      setRows(unwrap(r.data));
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setLoading(false);
    }
  }, [c, search]);

  // Handle entity change or initial mount
  useEffect(() => {
    setSearch("");
    setForm({});
    setEditing(null);
    load("");
  }, [type]);

  // Debounced live search
  useEffect(() => {
    if (!c.search) return;
    const timer = setTimeout(() => {
      load(search);
    }, 350);
    return () => clearTimeout(timer);
  }, [search, c.search, load]);

  // Helper for status badge rendering
  const renderBadge = (val) => {
    const statusStr = String(val).toUpperCase();
    let badgeStyle = "bg-slate-100 text-slate-700 border-slate-200";

    if (["ACTIVE", "WON", "CLOSED_WON", "COMPLETED", "QUALIFIED"].includes(statusStr)) {
      badgeStyle = "bg-emerald-50 text-emerald-700 border-emerald-200";
    } else if (["PENDING", "NEW", "CONTACTED", "PROSPECTING"].includes(statusStr)) {
      badgeStyle = "bg-amber-50 text-amber-700 border-amber-200";
    } else if (["INACTIVE", "LOST", "CLOSED_LOST", "CANCELLED"].includes(statusStr)) {
      badgeStyle = "bg-rose-50 text-rose-700 border-rose-200";
    } else if (["HIGH"].includes(statusStr)) {
      badgeStyle = "bg-red-50 text-red-700 border-red-200 font-bold";
    }

    return (
      <span className={`inline-flex items-center rounded-lg border px-2.5 py-0.5 text-xs font-medium ${badgeStyle}`}>
        {titleCase(statusStr)}
      </span>
    );
  };

  const cols = useMemo(
    () =>
      c.columns.map((k) => ({
        key: k,
        label: titleCase(k),
        render: (v) => {
          if (v === null || v === undefined) return "-";
          if (k.toLowerCase().includes("date")) return dateTime(v);
          if (["amount", "expectedValue"].includes(k)) return money(v);
          if (["status", "stage", "priority"].includes(k)) return renderBadge(v);
          if (k === "probability") return `${v}%`;
          return String(v);
        },
      })),
    [c.columns]
  );

  const openCreate = () => {
    setEditing(null);
    setForm({});
    setModal(true);
  };

  const openEdit = (row) => {
    setEditing(row);
    setForm({ ...row });
    setModal(true);
  };

  const change = (e) => {
    const { name, value, type: inputType } = e.target;
    setForm((f) => ({
      ...f,
      [name]: inputType === "number" ? (value === "" ? "" : Number(value)) : value,
    }));
  };

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = { ...form };
      Object.keys(payload).forEach((k) => {
        if (payload[k] === "" || payload[k] === undefined) delete payload[k];
      });

      if (editing) {
        await c.api.update(editing.id, payload);
      } else {
        await c.api.create(payload);
      }

      setModal(false);
      await load();
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  const remove = async (row) => {
    const recordName =
      row.customerName ||
      row.leadName ||
      row.opportunityName ||
      row.subject ||
      `Record #${row.id}`;

    if (!confirm(`Are you sure you want to delete "${recordName}"?`)) return;

    try {
      await c.api.remove(row.id);
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  const singularTitle = c.title.endsWith("ies")
    ? `${c.title.slice(0, -3)}y`
    : c.title.slice(0, -1);

  return (
    <div className="space-y-6">
      <PageHeader
        title={c.title}
        description={`Manage, filter, and track ${c.title.toLowerCase()} records.`}
        action={
          <button
            onClick={openCreate}
            className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-indigo-700"
          >
            <Plus size={18} />
            Add {singularTitle}
          </button>
        }
      />

      {error && (
        <div className="flex items-center gap-2 rounded-2xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-800">
          <AlertCircle size={18} className="text-rose-600" />
          <span>{error}</span>
        </div>
      )}

      {c.search && (
        <div className="flex items-center gap-3">
          <div className="relative max-w-md flex-1">
            <Search
              className="absolute left-3.5 top-3 text-slate-400"
              size={18}
            />
            <input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder={`Search ${c.title.toLowerCase()}...`}
              className="w-full rounded-2xl border border-slate-200 bg-white py-2.5 pl-10 pr-10 text-sm text-slate-800 outline-none transition-all focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
            />
            {search && (
              <button
                onClick={() => setSearch("")}
                className="absolute right-3 top-3 text-slate-400 hover:text-slate-600"
              >
                <X size={16} />
              </button>
            )}
          </div>
        </div>
      )}

      {loading ? (
        <div className="flex flex-col items-center justify-center py-16 text-slate-400">
          <Loader2 size={32} className="animate-spin text-indigo-600 mb-2" />
          <p className="text-sm">Fetching {c.title.toLowerCase()}...</p>
        </div>
      ) : (
        <DataTable
          data={rows}
          columns={cols}
          onEdit={openEdit}
          onDelete={remove}
        />
      )}

      <Modal
        open={modal}
        onClose={() => setModal(false)}
        title={`${editing ? "Edit" : "Create"} ${singularTitle}`}
      >
        <form onSubmit={save} className="grid gap-4 sm:grid-cols-2">
          {c.fields.map(([name, label, type = "text", options]) => {
            const isFullWidth = [
              "address",
              "remarks",
              "description",
              "notes",
            ].includes(name);

            return (
              <div
                key={name}
                className={isFullWidth ? "sm:col-span-2" : "sm:col-span-1"}
              >
                <FormField
                  label={label}
                  name={name}
                  value={form[name] ?? ""}
                  onChange={change}
                  type={type}
                  options={options}
                />
              </div>
            );
          })}

          <div className="mt-4 flex justify-end gap-3 sm:col-span-2 border-t pt-4 border-slate-100">
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
              {saving ? "Saving..." : "Save Record"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}