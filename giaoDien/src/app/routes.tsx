import { createBrowserRouter } from "react-router";
import { LoginScreen } from "./components/LoginScreen";
import { SignUpScreen } from "./components/SignUpScreen";
import { MainLayout } from "./components/MainLayout";
import { AdminPanel } from "./components/AdminPanel";
import { TableManagement } from "./components/TableManagement";
import { StaffScreen } from "./components/StaffScreen";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <LoginScreen />,
  },
  {
    path: "/signup",
    element: <SignUpScreen />,
  },
  {
    path: "/app",
    element: <MainLayout />,
    children: [
      { path: "admin", element: <AdminPanel /> },
      { path: "tables", element: <TableManagement /> },
      { path: "staff", element: <StaffScreen staffName="Nhân viên" /> },
    ],
  },
]);
