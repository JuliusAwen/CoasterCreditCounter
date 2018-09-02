package de.juliusawen.coastercreditcounter.presentation.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public abstract class BaseActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Bundle savedInstanceState;

    private FloatingActionButton floatingActionButton;
    private HelpOverlayFragment helpOverlayFragment;
    private ConfirmDialogFragment confirmDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(helpOverlayFragment != null)
        {
            if(menu.findItem(Selection.HELP.ordinal()) == null)
            {
                menu.add(0, Selection.HELP.ordinal(), Menu.NONE, R.string.selection_help);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        switch (selection)
        {
            case HELP:
                this.setHelpOverlayVisibility(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        switch (buttonFunction)
        {
            case CLOSE:
                this.setHelpOverlayVisibility(false);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(getSupportActionBar() != null)
        {
            outState.putString(Constants.KEY_TITLE, getSupportActionBar().getTitle() != null ? getSupportActionBar().getTitle().toString() : "");
            outState.putString(Constants.KEY_SUBTITLE, getSupportActionBar().getSubtitle() != null ? getSupportActionBar().getSubtitle().toString() : "");
        }

        outState.putBoolean(Constants.KEY_HELP_VISIBILITY, this.helpOverlayFragment.isVisible());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.setToolbarTitle(savedInstanceState.getString(Constants.KEY_TITLE));
        this.setToolbarSubtitle(savedInstanceState.getString(Constants.KEY_SUBTITLE));

        if(this.helpOverlayFragment != null)
        {
            this.setHelpOverlayVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBILITY));
        }

    }

    protected void createToolbar(View view, String title, String subtitle, boolean setHomeButton)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setToolbarTitle(title);
        this.setToolbarSubtitle(subtitle);

        if (getSupportActionBar() != null && setHomeButton)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    finish();
                }

            });
        }
    }

    protected void setToolbarTitle(String title)
    {
        if (getSupportActionBar() != null && title != null && !title.trim().isEmpty())
        {
            getSupportActionBar().setTitle(title);
        }
    }

    protected void setToolbarSubtitle(String subtitle)
    {
        if (getSupportActionBar() != null && subtitle != null && !subtitle.trim().isEmpty())
        {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    protected void createFloatingActionButton(FloatingActionButton floatingActionButton, Drawable icon)
    {
        this.floatingActionButton = floatingActionButton;
        this.floatingActionButton.setImageDrawable(icon);
    }

    protected void setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
        this.floatingActionButton.setOnClickListener(onClickListener);
    }

    protected void setFloatingActionButtonVisibility(boolean isVisible)
    {
        floatingActionButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    protected void createHelpOverlayFragment(View containerView, CharSequence helpText, boolean isVisible)
    {
        if (this.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(helpText, isVisible);
            fragmentTransaction.add(containerView.getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.commit();
        }
        else
        {
            this.helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);
        }
    }

    protected void setHelpOverlayText(CharSequence helpText)
    {
        this.helpOverlayFragment.setHelpText(helpText);
    }

    protected void setHelpOverlayVisibility(boolean isVisible)
    {
        this.helpOverlayFragment.setVisibility(isVisible);

        if(this.floatingActionButton != null)
        {
            this.setFloatingActionButtonVisibility(!isVisible);
        }

        if(this.confirmDialogFragment != null)
        {
            this.confirmDialogFragment.setVisibility(!isVisible);
        }
    }

    protected void createConfirmDialogFragment(View containerView)
    {
        if(this.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.confirmDialogFragment = ConfirmDialogFragment.newInstance();
            fragmentTransaction.add(containerView.getId(), this.confirmDialogFragment, Constants.FRAGMENT_TAG_CONFIRM_DIALOG);
            fragmentTransaction.commit();
        }
        else
        {
            this.confirmDialogFragment = (ConfirmDialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_CONFIRM_DIALOG);
        }
    }
}
