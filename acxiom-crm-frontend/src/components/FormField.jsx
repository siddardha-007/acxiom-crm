export default function FormField({
  label,
  name,
  value,
  onChange,
  type = "text",
  required = false,
  options,
  placeholder,
}) {
  const common = {
    name,
    value: value ?? "",
    onChange,
    required,
    placeholder,
    className:
      "mt-1 w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100",
  };
  return (
    <label className="block text-sm font-medium text-slate-700">
      <span>{label}</span>
      {options ? (
        <select {...common}>
          <option value="">Select {label}</option>
          {options.map((o) => (
            <option key={o} value={o}>
              {o.replaceAll("_", " ")}
            </option>
          ))}
        </select>
      ) : (
        <input {...common} type={type} />
      )}
    </label>
  );
}
