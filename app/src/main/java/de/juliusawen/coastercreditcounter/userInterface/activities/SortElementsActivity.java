package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public class SortElementsActivity extends BaseActivity
{
    private SortElementsViewModel viewModel;

    private View frameLayoutDialogDown;
    private View frameLayoutDialogUp;

    protected void setContentView()
    {
        setContentView(R.layout.activity_sort_elements);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(SortElementsViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.getValue(getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0));
            Log.d(String.format("%s", this.viewModel.requestCode));
        }

        if(this.viewModel.elementsToSort == null)
        {
            this.viewModel.elementsToSort = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }

        if(this.viewModel.adapterFacade == null)
        {
            if(App.preferences.defaultPropertiesAlwaysAtTop() && this.viewModel.elementsToSort.get(0).isProperty())
            {
                for(IElement element : this.viewModel.elementsToSort)
                {
                    if(((IProperty) element).isDefault())
                    {
                        Log.i(String.format("removing %s - App.preferences.defaultPropertiesAlwaysAtTop = TRUE", element));
                        this.viewModel.defaultProperty = element;
                        this.viewModel.elementsToSort.remove(element);
                        break;
                    }
                }
            }

            this.viewModel.adapterFacade = new ContentRecyclerViewAdapterFacade();
            this.viewModel.adapterFacade.createPreconfiguredAdapter(this.viewModel.requestCode);
            this.viewModel.adapterFacade.getAdapter().setContent(this.viewModel.elementsToSort);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSortElements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter((ContentRecyclerViewAdapter) this.viewModel.adapterFacade.getAdapter());


        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getString(R.string.help_text_sort_elements));
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();

        super.setOptionsMenuButlerViewModel(this.viewModel);


        this.createActionDialog();
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.check, R.color.white));

        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d("FloatingActionButton clicked");
                returnResult(RESULT_OK);
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void createActionDialog()
    {
        ImageButton buttonDown = findViewById(R.id.buttonActionDialogUpDown_Down);
        buttonDown.setImageDrawable(DrawableProvider.getColoredDrawable(R.drawable.arrow_downward, R.color.white));
        buttonDown.setId(ButtonFunction.MOVE_SELECTION_DOWN.ordinal());
        this.frameLayoutDialogDown = findViewById(R.id.frameLayoutDialogUpDown_Down);
        this.frameLayoutDialogDown.setOnClickListener(this.getOnActionDialogClickListener());
        this.frameLayoutDialogDown.setOnTouchListener(this.getOnActionDialogTouchListener());

        ImageButton buttonUp = findViewById(R.id.buttonActionDialogUpDown_Up);
        buttonUp.setImageDrawable(DrawableProvider.getColoredDrawable(R.drawable.arrow_upward, R.color.white));
        buttonUp.setId(ButtonFunction.MOVE_SELECTION_UP.ordinal());
        this.frameLayoutDialogUp = findViewById(R.id.frameLayoutDialogUpDown_Up);
        this.frameLayoutDialogUp.setOnClickListener(this.getOnActionDialogClickListener());
        this.frameLayoutDialogUp.setOnTouchListener(this.getOnActionDialogTouchListener());
    }

    private View.OnClickListener getOnActionDialogClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleActionDialogClick(view);
            }
        };
    }

    private void handleActionDialogClick(View view)
    {
        this.viewModel.selectedElement = this.viewModel.adapterFacade.getAdapter().getLastSelectedItem();

        if(view.equals(this.frameLayoutDialogDown))
        {
            Log.v("button <DOWN> clicked");
        }
        else if(view.equals(this.frameLayoutDialogUp))
        {
            Log.v("button <UP> clicked");
        }
    }

    private View.OnTouchListener getOnActionDialogTouchListener()
    {
        return (new View.OnTouchListener()
        {
            private final long DELAY_IN_MS = 200;
            private Handler handler;

            @Override public boolean onTouch(View view, MotionEvent event)
            {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        view.performClick();

                        if (handler != null)
                        {
                            return true;
                        }

                        handler = new Handler();
                        if(view.equals(frameLayoutDialogDown))
                        {
                            handler.post(actionSortDown);
                        }
                        else if(view.equals(frameLayoutDialogUp))
                        {
                            handler.post(actionSortUp);
                        }

                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    {
                        if (handler == null)
                        {
                            return true;
                        }

                        if(view.equals(frameLayoutDialogDown))
                        {
                            handler.removeCallbacks(actionSortDown);
                        }
                        else if(view.equals(frameLayoutDialogUp))
                        {
                            handler.removeCallbacks(actionSortUp);
                        }

                        handler = null;
                        return true;
                    }
                }

                return false;
            }

            final Runnable actionSortDown = new Runnable()
            {
                @Override public void run()
                {
                    sortDown();
                    handler.postDelayed(this, DELAY_IN_MS);
                }
            };

            final Runnable actionSortUp = new Runnable()
            {
                @Override public void run()
                {
                    sortUp();
                    handler.postDelayed(this, DELAY_IN_MS);
                }
            };
        });
    }

    private void sortDown()
    {
        if(this.viewModel.selectedElement != null)
        {
            int position = this.viewModel.elementsToSort.indexOf(this.viewModel.selectedElement);

            if(position < this.viewModel.elementsToSort.size() - 1)
            {
                Log.v("swapping elements");
                this.viewModel.adapterFacade.getAdapter().swapItems(this.viewModel.elementsToSort.get(position), this.viewModel.elementsToSort.get(position + 1));
            }
            else
            {
                Log.v("end of list - not swapping elements");
            }
        }
        else
        {
            Log.v("no element selected");
        }
    }

    private void sortUp()
    {
        if(this.viewModel.selectedElement != null)
        {
            int position = this.viewModel.elementsToSort.indexOf(this.viewModel.selectedElement);

            if(position > 0)
            {
                Log.v("swapping elements");

                this.viewModel.adapterFacade.getAdapter().swapItems(this.viewModel.elementsToSort.get(position), this.viewModel.elementsToSort.get(position - 1));
            }
            else
            {
                Log.v("end of list - not swapping elements");
            }
        }
        else
        {
            Log.v("no element selected");
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(String.format("%s", StringTool.resultCodeToString(resultCode)));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(this.viewModel.defaultProperty != null)
            {
                Log.i(String.format("adding %s at index 0 - App.prefereneces.defaultPropertiesAlwaysAtTop = TRUE", this.viewModel.defaultProperty));

                this.viewModel.elementsToSort.add(0, this.viewModel.defaultProperty);
            }

            Log.d(String.format(Locale.getDefault(), "returning [%d] Elements as result", this.viewModel.elementsToSort.size()));
            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(this.viewModel.elementsToSort));

            if(!this.viewModel.adapterFacade.getAdapter().getSelectedItemsInOrderOfSelection().isEmpty())
            {
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.adapterFacade.getAdapter().getLastSelectedItem().getUuid().toString());
            }
        }

        setResult(resultCode, intent);
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', false);
        finish();
    }
}