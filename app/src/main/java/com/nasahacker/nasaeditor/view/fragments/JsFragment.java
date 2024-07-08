package com.nasahacker.nasaeditor.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nasahacker.nasaeditor.databinding.FragmentJsBinding;

/**
 * CodeWithTamim
 *
 * @developer Tamim Hossain
 * @mail tamimh.dev@gmail.com
 */
public class JsFragment extends Fragment
{
    private FragmentJsBinding binding;
    private boolean isViewCreated = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentJsBinding.inflate(inflater, container, false);
        isViewCreated = true;
        return binding.getRoot();
    }

    public String getJsCode()
    {
        if (!isViewCreated) return "";
        return binding.edtJavaScript.getText().toString();
    }
}
