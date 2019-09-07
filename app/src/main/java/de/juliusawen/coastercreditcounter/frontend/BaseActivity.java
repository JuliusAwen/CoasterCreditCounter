package de.juliusawen.coastercreditcounter.frontend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.ITemporaryElement;
import de.juliusawen.coastercreditcounter.frontend.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.DrawableProvider;
import de.juliusawen.coastercreditcounter.toolbox.IMenuAgentClient;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public abstract class BaseActivity extends AppCompatActivity  implements IMenuAgentClient, HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private FloatingActionButton floatingActionButton;
    private View.OnClickListener floatingActionButtonOnClickListener;

    private BaseActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "BaseActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(BaseActivityViewModel.class);

        if(App.isInitialized)
        {
            this.viewModel.isInitializingApp = false;
        }
        else
        {
            Log.w(Constants.LOG_TAG, "BaseActivity.onCreate:: app is not initialized");

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

    @Override
    protected void onResume()
    {
        super.onResume();

        if(App.isInitialized)
        {
            HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

            if(helpOverlayFragment != null)
            {
                this.setHelpOverlayVisibility(this.viewModel.helpOverlayFragmentIsVisible);
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.synchronizePersistency();
    }


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
                    "not able to initialize app without permission to write to external storage (change configuration <useExternalStorage> to 'false' or grant permission!)");
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
            this.showProgressBar();
        }
        else
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.startAppInitialization:: starting async app initialization...");

            this.viewModel.isInitializingApp = true;
            this.showProgressBar();
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

    public void finishAppInitialization()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.finishAppInitialization:: finishing app initialization...");

        this.hideProgressBar();

        if(App.config.jumpToTestActivityOnStart())
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.finishAppInitialization:: starting TestActivity");
            ActivityDistributor.startActivityViaClass(this, TestActivity.class);
        }
        else
        {
            Intent intent = getIntent();

            Log.i(Constants.LOG_TAG, String.format("BaseActivity.finishAppInitialization:: restarting [%s]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); //this clears the stacktrace
            ActivityDistributor.startActivityViaIntent(this, intent);
        }
    }


    // region handleOptionsMenuItemSelected implementations

    @Override
    public boolean handleInvalidOptionsMenuItemSelected(MenuItem item)
    {
        Log.e(Constants.LOG_TAG, String.format("BaseActivity.onOptionsItemSelected:: [%d] selected - not handled!", item.getItemId()));

        return true;
    }

    public boolean handleMenuItemHelpSelected()
    {
        HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

        if(helpOverlayFragment != null)
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.handleMenuItemHelpSelected:: showing HelpOverlay");
            this.setHelpOverlayVisibility(true);
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.handleMenuItemHelpSelected:: HelpOverlayFragment not added");
            return false;
        }
    }

    @Override
    public boolean handleMenuItemExpandAllSelected() { return false; }
    @Override
    public boolean handleMenuItemCollapseAllSelected() { return false; }
    @Override
    public boolean handleMenuItemGroupByLocationSelected() { return false; }
    @Override
    public boolean handleMenuItemGroupByAttractionCategorySelected() { return false; }
    @Override
    public boolean handleMenuItemGroupByManufacturerSelected() { return false; }
    @Override
    public boolean handleMenuItemGroupByStatusSelected() { return false; }
    @Override
    public boolean handleMenuItemSortAscendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortDescendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByYearAscendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByYearDescendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByNameAscendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByNameDescendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByLocationAscendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByLocationDescendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByAttractionCategoryAscendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByAttractionCategoryDescendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByManufacturerAscendingSelected() { return false; }
    @Override
    public boolean handleMenuItemSortByManufacturerDescendingSelected() { return false; }
    @Override
    public boolean handleMenuItemGoToCurrentVisitSelected() { return false; }
    @Override
    public boolean handleMenuItemEnableEditingSelected() { return false; }
    @Override
    public boolean handleMenuItemDisableEditingSelected() { return false; }

    // endregion handleOptionsMenuItemSelected implementations

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

    public void addHelpOverlayFragment(String title, CharSequence message)
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

    public void setHelpOverlayTitleAndMessage(String title, String message)
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

    public void setHelpOverlayVisibility(boolean isVisible)
    {
        HelpOverlayFragment helpOverlayFragment = this.fetchHelpOverlayFragment();

        if(helpOverlayFragment != null)
        {
            Log.v(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayVisibility:: HelpOverlayFragment isVisible[%s]", isVisible));

            if(isVisible)
            {
                this.showFragmentFadeIn(helpOverlayFragment);
            }
            else
            {
                this.hideFragmentFadeOut(helpOverlayFragment);
            }

            this.viewModel.helpOverlayFragmentIsVisible = isVisible;
            this.setFloatingActionButtonVisibility(!isVisible);
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



    public void addToolbar()
    {
        Toolbar toolbar = this.fetchToolbar();
        if(toolbar != null)
        {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbar:: setting toolbar...");
        }
    }

    public void addToolbarHomeButton()
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

    public void addToolbarMenuIcon()
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarMenuIcon:: adding menu icon to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_menu, R.color.white));
        }
    }

    private  void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.onToolbarHomeButtonBackClicked:: toolbar home button pressed...");
        this.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP));
    }

    public void setToolbarTitleAndSubtitle(String title, String subtitle)
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setToolbarTitleAndSubtitle:: setting toolbar title[%s] and subtitle[%s]", title, subtitle));

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
            Log.e(Constants.LOG_TAG, "BaseActivity.setToolbarTitleAndSubtitle:: SupportActionBar not found.");
        }
    }

    private Toolbar fetchToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar != null)
        {
            return toolbar;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.fetchToolbar:: View<toolbar> not found.");
            return null;
        }
    }

    public void addFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createFloatingActionButton:: creating FloatingActionButton...");

        this.floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    public void setFloatingActionButtonIcon(Drawable icon)
    {
        if(this.floatingActionButton != null)
        {
            icon.setTint(Color.WHITE);
            this.floatingActionButton.setImageDrawable(icon);
        }
    }

    public void setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
        if(this.floatingActionButton != null)
        {
            this.floatingActionButton.setOnClickListener(onClickListener);
            this.floatingActionButtonOnClickListener = onClickListener;
        }
    }

    public void setFloatingActionButtonVisibility(boolean isVisible)
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

    public void disableFloatingActionButton()
    {
        if(this.floatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.disableFloatingActionButton:: disabling FloatingActionButton...");

            this.floatingActionButton.hide();
            this.floatingActionButton = null;
        }
    }

    public void animateFloatingActionButtonTransition(Drawable icon)
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

    private void showFragmentFadeIn(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(fragment)
                .commit();
    }

    private void hideFragmentFadeOut(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(fragment)
                .commit();
    }

    private void showFragment(Fragment fragment)
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
            Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showProgressBar()
    {
        View progressBar = this.fetchProgressBar();

        if(progressBar == null)
        {
            ViewGroup viewGroup = (ViewGroup)this.fetchRootView();
            progressBar = getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
            viewGroup.addView(progressBar);
        }

        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar()
    {
        View progressBar = this.fetchProgressBar();

        if(progressBar != null)
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

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == Constants.REQUEST_CODE_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
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

    public void markForCreation(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.markForCreation(element);
        }
    }

    public void markForCreation(IElement element)
    {
        if(!(element instanceof ITemporaryElement))
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForCreation:: marking %s for creation", element));
            this.viewModel.elementsToCreate.add(element);
        }
        App.content.addElement(element);
    }

    public void markForUpdate(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.markForUpdate(element);
        }
    }

    public void markForUpdate(IElement element)
    {
        if(!(element instanceof ITemporaryElement))
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForUpdate:: marking %s for update", element));
            this.viewModel.elementsToUpdate.add(element);
        }
    }

    public void markForDeletion(List<IElement> elements, boolean deleteDescendants)
    {
        for(IElement element : elements)
        {
            this.markForDeletion(element, deleteDescendants);
        }
    }

    public void markForDeletion(IElement element, boolean deleteDescendants)
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

    public void synchronizePersistency()
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