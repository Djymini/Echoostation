package com.djymini.echoostation.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareSearchViewModel extends ViewModel {
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    public void setQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<String> getQuery() {
        return searchQuery;
    }
}
