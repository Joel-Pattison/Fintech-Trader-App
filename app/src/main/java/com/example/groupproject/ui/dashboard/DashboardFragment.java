package com.example.groupproject.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.groupproject.AddBalanceActivity;
import com.example.groupproject.FinantialInformationActivity;
import com.example.groupproject.ManageBrokerBalanceActivity;
import com.example.groupproject.PredictedFuturesActivity;
import com.example.groupproject.TradeActivity;
import com.example.groupproject.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private Button btnTrade, btnFinInfo, btnFinFutures, btnAddBalance, btnManageBrokerBalance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnTrade = binding.btnTrade;
        btnFinInfo = binding.btnViewFinancialInfo;
        btnFinFutures = binding.btnViewPredictedFutures;
        btnAddBalance = binding.btnAddBalance;
        btnManageBrokerBalance = binding.btnManageBrokerBalance;

        btnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), TradeActivity.class);
                startActivity(send);
            }
        });

        btnFinInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), FinantialInformationActivity.class);
                startActivity(send);
            }
        });

        btnFinFutures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), PredictedFuturesActivity.class);
                startActivity(send);
            }
        });

        btnAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), AddBalanceActivity.class);
                startActivity(send);
            }
        });

        btnManageBrokerBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), ManageBrokerBalanceActivity.class);
                startActivity(send);
            }
        });

        //final TextView textView = binding.textDashboard;
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}