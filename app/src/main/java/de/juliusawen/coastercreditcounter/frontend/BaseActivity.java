package de.juliusawen.coastercreditcounter.frontend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.ITemporaryElement;
import de.juliusawen.coastercreditcounter.frontend.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.frontend.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
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
        Log.d(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "BaseActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);

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
            Log.w(Constants.LOG_TAG, "BaseActivity.onCreate:: app is not initialized");

            if(App.DEBUG && App.config.useExternalStorage())
            {
                this.requestWriteToExternalStoragePermissionForDebugBuild();
            }
            else
            {
                this.startAppInitialization();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.synchronizePersistency();
    }

    private void requestWriteToExternalStoragePermissionForDebugBuild()
    {
        Log.e(Constants.LOG_TAG, "BaseActivity.requestWriteToExternalStoragePermissionForDebugBuild::" +
                " permission to write to external storage needed for initialization with this configuration");

        this.viewModel.writeToExternalStoragePermissionNeededToInitialize = true;

        if(this.requestPermissionWriteExternalStorage())
        {
            this.startAppInitialization();
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.requestWriteToExternalStoragePermissionForDebugBuild:: not able to initialize app without permission to write to external storage" +
                    " (change configuration <useExternalStorage> to 'false' or grant permission!)");
        }
    }

    private void startAppInitialization()
    {
        //Todo: introduce SplashScreen
        this.addToolbar();
        this.setToolbarTitleAndSubtitle(getString(R.string.title_app_name), null);

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
            ActivityTool.startActivity(this, TestActivity.class);
        }
        else
        {
            Intent intent = getIntent();

            Log.i(Constants.LOG_TAG, String.format("BaseActivity.finishAppInitialization:: restarting [%s]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityTool.startActivity(this, intent);
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
                Log.d(Constants.LOG_TAG, String.format("BaseActivity.onOptionsItemSelected:: [%s] selected", selection));
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
                Log.i(Constants.LOG_TAG, String.format("BaseActivity.onHelpOverlayFragmentInteraction:: [%s] selected", buttonFunction));
                this.setHelpOverlayVisibility(false);
                break;
        }
    }

    protected void addToolbar()
    {
        if(App.isInitialized)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbar:: setting toolbar...");
            this.toolbar = findViewById(R.id.toolbar);
            this.toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            this.actionBar = getSupportActionBar();
        }
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
            this.actionBar.setHomeAsUpIndicator(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_menu, R.color.white));
        }
    }

    protected void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "BaseActivity.onToolbarHomeButtonBackClicked:: toolbar home button pressed...");
        this.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP));
    }

    protected void setToolbarTitleAndSubtitle(String title, String subtitle)
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
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Log.d(Constants.LOG_TAG, "BaseActivity.onKeyDown<BACK>:: finishing activity");
                Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
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
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setFloatingActionButtonVisibility:: floating action button isVisible[%s]", isVisible));

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
        if(this.floatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.disableFloatingActionButton:: disabling floating action button...");

            this.floatingActionButton.hide();
            this.floatingActionButton = null;
        }
    }

    protected void animateFloatingActionButtonTransition(Drawable icon)
    {
        if(this.floatingActionButton != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.animateFloatingActionButtonTransition:: animating floating action button transition...");

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

    public void showProgressBar()
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

    public void hideProgressBar()
    {
        this.progressBar.setVisibility(View.GONE);
    }

    protected boolean requestPermissionWriteExternalStorage()
    {
        if(ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.requestPermissionWriteExternalStorage:: permission to write to external storage denied - requesting permission");

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == Constants.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
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
        Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForCreation:: marking %s for creation", element));
        this.viewModel.elementsToCreate.add(element);
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
        Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForUpdate:: marking %s for update", element));
        this.viewModel.elementsToUpdate.add(element);
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
        Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForDeletion:: marking %s for deletion", element));
        if(deleteDescendants && element.hasChildren())
        {
            for(IElement child : element.getChildren())
            {
                if(!(child instanceof ITemporaryElement))
                {
                    this.markForDeletion(child, true);
                }
            }
        }
        this.viewModel.elementsToDelete.add(element);
    }



    protected void synchronizePersistency()
    {
        if(!(this.viewModel.elementsToCreate.isEmpty() && this.viewModel.elementsToUpdate.isEmpty() && this.viewModel.elementsToDelete.isEmpty()))
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.synchronizePersistency:: synchronizing persistence...");

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