import { useEffect, useState } from "react";
import {
  Users,
  UserPlus,
  BriefcaseBusiness,
  CalendarClock,
  IndianRupee,
} from "lucide-react";
import {
  dashboardApi,
  customerApi,
  leadApi,
  opportunityApi,
  followupApi,
} from "../services/api";
import PageHeader from "../components/PageHeader";
import Spinner from "../components/Spinner";
import { unwrap, money, errorMessage } from "../utils/helpers";

export default function Dashboard() {
  const [data, setData] = useState(null),
    [counts, setCounts] = useState({}),
    [loading, setLoading] = useState(true),
    [error, setError] = useState("");
  useEffect(() => {
    (async () => {
      try {
        const [d, c, l, o, f] = await Promise.all([
          dashboardApi.get({ rangeType: "THIS_MONTH" }),
          customerApi.all(),
          leadApi.all(),
          opportunityApi.all(),
          followupApi.all(),
        ]);
        setData(d.data);
        setCounts({
          customers: unwrap(c.data).length,
          leads: unwrap(l.data).length,
          opportunities: unwrap(o.data).length,
          followups: unwrap(f.data).length,
        });
      } catch (e) {
        setError(errorMessage(e));
      } finally {
        setLoading(false);
      }
    })();
  }, []);
  if (loading) return <Spinner />;
  const obj = data && typeof data === "object" ? data : {};
  const cards = [
    ["Customers", counts.customers, Users],
    ["Leads", counts.leads, UserPlus],
    ["Opportunities", counts.opportunities, BriefcaseBusiness],
    ["Follow-Ups", counts.followups, CalendarClock],
  ];
  return (
    <>
      <PageHeader
        title="Dashboard"
        description="Overview of your CRM activity and sales pipeline."
      />
      <>
        {error && (
          <div className="mb-5 rounded-xl bg-amber-50 p-4 text-sm text-amber-700">
            Some dashboard data could not be loaded: {error}
          </div>
        )}
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {cards.map(([label, value, Icon]) => (
            <div
              key={label}
              className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-slate-500">{label}</p>
                  <p className="mt-2 text-3xl font-bold text-slate-900">
                    {value}
                  </p>
                </div>
                <div className="rounded-xl bg-indigo-50 p-3 text-indigo-600">
                  <Icon size={22} />
                </div>
              </div>
            </div>
          ))}
        </div>
        <div className="mt-6 grid gap-6 lg:grid-cols-2">
          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h2 className="font-bold text-slate-900">Dashboard API</h2>
            <p className="mt-1 text-sm text-slate-500">
              Live response for THIS_MONTH.
            </p>
            <pre className="mt-4 max-h-96 overflow-auto rounded-xl bg-slate-950 p-4 text-xs text-slate-200">
              {JSON.stringify(obj, null, 2)}
            </pre>
          </div>
          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h2 className="font-bold text-slate-900">Key Metrics</h2>
            <div className="mt-4 space-y-4">
              {Object.entries(obj)
                .slice(0, 8)
                .map(([k, v]) => (
                  <div
                    key={k}
                    className="flex items-center justify-between border-b pb-3"
                  >
                    <span className="text-sm text-slate-500">
                      {k.replaceAll("_", " ")}
                    </span>
                    <span className="font-semibold text-slate-900">
                      {typeof v === "number" &&
                      k.toLowerCase().includes("value")
                        ? money(v)
                        : String(v)}
                    </span>
                  </div>
                ))}
            </div>
          </div>
        </div>
      </>
    </>
  );
}
