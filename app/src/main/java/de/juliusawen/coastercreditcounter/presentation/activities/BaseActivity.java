package de.juliusawen.coastercreditcounter.presentation.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public abstract class BaseActivity extends AppCompatActivity implements
        HelpOverlayFragment.HelpOverlayFragmentInteractionListener,
        ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    public static Content content;

    private Bundle savedInstanceState;

    private Toolbar toolbar;
    private HelpOverlayFragment helpOverlayFragment;
    private ConfirmDialogFragment confirmDialogFragment;
    private FloatingActionButton activeFloatingActionButton;
    private View.OnClickListener onClickListenerFloatingActionButton;

    //region @OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, Constants.LOG_DIVIDER + "BaseActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        content = Content.getInstance();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.onPrepareOptionsMenu:: OptionsMenu prepared");

        if(helpOverlayFragment != null)
        {
            if(menu.findItem(Selection.HELP.ordinal()) == null)
            {
                menu.add(Menu.NONE, Selection.HELP.ordinal(), Menu.NONE, R.string.selection_help);
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
                Log.i(Constants.LOG_TAG, String.format("BaseActivity.onOptionsItemSelected:: [%S] selected", selection));
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
                Log.i(Constants.LOG_TAG, String.format("BaseActivity.onHelpOverlayFragmentInteraction:: [%S] selected", buttonFunction));
                this.setHelpOverlayVisibility(false);
                break;
        }
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view) {}

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(getSupportActionBar() != null)
        {
            outState.putString(Constants.KEY_TITLE, getSupportActionBar().getTitle() != null ? getSupportActionBar().getTitle().toString() : "");
            outState.putString(Constants.KEY_SUBTITLE, getSupportActionBar().getSubtitle() != null ? getSupportActionBar().getSubtitle().toString() : "");
        }

        if(this.helpOverlayFragment != null)
        {
            outState.putBoolean(Constants.KEY_HELP_OVERLAY_IS_VISIBLE, this.helpOverlayFragment.isVisible());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        if(getSupportActionBar() != null)
        {
            this.setToolbarTitleAndSubtitle(savedInstanceState.getString(Constants.KEY_TITLE), savedInstanceState.getString(Constants.KEY_SUBTITLE));
        }

        if(this.helpOverlayFragment != null)
        {
            this.setHelpOverlayVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_OVERLAY_IS_VISIBLE));
        }
    }
    //endregion

    //region TOOLBAR
    protected void addToolbar()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createToolbar:: creating toolbar...");

        this.toolbar = this.findViewById(android.R.id.content).findViewById(R.id.toolbar);
        this.toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
    }

    protected void addToolbarHomeButton()
    {
        if (getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarHomeButton:: adding home button to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            this.toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onToolbarHomeButtonBackClicked();
                }

            });
        }
    }

    protected void setToolbarTitleAndSubtitle(String title, String subtitle)
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setToolbarTitleAndSubtitle:: setting toolbar title[%s] and subtitle[%s]", title, subtitle));

            if(title != null && !title.trim().isEmpty())
            {
                getSupportActionBar().setTitle(title);
            }

            if(subtitle != null && !subtitle.trim().isEmpty())
            {
                getSupportActionBar().setSubtitle(subtitle);
            }
        }
    }

    protected void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.onToolbarHomeButtonBackClicked:: finishing activity...");
        finish();
    }
    //endregion

    //region FLOATING ACTION BUTTON
    protected void addFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createFloatingActionButton:: creating floating action button...");

        this.activeFloatingActionButton = this.findViewById(android.R.id.content).getRootView().findViewById(R.id.floatingActionButton);
    }

    protected FloatingActionButton getActiveFloatingActionButton()
    {
        return this.activeFloatingActionButton;
    }

    public void setFloatingActionButtonIcon(Drawable icon)
    {
        if(this.activeFloatingActionButton != null)
        {
            this.activeFloatingActionButton.setImageDrawable(icon);
        }
    }

    public void setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
        if(this.activeFloatingActionButton != null)
        {
            this.activeFloatingActionButton.setOnClickListener(onClickListener);
            this.onClickListenerFloatingActionButton = onClickListener;
        }
    }

    protected void setFloatingActionButtonVisibility(boolean isVisible)
    {
        if(this.activeFloatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setFloatingActionButtonVisibility:: isVisible[%s]", isVisible));

            if(isVisible)
            {
                this.activeFloatingActionButton.show();
            }
            else
            {
                this.activeFloatingActionButton.hide();
            }
        }
    }

    protected void animateFloatingActionButton(Drawable icon)
    {
        this.activeFloatingActionButton.hide();

        FloatingActionButton newFloatingActionButton;
        if(this.activeFloatingActionButton.getId() == R.id.floatingActionButton)
        {
            newFloatingActionButton = this.findViewById(android.R.id.content).getRootView().findViewById(R.id.floatingActionButton_Animation);
        }
        else
        {
            newFloatingActionButton = this.findViewById(android.R.id.content).getRootView().findViewById(R.id.floatingActionButton);
        }

        newFloatingActionButton.setOnClickListener(this.onClickListenerFloatingActionButton);
        newFloatingActionButton.setImageDrawable(icon != null ? icon : this.activeFloatingActionButton.getDrawable());
        this.activeFloatingActionButton = newFloatingActionButton;
        this.activeFloatingActionButton.show();
    }

    //endregion

    //region HELP OVERLAY
    protected void addHelpOverlay(String title, CharSequence message)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.addHelpOverlay:: adding help overlay...");

        if (this.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(title, message);
            fragmentTransaction.add(this.findViewById(android.R.id.content).getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(this.helpOverlayFragment);
            fragmentTransaction.commit();
        }
        else
        {
            this.helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);
        }
    }

    protected void setHelpOverlayTitleAndMessage(String title, CharSequence message)
    {
        if(this.helpOverlayFragment != null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(title, message);
            fragmentTransaction.replace(this.findViewById(android.R.id.content).getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(this.helpOverlayFragment);
            fragmentTransaction.commit();
        }
    }

    protected void setHelpOverlayVisibility(boolean isVisible)
    {
        if(this.helpOverlayFragment != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayVisibility:: isVisible[%s]", isVisible));

            if(isVisible)
            {
                this.showFragment(this.helpOverlayFragment);
            }
            else
            {
                this.hideFragment(this.helpOverlayFragment);
            }

            this.setFloatingActionButtonVisibility(!isVisible);
            this.setConfirmDialogVisibility(!isVisible);
        }
    }
    //endregion

    //region CONFIRM DIALOG
    protected void addConfirmDialog()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createConfimDialogFragment:: creating fragment...");

        if(this.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.confirmDialogFragment = ConfirmDialogFragment.newInstance();
            fragmentTransaction.add(this.findViewById(android.R.id.content).getId(), this.confirmDialogFragment, Constants.FRAGMENT_TAG_CONFIRM_DIALOG);
            fragmentTransaction.commit();
        }
        else
        {
            this.confirmDialogFragment = (ConfirmDialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_CONFIRM_DIALOG);
        }
    }

    protected void setConfirmDialogVisibility(boolean isVisible)
    {
        if(this.confirmDialogFragment != null)
        {
            if(isVisible)
            {
                this.showFragment(this.confirmDialogFragment);
            }
            else
            {
                this.hideFragment(this.confirmDialogFragment);
            }
        }
    }
    //endregion

    private void showFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(fragment)
                .commit();
    }

    private void hideFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(fragment)
                .commit();
    }
}
