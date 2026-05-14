# Android Job App API Integration Guide (Java + Retrofit)

## Overview

Tài liệu này dùng cho AI Agent hoặc developer Android Java frontend để tích hợp backend API cho app tìm việc.

Tech stack đề xuất:

* Java
* Retrofit2
* OkHttp3
* Gson
* JWT Authentication

---

# 1. Dependencies

## build.gradle (Module: app)

```gradle
dependencies {

    implementation 'com.squareup.retrofit2:retrofit:2.11.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.11.0'

    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

}
```

---

# 2. Base URL

```java
public class ApiConfig {

    public static final String BASE_URL =
            "http://10.0.2.2:8080/api/v1/";

}
```

> Nếu dùng máy thật:
>
> đổi localhost thành IP LAN của backend server.

Ví dụ:

```java
http://192.168.1.10:8080/api/v1/
```

---

# 3. Retrofit Client

## RetrofitClient.java

```java
package com.example.jobapp.network;

import com.example.jobapp.utils.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient() {

        HttpLoggingInterceptor logging =
                new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor((Interceptor.Chain chain) -> {

                    Request original = chain.request();

                    String token = TokenManager.getToken();

                    Request.Builder builder = original.newBuilder();

                    if (token != null && !token.isEmpty()) {
                        builder.addHeader(
                                "Authorization",
                                "Bearer " + token
                        );
                    }

                    return chain.proceed(builder.build());

                })
                .build();

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
```

---

# 4. Generic API Response

## ApiResponse.java

```java
package com.example.jobapp.models.common;

public class ApiResponse<T> {

    private boolean success;
    private Meta meta;
    private T data;
    private Object error;
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public Meta getMeta() {
        return meta;
    }

    public T getData() {
        return data;
    }

    public Object getError() {
        return error;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
```

---

# 5. Pagination Response

## PaginationResponse.java

```java
package com.example.jobapp.models.common;

import java.util.List;

public class PaginationResponse<T> {

    private MetaPage meta;
    private List<T> items;

    public MetaPage getMeta() {
        return meta;
    }

    public List<T> getItems() {
        return items;
    }
}
```

---

# 6. Token Manager

## TokenManager.java

```java
package com.example.jobapp.utils;

public class TokenManager {

    private static String token;

    public static void saveToken(String accessToken) {
        token = accessToken;
    }

    public static String getToken() {
        return token;
    }

    public static void clear() {
        token = null;
    }
}
```

---

# 7. Authentication APIs

## AuthApiService.java

```java
package com.example.jobapp.network.services;

import com.example.jobapp.models.auth.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Call<ApiResponse<UserResponse>> register(
            @Body RegisterRequest request
    );

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(
            @Body LoginRequest request
    );

    @POST("auth/refresh")
    Call<ApiResponse<RefreshResponse>> refreshToken(
            @Body RefreshTokenRequest request
    );

    @GET("auth/me")
    Call<ApiResponse<UserResponse>> getMe();

    @POST("auth/logout")
    Call<ApiResponse<Object>> logout(
            @Body LogoutRequest request
    );
}
```

---

# 8. User APIs

## UserApiService.java

```java
package com.example.jobapp.network.services;

import retrofit2.Call;
import retrofit2.http.*;

public interface UserApiService {

    @POST("users")
    Call<ApiResponse<UserResponse>> createUser(
            @Body CreateUserRequest request
    );

    @GET("users/{id}")
    Call<ApiResponse<UserResponse>> getUserById(
            @Path("id") int id
    );

    @PUT("users/{id}")
    Call<ApiResponse<UserResponse>> updateUser(
            @Path("id") int id,
            @Body UpdateUserRequest request
    );

    @DELETE("users/{id}")
    Call<ApiResponse<Object>> deleteUser(
            @Path("id") int id
    );

    @PATCH("users/{id}/lock")
    Call<ApiResponse<UserResponse>> lockUser(
            @Path("id") int id
    );

    @PATCH("users/{id}/unlock")
    Call<ApiResponse<UserResponse>> unlockUser(
            @Path("id") int id
    );

    @PATCH("users/{id}/disable")
    Call<ApiResponse<UserResponse>> disableUser(
            @Path("id") int id
    );

    @PATCH("users/{id}/enable")
    Call<ApiResponse<UserResponse>> enableUser(
            @Path("id") int id
    );

    @GET("users")
    Call<ApiResponse<PaginationResponse<UserResponse>>> getUsers(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );
}
```

---

# 9. Company APIs

## CompanyApiService.java

```java
package com.example.jobapp.network.services;

import retrofit2.Call;
import retrofit2.http.*;

public interface CompanyApiService {

    @POST("companies")
    Call<ApiResponse<CompanyResponse>> createCompany(
            @Body CompanyRequest request
    );

    @GET("companies/{id}")
    Call<ApiResponse<CompanyResponse>> getCompanyById(
            @Path("id") int id
    );

    @PUT("companies/{id}")
    Call<ApiResponse<CompanyResponse>> updateCompany(
            @Path("id") int id,
            @Body CompanyRequest request
    );

    @DELETE("companies/{id}")
    Call<ApiResponse<Object>> deleteCompany(
            @Path("id") int id
    );

    @GET("companies")
    Call<ApiResponse<PaginationResponse<CompanyResponse>>> getCompanies(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );
}
```

---

# 10. Skill APIs

## SkillApiService.java

```java
package com.example.jobapp.network.services;

import retrofit2.Call;
import retrofit2.http.*;

public interface SkillApiService {

    @POST("skills")
    Call<ApiResponse<SkillResponse>> createSkill(
            @Body SkillRequest request
    );

    @GET("skills/{id}")
    Call<ApiResponse<SkillResponse>> getSkillById(
            @Path("id") int id
    );

    @PUT("skills/{id}")
    Call<ApiResponse<SkillResponse>> updateSkill(
            @Path("id") int id,
            @Body SkillRequest request
    );

    @DELETE("skills/{id}")
    Call<ApiResponse<Object>> deleteSkill(
            @Path("id") int id
    );

    @GET("skills")
    Call<ApiResponse<PaginationResponse<SkillResponse>>> getSkills(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );
}
```

---

# 11. Job APIs

## JobApiService.java

```java
package com.example.jobapp.network.services;

import retrofit2.Call;
import retrofit2.http.*;

public interface JobApiService {

    @POST("jobs")
    Call<ApiResponse<JobResponse>> createJob(
            @Body JobRequest request
    );

    @GET("jobs/{id}")
    Call<ApiResponse<JobResponse>> getJobById(
            @Path("id") int id
    );

    @PUT("jobs/{id}")
    Call<ApiResponse<JobResponse>> updateJob(
            @Path("id") int id,
            @Body JobRequest request
    );

    @DELETE("jobs/{id}")
    Call<ApiResponse<Object>> deleteJob(
            @Path("id") int id
    );

    @PATCH("jobs/{id}/close")
    Call<ApiResponse<JobResponse>> closeJob(
            @Path("id") int id
    );

    @PATCH("jobs/{id}/reopen")
    Call<ApiResponse<JobResponse>> reopenJob(
            @Path("id") int id
    );

    @GET("jobs")
    Call<ApiResponse<PaginationResponse<JobResponse>>> getJobs(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("keyword") String keyword,
            @Query("filter") String filter
    );

    @GET("jobs/search")
    Call<ApiResponse<PaginationResponse<JobResponse>>> searchJobs(
            @Query("keyword") String keyword,
            @Query("skill") Integer skill,
            @Query("skills") String skills,
            @Query("salaryFrom") Double salaryFrom,
            @Query("salaryTo") Double salaryTo,
            @Query("location") String location,
            @Query("level") String level,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("jobs/company/{companyId}")
    Call<ApiResponse<PaginationResponse<JobResponse>>> getJobsByCompany(
            @Path("companyId") int companyId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("jobs/skills/{skillId}")
    Call<ApiResponse<PaginationResponse<JobResponse>>> getJobsBySkill(
            @Path("skillId") int skillId,
            @Query("page") int page,
            @Query("size") int size
    );
}
```

---

# 12. Resume APIs

## ResumeApiService.java

```java
package com.example.jobapp.network.services;

import retrofit2.Call;
import retrofit2.http.*;

public interface ResumeApiService {

    @POST("resumes")
    Call<ApiResponse<ResumeResponse>> createResume(
            @Body ResumeRequest request
    );

    @GET("resumes/{id}")
    Call<ApiResponse<ResumeResponse>> getResumeById(
            @Path("id") int id
    );

    @PUT("resumes/{id}")
    Call<ApiResponse<ResumeResponse>> updateResume(
            @Path("id") int id,
            @Body ResumeRequest request
    );

    @DELETE("resumes/{id}")
    Call<ApiResponse<Object>> deleteResume(
            @Path("id") int id
    );

    @PATCH("resumes/{id}/status")
    Call<ApiResponse<ResumeResponse>> updateResumeStatus(
            @Path("id") int id,
            @Body ResumeStatusRequest request
    );

    @GET("resumes")
    Call<ApiResponse<PaginationResponse<ResumeResponse>>> getResumes(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("resumes/my")
    Call<ApiResponse<PaginationResponse<ResumeResponse>>> getMyResumes(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("resumes/company/{companyId}")
    Call<ApiResponse<PaginationResponse<ResumeResponse>>> getCompanyResumes(
            @Path("companyId") int companyId,
            @Query("page") int page,
            @Query("size") int size
    );
}
```

---

# 13. Subscriber APIs

## SubscriberApiService.java

```java
package com.example.jobapp.network.services;

import retrofit2.Call;
import retrofit2.http.*;

public interface SubscriberApiService {

    @POST("subscribers")
    Call<ApiResponse<SubscriberResponse>> createSubscriber(
            @Body SubscriberRequest request
    );

    @GET("subscribers/{id}")
    Call<ApiResponse<SubscriberResponse>> getSubscriber(
            @Path("id") int id
    );

    @PUT("subscribers/{id}")
    Call<ApiResponse<SubscriberResponse>> updateSubscriber(
            @Path("id") int id,
            @Body SubscriberRequest request
    );

    @DELETE("subscribers/{id}")
    Call<ApiResponse<Object>> deleteSubscriber(
            @Path("id") int id
    );

    @PATCH("subscribers/{id}/enable")
    Call<ApiResponse<SubscriberResponse>> enableSubscriber(
            @Path("id") int id
    );

    @PATCH("subscribers/{id}/disable")
    Call<ApiResponse<SubscriberResponse>> disableSubscriber(
            @Path("id") int id
    );

    @GET("subscribers")
    Call<ApiResponse<PaginationResponse<SubscriberResponse>>> getSubscribers(
            @Query("page") int page,
            @Query("size") int size
    );
}
```

---

# 14. Forgot Password APIs

## ForgotPasswordApiService.java

```java
package com.example.jobapp.network.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ForgotPasswordApiService {

    @POST("forgot-password/verify-email")
    Call<ApiResponse<Object>> verifyEmail(
            @Body VerifyEmailRequest request
    );

    @POST("forgot-password/verify-otp")
    Call<ApiResponse<Object>> verifyOtp(
            @Body VerifyOtpRequest request
    );

    @POST("forgot-password/change-password")
    Call<ApiResponse<Object>> changePassword(
            @Body ChangePasswordRequest request
    );
}
```

---

# 15. Example Login Flow

```java
AuthApiService apiService =
        RetrofitClient
                .getClient()
                .create(AuthApiService.class);

LoginRequest request =
        new LoginRequest(
                "test@example.com",
                "123456"
        );

apiService.login(request)
        .enqueue(new Callback<ApiResponse<LoginResponse>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<LoginResponse>> call,
                    Response<ApiResponse<LoginResponse>> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    String token =
                            response.body()
                                    .getData()
                                    .getAccessToken();

                    TokenManager.saveToken(token);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<LoginResponse>> call,
                    Throwable t
            ) {

            }
        });
```

---

# 16. Role System

## Available Roles

```text
ROLE_ADMIN
ROLE_RECRUITER
ROLE_CANDIDATE
```

---

# 17. Job Level Enum

```text
INTERN
FRESHER
JUNIOR
MIDDLE
SENIOR
LEADER
```

---

# 18. Resume Status Enum

```text
PENDING
REVIEWING
APPROVED
REJECTED
```

---

# 19. Job Status Enum

```text
OPEN
CLOSED
DELETED
```

---

# 20. Common Error Handling

## Example Error Response

```json
{
  "success": false,
  "data": {
    "code": 7001,
    "message": "Job not found",
    "path": "/api/v1/jobs/999"
  },
  "code": 404,
  "path": "/api/v1/jobs/999"
}
```

## Android Handling

```java
if (!response.isSuccessful()) {

    Log.e("API", "Error: " + response.code());

}
```

---

# 21. Suggested Android Package Structure

```text
com.example.jobapp
|
|-- models
|-- network
|   |-- services
|   |-- RetrofitClient.java
|
|-- repositories
|-- ui
|-- adapters
|-- utils
```

---

# 22. Recommended Architecture

Khuyến nghị:

```text
MVVM Architecture
```

Recommended:

* ViewModel
* Repository
* Retrofit
* LiveData
* RecyclerView
* Pagination
* Glide/Picasso
* Room Cache

---

# 23. Recommended Features

## Candidate

* Login/Register
* Search Jobs
* Filter Jobs
* Apply Job
* Upload CV
* Subscribe Job Email
* Manage Resume

## Recruiter

* CRUD Jobs
* View Applied Resumes
* Approve/Reject Resume

## Admin

* User Management
* Company Management
* Skill Management
* Subscriber Management

---

# 24. Important Notes

## Authorization

Protected APIs cần:

```http
Authorization: Bearer <access_token>
```

## Pagination

Backend page bắt đầu từ:

```text
1
```

## Soft Delete

DELETE APIs dùng soft delete.

## Refresh Token

Access token hết hạn:

* gọi `/auth/refresh`
* lưu token mới

---

# 25. AI Agent Instructions

Nếu AI Agent generate code:

## Requirements

* Use Retrofit2
* Use Java
* Use MVVM
* Use RecyclerView
* Use ViewBinding
* Handle loading/error/success state
* Handle pagination
* Handle JWT token
* Use Repository Pattern

## Network Layer Rules

* All APIs must use Retrofit interface
* All authenticated APIs automatically attach Bearer token
* Handle 401 unauthorized globally
* Parse API response using generic ApiResponse<T>

---

# END
