import { useState } from "react";
import { Plus, Minus, Trash2, ShoppingBag } from "lucide-react";
import { ProductCard } from "./ProductCard";

type Product = {
  id: number;
  name: string;
  price: number;
  quantity: number;
};

export function OrderScreen() {
  const [availableProducts] = useState([
    { id: 1, name: "Coca Cola", price: 15000 },
    { id: 2, name: "Pepsi", price: 15000 },
    { id: 3, name: "Sting", price: 12000 },
    { id: 4, name: "7-Up", price: 15000 },
    { id: 5, name: "Trà xanh C2", price: 10000 },
    { id: 6, name: "Nước suối", price: 8000 },
  ]);

  const [cart, setCart] = useState<Product[]>([]);

  const addToCart = (product: { id: number; name: string; price: number }, quantity: number) => {
    const existingItem = cart.find((item) => item.id === product.id);
    if (existingItem) {
      setCart(
        cart.map((item) =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        )
      );
    } else {
      setCart([...cart, { ...product, quantity }]);
    }
  };

  const updateQuantity = (id: number, delta: number) => {
    setCart(
      cart
        .map((item) =>
          item.id === id
            ? { ...item, quantity: Math.max(0, item.quantity + delta) }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  const removeFromCart = (id: number) => {
    setCart(cart.filter((item) => item.id !== id));
  };

  const total = cart.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const handleConfirmOrder = () => {
    if (cart.length === 0) {
      alert("Giỏ hàng trống!");
      return;
    }
    alert(`Đơn hàng đã được xác nhận!\nTổng: ${total.toLocaleString()} VNĐ`);
    setCart([]);
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div className="lg:col-span-2">
        <h2 className="mb-6">Danh sách sản phẩm</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {availableProducts.map((product) => (
            <ProductCard
              key={product.id}
              name={product.name}
              price={product.price}
              onAddToCart={(quantity) => addToCart(product, quantity)}
            />
          ))}
        </div>
      </div>

      <div className="lg:col-span-1">
        <div className="bg-white rounded-xl shadow-lg p-6 sticky top-6">
          <h3 className="flex items-center space-x-2 mb-6">
            <ShoppingBag size={22} />
            <span>Giỏ hàng</span>
          </h3>

          {cart.length === 0 ? (
            <p className="text-center text-gray-500 py-8">Giỏ hàng trống</p>
          ) : (
            <>
              <div className="space-y-3 mb-6 max-h-96 overflow-y-auto">
                {cart.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                  >
                    <div className="flex-1">
                      <p className="font-medium text-gray-900">{item.name}</p>
                      <p className="text-sm text-gray-600">
                        {item.price.toLocaleString()} ₫
                      </p>
                    </div>
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => updateQuantity(item.id, -1)}
                        className="p-1 bg-gray-200 hover:bg-gray-300 rounded"
                      >
                        <Minus size={16} />
                      </button>
                      <span className="w-8 text-center font-semibold">
                        {item.quantity}
                      </span>
                      <button
                        onClick={() => updateQuantity(item.id, 1)}
                        className="p-1 bg-gray-200 hover:bg-gray-300 rounded"
                      >
                        <Plus size={16} />
                      </button>
                      <button
                        onClick={() => removeFromCart(item.id)}
                        className="p-1 text-red-600 hover:bg-red-50 rounded ml-2"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              <div className="border-t pt-4 mb-4">
                <div className="flex justify-between items-center mb-4">
                  <span className="text-lg font-semibold">Tổng tạm tính:</span>
                  <span className="text-2xl font-bold text-blue-600">
                    {total.toLocaleString()} ₫
                  </span>
                </div>
              </div>

              <button
                onClick={handleConfirmOrder}
                className="w-full bg-gradient-to-r from-green-600 to-emerald-600 text-white py-3 rounded-lg hover:from-green-700 hover:to-emerald-700 transition shadow-md hover:shadow-lg"
              >
                Xác nhận đơn
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
