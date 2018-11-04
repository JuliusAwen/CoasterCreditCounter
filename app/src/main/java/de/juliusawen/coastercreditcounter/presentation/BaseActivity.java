package de.juliusawen.coastercreditcounter.presentation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public abstract class BaseActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    protected Bundle savedInstanceState;

    private Toolbar toolbar;
    private HelpOverlayFragment helpOverlayFragment;
    private ConfirmDialogFragment confirmDialogFragment;
    private FloatingActionButton floatingActionButton;
    private View.OnClickListener onClickListenerFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.onCreate:: creating activity...");

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
                Log.d(Constants.LOG_TAG, String.format("BaseActivity.onOptionsItemSelected:: [%S] selected", selection));
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
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(this.helpOverlayFragment != null)
        {
            outState.putBoolean(Constants.KEY_HELP_OVERLAY_IS_VISIBLE, this.helpOverlayFragment.isVisible());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        if(this.helpOverlayFragment != null)
        {
            this.setHelpOverlayVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_OVERLAY_IS_VISIBLE));
        }
    }

    protected void addToolbar()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.addToolbar:: setting toolbar...");
        this.toolbar = findViewById(R.id.toolbar);
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

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Log.d(Constants.LOG_TAG, "BaseActivity.onKeyDown<BACK>:: hardware back button pressed");
                this.onToolbarHomeButtonBackClicked();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.onToolbarHomeButtonBackClicked:: finishing activity...");
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
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

    protected void addFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createFloatingActionButton:: creating floating action button...");

        this.floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    protected FloatingActionButton getFloatingActionButton()
    {
        return this.floatingActionButton;
    }

    public void setFloatingActionButtonIcon(Drawable icon)
    {
        if(this.floatingActionButton != null)
        {
            this.floatingActionButton.setImageDrawable(icon);
        }
    }

    public void setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
        if(this.floatingActionButton != null)
        {
            this.floatingActionButton.setOnClickListener(onClickListener);
            this.onClickListenerFloatingActionButton = onClickListener;
        }
    }

    protected void setFloatingActionButtonVisibility(boolean isVisible)
    {
        if(this.floatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setFloatingActionButtonVisibility:: isVisible[%s]", isVisible));

            if(isVisible)
            {
                this.floatingActionButton.show();
            }
            else
            {
                this.floatingActionButton.hide();
            }
        }
    }

    protected void disableFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.disableFloatingActionButton:: disabling floating action button...");

        this.floatingActionButton.hide();
        this.floatingActionButton = null;
    }

    protected void animateFloatingActionButtonTransition(Drawable icon)
    {
        this.floatingActionButton.hide();

        FloatingActionButton newFloatingActionButton;
        if(this.floatingActionButton.getId() == R.id.floatingActionButton)
        {
            newFloatingActionButton = findViewById(R.id.floatingActionButton_Animation);
        }
        else
        {
            newFloatingActionButton = findViewById(R.id.floatingActionButton);
        }

        newFloatingActionButton.setOnClickListener(this.onClickListenerFloatingActionButton);
        newFloatingActionButton.setImageDrawable(icon != null ? icon : this.floatingActionButton.getDrawable());
        this.floatingActionButton = newFloatingActionButton;
        this.floatingActionButton.show();
    }

    protected void addHelpOverlayFragment(String title, CharSequence message)
    {
        if (this.savedInstanceState == null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addHelpOverlayFragment:: creating fragment...");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(title, message);
            fragmentTransaction.add(this.findViewById(android.R.id.content).getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(this.helpOverlayFragment);
            fragmentTransaction.commit();
        }
        else
        {
            Log.v(Constants.LOG_TAG, "BaseActivity.addHelpOverlayFragment:: re-using fragment...");
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
            Log.v(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayVisibility:: isVisible[%s]", isVisible));

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

    protected void addConfirmDialogFragment()
    {
        if(this.savedInstanceState == null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addConfirmDialogFragment:: creating confirm dialog fragment...");

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.confirmDialogFragment = ConfirmDialogFragment.newInstance();
            fragmentTransaction.add(findViewById(android.R.id.content).getId(), this.confirmDialogFragment, Constants.FRAGMENT_TAG_CONFIRM_DIALOG);
            fragmentTransaction.commit();
        }
        else
        {
            Log.v(Constants.LOG_TAG, "BaseActivity.addConfirmDialogFragment:: re-using fragment...");
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
