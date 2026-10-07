import { Edit, Trash2, Eye } from "lucide-react";
import { titleCase } from "../utils/helpers";

export default function DataTable({
  data = [],
  columns = [],
  onEdit,
  onDelete,
  onView,
}) {
  if (!data.length)
    return (
      <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-12 text-center text-slate-500">
        No records found.
      </div>
    );
  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <div className="overflow-x-auto">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-50 text-xs uppercase tracking-wider text-slate-500">
            <tr>
              {columns.map((c) => (
                <th key={c.key} className="px-5 py-3 font-semibold">
                  {c.label || titleCase(c.key)}
                </th>
              ))}
              {(onEdit || onDelete || onView) && (
                <th className="px-5 py-3 text-right">Actions</th>
              )}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {data.map((row, i) => (
              <tr key={row.id ?? i} className="hover:bg-slate-50">
                {columns.map((c) => (
                  <td key={c.key} className="px-5 py-4 text-slate-700">
                    {c.render
                      ? c.render(row[c.key], row)
                      : String(row[c.key] ?? "-")}
                  </td>
                ))}
                {(onEdit || onDelete || onView) && (
                  <td className="px-5 py-4">
                    <div className="flex justify-end gap-2">
                      {onView && (
                        <button
                          title="View"
                          onClick={() => onView(row)}
                          className="rounded-lg p-2 text-slate-500 hover:bg-slate-100"
                        >
                          <Eye size={17} />
                        </button>
                      )}
                      {onEdit && (
                        <button
                          title="Edit"
                          onClick={() => onEdit(row)}
                          className="rounded-lg p-2 text-indigo-600 hover:bg-indigo-50"
                        >
                          <Edit size={17} />
                        </button>
                      )}
                      {onDelete && (
                        <button
                          title="Delete"
                          onClick={() => onDelete(row)}
                          className="rounded-lg p-2 text-red-600 hover:bg-red-50"
                        >
                          <Trash2 size={17} />
                        </button>
                      )}
                    </div>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
