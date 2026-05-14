package com.example.billards.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.billards.Activities.LoginScreen;
import com.example.billards.Models.UserSession;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StaffFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TabLayout tab_layout;
    ViewPager2 pager;
    ViewPagerFragmentAdapter adapter;
    TextView tvname;

    public StaffFragment() {
        // Required empty public constructor
    }

    public static StaffFragment newInstance(String param1, String param2) {
        StaffFragment fragment = new StaffFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvname = view.findViewById(R.id.tvname);

        updateStaffName();

        tvname.setOnClickListener(v -> showPopupMenu());

        tab_layout = view.findViewById(R.id.tablayout);
        pager = view.findViewById(R.id.pager);
        adapter = new ViewPagerFragmentAdapter(this, tab_layout.getTabCount());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tab_layout, pager, ((tab, position) -> {
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
        })).attach();
    }

    private void updateStaffName() {
        Users currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            tvname.setText("Nhân viên: " + currentUser.getName());
        }
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), tvname);
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
        Intent intent = new Intent(getActivity(), LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    public static class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        int size;

        public ViewPagerFragmentAdapter(@NonNull Fragment fragment, int size) {
            super(fragment);
            this.size = size;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return new OrderFragment();
            }
            return new PaymentFragment();
        }

        @Override
        public int getItemCount() {
            return size;
        }
    }
}
