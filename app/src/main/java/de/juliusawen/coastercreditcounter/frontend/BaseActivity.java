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
import android.view.Menu;
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
import de.juliusawen.coastercreditcounter.frontend.menuAgent.IMenuAgentClient;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.MenuAgent;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.MenuType;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.DrawableProvider;
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

        //VieModel has to instantiated before initialization
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
            if(this.viewModel.optionsMenuAgent == null)
            {
                this.viewModel.optionsMenuAgent = new MenuAgent(MenuType.OPTIONS_MENU);
            }


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

        if(App.isInitialized)
        {
            this.synchronizePersistency();
        }
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

    private void finishAppInitialization()
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


    //region --- OPTIONS MENU

    protected void preventHelpBeingAddedToOptionsMenu(boolean prevent)
    {
        this.viewModel.preventHelpBeingAddedToOptionsMenu = prevent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(App.isInitialized && !this.viewModel.preventHelpBeingAddedToOptionsMenu)
        {
            this.viewModel.optionsMenuAgent.addMenuItem(MenuAgent.HELP).create(menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(this.viewModel.optionsMenuAgent.handleMenuItemSelected(item, this))
        {
            return true;
        }

        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void handleMenuItemHelpSelected()
    {
        HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

        if(helpOverlayFragment != null)
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.handleMenuItemHelpSelected:: showing HelpOverlay");
            this.setHelpOverlayVisibility(true);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.handleMenuItemHelpSelected:: HelpOverlayFragment not added");
        }
    }

    @Override
    public void handleMenuItemExpandAllSelected()
    {
    }
    @Override
    public void handleMenuItemCollapseAllSelected()
    {
    }
    @Override
    public void handleMenuItemGroupByLocationSelected()
    {
    }
    @Override
    public void handleMenuItemGroupByAttractionCategorySelected()
    {
    }
    @Override
    public void handleMenuItemGroupByManufacturerSelected()
    {
    }
    @Override
    public void handleMenuItemGroupByStatusSelected()
    {
    }
    @Override
    public void handleMenuItemSortAscendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortDescendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortAttractionCategoriesSelected()
    {
    }
    @Override
    public void handleMenuItemSortManufacturersSelected()
    {
    }
    @Override
    public void handleMenuItemSortStatusesSelected()
    {
    }
    @Override
    public void handleMenuItemSortByYearAscendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByYearDescendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByNameAscendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByNameDescendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByLocationAscendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByLocationDescendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByAttractionCategoryAscendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByAttractionCategoryDescendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByManufacturerAscendingSelected()
    {
    }
    @Override
    public void handleMenuItemSortByManufacturerDescendingSelected()
    {
    }
    @Override
    public void handleMenuItemGoToCurrentVisitSelected()
    {
    }
    @Override
    public void handleMenuItemEnableEditingSelected()
    {
    }
    @Override
    public void handleMenuItemDisableEditingSelected()
    {
    }

    //endregion --- OPTIONS MENU

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
            Log.v(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayVisibility:: HelpOverlayFragment isVisible[%s]", isVisible));

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

    protected void addFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createFloatingActionButton:: creating FloatingActionButton...");

        this.floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    protected void setFloatingActionButtonIcon(Drawable icon)
    {
        if(this.floatingActionButton != null)
        {
            icon.setTint(Color.WHITE);
            this.floatingActionButton.setImageDrawable(icon);
        }
    }

    protected void setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
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
            Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void showProgressBar()
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

    protected void hideProgressBar()
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