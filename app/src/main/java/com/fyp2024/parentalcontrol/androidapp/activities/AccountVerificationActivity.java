package com.fyp2024.parentalcontrol.androidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.fyp2024.parentalcontrol.androidapp.R;
import com.fyp2024.parentalcontrol.androidapp.utils.LocaleUtils;

public class AccountVerificationActivity extends AppCompatActivity {
	private FirebaseAuth.AuthStateListener authStateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_verification);
		sendVerificationMessage();

		if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
			startActivity(new Intent(this, LoginActivity.class));

		final Button btnVerify = findViewById(R.id.btnVerify);
		startCountDownTimer(btnVerify);

		btnVerify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendVerificationMessage();
				startCountDownTimer(btnVerify);

			}
		});

		authStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null && user.isEmailVerified()) {
					// The user's email is verified, start the LoginActivity
					startLoginActivity();
				}
			}
		};

		// Add the AuthStateListener to the FirebaseAuth instance
		FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
	}
	private void startLoginActivity() {
		Intent intent = new Intent(AccountVerificationActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
	}
	private void startCountDownTimer(final Button btnVerify) {
		btnVerify.setEnabled(false);
		btnVerify.setClickable(false);

		new CountDownTimer(15000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				btnVerify.setText(String.format(getString(R.string.seconds_left), millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				btnVerify.setEnabled(true);
				btnVerify.setClickable(true);
				btnVerify.setText(R.string.resend_verification_email);

				// Check if the email is still not verified after timer finishes
				FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
				if (currentUser != null && !currentUser.isEmailVerified()) {
					// Redirect to LoginActivity after timer finishes if email is still not verified
					startLoginActivity();
				}
			}
		}.start();
	}
//	private void startCountDownTimer(final Button btnVerify) {
//		btnVerify.setEnabled(false);
//		btnVerify.setClickable(false);
//		new CountDownTimer(15000, 1000) {
//			@Override
//			public void onTick(long l) {
//				btnVerify.setText(String.valueOf(l / 1000));
//			}
//
//			@Override
//			public void onFinish() {
//				btnVerify.setEnabled(true);
//				btnVerify.setClickable(true);
//				btnVerify.setText(R.string.resend_verification_email);
//
//			}
//		}.start();

	private void sendVerificationMessage() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		FirebaseAuth.getInstance().setLanguageCode(LocaleUtils.getAppLanguage());
		user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Toast.makeText(AccountVerificationActivity.this, getString(R.string.verification_email_sent_it_may_be_within_your_drafts), Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(AccountVerificationActivity.this, "Failed to to send verification email", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
