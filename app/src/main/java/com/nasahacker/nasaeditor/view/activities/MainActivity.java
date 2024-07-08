package com.nasahacker.nasaeditor.view.activities;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nasahacker.nasaeditor.R;
import com.nasahacker.nasaeditor.adapter.ViewPagerAdapter;
import com.nasahacker.nasaeditor.databinding.ActivityMainBinding;
import com.nasahacker.nasaeditor.util.NasaEditor;
import com.nasahacker.nasaeditor.view.fragments.CssFragment;
import com.nasahacker.nasaeditor.view.fragments.HtmlFragment;
import com.nasahacker.nasaeditor.view.fragments.JsFragment;

import android.view.ScaleGestureDetector;

public class MainActivity extends AppCompatActivity
{

    private ViewPagerAdapter viewPagerAdapter;
    private ActivityMainBinding binding;
    private NasaEditor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mEditor = new NasaEditor(binding.webView);

        HtmlFragment htmlFragment = new HtmlFragment();
        CssFragment cssFragment = new CssFragment();
        JsFragment jsFragment = new JsFragment();

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(htmlFragment, "Html");
        viewPagerAdapter.addFragment(cssFragment, "Css");
        viewPagerAdapter.addFragment(jsFragment, "JavaScript");

        binding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) ->
        {
            tab.setText(viewPagerAdapter.getFragmentTitle(position));
            switch (position)
            {
                case 0:
                    tab.setIcon(R.drawable.html_ic);
                    break;
                case 1:
                    tab.setIcon(R.drawable.css_ic);
                    break;
                case 2:
                    tab.setIcon(R.drawable.js_ic);
                    break;
            }
        }).attach();

        binding.btnRun.setOnClickListener(v ->
        {
            if (htmlFragment.getHtmlCode().isEmpty())
            {
                Toast.makeText(this, "Html code is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            String htmlCode = htmlFragment.getHtmlCode();
            String cssCode = cssFragment.getCssCode();
            String jsCode = jsFragment.getJsCode();

            mEditor.setHtmlCode(htmlCode);
            mEditor.setCssCode(cssCode);
            mEditor.setJsCode(jsCode);
            mEditor.runCode();
            binding.webView.setVisibility(View.VISIBLE);
        });

    }

}
