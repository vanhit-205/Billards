import { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router';
import { Toaster } from './components/ui/sonner';
import { toast } from 'sonner';
import { Navbar } from './components/layout/Navbar';
import { HomePage } from './components/pages/HomePage';
import { LoginPage } from './components/auth/LoginPage';
import { RegisterPage } from './components/auth/RegisterPage';
import { ForgotPasswordPage } from './components/auth/ForgotPasswordPage';
import { JobListPage } from './components/jobs/JobListPage';
import { JobDetailPage } from './components/jobs/JobDetailPage';
import { ProfilePage } from './components/profile/ProfilePage';
import { RecruiterDashboard } from './components/recruiter/RecruiterDashboard';
import { AdminPanel } from './components/admin/AdminPanel';
import { ProtectedRoute } from './components/routing/ProtectedRoute';
import { PublicRoute } from './components/routing/PublicRoute';

function AppContent() {
  const [user, setUser] = useState<{ name: string; email: string; role: string } | null>(null);
  const navigate = useNavigate();

  const handleLogin = async (email: string, password: string) => {
    toast.promise(
      new Promise((resolve) => setTimeout(resolve, 1000)),
      {
        loading: 'Đang đăng nhập...',
        success: () => {
          setUser({
            name: 'Nguyễn Văn A',
            email: email,
            role: 'ROLE_CANDIDATE',
          });
          navigate('/jobs');
          return 'Đăng nhập thành công!';
        },
        error: 'Đăng nhập thất bại!',
      }
    );
  };

  const handleRegister = async (data: { name: string; email: string; password: string; role: string }) => {
    toast.promise(
      new Promise((resolve) => setTimeout(resolve, 1000)),
      {
        loading: 'Đang đăng ký...',
        success: () => {
          setUser({
            name: data.name,
            email: data.email,
            role: data.role,
          });
          navigate('/jobs');
          return 'Đăng ký thành công!';
        },
        error: 'Đăng ký thất bại!',
      }
    );
  };

  const handleLogout = () => {
    setUser(null);
    navigate('/login');
    toast.success('Đã đăng xuất');
  };

  const handleApply = (jobId: number, resumeId: number, coverLetter: string) => {
    toast.success('Nộp hồ sơ thành công!');
  };

  return (
    <>
      <Routes>
        <Route
          path="/"
          element={
            <PublicRoute user={user}>
              <HomePage />
            </PublicRoute>
          }
        />
        <Route
          path="/login"
          element={
            <PublicRoute user={user}>
              <LoginPage onLogin={handleLogin} />
            </PublicRoute>
          }
        />
        <Route
          path="/register"
          element={
            <PublicRoute user={user}>
              <RegisterPage onRegister={handleRegister} />
            </PublicRoute>
          }
        />
        <Route
          path="/forgot-password"
          element={
            <PublicRoute user={user}>
              <ForgotPasswordPage
                onVerifyEmail={async () => {}}
                onVerifyOtp={async () => {}}
                onChangePassword={async () => {}}
              />
            </PublicRoute>
          }
        />
        <Route
          path="/jobs"
          element={
            <ProtectedRoute user={user}>
              <>
                <Navbar user={user} onLogout={handleLogout} />
                <JobListPage
                  onSearch={(filters) => {
                    toast.info('Đang tìm kiếm với bộ lọc...');
                  }}
                />
              </>
            </ProtectedRoute>
          }
        />
        <Route
          path="/jobs/:id"
          element={
            <ProtectedRoute user={user}>
              <>
                <Navbar user={user} onLogout={handleLogout} />
                <JobDetailPage onApply={handleApply} />
              </>
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute user={user} requireRole={['ROLE_CANDIDATE']}>
              <>
                <Navbar user={user} onLogout={handleLogout} />
                <ProfilePage
                  onUpdateProfile={(data) => {
                    toast.success('Cập nhật thông tin thành công!');
                  }}
                  onCreateResume={(data) => {
                    toast.success('Tạo CV thành công!');
                  }}
                  onDeleteResume={(resumeId) => {
                    toast.success('Xóa CV thành công!');
                  }}
                />
              </>
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruiter"
          element={
            <ProtectedRoute user={user} requireRole={['ROLE_RECRUITER']}>
              <>
                <Navbar user={user} onLogout={handleLogout} />
                <RecruiterDashboard
                  onCreateJob={(data) => {
                    toast.success('Đăng tin tuyển dụng thành công!');
                  }}
                  onUpdateJob={(jobId, data) => {
                    toast.success('Cập nhật tin tuyển dụng thành công!');
                  }}
                  onDeleteJob={(jobId) => {
                    toast.success('Xóa tin tuyển dụng thành công!');
                  }}
                  onUpdateResumeStatus={(resumeId, status) => {
                    toast.success('Cập nhật trạng thái hồ sơ thành công!');
                  }}
                />
              </>
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <ProtectedRoute user={user} requireRole={['ROLE_ADMIN']}>
              <>
                <Navbar user={user} onLogout={handleLogout} />
                <AdminPanel
                  onCreateUser={(data) => {
                    toast.success('Tạo người dùng thành công!');
                  }}
                  onUpdateUser={(userId, data) => {
                    toast.success('Cập nhật người dùng thành công!');
                  }}
                  onDeleteUser={(userId) => {
                    toast.success('Xóa người dùng thành công!');
                  }}
                  onLockUser={(userId) => {
                    toast.success('Khóa người dùng thành công!');
                  }}
                  onUnlockUser={(userId) => {
                    toast.success('Mở khóa người dùng thành công!');
                  }}
                  onCreateCompany={(data) => {
                    toast.success('Tạo công ty thành công!');
                  }}
                  onUpdateCompany={(companyId, data) => {
                    toast.success('Cập nhật công ty thành công!');
                  }}
                  onDeleteCompany={(companyId) => {
                    toast.success('Xóa công ty thành công!');
                  }}
                  onCreateSkill={(data) => {
                    toast.success('Tạo kỹ năng thành công!');
                  }}
                  onUpdateSkill={(skillId, data) => {
                    toast.success('Cập nhật kỹ năng thành công!');
                  }}
                  onDeleteSkill={(skillId) => {
                    toast.success('Xóa kỹ năng thành công!');
                  }}
                />
              </>
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      <Toaster position="top-center" richColors />
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}
