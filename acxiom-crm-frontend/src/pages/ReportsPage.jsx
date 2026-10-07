import { useState } from "react";
import PageHeader from "../components/PageHeader";
import { reportsApi } from "../services/api";
import { errorMessage } from "../utils/helpers";
const reports = [
  ["customers", "Customer Report"],
  ["leads", "Lead Report"],
  ["opportunities", "Opportunity Report"],
  ["followups", "Follow-Up Report"],
  ["pipeline", "Pipeline Report"],
  ["conversion", "Conversion Report"],
  ["users", "User Activity Report"],
  ["audit", "Audit Report"],
];
export default function ReportsPage() {
  const [data, setData] = useState(null),
    [error, setError] = useState("");
  const run = async (key) => {
    try {
      const r = await reportsApi[key]();
      setData({ name: reports.find((x) => x[0] === key)?.[1], value: r.data });
    } catch (e) {
      setError(errorMessage(e));
    }
  };
  return (
    <>
      <PageHeader
        title="Reports"
        description="Run the CRM reports exposed by the backend."
      />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {reports.map(([key, label]) => (
          <button
            key={key}
            onClick={() => run(key)}
            className="rounded-2xl border bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-indigo-300 hover:shadow-md"
          >
            <div className="font-semibold text-slate-900">{label}</div>
            <div className="mt-2 text-xs text-slate-500">
              GET /api/reports/{key}
            </div>
          </button>
        ))}
      </div>
      {error && (
        <div className="mt-5 rounded-xl bg-red-50 p-3 text-sm text-red-700">
          {error}
        </div>
      )}
      {data && (
        <div className="mt-6 rounded-2xl border bg-white p-6">
          <h2 className="font-bold">{data.name}</h2>
          <pre className="mt-4 max-h-[600px] overflow-auto rounded-xl bg-slate-950 p-5 text-xs text-slate-200">
            {JSON.stringify(data.value, null, 2)}
          </pre>
        </div>
      )}
    </>
  );
}
