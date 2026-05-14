import { useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Button } from '../ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Badge } from '../ui/badge';
import { Avatar, AvatarFallback } from '../ui/avatar';
import { Separator } from '../ui/separator';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { Textarea } from '../ui/textarea';
import { Label } from '../ui/label';
import { ArrowLeft, MapPin, DollarSign, Briefcase, Building2, Calendar, Send } from 'lucide-react';

interface JobDetailPageProps {
  onApply: (jobId: number, resumeId: number, coverLetter: string) => void;
}

export function JobDetailPage({ onApply }: JobDetailPageProps) {
  const navigate = useNavigate();
  const { id } = useParams();
  const jobId = parseInt(id || '1');
  const [isApplyDialogOpen, setIsApplyDialogOpen] = useState(false);
  const [selectedResume, setSelectedResume] = useState<number | null>(null);
  const [coverLetter, setCoverLetter] = useState('');

  const mockJob = {
    id: jobId,
    title: 'Senior Frontend Developer',
    company: {
      id: 1,
      name: 'TechCorp Vietnam',
      description: 'Công ty công nghệ hàng đầu chuyên về phát triển phần mềm',
      logo: 'TC',
    },
    location: 'Hà Nội',
    level: 'SENIOR',
    salaryFrom: 20000000,
    salaryTo: 35000000,
    skills: [
      { id: 1, name: 'React' },
      { id: 2, name: 'TypeScript' },
      { id: 3, name: 'Tailwind CSS' },
      { id: 8, name: 'Next.js' },
      { id: 9, name: 'GraphQL' },
    ],
    description: `
## Mô tả công việc

Chúng tôi đang tìm kiếm một Senior Frontend Developer tài năng để tham gia đội ngũ phát triển sản phẩm của chúng tôi. Bạn sẽ chịu trách nhiệm:

- Phát triển và duy trì các ứng dụng web sử dụng React, TypeScript
- Xây dựng các UI components tái sử dụng và hiệu quả
- Tối ưu hóa hiệu suất ứng dụng
- Làm việc chặt chẽ với đội ngũ Backend và Design
- Review code và hướng dẫn các developer junior

## Yêu cầu

- Có ít nhất 4 năm kinh nghiệm làm việc với React
- Thành thạo TypeScript, HTML5, CSS3
- Kinh nghiệm với Next.js, Redux/Zustand
- Hiểu biết về RESTful APIs và GraphQL
- Có kinh nghiệm với Git, CI/CD
- Khả năng làm việc nhóm tốt

## Quyền lợi

- Mức lương hấp dẫn từ 20-35 triệu VNĐ
- Thưởng hiệu suất, thưởng dự án
- Bảo hiểm đầy đủ theo quy định
- Làm việc trong môi trường hiện đại, năng động
- Cơ hội thăng tiến rõ ràng
- Team building, du lịch hàng năm
    `,
    createdAt: '2026-05-10',
    deadline: '2026-06-10',
  };

  const mockResumes = [
    { id: 1, title: 'CV Frontend Developer 2026' },
    { id: 2, title: 'CV Full Stack Developer' },
  ];

  const levelLabels: Record<string, string> = {
    INTERN: 'Thực tập sinh',
    FRESHER: 'Fresher',
    JUNIOR: 'Junior',
    MIDDLE: 'Middle',
    SENIOR: 'Senior',
    LEADER: 'Leader',
  };

  const formatSalary = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount);
  };

  const handleApply = () => {
    if (selectedResume) {
      onApply(jobId, selectedResume, coverLetter);
      setIsApplyDialogOpen(false);
      setCoverLetter('');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-600 text-white py-6">
        <div className="container mx-auto px-4">
          <Button variant="ghost" className="text-white hover:text-white hover:bg-blue-700 mb-4" onClick={() => navigate('/jobs')}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Quay lại
          </Button>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <div className="flex items-start gap-4">
                  <Avatar className="w-16 h-16">
                    <AvatarFallback className="bg-blue-600 text-white text-xl">
                      {mockJob.company.logo}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex-1">
                    <CardTitle className="text-2xl mb-2">{mockJob.title}</CardTitle>
                    <CardDescription className="flex items-center gap-2 text-base">
                      <Building2 className="w-4 h-4" />
                      {mockJob.company.name}
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="flex items-center gap-2">
                    <MapPin className="w-5 h-5 text-gray-500" />
                    <div>
                      <p className="text-sm text-gray-500">Địa điểm</p>
                      <p className="font-medium">{mockJob.location}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Briefcase className="w-5 h-5 text-gray-500" />
                    <div>
                      <p className="text-sm text-gray-500">Cấp bậc</p>
                      <p className="font-medium">{levelLabels[mockJob.level]}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <DollarSign className="w-5 h-5 text-gray-500" />
                    <div>
                      <p className="text-sm text-gray-500">Mức lương</p>
                      <p className="font-medium text-green-600">
                        {formatSalary(mockJob.salaryFrom)} - {formatSalary(mockJob.salaryTo)}
                      </p>
                    </div>
                  </div>
                </div>

                <Separator />

                <div>
                  <h3 className="font-semibold mb-3">Kỹ năng yêu cầu</h3>
                  <div className="flex flex-wrap gap-2">
                    {mockJob.skills.map((skill) => (
                      <Badge key={skill.id} variant="secondary" className="text-sm">
                        {skill.name}
                      </Badge>
                    ))}
                  </div>
                </div>

                <Separator />

                <div className="prose max-w-none">
                  <div
                    className="text-gray-700 whitespace-pre-line"
                    dangerouslySetInnerHTML={{ __html: mockJob.description }}
                  />
                </div>

                <Separator />

                <div className="flex items-center gap-4 text-sm text-gray-600">
                  <div className="flex items-center gap-1">
                    <Calendar className="w-4 h-4" />
                    <span>Đăng: {new Date(mockJob.createdAt).toLocaleDateString('vi-VN')}</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Calendar className="w-4 h-4" />
                    <span className="text-red-600">
                      Hạn nộp: {new Date(mockJob.deadline).toLocaleDateString('vi-VN')}
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          <div className="lg:col-span-1">
            <div className="sticky top-4 space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Ứng tuyển ngay</CardTitle>
                </CardHeader>
                <CardContent>
                  <Button className="w-full" size="lg" onClick={() => setIsApplyDialogOpen(true)}>
                    <Send className="w-4 h-4 mr-2" />
                    Nộp hồ sơ
                  </Button>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Về công ty</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center gap-3">
                    <Avatar className="w-12 h-12">
                      <AvatarFallback className="bg-blue-600 text-white">
                        {mockJob.company.logo}
                      </AvatarFallback>
                    </Avatar>
                    <div>
                      <p className="font-semibold">{mockJob.company.name}</p>
                    </div>
                  </div>
                  <p className="text-sm text-gray-600">{mockJob.company.description}</p>
                  <Button variant="outline" className="w-full">
                    Xem thêm công việc
                  </Button>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </div>

      <Dialog open={isApplyDialogOpen} onOpenChange={setIsApplyDialogOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>Ứng tuyển vị trí</DialogTitle>
            <DialogDescription>{mockJob.title}</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Chọn hồ sơ</Label>
              <div className="space-y-2">
                {mockResumes.map((resume) => (
                  <div
                    key={resume.id}
                    className={`p-3 border rounded-lg cursor-pointer transition-colors ${
                      selectedResume === resume.id
                        ? 'border-blue-600 bg-blue-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                    onClick={() => setSelectedResume(resume.id)}
                  >
                    <p className="font-medium">{resume.title}</p>
                  </div>
                ))}
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="coverLetter">Thư xin việc (không bắt buộc)</Label>
              <Textarea
                id="coverLetter"
                placeholder="Giới thiệu ngắn gọn về bản thân và lý do ứng tuyển..."
                rows={6}
                value={coverLetter}
                onChange={(e) => setCoverLetter(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsApplyDialogOpen(false)}>
              Hủy
            </Button>
            <Button onClick={handleApply} disabled={!selectedResume}>
              Nộp hồ sơ
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
