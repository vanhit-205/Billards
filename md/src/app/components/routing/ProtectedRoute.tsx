import { ReactNode } from 'react';
import { Navigate } from 'react-router';

interface ProtectedRouteProps {
  children: ReactNode;
  user: { name: string; email: string; role: string } | null;
  requireRole?: string[];
}

export function ProtectedRoute({ children, user, requireRole }: ProtectedRouteProps) {
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requireRole && !requireRole.includes(user.role)) {
    return <Navigate to="/jobs" replace />;
  }

  return <>{children}</>;
}
