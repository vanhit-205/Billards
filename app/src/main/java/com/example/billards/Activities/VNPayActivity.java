package com.example.billards.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.billards.R;
import com.example.billards.utils.VNPayConfig;
import com.example.billards.utils.VNPayHelper;

import java.util.Map;
import java.util.Objects;

public class VNPayActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        Toolbar toolbar = findViewById(R.id.toolbarVNPay);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thanh toán VNPAY");
        
        toolbar.setNavigationOnClickListener(v -> handleBack());

        webView = findViewById(R.id.webViewVNPay);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBack();
            }
        });

        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // Kiểm tra URL trả về từ VNPAY dựa trên vnp_ReturnUrl trong Config
                if (url.contains("vnp_ResponseCode") && (url.startsWith(VNPayConfig.vnp_ReturnUrl) || url.contains("https://www.google.com"))) {
                    handlePaymentReturn(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("vnp_ResponseCode") && (url.startsWith(VNPayConfig.vnp_ReturnUrl) || url.contains("google.com"))) {
                    handlePaymentReturn(url);
                }
            }
        });

        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            webView.loadUrl(paymentUrl);
        } else {
            finish();
        }
    }

    private void handleBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    private void handlePaymentReturn(String url) {
        // 1. Trích xuất các tham số từ URL trả về
        Map<String, String> params = VNPayHelper.extractParams(url);

        // 2. Kiểm tra tính toàn vẹn dữ liệu (Checksum) - Tương ứng với Config.hashAllFields trong mẫu
        boolean signatureValid = VNPayHelper.verifyResponse(params);
        String responseCode    = params.get("vnp_ResponseCode");

        Intent result = new Intent();
        result.putExtra("vnp_ResponseCode", responseCode);
        result.putExtra("vnp_TxnRef",       params.get("vnp_TxnRef"));
        result.putExtra("vnp_Amount",       params.get("vnp_Amount"));

        // Truyền lại thông tin bàn để MainActivity có thể reset trạng thái
        result.putExtra("TABLE_ID",      getIntent().getStringExtra("TABLE_ID"));
        result.putExtra("TABLE_NUMBER",  getIntent().getIntExtra("TABLE_NUMBER", -1));
        result.putExtra("TOTAL_AMOUNT",  getIntent().getLongExtra("TOTAL_AMOUNT", 0));
        result.putExtra("DIFF",          getIntent().getLongExtra("DIFF", 0));
        result.putExtra("TABLE_PRICE",   getIntent().getDoubleExtra("TABLE_PRICE", 0.0));
        result.putExtra("FOOD_PRICE",    getIntent().getDoubleExtra("FOOD_PRICE", 0.0));

        // Áp dụng logic hiển thị thông báo như hướng dẫn
        if (signatureValid) {
            if ("00".equals(responseCode)) {
                Toast.makeText(this, "Giao dịch thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, result);
            } else {
                Toast.makeText(this, "Giao dịch không thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED, result);
            }
        } else {
            Toast.makeText(this, "Chữ kỳ không hợp lệ", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED, result);
        }
        
        finish();
    }
}
