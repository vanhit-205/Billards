import { useState } from "react";
import { Users, BarChart3, Plus } from "lucide-react";
import { AddStaffDialog } from "./AddStaffDialog";
import { StaffCard } from "./StaffCard";
import { RevenueStatistics } from "./RevenueStatistics";

type Staff = {
  id: number;
  name: string;
  email: string;
};

export function AdminPanel() {
  const [activeTab, setActiveTab] = useState<"staff" | "stats">("staff");
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [staff, setStaff] = useState<Staff[]>([
    { id: 1, name: "Nguyễn Văn A", email: "vana@example.com" },
    { id: 2, name: "Trần Thị B", email: "thib@example.com" },
  ]);

  const handleAddStaff = (name: string, email: string, password: string) => {
    const newStaff: Staff = {
      id: Date.now(),
      name,
      email,
    };
    setStaff([...staff, newStaff]);
    setShowAddDialog(false);
  };

  const handleDeleteStaff = (id: number) => {
    if (confirm("Bạn có chắc muốn xóa nhân viên này?")) {
      setStaff(staff.filter((s) => s.id !== id));
    }
  };

  return (
    <div>
      <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
        <h2 className="mb-6">Quản Trị Viên</h2>

        <div className="flex border-b mb-6">
          <button
            onClick={() => setActiveTab("staff")}
            className={`flex items-center space-x-2 px-6 py-3 border-b-2 transition ${
              activeTab === "staff"
                ? "border-blue-600 text-blue-600"
                : "border-transparent text-gray-600 hover:text-gray-900"
            }`}
          >
            <Users size={18} />
            <span>Nhân viên</span>
          </button>
          <button
            onClick={() => setActiveTab("stats")}
            className={`flex items-center space-x-2 px-6 py-3 border-b-2 transition ${
              activeTab === "stats"
                ? "border-blue-600 text-blue-600"
                : "border-transparent text-gray-600 hover:text-gray-900"
            }`}
          >
            <BarChart3 size={18} />
            <span>Thống kê</span>
          </button>
        </div>

        {activeTab === "staff" && (
          <div>
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-lg font-semibold">Danh sách nhân viên</h3>
              <button
                onClick={() => setShowAddDialog(true)}
                className="flex items-center space-x-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition shadow-sm"
              >
                <Plus size={18} />
                <span>Thêm nhân viên</span>
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {staff.map((person) => (
                <StaffCard
                  key={person.id}
                  name={person.name}
                  email={person.email}
                  onDelete={() => handleDeleteStaff(person.id)}
                />
              ))}
            </div>
          </div>
        )}

        {activeTab === "stats" && <RevenueStatistics />}
      </div>

      {showAddDialog && (
        <AddStaffDialog
          onClose={() => setShowAddDialog(false)}
          onAdd={handleAddStaff}
        />
      )}
    </div>
  );
}
