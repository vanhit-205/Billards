import { useState } from "react";
import { CreditCard, Clock, DollarSign } from "lucide-react";

type TablePayment = {
  id: number;
  tableNumber: number;
  playTime: number;
  hourlyRate: number;
  orderTotal: number;
};

export function PaymentScreen() {
  const [tables] = useState<TablePayment[]>([
    {
      id: 1,
      tableNumber: 1,
      playTime: 3600,
      hourlyRate: 50000,
      orderTotal: 75000,
    },
    {
      id: 2,
      tableNumber: 3,
      playTime: 7200,
      hourlyRate: 50000,
      orderTotal: 120000,
    },
    {
      id: 3,
      tableNumber: 5,
      playTime: 1800,
      hourlyRate: 50000,
      orderTotal: 45000,
    },
  ]);

  const formatTime = (seconds: number) => {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    return `${hrs}h ${mins}m`;
  };

  const calculateTotal = (table: TablePayment) => {
    const hours = Math.ceil(table.playTime / 3600);
    const playFee = hours * table.hourlyRate;
    return playFee + table.orderTotal;
  };

  const handlePayment = (table: TablePayment) => {
    const total = calculateTotal(table);
    alert(
      `Thanh toán bàn ${table.tableNumber}\n` +
        `Tiền chơi: ${((Math.ceil(table.playTime / 3600) * table.hourlyRate)).toLocaleString()} ₫\n` +
        `Tiền đồ ăn/uống: ${table.orderTotal.toLocaleString()} ₫\n` +
        `Tổng cộng: ${total.toLocaleString()} ₫`
    );
  };

  return (
    <div className="bg-gray-50 min-h-screen p-4">
      <div className="max-w-4xl mx-auto">
        <h2 className="mb-6">Thanh toán</h2>

        <div className="space-y-4">
          {tables.map((table) => {
            const hours = Math.ceil(table.playTime / 3600);
            const playFee = hours * table.hourlyRate;
            const total = calculateTotal(table);

            return (
              <div
                key={table.id}
                className="bg-white rounded-xl shadow-md p-6 border border-gray-200"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center text-white text-lg font-bold">
                      {table.tableNumber}
                    </div>
                    <div>
                      <h3 className="font-bold text-lg">
                        Bàn {table.tableNumber}
                      </h3>
                      <p className="text-sm text-gray-500">Đang hoạt động</p>
                    </div>
                  </div>
                </div>

                <div className="space-y-3 mb-4 bg-gray-50 p-4 rounded-lg">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2 text-gray-700">
                      <Clock size={18} />
                      <span>Thời gian chơi</span>
                    </div>
                    <span className="font-semibold">{formatTime(table.playTime)}</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <span className="text-gray-700">Tiền chơi ({hours}h × {table.hourlyRate.toLocaleString()} ₫)</span>
                    <span className="font-semibold">{playFee.toLocaleString()} ₫</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2 text-gray-700">
                      <DollarSign size={18} />
                      <span>Tiền đồ ăn/uống</span>
                    </div>
                    <span className="font-semibold">{table.orderTotal.toLocaleString()} ₫</span>
                  </div>

                  <div className="border-t pt-3 flex items-center justify-between">
                    <span className="font-bold text-lg">Tổng cộng</span>
                    <span className="font-bold text-2xl text-blue-600">
                      {total.toLocaleString()} ₫
                    </span>
                  </div>
                </div>

                <button
                  onClick={() => handlePayment(table)}
                  className="w-full flex items-center justify-center space-x-2 bg-gradient-to-r from-green-600 to-emerald-600 text-white py-3 rounded-lg hover:from-green-700 hover:to-emerald-700 transition shadow-md"
                >
                  <CreditCard size={20} />
                  <span>Thanh toán</span>
                </button>
              </div>
            );
          })}
        </div>

        {tables.length === 0 && (
          <div className="text-center py-12 bg-white rounded-xl">
            <p className="text-gray-500">Không có bàn nào cần thanh toán</p>
          </div>
        )}
      </div>
    </div>
  );
}
