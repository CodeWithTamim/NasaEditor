package com.nasahacker.nasaeditor.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nasahacker.nasaeditor.databinding.FragmentCssBinding;

/**
 * CodeWithTamim
 *
 * @developer Tamim Hossain
 * @mail tamimh.dev@gmail.com
 */
public class CssFragment extends Fragment
{
    private FragmentCssBinding binding;
    private boolean isViewCreated = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentCssBinding.inflate(inflater, container, false);
        isViewCreated = true;
        return binding.getRoot();
    }

    public String getCssCode()
    {
        if (!isViewCreated) return "";
        return binding.edtCssCode.getText().toString();
    }
}
