package de.juliusawen.coastercreditcounter.frontend.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Location;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowLocationsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ShowLocationsActivityViewModel viewModel;

    private View.OnClickListener onClickListenerNavigationBar;
    private LinearLayout linearLayoutNavigationBar;
    private HorizontalScrollView horizontalScrollViewNavigationBar;

    private boolean actionConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_locations);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.linearLayoutNavigationBar = findViewById(R.id.linearLayoutShowLocations_NavigationBar);
            this.horizontalScrollViewNavigationBar = findViewById(R.id.horizontalScrollViewShowLocations_NavigationBar);
            RecyclerView recyclerView = findViewById(R.id.recyclerViewShowLocations);

            this.viewModel = ViewModelProviders.of(this).get(ShowLocationsActivityViewModel.class);

            if(this.viewModel.currentElement == null)
            {
                String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                this.viewModel.currentElement = elementUuid != null ? App.content.getContentByUuid(UUID.fromString(elementUuid)) : App.content.getRootLocation();
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(Location.class);
                childTypesToExpand.add(Park.class);

                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        this.viewModel.currentElement.getChildrenOfType(Location.class),
                        null,
                        childTypesToExpand);

                this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Location.class, Typeface.BOLD);
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(getString(R.string.title_locations), null);

            super.addFloatingActionButton();
            this.decorateFloatingActionButton();

            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_locations)), getString(R.string.help_text_show_locations));

            this.onClickListenerNavigationBar = this.getNavigationBarOnClickListener();

            this.updateActivityView();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(((Location)this.viewModel.currentElement).isRootLocation())
        {
            menu.add(Menu.NONE, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit_root_location);
        }

        if(this.viewModel.currentElement.getChildCountOfType(Location.class) > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_LOCATIONS.ordinal(), Menu.NONE, R.string.selection_sort_locations);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case EDIT_LOCATION:
                ActivityTool.startActivityEditForResult(this, Constants.REQUEST_EDIT_LOCATION, this.viewModel.currentElement);
                return true;

            case SORT_LOCATIONS:
                ActivityTool.startActivitySortForResult(
                        this,
                        Constants.REQUEST_SORT_LOCATIONS,
                        this.viewModel.currentElement.getChildrenOfType(Location.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_CREATE_LOCATION)
            {
                String resultElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                IElement resultElement = App.content.getContentByUuid(UUID.fromString(resultElementUuidString));

                this.setItemsInRecyclerViewAdapter();
                this.viewModel.contentRecyclerViewAdapter.scrollToItem(resultElement);
            }
            else if(requestCode == Constants.REQUEST_SORT_LOCATIONS || requestCode == Constants.REQUEST_SORT_PARKS)
            {
                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<IElement> resultElements = App.content.getContentByUuidStrings(resultElementsUuidStrings);

                IElement parent = resultElements.get(0).getParent();
                Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<SortElements>:: replacing children with sorted children in parent %s...", parent));
                parent.deleteChildren(resultElements);
                parent.addChildrenAndSetParents(resultElements);

                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.currentElement.getChildrenOfType(Location.class));

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    IElement selectedElement = App.content.getContentByUuid(UUID.fromString(selectedElementUuidString));
                    this.viewModel.contentRecyclerViewAdapter.scrollToItem(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowLocationsActivity.onActivityResult<SortElements>:: no selected element returned");
                }

                super.markForUpdate(parent);
            }
            else if(requestCode == Constants.REQUEST_EDIT_LOCATION)
            {
                IElement editedElement = App.content.getContentByUuid(UUID.fromString(data.getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
                this.updateActivityView();
                this.viewModel.contentRecyclerViewAdapter.updateItem(editedElement);

                super.markForUpdate(editedElement);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onKeyDown<BACK>:: hardware back button pressed");
                if(((Location)this.viewModel.currentElement).isRootLocation())
                {
                    super.onKeyDown(keyCode, event);
                }
                else
                {
                    IElement previousElement = this.viewModel.recentElements.get(this.viewModel.recentElements.size() - 2);
                    Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActonKeyDown<KEYCODE_BACK>:: returning to previous element %s", previousElement));
                    this.viewModel.recentElements.remove(this.viewModel.currentElement);
                    this.viewModel.recentElements.remove(previousElement);
                    this.viewModel.currentElement = previousElement;
                    this.updateActivityView();
                    this.setItemsInRecyclerViewAdapter();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void updateActivityView()
    {
        super.animateFloatingActionButtonTransition(null);
        this.updateNavigationBar();
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                PopupMenu popupMenu = new PopupMenu(ShowLocationsActivity.this, getFloatingActionButton());

                popupMenu.getMenu().add(0, Selection.CREATE_LOCATION.ordinal(), Menu.NONE, R.string.selection_create_location);
                popupMenu.getMenu().add(0, Selection.CREATE_PARK.ordinal(), Menu.NONE, R.string.selection_create_park)
                        .setEnabled(false); //Todo: implement CreatePark

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Selection selection = Selection.values()[item.getItemId()];
                        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickFloatingActionButton.PopupMenu.onMenuItemClick:: [%S] selected", selection));

                        switch (selection)
                        {
                            case CREATE_LOCATION:
                                ActivityTool.startActivityCreateForResult(ShowLocationsActivity.this, Constants.REQUEST_CREATE_LOCATION, viewModel.currentElement);
                                return true;

                            case CREATE_PARK:
                                Toaster.makeToast(ShowLocationsActivity.this, "AddPark not yet implemented");
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    private View.OnClickListener getNavigationBarOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar.onClick:: %s clicked", element));

                int length = viewModel.recentElements.size() - 1;
                for (int i = length; i >= 0; i--)
                {
                    if(viewModel.recentElements.get(i).equals(element))
                    {
                        viewModel.recentElements.remove(i);
                        break;
                    }
                    else
                    {
                        viewModel.recentElements.remove(i);
                    }
                }
                viewModel.currentElement = element;
                updateActivityView();
                setItemsInRecyclerViewAdapter();
            }
        };
    }

    private void updateNavigationBar()
    {
        Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateNavigationBar:: updating NavigationBar...");

        this.linearLayoutNavigationBar.removeAllViews();

        if(this.viewModel.recentElements.isEmpty() && !((Location)this.viewModel.currentElement).isRootLocation())
        {
            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateNavigationBar:: constructing NavigationBar");
            this.viewModel.recentElements.clear();
            this.constructNavigationBar(this.viewModel.currentElement.getParent());
        }

        if(!this.viewModel.recentElements.contains(this.viewModel.currentElement))
        {
            Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: adding CurrentElement %s to RecentElements...", this.viewModel.currentElement));
            this.viewModel.recentElements.add(this.viewModel.currentElement);
        }

        for (IElement recentElement : this.viewModel.recentElements)
        {
            Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: creating TextView for recent element %s...", recentElement));
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.text_view_navigation_bar, linearLayoutNavigationBar, false);

            if(this.viewModel.recentElements.indexOf(recentElement) != this.viewModel.recentElements.size() -1)
            {
                Drawable drawable = DrawableTool.getColoredDrawable(R.drawable.ic_baseline_chevron_right, R.color.white);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                textView.setText(recentElement.getName());
                textView.setTag(recentElement);
                textView.setOnClickListener(this.onClickListenerNavigationBar);
            }
            else
            {
                Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: %s is current element - applying special treatment", recentElement));
                textView.setText(StringTool.getSpannableString(recentElement.getName(), Typeface.BOLD_ITALIC));
            }

            this.linearLayoutNavigationBar.addView(textView);
        }

        this.linearLayoutNavigationBar.invalidate();

        this.horizontalScrollViewNavigationBar.post(new Runnable()
        {
            @Override
            public void run()
            {
                horizontalScrollViewNavigationBar.fullScroll(View.FOCUS_RIGHT);
            }
        });

        Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: NavigationBar holds #[%d] elements", this.viewModel.recentElements.size()));
    }

    private void constructNavigationBar(IElement element)
    {
        Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.constructNavigationBar:: adding %s to recent elements...", element));

        if(!((Location)element).isRootLocation())
        {
            this.viewModel.recentElements.add(0, element);
            this.constructNavigationBar(element.getParent());
        }
        else
        {
            this.viewModel.recentElements.add(0, element);
        }
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickLocationRecyclerView:: %s clicked", element));

                if(Location.class.isInstance(element))
                {
                    viewModel.currentElement = element;
                    updateActivityView();
                    setItemsInRecyclerViewAdapter();
                }
                else if(Park.class.isInstance(element))
                {
                    ActivityTool.startActivityShow(ShowLocationsActivity.this, Constants.REQUEST_SHOW_PARK, element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (Element) view.getTag();
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onLongClickLocationRecyclerView:: %s long clicked", viewModel.longClickedElement));

                if(Location.class.isInstance(viewModel.longClickedElement))
                {
                    PopupMenu popupMenu = new PopupMenu(ShowLocationsActivity.this, view);

                    popupMenu.getMenu().add(0, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit);
                    popupMenu.getMenu().add(0, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete);

                    popupMenu.getMenu().add(0, Selection.REMOVE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_remove).setEnabled(viewModel.longClickedElement.hasChildren());


                    if(viewModel.longClickedElement.getChildCountOfType(Park.class) > 1)
                    {
                        popupMenu.getMenu().add(0, Selection.SORT_PARKS.ordinal(), Menu.NONE, R.string.selection_sort_parks);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            Selection selection = Selection.values()[item.getItemId()];
                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickMenuItemPopupMenuLongClickLocationRecyclerView:: [%S] selected", selection));

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            switch (selection)
                            {
                                case EDIT_LOCATION:
                                    ActivityTool.startActivityEditForResult(ShowLocationsActivity.this, Constants.REQUEST_EDIT_LOCATION, viewModel.longClickedElement);
                                    return true;

                                case DELETE_ELEMENT:
                                    AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                                                    R.drawable.ic_baseline_warning,
                                                    getString(R.string.alert_dialog_delete_element_title),
                                                    getString(R.string.alert_dialog_delete_element_message, viewModel.longClickedElement.getName()),
                                                    getString(R.string.text_accept),
                                                    getString(R.string.text_cancel),
                                                    Constants.ALERT_DIALOG_REQUEST_CODE_DELETE
                                            );

                                    alertDialogFragmentDelete.setCancelable(false);
                                    alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);

                                    return true;

                                case REMOVE_ELEMENT:
                                    String alertDialogMessage;
                                    if(viewModel.longClickedElement.getParent().equals(App.content.getRootLocation()) && viewModel.longClickedElement.hasChildrenOfType(Park.class))
                                    {
                                        alertDialogMessage = getString(R.string.alert_dialog_remove_element_message_parent_is_root, viewModel.longClickedElement.getName(),
                                                        viewModel.longClickedElement.getParent().getName());
                                    }
                                    else
                                    {
                                        alertDialogMessage = getString(R.string.alert_dialog_remove_element_message, viewModel.longClickedElement.getName(),
                                                        viewModel.longClickedElement.getParent().getName());
                                    }

                                    AlertDialogFragment alertDialogFragmentRemove = AlertDialogFragment.newInstance(
                                            R.drawable.ic_baseline_warning,
                                            getString(R.string.alert_dialog_remove_element_title),
                                            alertDialogMessage,
                                            getString(R.string.text_accept),
                                            getString(R.string.text_cancel),
                                            Constants.ALERT_DIALOG_REQUEST_CODE_REMOVE
                                    );
                                    alertDialogFragmentRemove.setCancelable(false);
                                    alertDialogFragmentRemove.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                                    return true;

                                case SORT_PARKS:
                                    ActivityTool.startActivitySortForResult(
                                            ShowLocationsActivity.this,
                                            Constants.REQUEST_SORT_PARKS,
                                            viewModel.longClickedElement.getChildrenOfType(Park.class));
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
                return true;
            }
        };
    }

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        Snackbar snackbar;

        switch(requestCode)
        {
            case Constants.ALERT_DIALOG_REQUEST_CODE_DELETE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                            Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            actionConfirmed = true;
                            Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onSnackbarClick<DELETE>:: action <DELETE> confirmed");
                        }
                    });

                    snackbar.addCallback(new Snackbar.Callback()
                    {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event)
                        {
                            if(actionConfirmed)
                            {
                                actionConfirmed = false;

                                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onDismissed<DELETE>:: deleting %s...", viewModel.longClickedElement));

                                List<IElement> childrenToDelete = new ArrayList<>(viewModel.longClickedElement.getChildren());

                                viewModel.contentRecyclerViewAdapter.removeItem(viewModel.longClickedElement);

                                App.content.removeElementAndChildren(viewModel.longClickedElement);
                                viewModel.longClickedElement.deleteElementAndChildren();

                                ShowLocationsActivity.super.markForDeletion(viewModel.longClickedElement);
                                ShowLocationsActivity.super.markForDeletion(childrenToDelete);
                            }
                            else
                            {
                                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onDismissed<DELETE>:: action <DELETE> not confirmed - doing nothing");
                            }
                        }
                    });

                    snackbar.show();
                }
                break;
            }

            case Constants.ALERT_DIALOG_REQUEST_CODE_REMOVE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.action_confirm_remove_text, viewModel.longClickedElement.getName()),
                            Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            actionConfirmed = true;
                            Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onSnackbarClick<REMOVE>:: action <REMOVE> confirmed");
                        }
                    });

                    snackbar.addCallback(new Snackbar.Callback()
                    {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event)
                        {
                            if(actionConfirmed)
                            {
                                actionConfirmed = false;

                                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onSnackbarDismissed<REMOVE>:: removing %s...", viewModel.longClickedElement));

                                App.content.removeElement(viewModel.longClickedElement);
                                viewModel.longClickedElement.removeElement();

                                setItemsInRecyclerViewAdapter();

                                ShowLocationsActivity.super.markForDeletion(viewModel.longClickedElement);
                                ShowLocationsActivity.super.markForUpdate(viewModel.longClickedElement.getParent());
                            }
                            else
                            {
                                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onDismissed<REMOVE>:: action <REMOVE> not confirmed - doing nothing");
                            }
                        }
                    });

                    snackbar.show();
                }
                break;
            }
        }
    }

    private void setItemsInRecyclerViewAdapter()
    {
        viewModel.contentRecyclerViewAdapter.setItems(viewModel.currentElement.getChildrenOfType(Location.class));
    }
}
