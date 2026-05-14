import { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { Avatar, AvatarFallback } from '../ui/avatar';
import { Badge } from '../ui/badge';
import { Separator } from '../ui/separator';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { Textarea } from '../ui/textarea';
import { User, FileText, Send, Plus, Trash2, Edit } from 'lucide-react';

interface ProfilePageProps {
  onUpdateProfile: (data: { name: string; email: string }) => void;
  onCreateResume: (data: { title: string; content: string }) => void;
  onDeleteResume: (resumeId: number) => void;
}

export function ProfilePage({ onUpdateProfile, onCreateResume, onDeleteResume }: ProfilePageProps) {
  const [name, setName] = useState('Nguyễn Văn A');
  const [email, setEmail] = useState('nguyenvana@email.com');
  const [isCreateResumeOpen, setIsCreateResumeOpen] = useState(false);
  const [resumeTitle, setResumeTitle] = useState('');
  const [resumeContent, setResumeContent] = useState('');

  const mockUser = {
    id: 1,
    name: 'Nguyễn Văn A',
    email: 'nguyenvana@email.com',
    role: 'ROLE_CANDIDATE',
    createdAt: '2026-01-15',
  };

  const mockResumes = [
    {
      id: 1,
      title: 'CV Frontend Developer 2026',
      url: '/uploads/cv1.pdf',
      status: 'APPROVED',
      createdAt: '2026-05-01',
      appliedJobs: 5,
    },
    {
      id: 2,
      title: 'CV Full Stack Developer',
      url: '/uploads/cv2.pdf',
      status: 'PENDING',
      createdAt: '2026-05-05',
      appliedJobs: 2,
    },
  ];

  const mockApplications = [
    {
      id: 1,
      job: { id: 1, title: 'Senior Frontend Developer', company: 'TechCorp Vietnam' },
      resume: { id: 1, title: 'CV Frontend Developer 2026' },
      status: 'REVIEWING',
      appliedAt: '2026-05-10',
    },
    {
      id: 2,
      job: { id: 2, title: 'Backend Java Developer', company: 'VNPay Solutions' },
      resume: { id: 1, title: 'CV Frontend Developer 2026' },
      status: 'APPROVED',
      appliedAt: '2026-05-08',
    },
    {
      id: 3,
      job: { id: 3, title: 'Full Stack Developer', company: 'Startup ABC' },
      resume: { id: 2, title: 'CV Full Stack Developer' },
      status: 'REJECTED',
      appliedAt: '2026-05-05',
    },
  ];

  const handleUpdateProfile = () => {
    onUpdateProfile({ name, email });
  };

  const handleCreateResume = () => {
    onCreateResume({ title: resumeTitle, content: resumeContent });
    setIsCreateResumeOpen(false);
    setResumeTitle('');
    setResumeContent('');
  };

  const statusLabels: Record<string, { label: string; variant: 'default' | 'secondary' | 'destructive' | 'outline' }> = {
    PENDING: { label: 'Chờ duyệt', variant: 'secondary' },
    REVIEWING: { label: 'Đang xem xét', variant: 'default' },
    APPROVED: { label: 'Chấp nhận', variant: 'default' },
    REJECTED: { label: 'Từ chối', variant: 'destructive' },
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-600 text-white py-8">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-4">
            <Avatar className="w-20 h-20">
              <AvatarFallback className="bg-white text-blue-600 text-2xl">
                {mockUser.name.charAt(0)}
              </AvatarFallback>
            </Avatar>
            <div>
              <h1 className="text-3xl">{mockUser.name}</h1>
              <p className="text-blue-100">{mockUser.email}</p>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <Tabs defaultValue="profile" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3 max-w-md">
            <TabsTrigger value="profile">
              <User className="w-4 h-4 mr-2" />
              Hồ sơ
            </TabsTrigger>
            <TabsTrigger value="resumes">
              <FileText className="w-4 h-4 mr-2" />
              CV của tôi
            </TabsTrigger>
            <TabsTrigger value="applications">
              <Send className="w-4 h-4 mr-2" />
              Việc đã ứng tuyển
            </TabsTrigger>
          </TabsList>

          <TabsContent value="profile">
            <Card>
              <CardHeader>
                <CardTitle>Thông tin cá nhân</CardTitle>
                <CardDescription>Cập nhật thông tin tài khoản của bạn</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Họ và tên</Label>
                  <Input
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label>Vai trò</Label>
                  <Input value="Ứng viên" disabled />
                </div>
                <div className="space-y-2">
                  <Label>Ngày tạo tài khoản</Label>
                  <Input
                    value={new Date(mockUser.createdAt).toLocaleDateString('vi-VN')}
                    disabled
                  />
                </div>
                <Separator />
                <Button onClick={handleUpdateProfile}>Cập nhật thông tin</Button>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="resumes">
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <div>
                  <h2 className="text-2xl">CV của tôi</h2>
                  <p className="text-gray-600">Quản lý các CV để ứng tuyển</p>
                </div>
                <Button onClick={() => setIsCreateResumeOpen(true)}>
                  <Plus className="w-4 h-4 mr-2" />
                  Tạo CV mới
                </Button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {mockResumes.map((resume) => (
                  <Card key={resume.id}>
                    <CardHeader>
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <CardTitle className="text-lg">{resume.title}</CardTitle>
                          <CardDescription>
                            Tạo ngày {new Date(resume.createdAt).toLocaleDateString('vi-VN')}
                          </CardDescription>
                        </div>
                        <Badge variant={statusLabels[resume.status].variant}>
                          {statusLabels[resume.status].label}
                        </Badge>
                      </div>
                    </CardHeader>
                    <CardContent className="space-y-3">
                      <div className="text-sm text-gray-600">
                        Đã ứng tuyển: <strong>{resume.appliedJobs} công việc</strong>
                      </div>
                      <Separator />
                      <div className="flex gap-2">
                        <Button variant="outline" size="sm" className="flex-1">
                          <FileText className="w-4 h-4 mr-2" />
                          Xem
                        </Button>
                        <Button variant="outline" size="sm" className="flex-1">
                          <Edit className="w-4 h-4 mr-2" />
                          Sửa
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => onDeleteResume(resume.id)}
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          </TabsContent>

          <TabsContent value="applications">
            <Card>
              <CardHeader>
                <CardTitle>Việc làm đã ứng tuyển</CardTitle>
                <CardDescription>Theo dõi trạng thái các đơn ứng tuyển</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {mockApplications.map((application) => (
                    <div key={application.id} className="border rounded-lg p-4">
                      <div className="flex items-start justify-between mb-3">
                        <div className="flex-1">
                          <h3 className="font-semibold text-lg">{application.job.title}</h3>
                          <p className="text-gray-600">{application.job.company}</p>
                        </div>
                        <Badge variant={statusLabels[application.status].variant}>
                          {statusLabels[application.status].label}
                        </Badge>
                      </div>
                      <div className="text-sm text-gray-600 space-y-1">
                        <p>CV: {application.resume.title}</p>
                        <p>
                          Ngày ứng tuyển:{' '}
                          {new Date(application.appliedAt).toLocaleDateString('vi-VN')}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      <Dialog open={isCreateResumeOpen} onOpenChange={setIsCreateResumeOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>Tạo CV mới</DialogTitle>
            <DialogDescription>Tạo một CV để ứng tuyển công việc</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="resumeTitle">Tiêu đề CV</Label>
              <Input
                id="resumeTitle"
                placeholder="VD: CV Frontend Developer 2026"
                value={resumeTitle}
                onChange={(e) => setResumeTitle(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="resumeContent">Nội dung hoặc đường dẫn file</Label>
              <Textarea
                id="resumeContent"
                placeholder="Nhập nội dung CV hoặc đường dẫn file PDF..."
                rows={6}
                value={resumeContent}
                onChange={(e) => setResumeContent(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateResumeOpen(false)}>
              Hủy
            </Button>
            <Button onClick={handleCreateResume} disabled={!resumeTitle}>
              Tạo CV
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
