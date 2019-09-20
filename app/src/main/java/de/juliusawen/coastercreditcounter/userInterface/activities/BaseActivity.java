package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.temporaryElements.ITemporaryElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.IOptionsMenuAgentClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.IPopupMenuAgentClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.HelpOverlayFragment;

public abstract class BaseActivity extends AppCompatActivity  implements IOptionsMenuAgentClient, IPopupMenuAgentClient, HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private FloatingActionButton floatingActionButton;
    private View.OnClickListener floatingActionButtonOnClickListener;

    private BaseActivityViewModel viewModel;

    @Override
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "BaseActivity.onCreate:: creating activity...");

        this.setContentView();

        //ViewModel has to be instantiated before initialization!
        this.viewModel = ViewModelProviders.of(this).get(BaseActivityViewModel.class);

        if(App.isInitialized)
        {
            this.viewModel.isInitializingApp = false;

            Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + String.format("BaseActivity.onCreate:: calling [%s].create()", this.getClass().getSimpleName()));
            this.create();
            this.viewModel.activityIsCreated = true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "BaseActivity.onCreate:: app is not initialized");

            if(!App.config.useExternalStorage())
            {
                this.startAppInitialization();
            }
            else
            {
                this.requestWriteToExternalStoragePermissionForDebugBuildAndStartAppInitialization();
            }
        }
    }

    protected abstract void setContentView();
    protected abstract void create();

    @Override
    protected final void onResume()
    {
        super.onResume();

        if(App.isInitialized)
        {
            if(!this.viewModel.activityIsCreated)
            {
                Log.w(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_RESUME + String.format("BaseActivity.onResume:: derived activity is not created - calling [%s].create()", this.getClass().getSimpleName()));
                this.create();
            }

            if(this.viewModel.optionsMenuAgent == null)
            {
                this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
            }


            HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

            if(helpOverlayFragment != null)
            {
                this.setHelpOverlayVisibility(this.viewModel.helpOverlayFragmentIsVisible);
            }

            Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_RESUME + String.format("BaseActivity.onResume:: calling [%s].resume()", this.getClass().getSimpleName()));
            this.resume();
        }
    }

    protected void resume() {}

    @Override
    protected final void onPause()
    {
        super.onPause();

        if(App.isInitialized)
        {
            this.synchronizePersistency();
            this.pause();
        }
    }

    protected void pause() {}


    private void requestWriteToExternalStoragePermissionForDebugBuildAndStartAppInitialization()
    {
        this.viewModel.writeToExternalStoragePermissionNeededToInitialize = true;

        if(this.requestPermissionWriteExternalStorage())
        {
            this.startAppInitialization();
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.requestWriteToExternalStoragePermissionForDebugBuildAndStartAppInitialization:: " +
                    "not able to create app without permission to write to external storage (change configuration <useExternalStorage> to 'false' or grant permission!)");
        }
    }

    private void startAppInitialization()
    {
        //Todo: introduce SplashScreen
        this.addToolbar();
        this.setToolbarTitleAndSubtitle(getString(R.string.name_app), null);

        if(this.viewModel.isInitializingApp)
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.startAppInitialization:: app is initializing...");
            this.showProgressBar(true);
        }
        else
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.startAppInitialization:: starting async app initialization...");

            this.viewModel.isInitializingApp = true;
            this.showProgressBar(true);
            new InitializeApp().execute(this);
        }
    }

    private static class InitializeApp extends AsyncTask<BaseActivity, Void, BaseActivity>
    {
        @Override
        protected BaseActivity doInBackground(BaseActivity... baseActivities)
        {
            boolean success = App.initialize();

            if(success)
            {
                return baseActivities[0];
            }

            String message = "App initialization failed";
            Log.e(Constants.LOG_TAG, String.format("BaseActivity.InitializeApp.doInBackground:: %s", message));
            throw new IllegalStateException(message);
        }

        @Override
        protected void onPostExecute(BaseActivity baseActivity)
        {
            super.onPostExecute(baseActivity);
            baseActivity.finishAppInitialization();
        }
    }

    private void finishAppInitialization()
    {
        this.showProgressBar(false);

        Intent intent = getIntent();
        Log.i(Constants.LOG_TAG, String.format("BaseActivity.finishAppInitialization:: restarting [%s] in new thread - existing thread is cleared", StringTool.parseActivityName(intent.getComponent().getShortClassName())));

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //this clears the stacktrace
        ActivityDistributor.startActivityViaIntent(this, intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(App.isInitialized)
        {
            this.viewModel.optionsMenuAgent.add(OptionsItem.HELP).create(menu);
            menu = this.createOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected Menu createOptionsMenu(Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("BaseActivity.createOptionsMenu:: [%s] does not override createOptionsMenu()", this.getClass().getSimpleName()));
        return menu;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(App.isInitialized)
        {
            menu = this.prepareOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected Menu prepareOptionsMenu(Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("BaseActivity.prepareOptionsMenu:: [%s] does not override prepareOptionsMenu()", this.getClass().getSimpleName()));
        return menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return this.viewModel.optionsMenuAgent.handleOptionsItemSelected(item, this);
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        if(item == OptionsItem.HELP)
        {
            HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

            if(helpOverlayFragment != null)
            {
                Log.i(Constants.LOG_TAG, "BaseActivity.handleOptionsItemSelected:: showing HelpOverlay");
                this.setHelpOverlayVisibility(true);
            }
            else
            {
                Log.e(Constants.LOG_TAG, "BaseActivity.handleOptionsItemSelected:: HelpOverlayFragment not added");
            }
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("BaseActivity.handleOptionsItemSelected:: OptionsMenuItem [%s] unhandled", item));
            return false;
        }
    }

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        Log.e(Constants.LOG_TAG, String.format("BaseActivity.handleOptionsItemSelected:: PopupMenuItem [%s] clicked - unhandled", item));
    }


    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        if(buttonFunction == ButtonFunction.CLOSE)
        {
            Log.i(Constants.LOG_TAG, String.format("BaseActivity.onHelpOverlayFragmentInteraction:: [%s] selected", buttonFunction));
            this.setHelpOverlayVisibility(false);
        }
    }

    protected void addHelpOverlayFragment(String title, CharSequence message)
    {
        HelpOverlayFragment helpOverlayFragment = this.fetchHelpOverlayFragment();

        if(helpOverlayFragment == null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.addHelpOverlayFragment:: creating HelpOverlayFragment with title [%s] and message", title));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            helpOverlayFragment = HelpOverlayFragment.newInstance(title, message);
            fragmentTransaction.add(this.findViewById(android.R.id.content).getId(), helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(helpOverlayFragment);
            fragmentTransaction.commit();
        }
        else
        {
            Log.v(Constants.LOG_TAG, "BaseActivity.addHelpOverlayFragment:: re-using HelpOverlayFragment");
        }
    }

    protected void setHelpOverlayTitleAndMessage(String title, String message)
    {
        HelpOverlayFragment helpOverlayFragment = this.fetchHelpOverlayFragment();

        if(helpOverlayFragment != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayTitleAndMessage:: setting HelpOverlay title [%s] and message", title));
            helpOverlayFragment.setTitleAndMessage(title, message);
        }
        else
        {
            this.addHelpOverlayFragment(title, message);
        }
    }

    protected void setHelpOverlayVisibility(boolean isVisible)
    {
        HelpOverlayFragment helpOverlayFragment = this.fetchHelpOverlayFragment();

        if(helpOverlayFragment != null)
        {
            Log.v(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayVisibility:: HelpOverlayFragment isVisible [%s]", isVisible));

            if(isVisible)
            {
                if(this.floatingActionButton != null)
                {
                    this.viewModel.wasFloatingActionButtonVisibleBeforeShowingHelpOverlay = this.floatingActionButton.getVisibility() == View.VISIBLE;
                    this.setFloatingActionButtonVisibility(false);
                }
                this.showFragmentFadeIn(helpOverlayFragment);
            }
            else
            {
                this.hideFragmentFadeOut(helpOverlayFragment);
                if(this.floatingActionButton != null && this.viewModel.wasFloatingActionButtonVisibleBeforeShowingHelpOverlay)
                {
                    this.setFloatingActionButtonVisibility(true);
                }
            }

            this.viewModel.helpOverlayFragmentIsVisible = isVisible;
        }
        else
        {
            this.addHelpOverlayFragment(this.viewModel.helpOverlayFragmentTitle, this.viewModel.helpOverlayFragmentMessage);
        }
    }

    private HelpOverlayFragment fetchHelpOverlayFragment()
    {
        return (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);
    }



    protected void addToolbar()
    {
        Toolbar toolbar = this.fetchToolbar();
        if(toolbar != null)
        {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbar:: setting toolbar...");
        }
    }

    protected void addToolbarHomeButton()
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarHomeButton:: adding home button to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            Toolbar toolbar = this.fetchToolbar();

            if(toolbar != null)
            {
                this.fetchToolbar().setNavigationOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onToolbarHomeButtonBackClicked();
                    }
                });
            }
        }
    }

    protected void addToolbarMenuIcon()
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarMenuIcon:: adding menu icon to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_menu, R.color.white));
        }
    }

    protected   void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.onToolbarHomeButtonBackClicked:: toolbar home button pressed...");
        this.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP));
    }

    protected void setToolbarTitleAndSubtitle(String title, String subtitle)
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setToolbarTitleAndSubtitle:: setting toolbar title [%s] and subtitle [%s]", title, subtitle));

            if(title != null && !title.trim().isEmpty())
            {
                getSupportActionBar().setTitle(title);
            }
            else
            {
                getSupportActionBar().setTitle("");
            }

            if(subtitle != null && !subtitle.trim().isEmpty())
            {
                getSupportActionBar().setSubtitle(subtitle);
            }
            else
            {
                getSupportActionBar().setSubtitle("");
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.setToolbarTitleAndSubtitle:: SupportActionBar not found");
        }
    }

    protected void setToolbarOnClickListener(View.OnClickListener onClickListener)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.setToolbarOnClickListener:: setting onClickListener");
        this.fetchToolbar().setOnClickListener(onClickListener);
    }

    public Toolbar fetchToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar != null)
        {
            return toolbar;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.fetchToolbar:: View<toolbar> not found");
            return null;
        }
    }

    protected void addFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createFloatingActionButton:: creating FloatingActionButton...");

        this.floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    protected void setFloatingActionButtonIcon(Drawable icon)
    {
        Log.v(Constants.LOG_TAG, "BaseActivity.setFloatingActionButtonIcon:: setting FloatingActionButton icon...");
        if(this.floatingActionButton != null)
        {
            icon.setTint(Color.WHITE);
            this.floatingActionButton.setImageDrawable(icon);
        }
    }

    protected void setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
        Log.v(Constants.LOG_TAG, "BaseActivity.setFloatingActionButtonOnClickListener:: setting FloatingActionButton onClickListener...");

        if(this.floatingActionButton != null)
        {
            this.floatingActionButton.setOnClickListener(onClickListener);
            this.floatingActionButtonOnClickListener = onClickListener;
        }
    }

    protected void setFloatingActionButtonVisibility(boolean isVisible)
    {
        if(this.floatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setFloatingActionButtonVisibility:: FloatingActionButton isVisible[%s]", isVisible));

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

    protected void animateFloatingActionButtonTransition(Drawable icon)
    {
        if(this.floatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.animateFloatingActionButtonTransition:: animating FloatingActionButton transition...");

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

            newFloatingActionButton.setOnClickListener(this.floatingActionButtonOnClickListener);
            newFloatingActionButton.setImageDrawable(icon != null ? icon : this.floatingActionButton.getDrawable());
            this.floatingActionButton = newFloatingActionButton;
            this.floatingActionButton.show();
        }
    }

    protected void showFragmentFadeIn(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(fragment)
                .commit();
    }

    protected void hideFragmentFadeOut(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(fragment)
                .commit();
    }

    protected void showFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .show(fragment)
                .commit();
    }

    private void hideFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .hide(fragment)
                .commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.onKeyDown<BACK>:: finishing activity");
            Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void showProgressBar(boolean show)
    {
        View progressBar = this.fetchProgressBar();

        if(progressBar == null)
        {
            ViewGroup viewGroup = (ViewGroup)this.fetchRootView();
            progressBar = getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
            viewGroup.addView(progressBar);
        }

        if(show)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }
    }

    private View fetchProgressBar()
    {
        return this.fetchRootView().findViewById(R.id.linearLayoutProgressBar);
    }

    private View fetchRootView()
    {
        View rootView = findViewById(android.R.id.content);
        if(rootView != null)
        {
            return rootView;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.fetchRootView:: View not found.");
            return null;
        }
    }

    protected boolean requestPermissionWriteExternalStorage()
    {
        if(ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.requestPermissionWriteExternalStorage:: permission to write to external storage denied - requesting permission");

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE.ordinal());
            return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == RequestCode.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE.ordinal())
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i(Constants.LOG_TAG, "BaseActivity.onRequestPermissionsResult:: permission to write to external storage granted by user");

                if(this.viewModel.writeToExternalStoragePermissionNeededToInitialize)
                {
                    this.viewModel.writeToExternalStoragePermissionNeededToInitialize = false;
                    this.startAppInitialization();
                }

            }
            else
            {
                if(this.viewModel.writeToExternalStoragePermissionNeededToInitialize)
                {
                    Log.e(Constants.LOG_TAG, "BaseActivity.onRequestPermissionsResult:: needed permission to write to external storage not granted by user - closing app");
                    finishAndRemoveTask();
                }
                else
                {
                    Log.i(Constants.LOG_TAG, "BaseActivity.onRequestPermissionsResult:: permission to write to external storage not granted by user");
                }
            }
        }
    }

    protected void markForCreation(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.markForCreation(element);
        }
    }

    protected void markForCreation(IElement element)
    {
        if(!(element instanceof ITemporaryElement))
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForCreation:: marking %s for creation", element));
            this.viewModel.elementsToCreate.add(element);
        }
        App.content.addElement(element);
    }

    protected void markForUpdate(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.markForUpdate(element);
        }
    }

    protected void markForUpdate(IElement element)
    {
        if(!(element instanceof ITemporaryElement))
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForUpdate:: marking %s for update", element));
            this.viewModel.elementsToUpdate.add(element);
        }
    }

    protected void markForDeletion(List<IElement> elements, boolean deleteDescendants)
    {
        for(IElement element : elements)
        {
            this.markForDeletion(element, deleteDescendants);
        }
    }

    protected void markForDeletion(IElement element, boolean deleteDescendants)
    {
        if(!(element instanceof ITemporaryElement))
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForDeletion:: marking %s for deletion", element));
            this.viewModel.elementsToDelete.add(element);
        }

        if(deleteDescendants && element.hasChildren())
        {
            for(IElement child : element.getChildren())
            {
                this.markForDeletion(child, true);
            }
        }
        App.content.removeElement(element);
    }

    protected void synchronizePersistency()
    {
        if(!(this.viewModel.elementsToCreate.isEmpty() && this.viewModel.elementsToUpdate.isEmpty() && this.viewModel.elementsToDelete.isEmpty()))
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.synchronizePersistency:: synchronizing persistence.");

            if(!App.persistence.synchronize(new HashSet<>(this.viewModel.elementsToCreate), new HashSet<>(this.viewModel.elementsToUpdate), new HashSet<>(this.viewModel.elementsToDelete)))
            {
                Log.e(Constants.LOG_TAG, "BaseActivity.synchronizePersistency:: synchronizing persistence failed");
                throw new IllegalStateException();
            }

            this.viewModel.elementsToCreate.clear();
            this.viewModel.elementsToUpdate.clear();
            this.viewModel.elementsToDelete.clear();
        }
        else
        {
            Log.v(Constants.LOG_TAG, "BaseActivity.synchronizePersistency:: persistence is synchronous");
        }
    }
}