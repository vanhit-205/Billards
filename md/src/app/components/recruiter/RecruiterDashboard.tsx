import { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { Badge } from '../ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { Textarea } from '../ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Briefcase, FileText, Plus, Edit, Trash2, Eye, CheckCircle, XCircle } from 'lucide-react';

interface RecruiterDashboardProps {
  onCreateJob: (data: any) => void;
  onUpdateJob: (jobId: number, data: any) => void;
  onDeleteJob: (jobId: number) => void;
  onUpdateResumeStatus: (resumeId: number, status: string) => void;
}

export function RecruiterDashboard({
  onCreateJob,
  onUpdateJob,
  onDeleteJob,
  onUpdateResumeStatus,
}: RecruiterDashboardProps) {
  const [isCreateJobOpen, setIsCreateJobOpen] = useState(false);
  const [jobTitle, setJobTitle] = useState('');
  const [jobDescription, setJobDescription] = useState('');
  const [jobLevel, setJobLevel] = useState('JUNIOR');
  const [jobLocation, setJobLocation] = useState('');
  const [salaryFrom, setSalaryFrom] = useState('');
  const [salaryTo, setSalaryTo] = useState('');

  const mockJobs = [
    {
      id: 1,
      title: 'Senior Frontend Developer',
      level: 'SENIOR',
      location: 'Hà Nội',
      status: 'OPEN',
      applicants: 15,
      createdAt: '2026-05-10',
    },
    {
      id: 2,
      title: 'Backend Java Developer',
      level: 'MIDDLE',
      location: 'Hồ Chí Minh',
      status: 'OPEN',
      applicants: 8,
      createdAt: '2026-05-11',
    },
    {
      id: 3,
      title: 'Full Stack Developer',
      level: 'JUNIOR',
      location: 'Đà Nẵng',
      status: 'CLOSED',
      applicants: 12,
      createdAt: '2026-04-20',
    },
  ];

  const mockApplications = [
    {
      id: 1,
      candidate: { id: 1, name: 'Nguyễn Văn A', email: 'nguyenvana@email.com' },
      job: { id: 1, title: 'Senior Frontend Developer' },
      resume: { id: 1, title: 'CV Frontend Developer 2026', url: '/uploads/cv1.pdf' },
      status: 'PENDING',
      appliedAt: '2026-05-10',
    },
    {
      id: 2,
      candidate: { id: 2, name: 'Trần Thị B', email: 'tranthib@email.com' },
      job: { id: 1, title: 'Senior Frontend Developer' },
      resume: { id: 2, title: 'CV React Developer', url: '/uploads/cv2.pdf' },
      status: 'REVIEWING',
      appliedAt: '2026-05-11',
    },
    {
      id: 3,
      candidate: { id: 3, name: 'Lê Văn C', email: 'levanc@email.com' },
      job: { id: 2, title: 'Backend Java Developer' },
      resume: { id: 3, title: 'CV Java Developer', url: '/uploads/cv3.pdf' },
      status: 'APPROVED',
      appliedAt: '2026-05-09',
    },
  ];

  const handleCreateJob = () => {
    onCreateJob({
      title: jobTitle,
      description: jobDescription,
      level: jobLevel,
      location: jobLocation,
      salaryFrom: parseFloat(salaryFrom),
      salaryTo: parseFloat(salaryTo),
    });
    setIsCreateJobOpen(false);
    setJobTitle('');
    setJobDescription('');
    setJobLevel('JUNIOR');
    setJobLocation('');
    setSalaryFrom('');
    setSalaryTo('');
  };

  const statusLabels: Record<string, { label: string; variant: 'default' | 'secondary' | 'destructive' | 'outline' }> = {
    OPEN: { label: 'Đang tuyển', variant: 'default' },
    CLOSED: { label: 'Đã đóng', variant: 'secondary' },
    PENDING: { label: 'Chờ xem', variant: 'secondary' },
    REVIEWING: { label: 'Đang xem', variant: 'default' },
    APPROVED: { label: 'Chấp nhận', variant: 'default' },
    REJECTED: { label: 'Từ chối', variant: 'destructive' },
  };

  const levelLabels: Record<string, string> = {
    INTERN: 'Thực tập sinh',
    FRESHER: 'Fresher',
    JUNIOR: 'Junior',
    MIDDLE: 'Middle',
    SENIOR: 'Senior',
    LEADER: 'Leader',
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-600 text-white py-8">
        <div className="container mx-auto px-4">
          <h1 className="text-3xl">Bảng điều khiển nhà tuyển dụng</h1>
          <p className="text-blue-100 mt-2">Quản lý tin tuyển dụng và hồ sơ ứng viên</p>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <Tabs defaultValue="jobs" className="space-y-6">
          <TabsList className="grid w-full grid-cols-2 max-w-md">
            <TabsTrigger value="jobs">
              <Briefcase className="w-4 h-4 mr-2" />
              Tin tuyển dụng
            </TabsTrigger>
            <TabsTrigger value="applications">
              <FileText className="w-4 h-4 mr-2" />
              Hồ sơ ứng tuyển
            </TabsTrigger>
          </TabsList>

          <TabsContent value="jobs">
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <div>
                  <h2 className="text-2xl">Tin tuyển dụng</h2>
                  <p className="text-gray-600">Quản lý các tin tuyển dụng của công ty</p>
                </div>
                <Button onClick={() => setIsCreateJobOpen(true)}>
                  <Plus className="w-4 h-4 mr-2" />
                  Đăng tin mới
                </Button>
              </div>

              <div className="grid grid-cols-1 gap-4">
                {mockJobs.map((job) => (
                  <Card key={job.id}>
                    <CardHeader>
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <CardTitle className="text-xl">{job.title}</CardTitle>
                          <CardDescription>
                            {levelLabels[job.level]} • {job.location} • {job.applicants} ứng viên
                          </CardDescription>
                        </div>
                        <Badge variant={statusLabels[job.status].variant}>
                          {statusLabels[job.status].label}
                        </Badge>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="flex gap-2">
                        <Button variant="outline" size="sm">
                          <Eye className="w-4 h-4 mr-2" />
                          Xem
                        </Button>
                        <Button variant="outline" size="sm">
                          <Edit className="w-4 h-4 mr-2" />
                          Sửa
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => onDeleteJob(job.id)}
                        >
                          <Trash2 className="w-4 h-4 mr-2" />
                          Xóa
                        </Button>
                        {job.status === 'OPEN' ? (
                          <Button variant="outline" size="sm">
                            Đóng tin
                          </Button>
                        ) : (
                          <Button variant="outline" size="sm">
                            Mở lại
                          </Button>
                        )}
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
                <CardTitle>Hồ sơ ứng tuyển</CardTitle>
                <CardDescription>Xem xét và phê duyệt hồ sơ ứng viên</CardDescription>
              </CardHeader>
              <CardContent>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Ứng viên</TableHead>
                      <TableHead>Vị trí</TableHead>
                      <TableHead>CV</TableHead>
                      <TableHead>Ngày nộp</TableHead>
                      <TableHead>Trạng thái</TableHead>
                      <TableHead>Hành động</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {mockApplications.map((application) => (
                      <TableRow key={application.id}>
                        <TableCell>
                          <div>
                            <p className="font-medium">{application.candidate.name}</p>
                            <p className="text-sm text-gray-600">{application.candidate.email}</p>
                          </div>
                        </TableCell>
                        <TableCell>{application.job.title}</TableCell>
                        <TableCell>
                          <button className="text-blue-600 hover:underline">
                            {application.resume.title}
                          </button>
                        </TableCell>
                        <TableCell>
                          {new Date(application.appliedAt).toLocaleDateString('vi-VN')}
                        </TableCell>
                        <TableCell>
                          <Badge variant={statusLabels[application.status].variant}>
                            {statusLabels[application.status].label}
                          </Badge>
                        </TableCell>
                        <TableCell>
                          <div className="flex gap-2">
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => onUpdateResumeStatus(application.id, 'APPROVED')}
                            >
                              <CheckCircle className="w-4 h-4" />
                            </Button>
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => onUpdateResumeStatus(application.id, 'REJECTED')}
                            >
                              <XCircle className="w-4 h-4" />
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
        </Tabs>
      </div>

      <Dialog open={isCreateJobOpen} onOpenChange={setIsCreateJobOpen}>
        <DialogContent className="sm:max-w-[600px]">
          <DialogHeader>
            <DialogTitle>Đăng tin tuyển dụng</DialogTitle>
            <DialogDescription>Tạo một tin tuyển dụng mới</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="jobTitle">Tiêu đề công việc</Label>
              <Input
                id="jobTitle"
                placeholder="VD: Senior Frontend Developer"
                value={jobTitle}
                onChange={(e) => setJobTitle(e.target.value)}
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="jobLevel">Cấp bậc</Label>
                <Select value={jobLevel} onValueChange={setJobLevel}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="INTERN">Thực tập sinh</SelectItem>
                    <SelectItem value="FRESHER">Fresher</SelectItem>
                    <SelectItem value="JUNIOR">Junior</SelectItem>
                    <SelectItem value="MIDDLE">Middle</SelectItem>
                    <SelectItem value="SENIOR">Senior</SelectItem>
                    <SelectItem value="LEADER">Leader</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="jobLocation">Địa điểm</Label>
                <Input
                  id="jobLocation"
                  placeholder="Hà Nội, Hồ Chí Minh..."
                  value={jobLocation}
                  onChange={(e) => setJobLocation(e.target.value)}
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="salaryFrom">Lương từ (VNĐ)</Label>
                <Input
                  id="salaryFrom"
                  type="number"
                  placeholder="10000000"
                  value={salaryFrom}
                  onChange={(e) => setSalaryFrom(e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="salaryTo">Lương đến (VNĐ)</Label>
                <Input
                  id="salaryTo"
                  type="number"
                  placeholder="20000000"
                  value={salaryTo}
                  onChange={(e) => setSalaryTo(e.target.value)}
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="jobDescription">Mô tả công việc</Label>
              <Textarea
                id="jobDescription"
                placeholder="Nhập mô tả chi tiết về công việc..."
                rows={6}
                value={jobDescription}
                onChange={(e) => setJobDescription(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateJobOpen(false)}>
              Hủy
            </Button>
            <Button onClick={handleCreateJob} disabled={!jobTitle || !jobDescription}>
              Đăng tin
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
