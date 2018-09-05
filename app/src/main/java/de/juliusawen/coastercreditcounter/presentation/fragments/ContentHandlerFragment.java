package de.juliusawen.coastercreditcounter.presentation.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;

public class ContentHandlerFragment extends Fragment
{
    private Content content;
    private ProgressBar progressBar;

    public ContentHandlerFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_content_handler, container, false);
        this.progressBar = linearLayout.findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.GONE);

        return linearLayout;
    }

    public Content getContent()
    {
        if(this.content != null)
        {
            return this.content;
        }
        else
        {
            this.fetchContent();
            return this.content;
        }
    }

    public void fetchContent()
    {
        this.progressBar.setVisibility(View.VISIBLE);
        this.content = Content.getInstance();
        this.progressBar.setVisibility(View.GONE);
    }
}
