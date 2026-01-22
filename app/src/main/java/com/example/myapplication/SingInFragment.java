package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.FragmentSingInBinding;

public class SingInFragment extends Fragment {

    private FragmentSingInBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSingInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Загружаем изображения через Glide
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/Tq9A16g2bp/pyav2p0s_expires_30_days.png")
                .into(binding.rundefined);

        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/Tq9A16g2bp/pyav2p0s_expires_30_days.png")
                .into(binding.r53b7xf8wfk);

        // Настройка кнопок
        binding.r1uy45p5ur5r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
            }
        });


        // Существующая навигационная логика
        binding.tosingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SingInFragment.this)
                        .navigate(R.id.action_singInFragment_to_singUpFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
