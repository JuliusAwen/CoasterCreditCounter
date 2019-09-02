package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class MenuAgent
{
    private MenuType menuType;

    private Map<Selection, Pair<Boolean, Boolean>> isAddedAndisEnabledPairsBySelection = new HashMap<>();

    private Map<Selection, Integer> stringResourcesBySelection = new HashMap<>();

    private List<Selection> optionsMenuFlatSelections = new ArrayList<>();
    private List<Selection> optionsMenuDeepSelections = new ArrayList<>();
    private List<Selection> optionsMenuActionSelections = new ArrayList<>();

    private List<Selection> popupMenuFlatSelections = new ArrayList<>();
    private List<Selection> popupMenuDeepSelections = new ArrayList<>();

    public MenuAgent(MenuType menuType)
    {
        this.menuType = menuType;

        this.initializeIsAddedAndIsEnabledPairsBySelection();

        this.initializeStringResourcesBySelection();

        this.initializeOptionsMenuFlatSelections();
        this.initializeOptionsMenuDeepSelections();
        this.initializeOptionsMenuActionSelections();

        this.initializePopupMenuFlatSelections();
        this.initializePopupMenuDeepSelections();
    }

    // region initialization

    private void initializeIsAddedAndIsEnabledPairsBySelection()
    {

    }

    private void initializeStringResourcesBySelection()
    {
        this.stringResourcesBySelection.put(Selection.EXPAND_ALL, R.string.selection_expand_all);
        this.stringResourcesBySelection.put(Selection.COLLAPSE_ALL, R.string.selection_collapse_all);
    }

    private void initializeOptionsMenuFlatSelections()
    {
//        this.optionsMenuFlatSelections.add(Selection.SORT);

        this.optionsMenuFlatSelections.add(Selection.EXPAND_ALL);
        this.optionsMenuFlatSelections.add(Selection.COLLAPSE_ALL);
    }

    private void initializeOptionsMenuDeepSelections()
    {
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_NAME_ASCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_NAME_DESCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_YEAR_ASCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_YEAR_DESCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_LOCATION_ASCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_LOCATION_DESCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_MANUFACTURER_ASCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_MANUFACTURER_DESCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_ATTRACTION_CATEGORY_ASCENDING);
//        this.optionsMenuDeepSelections.add(Selection.SORT_BY_ATTRACTION_CATEGORY_DESCENDING);
//        this.optionsMenuDeepSelections.add(Selection.GROUP_BY);
//        this.optionsMenuDeepSelections.add(Selection.GROUP_BY_LOCATION);
//        this.optionsMenuDeepSelections.add(Selection.GROUP_BY_ATTRACTION_CATEGORY);
//        this.optionsMenuDeepSelections.add(Selection.GROUP_BY_MANUFACTURER);
//        this.optionsMenuDeepSelections.add(Selection.GROUP_BY_STATUS);
    }

    private void initializeOptionsMenuActionSelections()
    {
//        this.optionsMenuActionSelections.add(Selection.SHORTCUT_TO_CURRENT_VISIT);
//        this.optionsMenuActionSelections.add(Selection.ENABLE_EDITING);
//        this.optionsMenuActionSelections.add(Selection.DISABLE_EDITING);
    }

    private void initializePopupMenuFlatSelections()
    {
//        this.popupMenuFlatSelections.add(Selection.EDIT);
//        this.popupMenuFlatSelections.add(Selection.DELETE);
//        this.popupMenuFlatSelections.add(Selection.REMOVE);
//        this.popupMenuFlatSelections.add(Selection.RELOCATE);
//        this.popupMenuFlatSelections.add(Selection.ASSIGN_TO_ATTRACTIONS);
//        this.popupMenuFlatSelections.add(Selection.SET_AS_DEFAULT);
    }

    private void initializePopupMenuDeepSelections()
    {
//        this.popupMenuDeepSelections.add(Selection.SORT_LOCATIONS);
//        this.popupMenuDeepSelections.add(Selection.SORT_PARKS);
//        this.popupMenuDeepSelections.add(Selection.CREATE_LOCATION);
//        this.popupMenuDeepSelections.add(Selection.CREATE_PARK);
    }

    // endregion initialization

    // region setter

    public MenuAgent addExpandAllSetEnabled(boolean enabled)
    {
        this.isAddedAndisEnabledPairsBySelection.put(Selection.EXPAND_ALL, new Pair<>(true, enabled));
        return this;
    }

    public MenuAgent addCollapseAllSetEnabled(boolean enabled)
    {
        this.isAddedAndisEnabledPairsBySelection.put(Selection.COLLAPSE_ALL, new Pair<>(true, enabled));
        return this;
    }



    // endregion setter

    public void handle(Menu menu)
    {
        switch(this.menuType)
        {
            case OPTIONS_MENU:

                this.initializeIsAddedAndIsEnabledPairsBySelection();

                for(Selection flatSelection : this.optionsMenuFlatSelections)
                {
                    if(this.isAddedAndisEnabledPairsBySelection.get(flatSelection).first)
                    {
                        menu.add(Menu.NONE, flatSelection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(flatSelection))
                                .setEnabled(this.isAddedAndisEnabledPairsBySelection.get(flatSelection).second);
                    }
                }

                menu.add(Menu.NONE, Selection.HELP.ordinal(), Menu.NONE, R.string.selection_help);
                break;

            case POPUP_MENU:

                break;
        }
    }


    public boolean handleMenuItemSelection(MenuItem item, IMenuAgentClient client)
    {
        Log.i(Constants.LOG_TAG, String.format("MenuAgent.handleMenuItemSelection:: [%s] selected", Selection.values()[item.getItemId()].toString()));

        switch(Selection.values()[item.getItemId()])
        {
            case HELP:
                return client.handleOptionsMenuSelectionHelp();

            case EXPAND_ALL:
                return client.handleOptionsMenuSelectionExpandAll();

            case COLLAPSE_ALL:
                return client.handleOptionsMenuSelectionCollapseAll();



                default:
                    return false;
        }
    }

    public enum MenuType
    {
        OPTIONS_MENU,
        POPUP_MENU
    }

    public enum Selection
    {
        //OptionsMenu FLAT


        SORT,
        SORT_LOCATIONS,
        SORT_PARKS,
        SORT_ATTRACTIONS,

        EXPAND_ALL,
        COLLAPSE_ALL,

        HELP,


        //OptionMenus DEEP

        SORT_BY,
        SORT_BY_NAME_ASCENDING,
        SORT_BY_NAME_DESCENDING,
        SORT_BY_YEAR_ASCENDING,
        SORT_BY_YEAR_DESCENDING,
        SORT_BY_LOCATION_ASCENDING,
        SORT_BY_LOCATION_DESCENDING,
        SORT_BY_MANUFACTURER_ASCENDING,
        SORT_BY_MANUFACTURER_DESCENDING,
        SORT_BY_ATTRACTION_CATEGORY_ASCENDING,
        SORT_BY_ATTRACTION_CATEGORY_DESCENDING,

        GROUP_BY,
        GROUP_BY_LOCATION,
        GROUP_BY_MANUFACTURER,
        GROUP_BY_ATTRACTION_CATEGORY,
        GROUP_BY_STATUS,


        //OptionsMenu ACTION

        SHORTCUT_TO_CURRENT_VISIT,
        ENABLE_EDITING,
        DISABLE_EDITING,


        //PopupMenu FLAT

        EDIT,
        DELETE,
        REMOVE,
        RELOCATE,

        ASSIGN_TO_ATTRACTIONS,
        SET_AS_DEFAULT,


        //PopupMenu DEEP

        CREATE_LOCATION,
        CREATE_PARK,
    }

    public static String getSelectionString(int itemId)
    {
        return Selection.values()[itemId].toString();
    }
}



