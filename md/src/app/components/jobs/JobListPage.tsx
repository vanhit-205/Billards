import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../ui/card';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Slider } from '../ui/slider';
import { Search, MapPin, DollarSign, Briefcase, Building2, ChevronRight } from 'lucide-react';
import { Separator } from '../ui/separator';

interface Job {
  id: number;
  title: string;
  company: { id: number; name: string; logo?: string };
  location: string;
  level: string;
  salaryFrom: number;
  salaryTo: number;
  skills: Array<{ id: number; name: string }>;
  description: string;
  createdAt: string;
}

interface JobListPageProps {
  onSearch: (filters: {
    keyword: string;
    location: string;
    level: string;
    skills: string;
    salaryFrom: number;
    salaryTo: number;
  }) => void;
}

export function JobListPage({ onSearch }: JobListPageProps) {
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState('');
  const [location, setLocation] = useState('');
  const [level, setLevel] = useState('');
  const [skills, setSkills] = useState('');
  const [salaryRange, setSalaryRange] = useState([0, 50000000]);

  const mockJobs: Job[] = [
    {
      id: 1,
      title: 'Senior Frontend Developer',
      company: { id: 1, name: 'TechCorp Vietnam' },
      location: 'Hà Nội',
      level: 'SENIOR',
      salaryFrom: 20000000,
      salaryTo: 35000000,
      skills: [
        { id: 1, name: 'React' },
        { id: 2, name: 'TypeScript' },
        { id: 3, name: 'Tailwind CSS' },
      ],
      description: 'Tìm kiếm Senior Frontend Developer có kinh nghiệm với React và TypeScript...',
      createdAt: '2026-05-10',
    },
    {
      id: 2,
      title: 'Backend Java Developer',
      company: { id: 2, name: 'VNPay Solutions' },
      location: 'Hồ Chí Minh',
      level: 'MIDDLE',
      salaryFrom: 15000000,
      salaryTo: 25000000,
      skills: [
        { id: 4, name: 'Java' },
        { id: 5, name: 'Spring Boot' },
        { id: 6, name: 'MySQL' },
      ],
      description: 'Cần tuyển Backend Java Developer có kinh nghiệm với Spring Boot...',
      createdAt: '2026-05-11',
    },
    {
      id: 3,
      title: 'Full Stack Developer',
      company: { id: 3, name: 'Startup ABC' },
      location: 'Đà Nẵng',
      level: 'JUNIOR',
      salaryFrom: 10000000,
      salaryTo: 18000000,
      skills: [
        { id: 1, name: 'React' },
        { id: 4, name: 'Java' },
        { id: 7, name: 'PostgreSQL' },
      ],
      description: 'Tìm kiếm Full Stack Developer trẻ đầy nhiệt huyết...',
      createdAt: '2026-05-12',
    },
  ];

  const handleSearch = () => {
    onSearch({
      keyword,
      location,
      level,
      skills,
      salaryFrom: salaryRange[0],
      salaryTo: salaryRange[1],
    });
  };

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

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-600 text-white py-8">
        <div className="container mx-auto px-4">
          <h1 className="text-3xl mb-4">Tìm việc làm IT</h1>
          <div className="bg-white rounded-lg p-4 shadow-lg">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="md:col-span-2">
                <div className="relative">
                  <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                  <Input
                    placeholder="Vị trí, công nghệ, công ty..."
                    className="pl-10"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                  />
                </div>
              </div>
              <div>
                <div className="relative">
                  <MapPin className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                  <Input
                    placeholder="Địa điểm"
                    className="pl-10"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                  />
                </div>
              </div>
              <div>
                <Button onClick={handleSearch} className="w-full">
                  Tìm kiếm
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          <aside className="lg:col-span-1">
            <Card>
              <CardHeader>
                <CardTitle>Bộ lọc</CardTitle>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="space-y-2">
                  <Label>Cấp bậc</Label>
                  <Select value={level} onValueChange={setLevel}>
                    <SelectTrigger>
                      <SelectValue placeholder="Tất cả cấp bậc" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">Tất cả cấp bậc</SelectItem>
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
                  <Label>Kỹ năng</Label>
                  <Input
                    placeholder="React, Java, Python..."
                    value={skills}
                    onChange={(e) => setSkills(e.target.value)}
                  />
                </div>

                <div className="space-y-2">
                  <Label>Mức lương (VNĐ)</Label>
                  <div className="pt-2">
                    <Slider
                      value={salaryRange}
                      onValueChange={setSalaryRange}
                      max={50000000}
                      step={1000000}
                      className="mb-4"
                    />
                    <div className="flex justify-between text-sm text-gray-600">
                      <span>{formatSalary(salaryRange[0])}</span>
                      <span>{formatSalary(salaryRange[1])}</span>
                    </div>
                  </div>
                </div>

                <Button onClick={handleSearch} className="w-full" variant="outline">
                  Áp dụng bộ lọc
                </Button>
              </CardContent>
            </Card>
          </aside>

          <main className="lg:col-span-3">
            <div className="mb-4 flex items-center justify-between">
              <p className="text-gray-600">{mockJobs.length} công việc được tìm thấy</p>
            </div>

            <div className="space-y-4">
              {mockJobs.map((job) => (
                <Card
                  key={job.id}
                  className="hover:shadow-lg transition-shadow cursor-pointer"
                  onClick={() => navigate(`/jobs/${job.id}`)}
                >
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <CardTitle className="text-xl mb-2">{job.title}</CardTitle>
                        <CardDescription className="flex items-center gap-4 text-base">
                          <span className="flex items-center gap-1">
                            <Building2 className="w-4 h-4" />
                            {job.company.name}
                          </span>
                          <span className="flex items-center gap-1">
                            <MapPin className="w-4 h-4" />
                            {job.location}
                          </span>
                        </CardDescription>
                      </div>
                      <ChevronRight className="w-5 h-5 text-gray-400" />
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-3">
                      <div className="flex items-center gap-2">
                        <Briefcase className="w-4 h-4 text-gray-500" />
                        <Badge variant="secondary">{levelLabels[job.level]}</Badge>
                      </div>
                      <div className="flex items-center gap-2 text-green-600">
                        <DollarSign className="w-4 h-4" />
                        <span className="font-semibold">
                          {formatSalary(job.salaryFrom)} - {formatSalary(job.salaryTo)}
                        </span>
                      </div>
                      <div className="flex flex-wrap gap-2">
                        {job.skills.map((skill) => (
                          <Badge key={skill.id} variant="outline">
                            {skill.name}
                          </Badge>
                        ))}
                      </div>
                      <Separator />
                      <p className="text-sm text-gray-600 line-clamp-2">{job.description}</p>
                    </div>
                  </CardContent>
                  <CardFooter className="text-sm text-gray-500">
                    Đăng ngày: {new Date(job.createdAt).toLocaleDateString('vi-VN')}
                  </CardFooter>
                </Card>
              ))}
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}
