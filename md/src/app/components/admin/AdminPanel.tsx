import { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { Badge } from '../ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { Textarea } from '../ui/textarea';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Users, Building2, Code, Plus, Edit, Trash2, Lock, Unlock, Eye, EyeOff } from 'lucide-react';

interface AdminPanelProps {
  onCreateUser: (data: any) => void;
  onUpdateUser: (userId: number, data: any) => void;
  onDeleteUser: (userId: number) => void;
  onLockUser: (userId: number) => void;
  onUnlockUser: (userId: number) => void;
  onCreateCompany: (data: any) => void;
  onUpdateCompany: (companyId: number, data: any) => void;
  onDeleteCompany: (companyId: number) => void;
  onCreateSkill: (data: any) => void;
  onUpdateSkill: (skillId: number, data: any) => void;
  onDeleteSkill: (skillId: number) => void;
}

export function AdminPanel({
  onCreateUser,
  onUpdateUser,
  onDeleteUser,
  onLockUser,
  onUnlockUser,
  onCreateCompany,
  onUpdateCompany,
  onDeleteCompany,
  onCreateSkill,
  onUpdateSkill,
  onDeleteSkill,
}: AdminPanelProps) {
  const [isCreateUserOpen, setIsCreateUserOpen] = useState(false);
  const [isCreateCompanyOpen, setIsCreateCompanyOpen] = useState(false);
  const [isCreateSkillOpen, setIsCreateSkillOpen] = useState(false);
  const [userName, setUserName] = useState('');
  const [userEmail, setUserEmail] = useState('');
  const [userPassword, setUserPassword] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [companyDescription, setCompanyDescription] = useState('');
  const [skillName, setSkillName] = useState('');

  const mockUsers = [
    {
      id: 1,
      name: 'Nguyễn Văn A',
      email: 'nguyenvana@email.com',
      role: 'ROLE_CANDIDATE',
      locked: false,
      enabled: true,
      createdAt: '2026-01-15',
    },
    {
      id: 2,
      name: 'Trần Thị B',
      email: 'tranthib@email.com',
      role: 'ROLE_RECRUITER',
      locked: false,
      enabled: true,
      createdAt: '2026-02-10',
    },
    {
      id: 3,
      name: 'Lê Văn C',
      email: 'levanc@email.com',
      role: 'ROLE_CANDIDATE',
      locked: true,
      enabled: false,
      createdAt: '2026-03-05',
    },
  ];

  const mockCompanies = [
    {
      id: 1,
      name: 'TechCorp Vietnam',
      description: 'Công ty công nghệ hàng đầu',
      address: 'Hà Nội',
      createdAt: '2026-01-01',
    },
    {
      id: 2,
      name: 'VNPay Solutions',
      description: 'Giải pháp thanh toán',
      address: 'Hồ Chí Minh',
      createdAt: '2026-01-15',
    },
    {
      id: 3,
      name: 'Startup ABC',
      description: 'Startup công nghệ',
      address: 'Đà Nẵng',
      createdAt: '2026-02-01',
    },
  ];

  const mockSkills = [
    { id: 1, name: 'React', createdAt: '2026-01-01' },
    { id: 2, name: 'TypeScript', createdAt: '2026-01-01' },
    { id: 3, name: 'Tailwind CSS', createdAt: '2026-01-01' },
    { id: 4, name: 'Java', createdAt: '2026-01-02' },
    { id: 5, name: 'Spring Boot', createdAt: '2026-01-02' },
    { id: 6, name: 'MySQL', createdAt: '2026-01-02' },
  ];

  const handleCreateUser = () => {
    onCreateUser({ name: userName, email: userEmail, password: userPassword });
    setIsCreateUserOpen(false);
    setUserName('');
    setUserEmail('');
    setUserPassword('');
  };

  const handleCreateCompany = () => {
    onCreateCompany({ name: companyName, description: companyDescription });
    setIsCreateCompanyOpen(false);
    setCompanyName('');
    setCompanyDescription('');
  };

  const handleCreateSkill = () => {
    onCreateSkill({ name: skillName });
    setIsCreateSkillOpen(false);
    setSkillName('');
  };

  const roleLabels: Record<string, string> = {
    ROLE_ADMIN: 'Quản trị viên',
    ROLE_RECRUITER: 'Nhà tuyển dụng',
    ROLE_CANDIDATE: 'Ứng viên',
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-600 text-white py-8">
        <div className="container mx-auto px-4">
          <h1 className="text-3xl">Bảng điều khiển quản trị</h1>
          <p className="text-blue-100 mt-2">Quản lý người dùng, công ty và kỹ năng</p>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <Tabs defaultValue="users" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3 max-w-2xl">
            <TabsTrigger value="users">
              <Users className="w-4 h-4 mr-2" />
              Người dùng
            </TabsTrigger>
            <TabsTrigger value="companies">
              <Building2 className="w-4 h-4 mr-2" />
              Công ty
            </TabsTrigger>
            <TabsTrigger value="skills">
              <Code className="w-4 h-4 mr-2" />
              Kỹ năng
            </TabsTrigger>
          </TabsList>

          <TabsContent value="users">
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle>Quản lý người dùng</CardTitle>
                    <CardDescription>Xem và quản lý tất cả người dùng trong hệ thống</CardDescription>
                  </div>
                  <Button onClick={() => setIsCreateUserOpen(true)}>
                    <Plus className="w-4 h-4 mr-2" />
                    Thêm người dùng
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Tên</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Vai trò</TableHead>
                      <TableHead>Trạng thái</TableHead>
                      <TableHead>Ngày tạo</TableHead>
                      <TableHead>Hành động</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {mockUsers.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell>{user.id}</TableCell>
                        <TableCell className="font-medium">{user.name}</TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>
                          <Badge variant="outline">{roleLabels[user.role]}</Badge>
                        </TableCell>
                        <TableCell>
                          <div className="flex gap-1">
                            {user.locked ? (
                              <Badge variant="destructive">Khóa</Badge>
                            ) : (
                              <Badge variant="default">Hoạt động</Badge>
                            )}
                            {!user.enabled && <Badge variant="secondary">Vô hiệu</Badge>}
                          </div>
                        </TableCell>
                        <TableCell>
                          {new Date(user.createdAt).toLocaleDateString('vi-VN')}
                        </TableCell>
                        <TableCell>
                          <div className="flex gap-2">
                            <Button variant="outline" size="sm">
                              <Edit className="w-4 h-4" />
                            </Button>
                            {user.locked ? (
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => onUnlockUser(user.id)}
                              >
                                <Unlock className="w-4 h-4" />
                              </Button>
                            ) : (
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => onLockUser(user.id)}
                              >
                                <Lock className="w-4 h-4" />
                              </Button>
                            )}
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => onDeleteUser(user.id)}
                            >
                              <Trash2 className="w-4 h-4" />
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="companies">
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle>Quản lý công ty</CardTitle>
                    <CardDescription>Xem và quản lý tất cả công ty trong hệ thống</CardDescription>
                  </div>
                  <Button onClick={() => setIsCreateCompanyOpen(true)}>
                    <Plus className="w-4 h-4 mr-2" />
                    Thêm công ty
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {mockCompanies.map((company) => (
                    <Card key={company.id}>
                      <CardHeader>
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <CardTitle className="text-xl">{company.name}</CardTitle>
                            <CardDescription>{company.description}</CardDescription>
                            <p className="text-sm text-gray-600 mt-2">{company.address}</p>
                          </div>
                          <div className="flex gap-2">
                            <Button variant="outline" size="sm">
                              <Edit className="w-4 h-4" />
                            </Button>
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => onDeleteCompany(company.id)}
                            >
                              <Trash2 className="w-4 h-4" />
                            </Button>
                          </div>
                        </div>
                      </CardHeader>
                    </Card>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="skills">
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle>Quản lý kỹ năng</CardTitle>
                    <CardDescription>Xem và quản lý tất cả kỹ năng trong hệ thống</CardDescription>
                  </div>
                  <Button onClick={() => setIsCreateSkillOpen(true)}>
                    <Plus className="w-4 h-4 mr-2" />
                    Thêm kỹ năng
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  {mockSkills.map((skill) => (
                    <Card key={skill.id} className="group hover:shadow-md transition-shadow">
                      <CardContent className="p-4">
                        <div className="flex items-center justify-between">
                          <Badge variant="secondary" className="text-sm">
                            {skill.name}
                          </Badge>
                          <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                            <Button variant="ghost" size="sm" className="h-6 w-6 p-0">
                              <Edit className="w-3 h-3" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="h-6 w-6 p-0"
                              onClick={() => onDeleteSkill(skill.id)}
                            >
                              <Trash2 className="w-3 h-3" />
                            </Button>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      <Dialog open={isCreateUserOpen} onOpenChange={setIsCreateUserOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>Thêm người dùng mới</DialogTitle>
            <DialogDescription>Tạo một tài khoản người dùng mới</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="userName">Họ và tên</Label>
              <Input
                id="userName"
                placeholder="Nguyễn Văn A"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="userEmail">Email</Label>
              <Input
                id="userEmail"
                type="email"
                placeholder="example@email.com"
                value={userEmail}
                onChange={(e) => setUserEmail(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="userPassword">Mật khẩu</Label>
              <Input
                id="userPassword"
                type="password"
                placeholder="••••••••"
                value={userPassword}
                onChange={(e) => setUserPassword(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateUserOpen(false)}>
              Hủy
            </Button>
            <Button onClick={handleCreateUser} disabled={!userName || !userEmail || !userPassword}>
              Tạo người dùng
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isCreateCompanyOpen} onOpenChange={setIsCreateCompanyOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>Thêm công ty mới</DialogTitle>
            <DialogDescription>Tạo một công ty mới trong hệ thống</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="companyName">Tên công ty</Label>
              <Input
                id="companyName"
                placeholder="ABC Company"
                value={companyName}
                onChange={(e) => setCompanyName(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="companyDescription">Mô tả</Label>
              <Textarea
                id="companyDescription"
                placeholder="Mô tả về công ty..."
                rows={4}
                value={companyDescription}
                onChange={(e) => setCompanyDescription(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateCompanyOpen(false)}>
              Hủy
            </Button>
            <Button onClick={handleCreateCompany} disabled={!companyName}>
              Tạo công ty
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isCreateSkillOpen} onOpenChange={setIsCreateSkillOpen}>
        <DialogContent className="sm:max-w-[400px]">
          <DialogHeader>
            <DialogTitle>Thêm kỹ năng mới</DialogTitle>
            <DialogDescription>Tạo một kỹ năng mới trong hệ thống</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="skillName">Tên kỹ năng</Label>
              <Input
                id="skillName"
                placeholder="React, Java, Python..."
                value={skillName}
                onChange={(e) => setSkillName(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateSkillOpen(false)}>
              Hủy
            </Button>
            <Button onClick={handleCreateSkill} disabled={!skillName}>
              Tạo kỹ năng
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
