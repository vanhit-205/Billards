import { ReactNode } from 'react';
import { Navigate } from 'react-router';

interface PublicRouteProps {
  children: ReactNode;
  user: { name: string; email: string; role: string } | null;
}

export function PublicRoute({ children, user }: PublicRouteProps) {
  if (user) {
    return <Navigate to="/jobs" replace />;
  }

  return <>{children}</>;
}
