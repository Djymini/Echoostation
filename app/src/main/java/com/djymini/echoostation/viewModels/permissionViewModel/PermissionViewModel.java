package com.djymini.echoostation.viewModels.permissionViewModel;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.djymini.echoostation.helpers.PermissionManager;

public class PermissionViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isPermissionGranted = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> shouldShowRationale = new MutableLiveData<>(false);

    private final PermissionManager permissionManager;

    public PermissionViewModel(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public LiveData<Boolean> getIsPermissionGranted() {
        return isPermissionGranted;
    }

    public void setPermissionGranted(boolean granted) {
        isPermissionGranted.setValue(granted);
    }

    public LiveData<Boolean> getShouldShowRationale() {
        return shouldShowRationale;
    }

    public void checkPermission(Context context) {
        String permission = PermissionManager.getRequiredPermission();
        boolean granted = permissionManager.isPermissionGranted(permission);

        isPermissionGranted.setValue(granted);
        shouldShowRationale.setValue(ActivityCompat.shouldShowRequestPermissionRationale(
                (Activity) context,
                permission
        ));
    }

    public void requestPermission() {
        permissionManager.requestPermission(PermissionManager.getRequiredPermission());
    }

    public void checkAndRequestPermission(){
        permissionManager.checkAndRequestPermission();
    }
}
