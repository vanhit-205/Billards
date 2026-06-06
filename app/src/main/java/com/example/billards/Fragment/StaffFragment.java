package com.example.billards.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.billards.Activities.LoginScreen;
import com.example.billards.Models.UserSession;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.billards.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StaffFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private StaffPagerAdapter adapter;
    private TextView tvStaffTitle;

    public StaffFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStaffTitle = view.findViewById(R.id.tvStaffTitle);
        tabLayout = view.findViewById(R.id.staffTabLayout);
        viewPager = view.findViewById(R.id.staffViewPager);

        updateStaffName();

        tvStaffTitle.setOnClickListener(v -> showPopupMenu());

        adapter = new StaffPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Thanh toán");
                    tab.setIcon(R.drawable.payment);
                    break;
                case 1:
                    tab.setText("Order");
                    tab.setIcon(R.drawable.order);
                    break;
            }
        }).attach();
    }

    private void updateStaffName() {
        Users currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            tvStaffTitle.setText("Nhân Viên: " + currentUser.getName());
        } else {
            tvStaffTitle.setText("Nhân Viên");
        }
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), tvStaffTitle);
        popup.getMenuInflater().inflate(R.menu.account_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit_profile) {
                openEditDialog();
                return true;
            } else if (itemId == R.id.menu_logout) {
                handleLogout();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void openEditDialog() {
        Users currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa thông tin");

        final EditText input = new EditText(getContext());
        input.setText(currentUser.getName());
        builder.setView(input);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateProfile(newName);
            } else {
                Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateProfile(String newName) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid)
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    Users currentUser = UserSession.getInstance().getUser();
                    if (currentUser != null) {
                        currentUser.setName(newName);
                    }
                    updateStaffName();
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show());
    }

    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();
        UserSession.getInstance().clear();
        SessionManager.clearSession(getContext());
        Intent intent = new Intent(getActivity(), LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private static class StaffPagerAdapter extends FragmentStateAdapter {
        public StaffPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new PaymentFragment();
            }
            return new OrderFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
