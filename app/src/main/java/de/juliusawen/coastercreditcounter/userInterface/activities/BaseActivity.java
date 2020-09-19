package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.dataModel.statistics.IStatistic;
import de.juliusawen.coastercreditcounter.dataModel.statistics.StatisticType;
import de.juliusawen.coastercreditcounter.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.tools.menuTools.IPopupMenuAgentClient;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButler;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;
import de.juliusawen.coastercreditcounter.userInterface.activities.developerOptions.DeveloperOptionsActivity;
import de.juliusawen.coastercreditcounter.userInterface.baseViewModel.IBaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.HelpOverlayFragment;

public abstract class BaseActivity extends AppCompatActivity  implements IPopupMenuAgentClient, HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private View.OnClickListener floatingActionButtonOnClickListener;

    private HelpOverlayFragment helpOverlayFragment;

    private BaseActivityViewModel viewModel;

    // region ActivityLifecycle

    @Override
    protected final void onCreate(Bundle savedInstanceState)
    {
        Log.frame(LogLevel.VERBOSE, "creating...", '#', true);

        this.setContentView();

        this.viewModel = new ViewModelProvider(this).get(BaseActivityViewModel.class);

        if(App.isInitialized)
        {
            this.viewModel.isInitializingApp = false;
            this.toolbar = super.findViewById(R.id.toolbar);

            if(this.viewModel.optionsMenuButler == null)
            {
                this.viewModel.optionsMenuButler = new OptionsMenuButler(this);
            }

            Log.frame(LogLevel.INFO, String.format("creating [%s]", this.getClass().getSimpleName()), '#', false);
            this.create();
            this.viewModel.activityIsCreated = true;
        }
        else
        {
            Log.w("app is not initialized");
            this.startAppInitialization();
        }

        super.onCreate(savedInstanceState);
    }

    protected abstract void setContentView();
    protected abstract void create();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.frame(LogLevel.INFO, String.format("processing result [%s]", this.getClass().getSimpleName()), '*', false);
    }

    @Override
    protected final void onResume()
    {
        super.onResume();

        if(App.isInitialized && !this.viewModel.isInitializingApp)
        {
            if(!this.viewModel.activityIsCreated)
            {
                Log.e(String.format("derived activity is not created - calling [%s].create()", this.getClass().getSimpleName()));
                this.create();
            }

            if(this.viewModel.isHelpOverlayAdded)
            {
                this.helpOverlayFragment = (HelpOverlayFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);

                if(this.helpOverlayFragment != null)
                {
                    this.setHelpOverlayVisibility(this.viewModel.helpOverlayFragmentIsVisible);
                }
            }

            Log.frame(LogLevel.INFO, String.format("resuming [%s]", this.getClass().getSimpleName()), '*', false);
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

    protected void pause() {}

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.i(String.format("<%s> pressed", StringTool.keyCodeToString(keyCode)));

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '-', false);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    // endregion ActivityLifecycle

    // region AppInitialization

    private void startAppInitialization()
    {
        //Todo: introduce SplashScreen
        if(this.viewModel.isInitializingApp)
        {
            Log.i("app is initializing...");
            this.showProgressBar(true);
        }
        else
        {
            Log.i("starting async app initialization...");

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
            Log.i(String.format("restarting [%s] in new thread - existing thread is cleared", StringTool.parseActivityName(intent.getComponent().getShortClassName())));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //this clears the stacktrace
            ActivityDistributor.startActivityViaIntent(this, intent);
        }
        else
        {
            Log.e("App initialization failed - closing App");
            finishAndRemoveTask();
        }
    }

    protected void showProgressBar(boolean show)
    {
        View rootView = findViewById(android.R.id.content);
        View progressBar = rootView.findViewById(R.id.linearLayoutProgressBar);

        if(progressBar == null)
        {
            progressBar = getLayoutInflater().inflate(R.layout.layout_progress_bar, (ViewGroup)rootView, false);
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

    // endregion AppInitialization

    //region OptionsMenu

    protected void setOptionsMenuButlerViewModel(IBaseViewModel viewModel)
    {
        this.viewModel.optionsMenuButler.setViewModel(viewModel);
    }

    protected OptionsItem getOptionsItem(MenuItem item)
    {
        return this.viewModel.optionsMenuButler.getOptionsItem(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(App.isInitialized && !this.viewModel.isInitializingApp)
        {
            this.viewModel.optionsMenuButler.createOptionsMenu(menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(App.isInitialized && !this.viewModel.isInitializingApp)
        {
            this.viewModel.optionsMenuButler.prepareOptionsMenu(menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return this.viewModel.optionsMenuButler.handleMenuItemSelected(item)
                ? true
                : this.handleOptionsItemSelected(item);
    }

    protected boolean handleOptionsItemSelected(MenuItem item)
    {
        Log.e(String.format(Locale.getDefault(), "OptionsItem %s unhandled", this.viewModel.optionsMenuButler.getOptionsItem(item)));
        return false;
    }

    //endregion OptionsMenu

    // region PopupMenu

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        Log.e(String.format("[%s] clicked - unhandled", item));
    }

    // endregion PopupMenu

    // region HelpOverlayFragment

    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.getValue(view.getId());
        if(buttonFunction == ButtonFunction.CLOSE)
        {
            Log.i(String.format("[%s] selected", buttonFunction));
            this.setHelpOverlayVisibility(false);
        }
    }

    protected void createHelpOverlayFragment(String title, CharSequence message)
    {
        Log.d("preparing HelpOverlayFragment...");

        this.viewModel.isHelpOverlayAdded = true;
        this.viewModel.helpOverlayFragmentTitle = title;
        this.viewModel.helpOverlayFragmentMessage = message;
    }

    protected BaseActivity setHelpOverlayTitleAndMessage(String title, String message)
    {
        Log.d("setting HelpOverlay title and message...");
        Log.v(String.format("...with title [%s] and message [%s]", title, message));

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

    public void showHelpOverlayFragment()
    {
        if(this.helpOverlayFragment == null)
        {
            this.instantiateHelpOverlayFragment();
        }

        Log.i("showing HelpOverlayFragment");
        Log.v(String.format("...with title [%s] and message [%s]...", this.viewModel.helpOverlayFragmentTitle, this.viewModel.helpOverlayFragmentMessage));

        this.setHelpOverlayVisibility(true);
    }

    private void instantiateHelpOverlayFragment()
    {
        if(this.helpOverlayFragment == null)
        {
            Log.d("creating HelpOverlayFragment...");

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(this.viewModel.helpOverlayFragmentTitle, this.viewModel.helpOverlayFragmentMessage);
            fragmentTransaction.add(this.findViewById(android.R.id.content).getId(), this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.hide(this.helpOverlayFragment);
            fragmentTransaction.commit();
        }
        else
        {
            Log.e("re-using existing HelpOverlayFragment");
        }
    }

    private void setHelpOverlayVisibility(boolean isVisible)
    {
        if(this.helpOverlayFragment != null)
        {
            Log.v(String.format("isVisible[%S]...", isVisible));

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
            Log.e("HelpOverlayFragment not found");
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

    // endregion HelpOverlayFragment

    // region Toolbar

    protected void createToolbar()
    {
        Log.d("setting SupportActionBar...");
        setSupportActionBar(this.toolbar);
    }

    protected void addToolbarHomeButton()
    {
        if(getSupportActionBar() != null)
        {
            Log.d("adding to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            this.toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.i("toolbar home button clicked - firing KeyEvent...");
                    onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP));
                }
            });
        }
        else
        {
            Log.e("SupportActionBar not found");
        }
    }

    protected void addToolbarMenuIcon()
    {
        if(getSupportActionBar() != null)
        {
            Log.d("adding menu icon to toolbar...");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(DrawableProvider.getColoredDrawable(R.drawable.menu, R.color.white));
        }
        else
        {
            Log.e("SupportActionBar not found");
        }
    }

    protected void setToolbarTitleAndSubtitle(String title, String subtitle)
    {
        if(getSupportActionBar() != null)
        {
            Log.d(String.format("setting toolbar title [%s] and subtitle [%s]", title, subtitle));

            if(title != null)
            {
                getSupportActionBar().setTitle(title.trim());
            }

            if(subtitle != null)
            {
                getSupportActionBar().setSubtitle(subtitle.trim());
            }

            this.setToolbarOnClickListeners();
        }
        else
        {
            Log.e("SupportActionBar not found");
        }
    }

    private void setToolbarOnClickListeners()
    {
        this.toolbar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(viewModel.toolbarLastClickInMs + Constants.TOOLBAR_LAST_CLICK_MAX_DELAY > System.currentTimeMillis())
                {
                    viewModel.toolbarClickCount++;
                }
                else
                {
                    viewModel.toolbarClickCount = 0;
                }

                viewModel.toolbarLastClickInMs = System.currentTimeMillis();
            }
        });

        this.toolbar.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                if(viewModel.toolbarClickCount == Constants.TOOLBAR_CLICK_COUNT)
                {
                    ActivityDistributor.startActivityViaClass(BaseActivity.this, DeveloperOptionsActivity.class);
                }

                return true;
            }
        });
    }

    // endregion Toolbar

    // region FloatingActionButton

    protected void createFloatingActionButton()
    {
        Log.d("creating...");

        this.floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    protected void setFloatingActionButtonIcon(Drawable icon)
    {
        Log.v("setting icon...");
        if(this.floatingActionButton != null)
        {
            icon.setTint(Color.WHITE);
            this.floatingActionButton.setImageDrawable(icon);
        }
    }

    protected void setFloatingActionButtonOnClickListener(View.OnClickListener onClickListener)
    {
        Log.v("setting onClickListener...");

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
            Log.d(String.format("isVisible [%s]", isVisible));

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
            Log.d("animating...");

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

    // endregion FloatingActionButton

    // region OnElementTypeClickListener

    protected View.OnClickListener createOnElementTypeClickListener(ElementType elementType)
    {
        Log.v(String.format("%s", elementType));
        return new OnElementTypeClickListener(elementType, this);
    }

    private static class OnElementTypeClickListener implements View.OnClickListener
    {
        private final ElementType elementType;
        private final BaseActivity baseActivity;

        protected OnElementTypeClickListener(ElementType elementType, BaseActivity baseActivity)
        {
            this.elementType = elementType;
            this.baseActivity = baseActivity;
        }

        @Override
        public void onClick(View view)
        {
            Log.i(String.format("%s %s clicked", this.elementType, view.getTag()));
            this.baseActivity.handleOnElementTypeClick(this.elementType, view);
        }
    }

    protected void handleOnElementTypeClick(ElementType elementType, View view)
    {
        Log.e(String.format("unhandled click on %s", elementType));
    }

    protected View.OnLongClickListener createOnElementTypeLongClickListener(ElementType elementType)
    {
        Log.v(String.format("%s", elementType));
        return new OnElementTypeLongClickListener(elementType, this);
    }

    private static class OnElementTypeLongClickListener implements View.OnLongClickListener
    {
        private final ElementType elementType;
        private final BaseActivity baseActivity;

        protected OnElementTypeLongClickListener(ElementType elementType, BaseActivity baseActivity)
        {
            this.elementType = elementType;
            this.baseActivity = baseActivity;
        }

        @Override
        public boolean onLongClick(View view)
        {
            Log.i(String.format("%s %s long clicked", this.elementType, view.getTag()));
            return this.baseActivity.handleOnElementTypeLongClick(this.elementType, view);
        }
    }

    protected boolean handleOnElementTypeLongClick(ElementType elementType, View view)
    {
        Log.e(String.format("unhandled long click on %s", elementType));
        return false;
    }

    // endregion OnElementTypeClickListener

    // region Statistics

    protected void getStatistics(StatisticType statisticType)
    {
        Log.d(String.format("fetching type [%s]", statisticType));

        this.showProgressBar(true);
        new StatisticsProvider().execute(this, statisticType);
    }

    private static class StatisticsProvider extends AsyncTask<Object, Void, BaseActivity>
    {
        private StatisticType statisticType;
        private IStatistic statistics;

        @Override
        protected BaseActivity doInBackground(Object... params)
        {
            BaseActivity baseActivity = (BaseActivity)params[0];
            this.statisticType = (StatisticType)params[1];

            if(this.statisticType == StatisticType.GLOBAL_TOTALS)
            {
                this.statistics = App.persistence.fetchStatisticsGlobalTotals();
            }

            return baseActivity;
        }

        @Override
        protected void onPostExecute(BaseActivity baseActivity)
        {
            super.onPostExecute(baseActivity);
            baseActivity.provideStatistics(this.statisticType, this.statistics);
        }
    }

    private void provideStatistics(StatisticType statisticType, IStatistic statistics)
    {
        Log.d(String.format("statistics of type [%s] fetched - calling [%s].decorateStatistics(...)", statisticType, this.getClass().getSimpleName()));

        this.showProgressBar(false);
        this.decorateStatistics(statisticType, statistics);
    }

    protected void decorateStatistics(StatisticType statisticType, IStatistic statistics)
    {
        Log.e(String.format("[%s] does not override decorateStatistics()", this.getClass().getSimpleName()));
    }

    // endregion Statistics

    // region Persistency

    protected void markForCreation(IElement element)
    {
        if(element.isPersistable())
        {
            Log.i(String.format("marking %s for creation", element));
            this.viewModel.elementsToCreate.add(element);
        }
        else
        {
            Log.e(String.format("%s is not persistable", element));
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
        if(element.isPersistable())
        {
            Log.i(String.format("marking %s for update", element));
            this.viewModel.elementsToUpdate.add(element);
        }
        else
        {
            Log.e(String.format("%s is not persistable", element));
        }
    }

    protected void markForDeletion(IElement element)
    {
        this.markForDeletion(element, false);
    }

    private IElement originToDelete;
    protected void markForDeletion(IElement element, boolean deleteDescendants)
    {
        if(this.originToDelete == null)
        {
            this.originToDelete = element;
        }

        if(element.isPersistable())
        {
            Log.i(String.format("marking %s for deletion", element));
            this.viewModel.elementsToDelete.add(element);

            if(deleteDescendants && element.hasChildren())
            {
                for(IElement child : element.getChildren())
                {
                    if(child.isPersistable())
                    {
                        this.markForDeletion(child, true);
                    }
                }
            }
        }
        else
        {
            Log.e(String.format("[%s] is not persistable", element));
        }

        if(deleteDescendants)
        {
            if(this.originToDelete != null && this.originToDelete.equals(element))
            {
                element.deleteElementAndDescendants();
                this.originToDelete = null;
            }
        }
        else
        {
            element.delete();
        }

        App.content.removeElement(element);
    }

    protected void synchronizePersistency()
    {
        if(!(this.viewModel.elementsToCreate.isEmpty() && this.viewModel.elementsToUpdate.isEmpty() && this.viewModel.elementsToDelete.isEmpty()))
        {
            Log.i("synchronizing...");

            App.persistence.trySynchronize(new HashSet<>(this.viewModel.elementsToCreate), new HashSet<>(this.viewModel.elementsToUpdate), new HashSet<>(this.viewModel.elementsToDelete));

            this.viewModel.elementsToCreate.clear();
            this.viewModel.elementsToUpdate.clear();
            this.viewModel.elementsToDelete.clear();
        }
        else
        {
            Log.v("persistence is synchronous");
        }
    }

    // endregion Persistency
}