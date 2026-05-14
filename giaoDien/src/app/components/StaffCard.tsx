import { User, Trash2 } from "lucide-react";

type Props = {
  name: string;
  email: string;
  onDelete: () => void;
};

export function StaffCard({ name, email, onDelete }: Props) {
  return (
    <div className="bg-white rounded-lg shadow-md p-4 border border-gray-200 hover:shadow-lg transition">
      <div className="flex items-center space-x-4">
        <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center text-white">
          <User size={24} />
        </div>

        <div className="flex-1">
          <h3 className="font-bold text-lg text-gray-900">{name}</h3>
          <p className="text-sm text-gray-600">{email}</p>
        </div>

        <button
          onClick={onDelete}
          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition"
        >
          <Trash2 size={20} />
        </button>
      </div>
    </div>
  );
}
