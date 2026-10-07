import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import DataTable from "../components/DataTable";
import { auditApi } from "../services/api";
import { unwrap, errorMessage, titleCase } from "../utils/helpers";
export default function AuditPage() {
  const [rows, setRows] = useState([]),
    [filters, setFilters] = useState({
      userId: "",
      action: "",
      entityName: "",
      start: "",
      end: "",
    }),
    [error, setError] = useState("");
  const load = async () => {
    try {
      const r = await auditApi.all(
        Object.fromEntries(Object.entries(filters).filter(([, v]) => v)),
      );
      setRows(unwrap(r.data));
    } catch (e) {
      setError(errorMessage(e));
    }
  };
  useEffect(() => {
    load();
  }, []);
  return (
    <>
      <PageHeader
        title="Audit Logs"
        description="Review administrative and business activity."
      />
      <div className="mb-5 grid gap-3 rounded-2xl border bg-white p-4 sm:grid-cols-2 lg:grid-cols-5">
        {[
          ["userId", "User ID"],
          ["action", "Action"],
          ["entityName", "Entity Name"],
          ["start", "Start"],
          ["end", "End"],
        ].map(([k, l]) => (
          <input
            key={k}
            type={k === "start" || k === "end" ? "date" : "text"}
            placeholder={l}
            value={filters[k]}
            onChange={(e) => setFilters({ ...filters, [k]: e.target.value })}
            className="rounded-xl border px-3 py-2.5 text-sm"
          />
        ))}
        <button
          onClick={load}
          className="rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white sm:col-span-2 lg:col-span-5"
        >
          Apply Filters
        </button>
      </div>
      {error && (
        <div className="mb-4 rounded-xl bg-red-50 p-3 text-sm text-red-700">
          {error}
        </div>
      )}
      <DataTable
        data={rows}
        columns={Object.keys(rows[0] || {}).map((k) => ({
          key: k,
          label: titleCase(k),
        }))}
      />
    </>
  );
}
