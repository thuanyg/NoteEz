package com.thuanht.noteez.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void setText(String s){
        mutableLiveData.setValue(s);
    }
    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }
}
