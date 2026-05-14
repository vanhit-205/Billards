import { Link, useNavigate } from 'react-router';
import { Button } from '../ui/button';
import { Avatar, AvatarFallback } from '../ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { Briefcase, User, LogOut, Settings, FileText, LayoutDashboard } from 'lucide-react';

interface NavbarProps {
  user: { name: string; email: string; role: string } | null;
  onLogout: () => void;
}

export function Navbar({ user, onLogout }: NavbarProps) {
  const navigate = useNavigate();
  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isRecruiter = user?.role === 'ROLE_RECRUITER';
  const isCandidate = user?.role === 'ROLE_CANDIDATE';

  return (
    <nav className="bg-white border-b shadow-sm sticky top-0 z-50">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-8">
            <Link
              to={user ? '/jobs' : '/'}
              className="flex items-center gap-2 hover:opacity-80 transition-opacity"
            >
              <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                <Briefcase className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-semibold text-gray-900">JobApp</span>
            </Link>

            {user && (
              <div className="hidden md:flex items-center gap-4">
                <Button variant="ghost" asChild>
                  <Link to="/jobs">Việc làm</Link>
                </Button>
                {isAdmin && (
                  <Button variant="ghost" asChild>
                    <Link to="/admin">
                      <LayoutDashboard className="w-4 h-4 mr-2" />
                      Quản trị
                    </Link>
                  </Button>
                )}
                {isRecruiter && (
                  <Button variant="ghost" asChild>
                    <Link to="/recruiter">
                      <LayoutDashboard className="w-4 h-4 mr-2" />
                      Bảng điều khiển
                    </Link>
                  </Button>
                )}
              </div>
            )}
          </div>

          <div className="flex items-center gap-4">
            {!user ? (
              <>
                <Button variant="ghost" asChild>
                  <Link to="/login">Đăng nhập</Link>
                </Button>
                <Button asChild>
                  <Link to="/register">Đăng ký</Link>
                </Button>
              </>
            ) : (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <button className="flex items-center gap-2 hover:opacity-80 transition-opacity">
                    <Avatar>
                      <AvatarFallback className="bg-blue-600 text-white">
                        {user.name.charAt(0)}
                      </AvatarFallback>
                    </Avatar>
                    <div className="hidden md:block text-left">
                      <p className="text-sm font-medium">{user.name}</p>
                      <p className="text-xs text-gray-500">{user.email}</p>
                    </div>
                  </button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <DropdownMenuLabel>Tài khoản của tôi</DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  {isCandidate && (
                    <>
                      <DropdownMenuItem onClick={() => navigate('/profile')}>
                        <User className="w-4 h-4 mr-2" />
                        Hồ sơ cá nhân
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => navigate('/profile')}>
                        <FileText className="w-4 h-4 mr-2" />
                        CV của tôi
                      </DropdownMenuItem>
                    </>
                  )}
                  {isRecruiter && (
                    <DropdownMenuItem onClick={() => navigate('/recruiter')}>
                      <LayoutDashboard className="w-4 h-4 mr-2" />
                      Bảng điều khiển
                    </DropdownMenuItem>
                  )}
                  {isAdmin && (
                    <DropdownMenuItem onClick={() => navigate('/admin')}>
                      <Settings className="w-4 h-4 mr-2" />
                      Quản trị hệ thống
                    </DropdownMenuItem>
                  )}
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={onLogout} className="text-red-600">
                    <LogOut className="w-4 h-4 mr-2" />
                    Đăng xuất
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
