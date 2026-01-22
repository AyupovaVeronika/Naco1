package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.FragmentSingUpBinding;

public class SingUpFragment extends Fragment {

    private FragmentSingUpBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSingUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Загрузка изображений с помощью Glide
        if (binding.getRoot().findViewById(R.id.rundefined) != null) {
            Glide.with(this)
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/Tq9A16g2bp/gip7dqdt_expires_30_days.png")
                    .into((ImageView) binding.getRoot().findViewById(R.id.rundefined));
        }

        if (binding.getRoot().findViewById(R.id.ra79ozg9aadw) != null) {
            Glide.with(this)
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/Tq9A16g2bp/gip7dqdt_expires_30_days.png")
                    .into((ImageView) binding.getRoot().findViewById(R.id.ra79ozg9aadw));
        }

        // Обработчики нажатий для кнопок
        View button1 = binding.getRoot().findViewById(R.id.rmiozh4dhgjg);
        if (button1 != null) {
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Pressed");
                }
            });
        }



        // Существующая навигационная логика
        binding.tosingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SingUpFragment.this)
                        .navigate(R.id.action_singUpFragment_to_singInFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}