import { useState, useMemo } from "react";
import {
  Edit,
  Trash2,
  Eye,
  Search,
  ArrowUpDown,
  ArrowUp,
  ArrowDown,
  ChevronLeft,
  ChevronRight,
  X,
  Inbox,
} from "lucide-react";
import { titleCase } from "../utils/helpers";

export default function DataTable({
  data = [],
  columns = [],
  onEdit,
  onDelete,
  onView,
  searchable = true,
  pagination = true,
  defaultPageSize = 10,
}) {
  const [searchQuery, setSearchQuery] = useState("");
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(defaultPageSize);

  // Filter Data
  const filteredData = useMemo(() => {
    if (!searchQuery.trim()) return data;

    const query = searchQuery.toLowerCase();
    return data.filter((row) =>
      columns.some((col) => {
        const val = row[col.key];
        if (val === null || val === undefined) return false;
        return String(val).toLowerCase().includes(query);
      })
    );
  }, [data, columns, searchQuery]);

  // Sort Data
  const sortedData = useMemo(() => {
    if (!sortConfig.key) return filteredData;

    return [...filteredData].sort((a, b) => {
      const aVal = a[sortConfig.key];
      const bVal = b[sortConfig.key];

      if (aVal === bVal) return 0;
      if (aVal === null || aVal === undefined) return 1;
      if (bVal === null || bVal === undefined) return -1;

      const isAsc = sortConfig.direction === "asc";
      if (typeof aVal === "number" && typeof bVal === "number") {
        return isAsc ? aVal - bVal : bVal - aVal;
      }

      return isAsc
        ? String(aVal).localeCompare(String(bVal))
        : String(bVal).localeCompare(String(aVal));
    });
  }, [filteredData, sortConfig]);

  // Paginate Data
  const totalPages = Math.ceil(sortedData.length / pageSize) || 1;
  const paginatedData = useMemo(() => {
    if (!pagination) return sortedData;
    const start = (currentPage - 1) * pageSize;
    return sortedData.slice(start, start + pageSize);
  }, [sortedData, currentPage, pageSize, pagination]);

  const handleSort = (key) => {
    setSortConfig((prev) => {
      if (prev.key === key) {
        if (prev.direction === "asc") return { key, direction: "desc" };
        return { key: null, direction: "asc" };
      }
      return { key, direction: "asc" };
    });
  };

  const hasActions = Boolean(onEdit || onDelete || onView);

  return (
    <div className="space-y-4">
      {/* Top Controls: Search & Filters */}
      {searchable && (
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div className="relative max-w-sm flex-1">
            <Search
              size={16}
              className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
            />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setCurrentPage(1);
              }}
              placeholder="Search records..."
              className="w-full rounded-xl border border-slate-200 bg-white py-2 pl-10 pr-9 text-sm text-slate-800 placeholder-slate-400 transition-colors focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-100"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery("")}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
              >
                <X size={14} />
              </button>
            )}
          </div>

          <div className="flex items-center gap-2 text-xs text-slate-500">
            <span>Show:</span>
            <select
              value={pageSize}
              onChange={(e) => {
                setPageSize(Number(e.target.value));
                setCurrentPage(1);
              }}
              className="rounded-lg border border-slate-200 bg-white px-2.5 py-1.5 text-xs font-medium text-slate-700 focus:border-indigo-500 focus:outline-none"
            >
              {[5, 10, 25, 50].map((size) => (
                <option key={size} value={size}>
                  {size} rows
                </option>
              ))}
            </select>
          </div>
        </div>
      )}

      {/* Table Container */}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-slate-50/80 text-xs uppercase tracking-wider text-slate-500 backdrop-blur-sm">
              <tr>
                {columns.map((c) => {
                  const isSorted = sortConfig.key === c.key;
                  return (
                    <th
                      key={c.key}
                      onClick={() => handleSort(c.key)}
                      className="group cursor-pointer px-5 py-3.5 font-semibold transition-colors hover:bg-slate-100/60"
                    >
                      <div className="flex items-center gap-1.5">
                        <span>{c.label || titleCase(c.key)}</span>
                        <span className="text-slate-400 group-hover:text-slate-600">
                          {isSorted ? (
                            sortConfig.direction === "asc" ? (
                              <ArrowUp size={14} className="text-indigo-600" />
                            ) : (
                              <ArrowDown size={14} className="text-indigo-600" />
                            )
                          ) : (
                            <ArrowUpDown
                              size={13}
                              className="opacity-0 transition-opacity group-hover:opacity-100"
                            />
                          )}
                        </span>
                      </div>
                    </th>
                  );
                })}
                {hasActions && (
                  <th className="px-5 py-3.5 text-right font-semibold">
                    Actions
                  </th>
                )}
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100">
              {paginatedData.length > 0 ? (
                paginatedData.map((row, i) => (
                  <tr
                    key={row.id ?? i}
                    className="transition-colors hover:bg-slate-50/70"
                  >
                    {columns.map((c) => (
                      <td key={c.key} className="px-5 py-4 text-slate-700">
                        {c.render
                          ? c.render(row[c.key], row)
                          : String(row[c.key] ?? "-")}
                      </td>
                    ))}

                    {hasActions && (
                      <td className="px-5 py-4">
                        <div className="flex justify-end gap-1">
                          {onView && (
                            <button
                              title="View details"
                              onClick={() => onView(row)}
                              className="rounded-lg p-2 text-slate-500 transition-colors hover:bg-slate-100 hover:text-slate-800"
                            >
                              <Eye size={16} />
                            </button>
                          )}
                          {onEdit && (
                            <button
                              title="Edit record"
                              onClick={() => onEdit(row)}
                              className="rounded-lg p-2 text-indigo-600 transition-colors hover:bg-indigo-50 hover:text-indigo-700"
                            >
                              <Edit size={16} />
                            </button>
                          )}
                          {onDelete && (
                            <button
                              title="Delete record"
                              onClick={() => onDelete(row)}
                              className="rounded-lg p-2 text-rose-600 transition-colors hover:bg-rose-50 hover:text-rose-700"
                            >
                              <Trash2 size={16} />
                            </button>
                          )}
                        </div>
                      </td>
                    )}
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan={columns.length + (hasActions ? 1 : 0)}
                    className="py-12 text-center"
                  >
                    <div className="flex flex-col items-center justify-center space-y-2">
                      <div className="rounded-full bg-slate-100 p-3 text-slate-400">
                        <Inbox size={24} />
                      </div>
                      <p className="font-medium text-slate-600">
                        No matching records found
                      </p>
                      <p className="text-xs text-slate-400">
                        {searchQuery
                          ? "Try adjusting your search query."
                          : "There are currently no items available to show."}
                      </p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Footer / Pagination Controls */}
        {pagination && sortedData.length > 0 && (
          <div className="flex flex-col gap-3 border-t border-slate-100 bg-slate-50/50 px-5 py-3.5 sm:flex-row sm:items-center sm:justify-between">
            <span className="text-xs text-slate-500">
              Showing{" "}
              <span className="font-semibold text-slate-700">
                {Math.min(
                  (currentPage - 1) * pageSize + 1,
                  sortedData.length
                )}
              </span>{" "}
              to{" "}
              <span className="font-semibold text-slate-700">
                {Math.min(currentPage * pageSize, sortedData.length)}
              </span>{" "}
              of{" "}
              <span className="font-semibold text-slate-700">
                {sortedData.length}
              </span>{" "}
              entries
            </span>

            <div className="flex items-center gap-1.5 self-end sm:self-auto">
              <button
                onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
                disabled={currentPage === 1}
                className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200 bg-white text-slate-600 transition-colors hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
              >
                <ChevronLeft size={16} />
              </button>

              <span className="px-2 text-xs font-medium text-slate-600">
                Page {currentPage} of {totalPages}
              </span>

              <button
                onClick={() =>
                  setCurrentPage((p) => Math.min(p + 1, totalPages))
                }
                disabled={currentPage === totalPages}
                className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200 bg-white text-slate-600 transition-colors hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}