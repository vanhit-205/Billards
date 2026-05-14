import { useState } from "react";
import { TrendingUp } from "lucide-react";

type FilterType = "day" | "month";

export function RevenueStatistics() {
  const [filter, setFilter] = useState<FilterType>("day");

  const dailyData = [
    { label: "T2", value: 2400000 },
    { label: "T3", value: 3200000 },
    { label: "T4", value: 2800000 },
    { label: "T5", value: 4100000 },
    { label: "T6", value: 5600000 },
    { label: "T7", value: 6800000 },
    { label: "CN", value: 7200000 },
  ];

  const monthlyData = [
    { label: "T1", value: 45000000 },
    { label: "T2", value: 52000000 },
    { label: "T3", value: 58000000 },
    { label: "T4", value: 61000000 },
    { label: "T5", value: 68000000 },
    { label: "T6", value: 72000000 },
  ];

  const data = filter === "day" ? dailyData : monthlyData;
  const maxValue = Math.max(...data.map((d) => d.value));
  const total = data.reduce((sum, d) => sum + d.value, 0);

  return (
    <div>
      <h3 className="text-lg font-semibold mb-6">Thống kê doanh thu</h3>

      <div className="flex items-center justify-center space-x-6 mb-6">
        <label className="flex items-center space-x-2 cursor-pointer">
          <input
            type="radio"
            name="filter"
            checked={filter === "day"}
            onChange={() => setFilter("day")}
            className="w-4 h-4 text-blue-600 cursor-pointer"
          />
          <span className="text-gray-700">Theo Ngày</span>
        </label>
        <label className="flex items-center space-x-2 cursor-pointer">
          <input
            type="radio"
            name="filter"
            checked={filter === "month"}
            onChange={() => setFilter("month")}
            className="w-4 h-4 text-blue-600 cursor-pointer"
          />
          <span className="text-gray-700">Theo Tháng</span>
        </label>
      </div>

      <div className="bg-white rounded-xl shadow-md p-6 mb-6">
        <div className="flex items-end justify-around h-64 border-b border-l border-gray-300 pb-2 pl-2">
          {data.map((item, index) => {
            const height = (item.value / maxValue) * 100;
            return (
              <div
                key={index}
                className="flex flex-col items-center justify-end flex-1 mx-1"
              >
                <div className="relative w-full flex items-end justify-center mb-2">
                  <div
                    className="w-full bg-gradient-to-t from-blue-600 to-blue-400 rounded-t-lg transition-all duration-500 hover:from-blue-700 hover:to-blue-500 cursor-pointer group relative"
                    style={{ height: `${height}%` }}
                  >
                    <div className="absolute -top-8 left-1/2 -translate-x-1/2 bg-gray-900 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
                      {(item.value / 1000000).toFixed(1)}M ₫
                    </div>
                  </div>
                </div>
                <span className="text-sm text-gray-600 font-medium mt-2">
                  {item.label}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      <div className="bg-gradient-to-br from-green-50 to-emerald-100 rounded-xl p-6">
        <div className="flex items-center space-x-2 mb-2">
          <TrendingUp className="text-green-700" size={20} />
          <span className="text-green-800">Tổng doanh thu:</span>
        </div>
        <p className="text-4xl font-bold text-green-900">
          {total.toLocaleString()} ₫
        </p>
      </div>
    </div>
  );
}
