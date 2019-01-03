package de.juliusawen.coastercreditcounter.frontend.attractions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public  class ShowAttractionsFragment extends Fragment
{
    private ShowAttractionsFragmentViewModel viewModel;

    public ShowAttractionsFragment() {}

    public static ShowAttractionsFragment newInstance(String uuidString)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowAttractionsFragment.newInstance:: instantiating fragment...");

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
            if(getArguments() != null)
            {
                this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
            }
        }

        if(this.viewModel.attractionCategoryHeaderProvider == null)
        {
            this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerViewAdapter();
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategoryHeader.class, Typeface.BOLD);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement selectedElement = ResultTool.fetchResultElement(data);

            if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
            {
                List<IElement> resultElements = ResultTool.fetchResultElements(data);

                IElement parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.viewModel.park.reorderChildren(resultElements);
                    Log.d(Constants.LOG_TAG,
                            String.format("ShowAttractionsFragment.onActivityResult<SortAttractions>:: replaced %s's <children> with <sorted children>", this.viewModel.park));

                    this.viewModel.contentRecyclerViewAdapter.setItems(
                            this.viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(
                                    new ArrayList<IAttraction>(this.viewModel.park.getChildrenAsType(IOnSiteAttraction.class))));

                    if(selectedElement != null)
                    {
                        Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<SortAttractions>:: scrolling to selected element %s...", selectedElement));
                        this.viewModel.contentRecyclerViewAdapter.scrollToItem(((Attraction)selectedElement).getAttractionCategory());
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
        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
        childTypesToExpand.add(Attraction.class);

        return ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                this.viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(new ArrayList<IAttraction>(this.viewModel.park.getChildrenAsType(IOnSiteAttraction.class))),
                null,
                childTypesToExpand);

    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(Attraction.class.isInstance(element))
                {
                    Toaster.makeToast(getContext(), String.format("ShowAttraction not yet implemented %s", (Element) view.getTag()));
                }
                else if(AttractionCategoryHeader.class.isInstance(element))
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                if((AttractionCategoryHeader.class.isInstance(view.getTag())))
                {
                    AttractionCategoryHeader.handleOnAttractionCategoryHeaderLongClick(getActivity(), view);
                }
                return true;
            }
        };
    }
}