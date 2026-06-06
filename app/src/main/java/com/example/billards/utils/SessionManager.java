package com.example.billards.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.billards.Models.Users;

/**
 * SessionManager - Quản lý lưu/đọc/xóa phiên đăng nhập bằng SharedPreferences.
 * Giúp người dùng không cần đăng nhập lại khi mở lại ứng dụng.
 */
public class SessionManager {

    private static final String PREF_NAME = "billards_user_session";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    /**
     * Lưu thông tin user vào SharedPreferences
     */
    public static void saveSession(Context context, Users user) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_UID, user.getUid());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Đọc thông tin user từ SharedPreferences
     * @return Users object nếu có session, null nếu không
     */
    public static Users loadSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return null;
        }

        String uid = prefs.getString(KEY_UID, null);
        String name = prefs.getString(KEY_NAME, null);
        String email = prefs.getString(KEY_EMAIL, null);
        String role = prefs.getString(KEY_ROLE, null);

        if (uid == null || email == null || role == null) {
            return null;
        }

        return new Users(uid, name, email, role);
    }

    /**
     * Xóa toàn bộ dữ liệu phiên đăng nhập
     */
    public static void clearSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    /**
     * Kiểm tra nhanh có session hợp lệ hay không
     */
    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
