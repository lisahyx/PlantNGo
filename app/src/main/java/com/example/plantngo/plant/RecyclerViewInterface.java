package com.example.plantngo.plant;

/**
 * Interface to handle item click events in a RecyclerView.
 */
public interface RecyclerViewInterface {

    /**
     * Called when an item in the RecyclerView is clicked.
     *
     * @param position   The position of the clicked item.
     * @param plantName  The name of the plant associated with the clicked item.
     */
    void onItemClick(int position, String plantName);
}
