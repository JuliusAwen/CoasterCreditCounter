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
import androidx.appcompat.app.ActionBar;
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
    private Toolbar toolbar;
    private ActionBar actionBar;
    private HelpOverlayFragment helpOverlayFragment;
    private FloatingActionButton floatingActionButton;
    private View.OnClickListener floatingActionButtonOnClickListener;
    private View progressBar;

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
            if(this.helpOverlayFragment != null)
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

    @Override
    protected void onStop()
    {
        super.onStop();
        this.toolbar = null;
        this.helpOverlayFragment = null;
        this.floatingActionButton = null;
        this.floatingActionButtonOnClickListener = null;
        this.progressBar = null;
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.e(Constants.LOG_TAG, String.format("BaseActivity.onOptionsItemSelected:: [%d] selected - not handled!", item.getItemId()));
        return super.onOptionsItemSelected(item);
    }

    // region handleOptionsMenuItemSelected implementations

    @Override
    public boolean handleInvalidOptionsMenuItemSelected(MenuItem item)
    {
        return this.onOptionsItemSelected(item);
    }

    public boolean handleOptionsMenuItemHelpSelected()
    {
        if(this.helpOverlayFragment != null)
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.handleOptionsMenuItemHelpSelected:: showing HelpOverlay");
            this.setHelpOverlayVisibility(true);
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.handleOptionsMenuItemHelpSelected:: HelpOverlayFragment not added");
            return false;
        }
    }

    @Override
    public boolean handleOptionsMenuItemExpandAllSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemCollapseAllSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemGroupByLocationSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemGroupByAttractionCategorySelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemGroupByManufacturerSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemGroupByStatusSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByYearAscendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByYearDescendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByNameAscendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByNameDescendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByLocationAscendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByLocationDescendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByAttractionCategoryAscendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByAttractionCategoryDescendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByManufacturerAscendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemSortByManufacturerDescendingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemGoToCurrentVisitSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemEnableEditingSelected() { return false; }
    @Override
    public boolean handleOptionsMenuItemDisableEditingSelected() { return false; }

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

    public BaseActivity addToolbar()
    {
        if(App.isInitialized)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbar:: setting toolbar...");
            this.toolbar = findViewById(R.id.toolbar);
            this.toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            this.actionBar = getSupportActionBar();
        }

        return this;
    }

    public BaseActivity addToolbarHomeButton()
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

        return this;
    }

    public BaseActivity addToolbarMenuIcon()
    {
        if(this.actionBar != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarMenuIcon:: adding menu icon to toolbar...");

            this.actionBar.setDisplayHomeAsUpEnabled(true);
            this.actionBar.setHomeAsUpIndicator(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_menu, R.color.white));
        }

        return this;
    }

    private  void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.onToolbarHomeButtonBackClicked:: toolbar home button pressed...");
        this.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP));
    }

    public BaseActivity setToolbarTitleAndSubtitle(String title, String subtitle)
    {
        if(this.actionBar != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setToolbarTitleAndSubtitle:: setting toolbar title[%s] and subtitle[%s]", title, subtitle));

            if(title != null && !title.trim().isEmpty())
            {
                this.actionBar.setTitle(title);
            }
            else
            {
                this.actionBar.setTitle("");
            }

            if(subtitle != null && !subtitle.trim().isEmpty())
            {
                this.actionBar.setSubtitle(subtitle);
            }
            else
            {
                this.actionBar.setSubtitle("");
            }
        }

        return this;
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

    public BaseActivity addFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createFloatingActionButton:: creating FloatingActionButton...");

        this.floatingActionButton = findViewById(R.id.floatingActionButton);

        return this;
    }

    public BaseActivity setFloatingActionButtonIcon(Drawable icon)
    {
        if(this.floatingActionButton != null)
        {
            icon.setTint(Color.WHITE);
            this.floatingActionButton.setImageDrawable(icon);
        }

        return this;
    }

    public BaseActivity setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
        if(this.floatingActionButton != null)
        {
            this.floatingActionButton.setOnClickListener(onClickListener);
            this.floatingActionButtonOnClickListener = onClickListener;
        }

        return this;
    }

    public BaseActivity setFloatingActionButtonVisibility(boolean isVisible)
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

        return this;
    }

    public BaseActivity disableFloatingActionButton()
    {
        if(this.floatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.disableFloatingActionButton:: disabling FloatingActionButton...");

            this.floatingActionButton.hide();
            this.floatingActionButton = null;
        }

        return this;
    }

    public BaseActivity animateFloatingActionButtonTransition(Drawable icon)
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

        return this;
    }

    public BaseActivity addHelpOverlayFragment(String title, CharSequence message)
    {
        this.helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

        if(this.helpOverlayFragment == null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addHelpOverlayFragment:: creating HelpOverlayFragment...");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(title, message);
            fragmentTransaction.add(this.findViewById(android.R.id.content).getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(this.helpOverlayFragment);
            fragmentTransaction.commit();
        }
        else
        {
            Log.v(Constants.LOG_TAG, "BaseActivity.addHelpOverlayFragment:: re-using HelpOverlayFragment");
        }

        return this;
    }

    public BaseActivity setHelpOverlayTitleAndMessage(String title, CharSequence message)
    {
        if(this.helpOverlayFragment != null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(title, message);
            fragmentTransaction.replace(this.findViewById(android.R.id.content).getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(this.helpOverlayFragment);
            fragmentTransaction.commit();
        }

        return this;
    }

    public BaseActivity setHelpOverlayVisibility(boolean isVisible)
    {
        if(this.helpOverlayFragment != null)
        {
            Log.v(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayVisibility:: HelpOverlayFragment isVisible[%s]", isVisible));

            if(isVisible)
            {
                this.showFragmentFadeIn(this.helpOverlayFragment);
            }
            else
            {
                this.hideFragmentFadeOut(this.helpOverlayFragment);
            }

            this.viewModel.helpOverlayFragmentIsVisible = isVisible;
            this.setFloatingActionButtonVisibility(!isVisible);
        }

        return this;
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

    public BaseActivity showProgressBar()
    {
        if(this.progressBar == null)
        {
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View progressBar = getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
            viewGroup.addView(progressBar);

            this.progressBar = progressBar;
        }

        this.progressBar.setVisibility(View.VISIBLE);

        return this;
    }

    public BaseActivity hideProgressBar()
    {
        this.progressBar.setVisibility(View.GONE);
        this.progressBar = null;

        return this;
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

    public BaseActivity markForCreation(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.markForCreation(element);
        }
        return this;
    }

    public BaseActivity markForCreation(IElement element)
    {
        if(!(element instanceof ITemporaryElement))
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForCreation:: marking %s for creation", element));
            this.viewModel.elementsToCreate.add(element);
        }
        App.content.addElement(element);

        return this;
    }

    public BaseActivity markForUpdate(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.markForUpdate(element);
        }

        return this;
    }

    public BaseActivity markForUpdate(IElement element)
    {
        if(!(element instanceof ITemporaryElement))
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForUpdate:: marking %s for update", element));
            this.viewModel.elementsToUpdate.add(element);
        }

        return this;
    }

    public BaseActivity markForDeletion(List<IElement> elements, boolean deleteDescendants)
    {
        for(IElement element : elements)
        {
            this.markForDeletion(element, deleteDescendants);
        }

        return this;
    }

    public BaseActivity markForDeletion(IElement element, boolean deleteDescendants)
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

        return this;
    }

    public BaseActivity synchronizePersistency()
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

        return this;
    }
}