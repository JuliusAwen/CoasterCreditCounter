package de.juliusawen.coastercreditcounter.presentation.attractions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public  class ShowAttractionsFragment extends Fragment
{
    private ShowAttractionsFragmentViewModel viewModel;

    public ShowAttractionsFragment() {}

    public static ShowAttractionsFragment newInstance(String uuidString)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowAttractionsFragment.newInstance:: instantiating fragment...");

        ShowAttractionsFragment showAttractionsFragment =  new ShowAttractionsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, uuidString);
        showAttractionsFragment.setArguments(args);

        return showAttractionsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.onCreate:: creating fragment...");
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowAttractionsFragmentViewModel.class);

        if(this.viewModel.park == null)
        {
            this.viewModel.park = (Park) App.content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerViewAdapter();
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_attractions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFragmentShowAttractions);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.updateContentRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case SORT_ATTRACTION_CATEGORIES:
                ActivityTool.startActivitySortForResult(
                        Objects.requireNonNull(getActivity()),
                        Constants.REQUEST_SORT_ATTRACTION_CATEGORIES,
                        new ArrayList<Element>(AttractionCategory.getAttractionCategories()));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_EDIT_ELEMENT)
            {
                this.updateContentRecyclerView();
                this.viewModel.contentRecyclerViewAdapter.scrollToElement(App.content.getElementByUuid(UUID.fromString(data.getStringExtra(Constants.EXTRA_ELEMENT_UUID))));
            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS || requestCode == Constants.REQUEST_SORT_ATTRACTION_CATEGORIES)
            {
                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                Element selectedElement = null;
                if(selectedElementUuidString != null)
                {
                    selectedElement = App.content.fetchElementByUuidString(selectedElementUuidString);
                    Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<OK>:: selected element %s returned", selectedElement));
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.onActivityResult<OK>:: no selected element returned");
                }

                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> resultElements = App.content.fetchElementsByUuidStrings(resultElementsUuidStrings);

                if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
                {
                    Element parent = resultElements.get(0).getParent();
                    if(parent != null)
                    {
                        this.viewModel.park.deleteChildren(resultElements);
                        this.viewModel.park.addChildren(resultElements);
                        Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<SortAttractions>:: replaced %s's <children> with <sorted children>", this.viewModel.park));
                    }

                    this.updateContentRecyclerView();

                    if(selectedElement != null)
                    {
                        Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<SortAttractions>:: scrolling to selected element %s...", selectedElement));
                        this.viewModel.contentRecyclerViewAdapter.scrollToElement(((Attraction)selectedElement).getCategory());
                    }

                }
                else
                {
                    AttractionCategory.setAttractionCategories(AttractionCategory.convertToAttractionCategories(resultElements));
                    this.updateContentRecyclerView();

                    if(selectedElement != null)
                    {
                        Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<SortAttractionCategory>:: scrolling to selected element %s...", selectedElement));
                        this.viewModel.contentRecyclerViewAdapter.scrollToElement(selectedElement);
                    }
                }
            }
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.viewModel = null;
    }

    private ContentRecyclerViewAdapter createContentRecyclerViewAdapter()
    {
        List<Element> attractions = this.viewModel.park.getChildrenOfType(Attraction.class);
        return ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                this.getAttractionsWithCategoryHeaders(attractions),
//                AttractionCategory.getAttractionCategoriesToExpandAccordingToSettings(attractions),
                null,
                Attraction.class);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element.isInstance(Attraction.class))
                {
                    Toaster.makeToast(getContext(), String.format("ShowAttractions not yet implemented %s", (Element) view.getTag()));
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                final Element longClickedElement = (Element) view.getTag();

                if(longClickedElement.isInstance(AttractionCategoryHeader.class))
                {
                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenu().add(0, Selection.EDIT_ATTRACTION_CATEGORY.ordinal(), Menu.NONE, R.string.selection_edit_attraction_category);

                    if(longClickedElement.getChildCountOfType(Attraction.class) > 1)
                    {
                        popupMenu.getMenu().add(0, Selection.SORT_ATTRACTIONS.ordinal(), Menu.NONE, R.string.selection_sort_attractions);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            Selection selection = Selection.values()[item.getItemId()];
                            Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onLongClickExpandableRecyclerView.onMenuItemClick:: [%S] selected", selection));

                            switch (selection)
                            {
                                case EDIT_ATTRACTION_CATEGORY:
                                    ActivityTool.startActivityEditForResult(
                                            Objects.requireNonNull(getActivity()),
                                            Constants.REQUEST_EDIT_ELEMENT,
                                            ((AttractionCategoryHeader)longClickedElement).getAttractionCategory());
                                    return true;

                                case SORT_ATTRACTIONS:
                                    ActivityTool.startActivitySortForResult(
                                            Objects.requireNonNull(getActivity()),
                                            Constants.REQUEST_SORT_ATTRACTIONS,
                                            longClickedElement.getChildrenOfType(Attraction.class));
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

    private void updateContentRecyclerView()
    {
        this.viewModel.contentRecyclerViewAdapter.updateContent(this.getAttractionsWithCategoryHeaders(this.viewModel.park.getChildrenOfType(Attraction.class)));
        this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
    }

    private List<Element> getAttractionsWithCategoryHeaders(List<Element> attractions)
    {
        if(!this.viewModel.attractionCategoryHeaders.isEmpty())
        {
            App.content.removeOrphanElements(Element.convertElementsToType(this.viewModel.attractionCategoryHeaders, OrphanElement.class));
        }
        this.viewModel.attractionCategoryHeaders = AttractionCategoryHeader.fetchAttractionCategoryHeaders(attractions);
        return this.viewModel.attractionCategoryHeaders;
    }
}