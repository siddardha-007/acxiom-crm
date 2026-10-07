import { useEffect, useState } from "react";
import {
  Users,
  UserPlus,
  BriefcaseBusiness,
  CalendarClock,
  IndianRupee,
  TrendingUp,
  ArrowUpRight,
  Sparkles,
  Code2,
  ChevronDown,
  ChevronUp,
  Plus,
} from "lucide-react";
import { Link } from "react-router-dom";
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
  const [data, setData] = useState(null);
  const [counts, setCounts] = useState({
    customers: 0,
    leads: 0,
    opportunities: 0,
    followups: 0,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showRawJson, setShowRawJson] = useState(false);

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
    {
      label: "Total Customers",
      value: counts.customers,
      icon: Users,
      color: "from-blue-500 to-indigo-600",
      bgColor: "bg-blue-50 text-blue-600 border-blue-100",
      path: "/customers",
    },
    {
      label: "Active Leads",
      value: counts.leads,
      icon: UserPlus,
      color: "from-emerald-500 to-teal-600",
      bgColor: "bg-emerald-50 text-emerald-600 border-emerald-100",
      path: "/leads",
    },
    {
      label: "Opportunities",
      value: counts.opportunities,
      icon: BriefcaseBusiness,
      color: "from-violet-500 to-purple-600",
      bgColor: "bg-violet-50 text-violet-600 border-violet-100",
      path: "/opportunities",
    },
    {
      label: "Pending Follow-Ups",
      value: counts.followups,
      icon: CalendarClock,
      color: "from-amber-500 to-orange-600",
      bgColor: "bg-amber-50 text-amber-600 border-amber-100",
      path: "/followups",
    },
  ];

  return (
    <div className="space-y-6">
      <PageHeader
        title="Dashboard"
        description="Overview of your CRM activity, revenue stream, and pipeline performance."
      />

      {error && (
        <div className="flex items-center gap-3 rounded-2xl border border-amber-200 bg-amber-50/80 p-4 text-sm text-amber-800 shadow-sm backdrop-blur-md">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-amber-100 font-bold text-amber-600">
            !
          </div>
          <div>
            <p className="font-semibold">Partial Data Loaded</p>
            <p className="text-amber-700/80">{error}</p>
          </div>
        </div>
      )}

      {/* KPI Cards Grid */}
      <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-4">
        {cards.map((card) => {
          const Icon = card.icon;
          return (
            <Link
              to={card.path}
              key={card.label}
              className="group relative overflow-hidden rounded-3xl border border-slate-200/80 bg-white p-6 shadow-sm transition-all duration-300 hover:-translate-y-1 hover:border-slate-300 hover:shadow-md"
            >
              <div className="flex items-start justify-between">
                <div>
                  <p className="text-xs font-medium uppercase tracking-wider text-slate-400">
                    {card.label}
                  </p>
                  <h3 className="mt-2 text-3xl font-extrabold tracking-tight text-slate-900">
                    {card.value}
                  </h3>
                </div>
                <div
                  className={`rounded-2xl border p-3 transition-transform duration-300 group-hover:scale-110 ${card.bgColor}`}
                >
                  <Icon size={24} />
                </div>
              </div>

              <div className="mt-4 flex items-center justify-between border-t border-slate-100 pt-3 text-xs text-slate-500">
                <span className="inline-flex items-center gap-1 font-medium text-emerald-600">
                  <TrendingUp size={14} /> Live Sync
                </span>
                <span className="flex items-center text-indigo-600 opacity-0 transition-opacity duration-200 group-hover:opacity-100">
                  View details <ArrowUpRight size={14} />
                </span>
              </div>
            </Link>
          );
        })}
      </div>

      {/* Quick Actions Bar */}
      <div className="rounded-3xl border border-slate-200/80 bg-slate-900 p-6 text-white shadow-xl">
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <div className="flex items-center gap-2">
              <Sparkles className="h-5 w-5 text-amber-400" />
              <h3 className="text-lg font-bold">Quick Actions</h3>
            </div>
            <p className="mt-1 text-xs text-slate-400">
              Accelerate your workflow with one-click entity creation.
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <Link
              to="/customers"
              className="inline-flex items-center gap-1.5 rounded-xl bg-slate-800 px-4 py-2 text-xs font-semibold text-slate-200 hover:bg-slate-700 hover:text-white"
            >
              <Plus size={14} /> New Customer
            </Link>
            <Link
              to="/leads"
              className="inline-flex items-center gap-1.5 rounded-xl bg-slate-800 px-4 py-2 text-xs font-semibold text-slate-200 hover:bg-slate-700 hover:text-white"
            >
              <Plus size={14} /> New Lead
            </Link>
            <Link
              to="/opportunities"
              className="inline-flex items-center gap-1.5 rounded-xl bg-indigo-600 px-4 py-2 text-xs font-semibold text-white hover:bg-indigo-500"
            >
              <Plus size={14} /> Add Opportunity
            </Link>
          </div>
        </div>
      </div>

      {/* Key Metrics Dashboard & Analytics Details */}
      <div className="grid gap-6 lg:grid-cols-3">
        {/* Main Metrics Breakdown */}
        <div className="lg:col-span-2 rounded-3xl border border-slate-200/80 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between border-b border-slate-100 pb-4">
            <div>
              <h2 className="text-lg font-bold text-slate-900">
                Monthly Performance Metrics
              </h2>
              <p className="text-xs text-slate-500">
                Detailed metrics breakdown for the current period (THIS_MONTH).
              </p>
            </div>
            <span className="rounded-full bg-indigo-50 px-3 py-1 text-xs font-semibold text-indigo-600">
              This Month
            </span>
          </div>

          <div className="mt-5 grid gap-4 sm:grid-cols-2">
            {Object.keys(obj).length > 0 ? (
              Object.entries(obj).map(([k, v]) => {
                const isCurrency =
                  typeof v === "number" && k.toLowerCase().includes("value");

                return (
                  <div
                    key={k}
                    className="flex flex-col justify-between rounded-2xl border border-slate-100 bg-slate-50/50 p-4 transition-colors hover:bg-slate-50"
                  >
                    <span className="text-xs font-medium uppercase tracking-wider text-slate-400">
                      {k.replaceAll("_", " ")}
                    </span>
                    <div className="mt-2 flex items-baseline justify-between">
                      <span className="text-xl font-bold text-slate-900">
                        {isCurrency ? money(v) : String(v)}
                      </span>
                      {isCurrency && (
                        <div className="rounded-lg bg-emerald-100 p-1 text-emerald-700">
                          <IndianRupee size={16} />
                        </div>
                      )}
                    </div>
                  </div>
                );
              })
            ) : (
              <div className="col-span-2 py-8 text-center text-sm text-slate-400">
                No custom metrics configured for this timeframe.
              </div>
            )}
          </div>
        </div>

        {/* System Summary Card */}
        <div className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-sm flex flex-col justify-between">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Workspace Health</h2>
            <p className="mt-1 text-xs text-slate-500">
              Overview of CRM dataset distribution.
            </p>

            <div className="mt-6 space-y-4">
              {[
                { label: "Customers", value: counts.customers, total: 100, color: "bg-blue-600" },
                { label: "Leads", value: counts.leads, total: 100, color: "bg-emerald-500" },
                { label: "Opportunities", value: counts.opportunities, total: 100, color: "bg-violet-600" },
                { label: "Follow-Ups", value: counts.followups, total: 100, color: "bg-amber-500" },
              ].map((item) => (
                <div key={item.label} className="space-y-1.5">
                  <div className="flex justify-between text-xs font-semibold text-slate-700">
                    <span>{item.label}</span>
                    <span className="text-slate-500">{item.value} Records</span>
                  </div>
                  <div className="h-2 w-full rounded-full bg-slate-100 overflow-hidden">
                    <div
                      className={`h-full rounded-full ${item.color}`}
                      style={{
                        width: `${Math.min(Math.max(item.value * 5, 8), 100)}%`,
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-6 rounded-2xl bg-indigo-50/60 p-4 border border-indigo-100">
            <p className="text-xs font-semibold text-indigo-900">Pro Tip</p>
            <p className="mt-1 text-xs text-indigo-700/80">
              Convert high-priority leads into opportunities early to improve closing accuracy.
            </p>
          </div>
        </div>
      </div>

      {/* Developer API Inspection Section (Collapsible) */}
      <div className="rounded-3xl border border-slate-200/80 bg-white shadow-sm overflow-hidden">
        <button
          onClick={() => setShowRawJson(!showRawJson)}
          className="flex w-full items-center justify-between p-5 text-left text-sm font-semibold text-slate-700 hover:bg-slate-50"
        >
          <div className="flex items-center gap-2">
            <Code2 size={18} className="text-slate-400" />
            <span>Developer API Response Payload</span>
          </div>
          {showRawJson ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
        </button>

        {showRawJson && (
          <div className="border-t border-slate-100 p-5 bg-slate-950">
            <pre className="max-h-80 overflow-auto text-xs font-mono text-emerald-400">
              {JSON.stringify(obj, null, 2)}
            </pre>
          </div>
        )}
      </div>
    </div>
  );
}