package com.nasahacker.nasaeditor.listener

interface OnClickListener<T> {
    fun onClick(data: T)
    fun onLongPress(data: T){

    }
}