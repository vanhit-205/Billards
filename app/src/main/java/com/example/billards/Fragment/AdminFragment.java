package com.example.billards.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.billards.Models.UserSession;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AdminPagerAdapter adapter;
    private TextView tvAdminTitle;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAdminTitle = view.findViewById(R.id.tvAdminTitle);
        tabLayout = view.findViewById(R.id.adminTabLayout);
        viewPager = view.findViewById(R.id.adminViewPager);

        Users currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            tvAdminTitle.setText("Admin: " + currentUser.getName());
        }

        adapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Nhân viên");
                    break;
                case 1:
                    tab.setText("Thống kê");
                    break;
            }
        }).attach();
    }

    private static class AdminPagerAdapter extends FragmentStateAdapter {
        public AdminPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new StaffManagementFragment();
            }
            return new RevenueStatisticsFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
