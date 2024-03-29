package com.example.groupproject.ui.settings;

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

import com.example.groupproject.BrokerContactActivity;
import com.example.groupproject.LeaderboardActivity;
import com.example.groupproject.MainActivity;
import com.example.groupproject.RatingActivity;
import com.example.groupproject.RegisterActivity;
import com.example.groupproject.SupportActivity;
import com.example.groupproject.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Button btnReview, btnBrokerContact, btnSupport, btnLeaderboard, btnLogout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnReview = binding.btnReview;
        btnBrokerContact = binding.btnRequestProfessional;
        btnSupport = binding.btnContactSupport;
        btnLeaderboard = binding.btnHighscores;
        btnLogout = binding.btnLogout;

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), RatingActivity.class);
                startActivity(send);
            }
        });

        btnBrokerContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), BrokerContactActivity.class);
                startActivity(send);
            }
        });

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), SupportActivity.class);
                startActivity(send);
            }
        });

        btnLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), LeaderboardActivity.class);
                startActivity(send);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent send = new Intent(getActivity(), MainActivity.class);
                startActivity(send);
            }
        });

        //final TextView textView = binding.textNotifications;
        //settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}