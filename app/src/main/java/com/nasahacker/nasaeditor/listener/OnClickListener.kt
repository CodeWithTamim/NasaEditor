package com.nasahacker.nasaeditor.listener

/**
 * A generic interface for handling click events.
 *
 * @param T The type of the data to be passed with the click event.
 */
interface OnClickListener<T> {

    /**
     * Called when an item is clicked.
     *
     * @param data The data associated with the click event.
     */
    fun onClick(data: T)

    /**
     * Called when an item is long-pressed. By default, it does nothing.
     *
     * @param data The data associated with the long press event.
     */
    fun onLongPress(data: T) {
        // Do nothing by default
    }
}
