package de.juliusawen.coastercreditcounter.presentation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.AppSettings;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.UserSettings;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public abstract class BaseActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Toolbar toolbar;
    private ActionBar actionBar;
    private HelpOverlayFragment helpOverlayFragment;
    private ConfirmDialogFragment confirmDialogFragment;
    private FloatingActionButton floatingActionButton;
    private View.OnClickListener onClickListenerFloatingActionButton;
    private View progressBar;

    private BaseActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.viewModel = ViewModelProviders.of(this).get(BaseActivityViewModel.class);

        if(App.isInitialized)
        {
            this.viewModel.isInitializingApp = false;

            if(this.helpOverlayFragment != null)
            {
                this.setHelpOverlayVisibility(this.viewModel.helpOverlayFragmentIsVisible);
            }
        }
        else
        {
            //Todo: introduce SplashScreen
            this.addToolbar();
            this.setToolbarTitleAndSubtitle(getString(R.string.title_app_name), null);


            if(this.viewModel.isInitializingApp)
            {
                this.showProgressBar();
            }
            else
            {
                this.viewModel.isInitializingApp = true;
                this.initializeApp();
            }
        }
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

    protected void addToolbar()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.addToolbar:: setting toolbar...");
        this.toolbar = findViewById(R.id.toolbar);
        this.toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        this.actionBar = getSupportActionBar();
    }

    protected void addToolbarHomeButton()
    {
        if(this.actionBar != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarHomeButton:: adding home button to toolbar...");

            this.actionBar.setDisplayHomeAsUpEnabled(true);
            this.actionBar.setDisplayShowHomeEnabled(true);

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

    protected void addToolbarMenuIcon()
    {
        if(this.actionBar != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarMenuIcon:: adding menu icon to toolbar...");

            this.actionBar.setDisplayHomeAsUpEnabled(true);
            this.actionBar.setHomeAsUpIndicator(DrawableTool.setTintToWhite(getDrawable(R.drawable.ic_baseline_menu), this));
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Log.d(Constants.LOG_TAG, "BaseActivity.onKeyDown<BACK>:: hardware back button pressed - finishing activity");
                finish();
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
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setToolbarTitleAndSubtitle:: setting toolbar title[%s] and subtitle[%s]", title, subtitle));

            if(title != null && !title.trim().isEmpty())
            {
                supportActionBar.setTitle(title);
            }

            if(subtitle != null && !subtitle.trim().isEmpty())
            {
                supportActionBar.setSubtitle(subtitle);
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

        if(floatingActionButton != null)
        {
            this.floatingActionButton.hide();
            this.floatingActionButton = null;
        }
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
        this.helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

        if(this.helpOverlayFragment == null)
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

            this.viewModel.helpOverlayFragmentIsVisible = isVisible;
            this.setFloatingActionButtonVisibility(!isVisible);
            this.setConfirmDialogVisibility(!isVisible);
        }
    }

    protected void addConfirmDialogFragment()
    {
        this.confirmDialogFragment = (ConfirmDialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_CONFIRM_DIALOG);

        if(this.confirmDialogFragment == null)
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

    private void initializeApp()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.initializeApp:: initializing app...");

        Object[] params = new Object[1];
        params[0] = this;

        Log.i(Constants.LOG_TAG, "BaseActivity.initializeApp:: executing async initialization...");
        new InitializeAppAsyncTask().execute(params);
    }

    private static class InitializeAppAsyncTask extends AsyncTask<Object, Void, BaseActivity>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected BaseActivity doInBackground(Object... params)
        {
            BaseActivity baseActivity = (BaseActivity)params[0];
            baseActivity.showProgressBar();

//            try
//            {
//                Thread.sleep(5000);
//            }
//            catch(InterruptedException e)
//            {
//                e.printStackTrace();
//            }

            Log.i(Constants.LOG_TAG, "BaseActivity.InitializeAppAsyncTask.doInBackground:: getting instance of <Content>...");
            App.content = Content.getInstance(App.persistency);
            App.content.initialize();

            Log.i(Constants.LOG_TAG, "BaseActivity.InitializeAppAsyncTask.doInBackground:: getting instance of <UserSettings>...");
            App.userSettings = UserSettings.getInstance(App.persistency);
            App.userSettings.initialize();

            return baseActivity;
        }

        @Override
        protected void onPostExecute(BaseActivity baseActivity)
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.InitializeAppAsyncTask.onPostExecute:: finishing initialization...");
            super.onPostExecute(baseActivity);

            baseActivity.hideProgressBar();

            App.isInitialized = true;

            if(AppSettings.jumpToTestActivityOnStart)
            {
                Log.e(Constants.LOG_TAG, "BaseActivity.InitializeAppAsyncTask.onPostExecute:: starting TestActivity");
                baseActivity.startActivity(new Intent(baseActivity, TestActivity.class));
            }
            else
            {
                Log.i(Constants.LOG_TAG, String.format("BaseActivity.InitializeAppAsyncTask.onPostExecute:: restarting [%s]",
                        StringTool.parseActivityName(Objects.requireNonNull(baseActivity.getIntent().getComponent()).getShortClassName())));
                baseActivity.startActivity(new Intent(baseActivity, baseActivity.getClass()));
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }

    private void showProgressBar()
    {
        if(this.progressBar == null)
        {
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View progressBar = getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
            viewGroup.addView(progressBar);

            this.progressBar = progressBar;
        }

        this.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar()
    {
        this.progressBar.setVisibility(View.GONE);
    }
}