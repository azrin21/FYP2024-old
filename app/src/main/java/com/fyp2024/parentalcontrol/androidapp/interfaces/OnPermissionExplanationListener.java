package com.fyp2024.parentalcontrol.androidapp.interfaces;

public interface OnPermissionExplanationListener {
    void onOk(int requestCode);

    void onCancel(int switchId);
}
