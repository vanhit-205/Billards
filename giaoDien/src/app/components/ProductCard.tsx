import { useState } from "react";
import { Plus, Minus } from "lucide-react";

type Props = {
  name: string;
  price: number;
  image?: string;
  onAddToCart: (quantity: number) => void;
};

export function ProductCard({ name, price, image, onAddToCart }: Props) {
  const [quantity, setQuantity] = useState(1);

  const handleIncrease = () => {
    setQuantity((prev) => prev + 1);
  };

  const handleDecrease = () => {
    setQuantity((prev) => Math.max(1, prev - 1));
  };

  const handleAddToCart = () => {
    onAddToCart(quantity);
    setQuantity(1);
  };

  return (
    <div className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200 hover:shadow-lg transition">
      <div className="w-full h-32 bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center">
        {image ? (
          <img src={image} alt={name} className="w-full h-full object-cover" />
        ) : (
          <div className="text-gray-400 text-4xl">🍽️</div>
        )}
      </div>

      <div className="p-4">
        <h3 className="font-bold text-gray-900 mb-2">{name}</h3>
        <p className="text-lg font-bold text-pink-600 mb-3">
          {price.toLocaleString()} VNĐ
        </p>

        <div className="flex items-center space-x-2 mb-3">
          <span className="text-sm text-gray-700">Số lượng</span>
          <div className="flex items-center border border-gray-300 rounded-md overflow-hidden">
            <button
              onClick={handleDecrease}
              className="px-3 py-1 bg-gray-100 hover:bg-gray-200 transition border-r"
            >
              <Minus size={16} />
            </button>
            <span className="px-4 py-1 font-semibold min-w-[50px] text-center">
              {quantity}
            </span>
            <button
              onClick={handleIncrease}
              className="px-3 py-1 bg-gray-100 hover:bg-gray-200 transition border-l"
            >
              <Plus size={16} />
            </button>
          </div>
        </div>

        <button
          onClick={handleAddToCart}
          className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-2 rounded-lg hover:from-blue-700 hover:to-indigo-700 transition shadow-sm"
        >
          Thêm vào giỏ
        </button>
      </div>
    </div>
  );
}
