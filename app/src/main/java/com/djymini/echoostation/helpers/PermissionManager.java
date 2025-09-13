package com.djymini.echoostation.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.viewModels.permissionViewModel.PermissionViewModel;

public class PermissionManager {
    private final Activity activity;
    private ActivityResultLauncher<String> permissionLauncher;
    private final Runnable onPermissionGranted;
    private final Runnable onPermissionDenied;

    public PermissionManager(Activity activity, ActivityResultLauncher<String> permissionLauncher, Runnable onPermissionGranted, Runnable onPermissionDenied) {
        this.activity = activity;
        this.permissionLauncher = permissionLauncher;
        this.onPermissionGranted = onPermissionGranted;
        this.onPermissionDenied = onPermissionDenied;

        this.permissionLauncher = ((MainActivity) activity)
                .registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    PermissionViewModel vm = new ViewModelProvider((MainActivity) activity)
                            .get(PermissionViewModel.class);
                    vm.setPermissionGranted(isGranted);

                    if (isGranted) {
                        onPermissionGranted.run();
                    } else {
                        if (onPermissionDenied != null) onPermissionDenied.run();
                    }
                });
    }

    public ActivityResultLauncher<String> getPermissionLauncher() {
        return permissionLauncher;
    }

    public void checkAndRequestPermission() {
        String permission = getRequiredPermission();

        if (isPermissionGranted(permission)) {
            onPermissionGranted.run();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            showRationaleDialog(permission);
        } else {
            requestPermission(permission);
        }
    }

    public static String getRequiredPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Constants.PERMISSION_READ_MEDIA_AUDIO;
        } else {
            return Constants.PERMISSION_READ_EXTERNAL_STORAGE;
        }
    }

    public boolean isPermissionGranted() {
        return isPermissionGranted(getRequiredPermission());
    }

    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void showRationaleDialog(String permission) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.permission_required_title)
                .setMessage(R.string.permission_required_message)
                .setPositiveButton(R.string.permission_allow, (dialog, which) -> requestPermission(permission))
                .setNegativeButton(R.string.permission_deny, (dialog, which) -> onPermissionDenied.run())
                .show();
    }

    public void requestPermission(String permission) {
        permissionLauncher.launch(permission);
    }

    public void registerPermissionLauncher() {
        permissionLauncher = ((MainActivity) activity)
                .registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    PermissionViewModel vm = new ViewModelProvider((MainActivity) activity)
                            .get(PermissionViewModel.class);
                    vm.setPermissionGranted(isGranted);

                    if (isGranted) onPermissionGranted.run();
                });
    }

    public void checkPermission(RelativeLayout authorizationLayout, ConstraintLayout appLayout, Context context){
        String permission = getRequiredPermission();

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            authorizationLayout.setVisibility(View.GONE);
            appLayout.setVisibility(View.VISIBLE);
            onPermissionGranted.run();
        } else {
            authorizationLayout.setVisibility(View.VISIBLE);
            appLayout.setVisibility(View.GONE);
        }
    }
}
