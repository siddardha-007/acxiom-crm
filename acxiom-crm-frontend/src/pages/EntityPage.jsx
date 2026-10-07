import { useEffect, useMemo, useState } from "react";
import { Plus, Search } from "lucide-react";
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
      ["status", "Status", "text", ["ACTIVE", "INACTIVE"]],
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
        "text",
        ["WEBSITE", "REFERRAL", "SOCIAL_MEDIA", "COLD_CALL"],
      ],
      [
        "status",
        "Status",
        "text",
        ["NEW", "CONTACTED", "QUALIFIED", "CONVERTED", "LOST"],
      ],
      ["priority", "Priority", "text", ["LOW", "MEDIUM", "HIGH"]],
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
        "text",
        [
          "PROSPECTING",
          "QUALIFICATION",
          "PROPOSAL",
          "NEGOTIATION",
          "CLOSED_WON",
          "CLOSED_LOST",
        ],
      ],
      ["probability", "Probability", "number"],
      ["expectedCloseDate", "Expected Close Date", "date"],
      ["status", "Status", "text", ["OPEN", "WON", "LOST"]],
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
      ["followUpType", "Type", "text", ["CALL", "MEETING", "EMAIL"]],
      ["subject", "Subject"],
      ["remarks", "Remarks"],
      ["status", "Status", "text", ["PENDING", "COMPLETED", "CANCELLED"]],
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
      ["activityType", "Type", "text", ["CALL", "MEETING", "EMAIL", "TASK"]],
      ["subject", "Subject"],
      ["description", "Description"],
      ["activityDate", "Activity Date", "datetime-local"],
      ["customerId", "Customer ID", "number"],
      ["leadId", "Lead ID", "number"],
      ["status", "Status", "text", ["PENDING", "COMPLETED", "CANCELLED"]],
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
  const c = configs[type],
    [rows, setRows] = useState([]),
    [loading, setLoading] = useState(true),
    [saving, setSaving] = useState(false),
    [modal, setModal] = useState(false),
    [editing, setEditing] = useState(null),
    [form, setForm] = useState({}),
    [search, setSearch] = useState(""),
    [error, setError] = useState("");
  const load = async () => {
    setLoading(true);
    try {
      const r =
        search && c.search
          ? await c.api.page({ page: 0, size: 10, search })
          : await c.api.all();
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
  const cols = useMemo(
    () =>
      c.columns.map((k) => ({
        key: k,
        label: titleCase(k),
        render: (v) =>
          k.toLowerCase().includes("date")
            ? dateTime(v)
            : ["amount", "expectedValue"].includes(k)
              ? money(v)
              : String(v ?? "-"),
      })),
    [c.columns],
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
  const change = (e) =>
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));
  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = { ...form };
      Object.keys(payload).forEach((k) => {
        if (payload[k] === "" || payload[k] === undefined) delete payload[k];
      });
      if (editing) await c.api.update(editing.id, payload);
      else await c.api.create(payload);
      setModal(false);
      await load();
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setSaving(false);
    }
  };
  const remove = async (row) => {
    if (
      !confirm(
        `Delete ${row.name || row.customerName || row.leadName || row.opportunityName || row.subject || "this record"}?`,
      )
    )
      return;
    try {
      await c.api.remove(row.id);
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };
  return (
    <>
      <PageHeader
        title={c.title}
        description={`Manage ${c.title.toLowerCase()} from the CRM backend.`}
        action={
          <button
            onClick={openCreate}
            className="flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-indigo-700"
          >
            <Plus size={18} />
            Add {c.title.slice(0, -1)}
          </button>
        }
      />
      {error && (
        <div className="mb-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">
          {error}
        </div>
      )}
      <div className="mb-5 flex gap-3">
        <div className="relative max-w-md flex-1">
          <Search className="absolute left-3 top-3 text-slate-400" size={18} />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && load()}
            placeholder="Search and press Enter..."
            className="w-full rounded-xl border bg-white py-2.5 pl-10 pr-3 text-sm outline-none focus:border-indigo-500"
          />
        </div>
        {search && (
          <button
            onClick={() => {
              setSearch("");
              setTimeout(load, 0);
            }}
            className="rounded-xl border bg-white px-4 text-sm"
          >
            Clear
          </button>
        )}
      </div>
      {loading ? (
        <div className="py-12 text-center text-slate-500">Loading...</div>
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
        title={`${editing ? "Edit" : "Create"} ${c.title.slice(0, -1)}`}
      >
        <form onSubmit={save} className="grid gap-4 sm:grid-cols-2">
          {c.fields.map(([name, label, type, options]) => (
            <div
              key={name}
              className={
                ["address", "remarks", "description", "notes"].includes(name)
                  ? "sm:col-span-2"
                  : ""
              }
            >
              <FormField
                label={label}
                name={name}
                value={form[name]}
                onChange={change}
                type={type || "text"}
                options={options}
              />
            </div>
          ))}
          <div className="flex justify-end gap-3 sm:col-span-2">
            <button
              type="button"
              onClick={() => setModal(false)}
              className="rounded-xl border px-4 py-2.5 text-sm"
            >
              Cancel
            </button>
            <button
              disabled={saving}
              className="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white"
            >
              {saving ? "Saving..." : "Save"}
            </button>
          </div>
        </form>
      </Modal>
    </>
  );
}
