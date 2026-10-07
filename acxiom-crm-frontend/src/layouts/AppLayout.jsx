import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  Users,
  UserPlus,
  BriefcaseBusiness,
  CalendarCheck,
  Activity,
  Shield,
  FileText,
  BarChart3,
  Menu,
  X,
  LogOut,
  UserCog,
} from "lucide-react";

const groups = [
  {
    label: "Workspace",
    items: [
      ["/", "Dashboard", LayoutDashboard],
      ["/customers", "Customers", Users],
      ["/leads", "Leads", UserPlus],
      ["/opportunities", "Opportunities", BriefcaseBusiness],
      ["/followups", "Follow-Ups", CalendarCheck],
      ["/activities", "Activities", Activity],
    ],
  },
  {
    label: "Management",
    items: [
      ["/users", "Users", UserCog],
      ["/audit", "Audit Logs", Shield],
      ["/reports", "Reports", BarChart3],
    ],
  },
];

export default function AppLayout() {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("crm_user") || "null");
  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };
  return (
    <div className="min-h-screen bg-slate-50">
      <aside
        className={`fixed inset-y-0 left-0 z-40 w-64 transform bg-slate-950 text-white transition-transform lg:translate-x-0 ${open ? "translate-x-0" : "-translate-x-full"}`}
      >
        <div className="flex h-16 items-center justify-between border-b border-white/10 px-5">
          <div>
            <div className="text-lg font-bold">Acxiom CRM</div>
            <div className="text-xs text-slate-400">Sales workspace</div>
          </div>
          <button className="lg:hidden" onClick={() => setOpen(false)}>
            <X />
          </button>
        </div>
        <nav className="space-y-6 p-4">
          {groups.map((g) => (
            <div key={g.label}>
              <p className="mb-2 px-3 text-[11px] font-semibold uppercase tracking-widest text-slate-500">
                {g.label}
              </p>
              {g.items.map(([to, label, Icon]) => (
                <NavLink
                  key={to}
                  to={to}
                  end={to === "/"}
                  onClick={() => setOpen(false)}
                  className={({ isActive }) =>
                    `mb-1 flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium ${isActive ? "bg-indigo-600 text-white" : "text-slate-300 hover:bg-white/10 hover:text-white"}`
                  }
                >
                  <Icon size={18} />
                  {label}
                </NavLink>
              ))}
            </div>
          ))}
        </nav>
        <div className="absolute bottom-0 w-full border-t border-white/10 p-4">
          <div className="mb-3 flex items-center gap-3 rounded-xl bg-white/5 p-3">
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-indigo-500 font-bold">
              {(user?.name || user?.username || "U")[0].toUpperCase()}
            </div>
            <div className="min-w-0">
              <p className="truncate text-sm font-semibold">
                {user?.name || user?.username || "User"}
              </p>
              <p className="truncate text-xs text-slate-400">
                {user?.role || "CRM User"}
              </p>
            </div>
          </div>
          <button
            onClick={logout}
            className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm text-slate-300 hover:bg-red-500/10 hover:text-red-300"
          >
            <LogOut size={18} />
            Logout
          </button>
        </div>
      </aside>
      <div className="lg:pl-64">
        <header className="sticky top-0 z-30 flex h-16 items-center border-b border-slate-200 bg-white/90 px-4 backdrop-blur sm:px-6">
          <button
            onClick={() => setOpen(true)}
            className="mr-3 rounded-lg p-2 hover:bg-slate-100 lg:hidden"
          >
            <Menu />
          </button>
          <div className="flex-1">
            <span className="text-sm font-medium text-slate-500">
              CRM Dashboard
            </span>
          </div>
          <span className="hidden text-sm text-slate-600 sm:block">
            {user?.email}
          </span>
        </header>
        <main className="p-4 sm:p-6 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
