import { Link } from 'react-router';
import { Button } from '../ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Briefcase, Search, Users, TrendingUp, CheckCircle } from 'lucide-react';

export function HomePage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-16">
        <div className="text-center mb-16">
          <div className="flex justify-center mb-6">
            <div className="w-20 h-20 bg-blue-600 rounded-full flex items-center justify-center">
              <Briefcase className="w-10 h-10 text-white" />
            </div>
          </div>
          <h1 className="text-5xl mb-4 text-gray-900">JobApp</h1>
          <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
            Nền tảng tìm việc làm IT hàng đầu Việt Nam. Kết nối ứng viên tài năng với các công ty công nghệ.
          </p>
          <div className="flex gap-4 justify-center">
            <Button size="lg" asChild>
              <Link to="/register">Đăng ký ngay</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/login">Đăng nhập</Link>
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          <Card>
            <CardHeader>
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
                <Search className="w-6 h-6 text-blue-600" />
              </div>
              <CardTitle>Tìm kiếm dễ dàng</CardTitle>
              <CardDescription>
                Tìm kiếm việc làm theo vị trí, công nghệ, mức lương và địa điểm một cách nhanh chóng
              </CardDescription>
            </CardHeader>
          </Card>

          <Card>
            <CardHeader>
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mb-4">
                <Users className="w-6 h-6 text-green-600" />
              </div>
              <CardTitle>Kết nối trực tiếp</CardTitle>
              <CardDescription>
                Ứng tuyển trực tiếp với nhà tuyển dụng, nhận phản hồi nhanh chóng
              </CardDescription>
            </CardHeader>
          </Card>

          <Card>
            <CardHeader>
              <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mb-4">
                <TrendingUp className="w-6 h-6 text-purple-600" />
              </div>
              <CardTitle>Cơ hội phát triển</CardTitle>
              <CardDescription>
                Hàng nghìn công việc từ startup đến doanh nghiệp lớn đang chờ bạn
              </CardDescription>
            </CardHeader>
          </Card>
        </div>

        <Card className="bg-white">
          <CardHeader>
            <CardTitle className="text-2xl text-center">Tại sao chọn JobApp?</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="flex gap-3">
                <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                <div>
                  <h3 className="font-semibold mb-1">Miễn phí 100%</h3>
                  <p className="text-gray-600">Hoàn toàn miễn phí cho ứng viên tìm việc</p>
                </div>
              </div>
              <div className="flex gap-3">
                <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                <div>
                  <h3 className="font-semibold mb-1">Công ty uy tín</h3>
                  <p className="text-gray-600">Tất cả công ty đều được xác minh</p>
                </div>
              </div>
              <div className="flex gap-3">
                <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                <div>
                  <h3 className="font-semibold mb-1">Bảo mật thông tin</h3>
                  <p className="text-gray-600">Thông tin cá nhân được bảo mật tuyệt đối</p>
                </div>
              </div>
              <div className="flex gap-3">
                <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                <div>
                  <h3 className="font-semibold mb-1">Hỗ trợ 24/7</h3>
                  <p className="text-gray-600">Đội ngũ hỗ trợ luôn sẵn sàng giúp đỡ bạn</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
