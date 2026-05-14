import { useState } from "react";
import { ShoppingCart, CreditCard } from "lucide-react";
import { OrderScreen } from "./OrderScreen";
import { PaymentScreen } from "./PaymentScreen";

type Props = {
  staffName: string;
};

export function StaffScreen({ staffName }: Props) {
  const [activeTab, setActiveTab] = useState<"order" | "payment">("order");

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <h2 className="mb-4">Xin chào, {staffName}</h2>

          <div className="flex border-b">
            <button
              onClick={() => setActiveTab("order")}
              className={`flex items-center space-x-2 px-6 py-3 border-b-2 transition ${
                activeTab === "order"
                  ? "border-blue-600 text-blue-600"
                  : "border-transparent text-gray-600 hover:text-gray-900"
              }`}
            >
              <ShoppingCart size={18} />
              <span>Order</span>
            </button>
            <button
              onClick={() => setActiveTab("payment")}
              className={`flex items-center space-x-2 px-6 py-3 border-b-2 transition ${
                activeTab === "payment"
                  ? "border-blue-600 text-blue-600"
                  : "border-transparent text-gray-600 hover:text-gray-900"
              }`}
            >
              <CreditCard size={18} />
              <span>Payment</span>
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeTab === "order" && <OrderScreen />}
        {activeTab === "payment" && <PaymentScreen />}
      </div>
    </div>
  );
}
