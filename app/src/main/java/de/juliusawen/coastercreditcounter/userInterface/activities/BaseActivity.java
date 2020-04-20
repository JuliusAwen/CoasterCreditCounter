package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.menuAgents.IOptionsMenuAgentClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.IPopupMenuAgentClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.HelpOverlayFragment;

public abstract class BaseActivity extends AppCompatActivity  implements IOptionsMenuAgentClient, IPopupMenuAgentClient, HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private View.OnClickListener floatingActionButtonOnClickListener;

    private HelpOverlayFragment helpOverlayFragment;

    private BaseActivityViewModel viewModel;

    @Override
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(Constants.LOG_TAG, "BaseActivity.onCreate:: creating activity...");

        this.setContentView();

        this.viewModel = new ViewModelProvider(this).get(BaseActivityViewModel.class);

        if(App.isInitialized)
        {
            this.viewModel.isInitializingApp = false;

            this.toolbar = findViewById(R.id.toolbar);

            Log.i(Constants.LOG_TAG, String.format(Constants.LOG_DIVIDER_ON_CREATE + "BaseActivity.onCreate:: calling [%s].create()", this.getClass().getSimpleName()));
            this.create();
            this.viewModel.activityIsCreated = true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, "BaseActivity.onCreate:: app is not initialized");
            this.startAppInitialization();
        }
    }

    protected abstract void setContentView();
    protected abstract void create();

    @Override
    protected final void onResume()
    {
        super.onResume();

        if(App.isInitialized && !this.viewModel.isInitializingApp)
        {
            if(!this.viewModel.activityIsCreated)
            {
                Log.w(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_RESUME +
                        String.format("BaseActivity.onResume:: derived activity is not created - calling [%s].create()", this.getClass().getSimpleName()));
                this.create();
            }

            if(this.viewModel.optionsMenuAgent == null)
            {
                this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
            }


            if(this.viewModel.isHelpOverlayAdded)
            {
                this.helpOverlayFragment = (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

                if(this.helpOverlayFragment != null)
                {
                    this.setHelpOverlayVisibility(this.viewModel.helpOverlayFragmentIsVisible);
                }
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

        if(App.isInitialized && !this.viewModel.isInitializingApp)
        {
            this.synchronizePersistency();
            this.pause();
        }
    }

    protected void pause()
    {
        //do stuff onPause
    }

    private void startAppInitialization()
    {
        //Todo: introduce SplashScreen
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
            baseActivities[0].viewModel.isAppProperlyInitialized = App.initialize();
            return baseActivities[0];
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

        if(this.viewModel.isAppProperlyInitialized)
        {
            Intent intent = getIntent();
            Log.i(Constants.LOG_TAG, String.format("BaseActivity.finishAppInitialization:: restarting [%s] in new thread - existing thread is cleared", StringTool.parseActivityName(intent.getComponent().getShortClassName())));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //this clears the stacktrace
            ActivityDistributor.startActivityViaIntent(this, intent);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.InitializeApp.doInBackground:: App initialization failed - closing App");
            finishAndRemoveTask();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(App.isInitialized && !this.viewModel.isInitializingApp)
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
        if(App.isInitialized && !this.viewModel.isInitializingApp)
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
            this.showHelpOverlayFragment();
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

    protected void createHelpOverlayFragment(String title, CharSequence message)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.addHelpOverlayFragment:: preparing HelpOverlayFragment...");

        this.viewModel.isHelpOverlayAdded = true;
        this.viewModel.helpOverlayFragmentTitle = title;
        this.viewModel.helpOverlayFragmentMessage = message;
    }

    protected BaseActivity setHelpOverlayTitleAndMessage(String title, String message)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.setHelpOverlayTitleAndMessage:: setting HelpOverlay title and message...");
        Log.v(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayTitleAndMessage:: setting title [%s] and message [%s]", title, message));

        title = title.trim();
        message = message.trim();

        if(this.helpOverlayFragment != null)
        {
            this.helpOverlayFragment.setTitleAndMessage(title, message);
        }

        this.viewModel.helpOverlayFragmentTitle = title;
        this.viewModel.helpOverlayFragmentMessage = message;

        return this;
    }

    private void showHelpOverlayFragment()
    {
        if(this.helpOverlayFragment == null)
        {
            this.instantiateHelpOverlayFragment();
        }

        Log.i(Constants.LOG_TAG, "BaseActivity.showHelpOverlayFragment:: showing HelpOverlayFragment");
        Log.v(Constants.LOG_TAG, String.format("BaseActivity.showHelpOverlayFragment:: ...with title [%s] and message [%s]...",
                this.viewModel.helpOverlayFragmentTitle, this.viewModel.helpOverlayFragmentMessage));

        this.setHelpOverlayVisibility(true);
    }

    private void instantiateHelpOverlayFragment()
    {
        if(this.helpOverlayFragment == null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.createHelpOverlayFragment:: creating HelpOverlayFragment...");

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(this.viewModel.helpOverlayFragmentTitle, this.viewModel.helpOverlayFragmentMessage);
            fragmentTransaction.add(this.findViewById(android.R.id.content).getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(this.helpOverlayFragment);
            fragmentTransaction.commit();
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.createHelpOverlayFragment:: re-using HelpOverlayFragment");
        }
    }

    private void setHelpOverlayVisibility(boolean isVisible)
    {
        if(this.helpOverlayFragment != null)
        {
            Log.v(Constants.LOG_TAG, String.format("BaseActivity.setHelpOverlayVisibility:: setting HelpOverlayFragment to isVisible[%S]...", isVisible));

            if(isVisible)
            {
                if(this.floatingActionButton != null)
                {
                    this.viewModel.wasFloatingActionButtonVisibleBeforeShowingHelpOverlay = this.floatingActionButton.getVisibility() == View.VISIBLE;
                    this.setFloatingActionButtonVisibility(false);
                }
                this.showFragmentFadeIn(this.helpOverlayFragment);
            }
            else
            {
                this.hideFragmentFadeOut(this.helpOverlayFragment);
                if(this.floatingActionButton != null && this.viewModel.wasFloatingActionButtonVisibleBeforeShowingHelpOverlay)
                {
                    this.setFloatingActionButtonVisibility(true);
                }
            }

            this.viewModel.helpOverlayFragmentIsVisible = isVisible;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.setHelpOverlayVisibility:: HelpOverlayFragment not created");
        }
    }

    protected BaseActivity createToolbar()
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.createToolbar:: setting SupportActionBar...");
        setSupportActionBar(this.toolbar);

        return this;
    }

    protected BaseActivity addToolbarHomeButton()
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarHomeButton:: adding home button to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            this.toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.i(Constants.LOG_TAG, "BaseActivity.onToolbarHomeButtonBackClicked:: toolbar home button clicked...");
                    onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP));
                }
            });
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.addToolbarHomeButton:: SupportActionBar not found");
        }

        return this;
    }

    protected BaseActivity addToolbarMenuIcon()
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, "BaseActivity.addToolbarMenuIcon:: adding menu icon to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_menu, R.color.white));
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.addToolbarMenuIcon:: SupportActionBar not found");
        }

        return this;
    }

    protected BaseActivity setToolbarTitleAndSubtitle(String title, String subtitle)
    {
        if(getSupportActionBar() != null)
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.setToolbarTitleAndSubtitle:: setting toolbar title [%s] and subtitle [%s]", title, subtitle));

            if(title != null)
            {
                getSupportActionBar().setTitle(title.trim());
            }

            if(subtitle != null)
            {
                getSupportActionBar().setSubtitle(subtitle.trim());
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, "BaseActivity.setToolbarTitleAndSubtitle:: SupportActionBar not found");
        }

        return this;
    }

    protected BaseActivity setToolbarOnClickListener(View.OnClickListener onClickListener)
    {
        Log.d(Constants.LOG_TAG, "BaseActivity.setToolbarOnClickListener:: setting onClickListener");
        this.toolbar.setOnClickListener(onClickListener);

        return this;
    }

    protected void createFloatingActionButton()
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
        View rootView = findViewById(android.R.id.content);
        View progressBar = rootView.findViewById(R.id.linearLayoutProgressBar);

        if(progressBar == null)
        {
            progressBar = getLayoutInflater().inflate(R.layout.progress_bar, (ViewGroup)rootView, false);
            ((ViewGroup)rootView).addView(progressBar);
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

    protected void markForCreation(IElement element)
    {
        if(element.isIPersistable())
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForCreation:: marking %s for creation", element));
            this.viewModel.elementsToCreate.add(element);
            App.content.addElement(element);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("BaseActivity.markForCreation:: [%s] is not persistable", element));
        }
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
        if(element.isIPersistable())
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForUpdate:: marking %s for update", element));
            this.viewModel.elementsToUpdate.add(element);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("BaseActivity.markForUpdate:: [%s] is not persistable", element));
        }
    }

    protected void markForDeletion(IElement element)
    {
        this.markForDeletion(element, false);
    }

    protected void markForDeletion(IElement element, boolean deleteDescendants)
    {
        if(element.isIPersistable())
        {
            Log.d(Constants.LOG_TAG, String.format("BaseActivity.markForDeletion:: marking %s for deletion", element));
            this.viewModel.elementsToDelete.add(element);

            if(deleteDescendants && element.hasChildren())
            {
                for(IElement child : element.getChildren())
                {
                    if(child.isIPersistable())
                    {
                        this.markForDeletion(child, true);
                    }
                }
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("BaseActivity.markForDeletion:: [%s] is not persistable", element));
        }

        App.content.removeElement(element);
    }

    protected void synchronizePersistency()
    {
        if(!(this.viewModel.elementsToCreate.isEmpty() && this.viewModel.elementsToUpdate.isEmpty() && this.viewModel.elementsToDelete.isEmpty()))
        {
            Log.i(Constants.LOG_TAG, "BaseActivity.synchronizePersistency:: synchronizing persistence.");

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