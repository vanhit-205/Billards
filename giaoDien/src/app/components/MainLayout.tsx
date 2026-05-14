import { Outlet, Link, useLocation } from "react-router";
import { LayoutDashboard, Table2, ShoppingCart, LogOut } from "lucide-react";

export function MainLayout() {
  const location = useLocation();

  const navItems = [
    { path: "/app/admin", icon: LayoutDashboard, label: "Quản trị" },
    { path: "/app/tables", icon: Table2, label: "Bàn chơi" },
    { path: "/app/staff", icon: ShoppingCart, label: "Nhân viên" },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-8">
              <h1 className="text-xl font-bold text-gray-900">Quản lý Bi-a</h1>

              <div className="hidden md:flex space-x-4">
                {navItems.map((item) => {
                  const Icon = item.icon;
                  const isActive = location.pathname === item.path;
                  return (
                    <Link
                      key={item.path}
                      to={item.path}
                      className={`flex items-center space-x-2 px-3 py-2 rounded-lg transition ${
                        isActive
                          ? "bg-blue-50 text-blue-600"
                          : "text-gray-600 hover:bg-gray-100"
                      }`}
                    >
                      <Icon size={18} />
                      <span>{item.label}</span>
                    </Link>
                  );
                })}
              </div>
            </div>

            <Link
              to="/"
              className="flex items-center space-x-2 text-gray-600 hover:text-red-600 transition"
            >
              <LogOut size={18} />
              <span>Đăng xuất</span>
            </Link>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}
