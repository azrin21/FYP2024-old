package com.fyp2024.parentalcontrol.androidapp.dialogfragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.fyp2024.parentalcontrol.androidapp.R;
import com.fyp2024.parentalcontrol.androidapp.interfaces.OnPasswordValidationListener;
import com.fyp2024.parentalcontrol.androidapp.utils.Constant;
import com.fyp2024.parentalcontrol.androidapp.utils.SharedPrefsUtils;

public class PasswordValidationDialogFragment extends DialogFragment {
	private EditText txtValidationPassword;
	private Button btnValidation;
	private Button btnCancelValidation;
	private OnPasswordValidationListener onPasswordValidationListener;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		return dialog;
	}
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.fragment_dialog_password_validation, container, false);
		View view = inflater.inflate(R.layout.fragment_dialog_password_validation, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onPasswordValidationListener = (OnPasswordValidationListener) getActivity();
//		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		txtValidationPassword = view.findViewById(R.id.txtValidationPassword);
		btnValidation = view.findViewById(R.id.btnValidation);
		btnCancelValidation = view.findViewById(R.id.btnCancelValidation);
		final String passwordPrefs = SharedPrefsUtils.getStringPreference(getContext(), Constant.PASSWORD, "");
		
		btnValidation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (txtValidationPassword.getText().toString().equals(passwordPrefs)) {
					onPasswordValidationListener.onValidationOk();
					dismiss();
				} else {
					txtValidationPassword.requestFocus();
					txtValidationPassword.setError(getString(R.string.wrong_password));
				}
				
			}
		});
		
		
		btnCancelValidation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		
		
	}
}
