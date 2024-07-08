package com.nasahacker.nasaeditor.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nasahacker.nasaeditor.databinding.FragmentHtmlBinding;

/**
 * CodeWithTamim
 *
 * @developer Tamim Hossain
 * @mail tamimh.dev@gmail.com
 */
public class HtmlFragment extends Fragment
{
    private FragmentHtmlBinding binding;
    private boolean isViewCreated = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentHtmlBinding.inflate(inflater, container, false);
        isViewCreated = true;
        return binding.getRoot();
    }

    public String getHtmlCode()
    {
        if (!isViewCreated) return "";
        return binding.edtHtmlCode.getText().toString();
    }
}
