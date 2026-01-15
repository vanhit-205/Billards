package com.example.billards.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.billards.Models.UserSession;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //khai bao table
    TabLayout tab_layout;
    ViewPager2 pager;
    ViewPagerFragmentAdapter adapter;
    TextView tvname;

    public StaffFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StaffFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvname=view.findViewById(R.id.tvname);

        Users currentUser = UserSession.getInstance().getUser();
        tvname.setText("Nhân viên: "+currentUser.getName());
        tab_layout = view.findViewById(R.id.tablayout);
        pager = view.findViewById(R.id.pager);
        adapter = new ViewPagerFragmentAdapter(this,tab_layout.getTabCount());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tab_layout,pager,((tab,position)->{
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
    public static class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        int size;
        public ViewPagerFragmentAdapter(@NonNull Fragment fragment,int size) {
            super(fragment);
            this.size=size;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
                if(position==1) {
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