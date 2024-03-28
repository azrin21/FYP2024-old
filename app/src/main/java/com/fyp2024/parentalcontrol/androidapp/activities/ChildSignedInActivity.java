package com.fyp2024.parentalcontrol.androidapp.activities;

import static com.fyp2024.parentalcontrol.androidapp.utils.Constant.SEND_SMS_PERMISSION_REQUEST_CODE;
import static com.fyp2024.parentalcontrol.androidapp.utils.generateTAC.generateRandomNumber;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.fyp2024.parentalcontrol.androidapp.R;
import com.fyp2024.parentalcontrol.androidapp.dialogfragments.InformationDialogFragment;
import com.fyp2024.parentalcontrol.androidapp.dialogfragments.PasswordValidationDialogFragment;
import com.fyp2024.parentalcontrol.androidapp.dialogfragments.PermissionExplanationDialogFragment;
import com.fyp2024.parentalcontrol.androidapp.interfaces.OnPasswordValidationListener;
import com.fyp2024.parentalcontrol.androidapp.interfaces.OnPermissionExplanationListener;
import com.fyp2024.parentalcontrol.androidapp.services.MainForegroundService;
import com.fyp2024.parentalcontrol.androidapp.utils.Constant;
import com.fyp2024.parentalcontrol.androidapp.utils.SharedPrefsUtils;
import com.fyp2024.parentalcontrol.androidapp.utils.Validators;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChildSignedInActivity extends AppCompatActivity implements OnPermissionExplanationListener, OnPasswordValidationListener {
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	FirebaseDatabase database = FirebaseDatabase.getInstance();

	public static final int JOB_ID = 38;
	public static final String CHILD_EMAIL = "childEmail";
	private static final String TAG = "ChildSignedInTAG";
	private FirebaseAuth auth;
	private FirebaseUser user;
	private ImageButton btnBack;
	private ImageButton btnSettings;
	private TextView txtTitle;
	private FrameLayout toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_signed_in);
		boolean childFirstLaunch = SharedPrefsUtils.getBooleanPreference(this, Constant.CHILD_FIRST_LAUNCH, true);


		auth = FirebaseAuth.getInstance();
		user = auth.getCurrentUser();

		DatabaseReference userRef = database.getReference().child("users").child("childs").child(user.getUid());

		userRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					// You can access its data here
					boolean verified = dataSnapshot.child("verified").getValue(boolean.class);
					String parentPhoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
					int tac = dataSnapshot.child("tac").getValue(Integer.class);
					if (verified) {
						Toast.makeText(ChildSignedInActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
						if (childFirstLaunch) {
							Intent intent = new Intent(ChildSignedInActivity.this, PermissionsActivity.class);
							startActivity(intent);
						} else {
							setupChildActivity();
						}

					} else {
						Toast.makeText(ChildSignedInActivity.this, "Child is NOT verified", Toast.LENGTH_SHORT).show();
						//redirect to TAC screen
//						String parentPhoneNumber = dataSnapshot.child("parentPhoneNumber").getValue(String.class);
//						int tac = generateRandomNumber();
						sendTacToParent(parentPhoneNumber, tac);

						setContentView(R.layout.tac);
						EditText tacEditText = findViewById(R.id.editTextTac);
						Button submitButton = findViewById(R.id.buttonSubmitTac);

						// Handle button click to submit the TAC
						submitButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								String enteredTacString = tacEditText.getText().toString().trim();
								if (!enteredTacString.isEmpty()) {
									try {
										int enteredTac = Integer.parseInt(enteredTacString);
										// Call a method to verify the entered TAC
										verifyTac(enteredTac);
									} catch (NumberFormatException e) {
										Toast.makeText(ChildSignedInActivity.this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();

									}
								}else {
									// Handle the case where the input string is empty
									Toast.makeText(ChildSignedInActivity.this, "Please enter a TAC.", Toast.LENGTH_SHORT).show();
								}

							}
						});
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Toast.makeText(ChildSignedInActivity.this, "Failed to get child", Toast.LENGTH_SHORT).show();
			}
		});

//		if (childFirstLaunch) startActivity(new Intent(this, PermissionsActivity.class));
//		else {
//
//			String email = user.getEmail();
//            /*PersistableBundle bundle = new PersistableBundle();
//            bundle.putString(CHILD_EMAIL, email);*/
//
//			toolbar = findViewById(R.id.toolbar);
//			btnBack = findViewById(R.id.btnBack);
//			btnBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_));
//			btnSettings = findViewById(R.id.btnSettings);
//			btnSettings.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					startPasswordValidationDialogFragment();
//				}
//			});
//			txtTitle = findViewById(R.id.txtTitle);
//			txtTitle.setText(getString(R.string.home));
//
//			//schedualJob(bundle);
//			startMainForegroundService(email);
//
//			if (!Validators.isLocationOn(this)) startPermissionExplanationDialogFragment();
//
//			if (!Validators.isInternetAvailable(this))
//				startInformationDialogFragment(getResources().getString(R.string.you_re_offline_ncheck_your_connection_and_try_again));
//
//		}

	}

	private void setupChildActivity() {
		String email = user.getEmail();
		toolbar = findViewById(R.id.toolbar);
		btnBack = findViewById(R.id.btnBack);
		btnBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_));
		btnSettings = findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startPasswordValidationDialogFragment();
			}
		});
		txtTitle = findViewById(R.id.txtTitle);
		txtTitle.setText(getString(R.string.home));

		startMainForegroundService(email);

		if (!Validators.isLocationOn(this)) startPermissionExplanationDialogFragment();

		if (!Validators.isInternetAvailable(this))
			startInformationDialogFragment(getResources().getString(R.string.you_re_offline_ncheck_your_connection_and_try_again));
	}

	private void sendTacToParent(String parentPhoneNumber, int tac) {
			if (parentPhoneNumber == null || parentPhoneNumber.isEmpty()) {
				Log.e(TAG, "Parent phone number is null or empty");
				return;
			}

			// Check if the device has permissions to send SMS
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
				Log.e(TAG, "SEND_SMS permission not granted, requesting permission...");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
				return;
			}

			try {
				String message = "Your child has requested access. The TAC code is " + tac;
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(parentPhoneNumber, null, message, null, null);
				Log.i(TAG, "SMS sent successfully to: " + parentPhoneNumber);
				Toast.makeText(this, "TAC has been sent to the parent's phone number.", Toast.LENGTH_SHORT).show();
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "Invalid destination address: " + e.getMessage());
				Toast.makeText(this, "Invalid destination address. Please provide a valid phone number.", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Log.e(TAG, "Error sending SMS: " + e.getMessage());
				Toast.makeText(this, "Failed to send SMS. Please try again.", Toast.LENGTH_SHORT).show();
			}
		}



//	private void sendTacToParent(String parentEmail) {
//		// Generate a random TAC
//		int tac = generateRandomNumber();
//
//		// Send TAC to parent's email
//		sendTacEmail(parentEmail, tac);
//
//		// Display message to child
//		Toast.makeText(this, "TAC has been sent to the parent's email.", Toast.LENGTH_SHORT).show();
//	}
//
//	private void sendTacEmail(String parentEmail, int tac) {
//		Map<String, Object> tacData = new HashMap<>();
//		tacData.put("tac", tac);
//
//		Map<String, Object> emailData = new HashMap<>();
//		emailData.put("to", Collections.singletonList(parentEmail)); // Use array for recipient(s)
//		emailData.put("subject", "TAC Code for Parental Control");
//		emailData.put("body", "Your child has requested access. The TAC code is " + tac);
//		emailData.put("tacData", tacData);
//
//		FirebaseFirestore.getInstance()
//				.collection("emails")
//				.add(emailData)
//				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//					@Override
//					public void onSuccess(DocumentReference documentReference) {
//						Log.d(TAG, "Email sent to: " + parentEmail);
//						// Display message to child
//						Toast.makeText(ChildSignedInActivity.this, "TAC has been sent to the parent's email.", Toast.LENGTH_SHORT).show();
//					}
//				})
//				.addOnFailureListener(new OnFailureListener() {
//					@Override
//					public void onFailure(@NonNull Exception e) {
//						Log.e(TAG, "Error sending email: " + e.getMessage());
//						// Display message to child
//						Toast.makeText(ChildSignedInActivity.this, "Failed to send TAC to parent's email. Please try again.", Toast.LENGTH_SHORT).show();
//					}
//				});
//	}
//
//	// Helper method to create the message data
//	private Map<String, Object> getMessageData(String parentEmail, int tac) {
//		Map<String, Object> messageData = new HashMap<>();
//		messageData.put("subject", "Your Child's Temporary Access Code (TAC)");
//		messageData.put("text", "Your child has requested a Temporary Access Code (TAC). The TAC is: " + tac);
//		// Optionally, you can include HTML content as well
//		// messageData.put("html", "HTML content here");
//
//		return messageData;
//	}

	private void verifyTac(int enteredTac) {
		// Get the reference to the Firebase database
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference userRef = database.getReference().child("users").child("childs").child(user.getUid());

		// Retrieve the expected TAC from the database
		userRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					// Get the expected TAC stored in the database
					int expectedTac = dataSnapshot.child("tac").getValue(Integer.class);

					// Compare the entered TAC with the expected TAC
					if (enteredTac == expectedTac) {
						// TAC is correct, mark the child as verified in the database
						userRef.child("verified").setValue(true)
								.addOnSuccessListener(new OnSuccessListener<Void>() {
									@Override
									public void onSuccess(Void aVoid) {
										// Child is now verified, proceed with the normal flow
										Toast.makeText(ChildSignedInActivity.this, "TAC verified. Child is now verified.", Toast.LENGTH_SHORT).show();
										// Proceed with the normal flow
										boolean childFirstLaunch = SharedPrefsUtils.getBooleanPreference(ChildSignedInActivity.this, Constant.CHILD_FIRST_LAUNCH, true);
										if (childFirstLaunch) {
											Intent intent = new Intent(ChildSignedInActivity.this, PermissionsActivity.class);
											startActivity(intent);
										} else {
											setupChildActivity();
										}

									}
								})
								.addOnFailureListener(new OnFailureListener() {
									@Override
									public void onFailure(@NonNull Exception e) {
										// Error handling
										Toast.makeText(ChildSignedInActivity.this, "Failed to update verification status. Please try again.", Toast.LENGTH_SHORT).show();
									}
								});
					} else {
						// Incorrect TAC, show error message
						Toast.makeText(ChildSignedInActivity.this, "Incorrect TAC. Please try again.", Toast.LENGTH_SHORT).show();
					}
				} else {
					// User data not found in the database
					Toast.makeText(ChildSignedInActivity.this, "Child data not found in the database.", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				// Error handling
				Toast.makeText(ChildSignedInActivity.this, "Database error occurred.", Toast.LENGTH_SHORT).show();
			}
		});
	}


	private void startMainForegroundService(String email) {
		Intent intent = new Intent(this, MainForegroundService.class);
		intent.putExtra(CHILD_EMAIL, email);
		ContextCompat.startForegroundService(this, intent);
		
	}
	
	private void startPermissionExplanationDialogFragment() {
		PermissionExplanationDialogFragment permissionExplanationDialogFragment = new PermissionExplanationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.PERMISSION_REQUEST_CODE, Constant.CHILD_LOCATION_PERMISSION_REQUEST_CODE);
		permissionExplanationDialogFragment.setArguments(bundle);
		permissionExplanationDialogFragment.setCancelable(false);
		permissionExplanationDialogFragment.show(getSupportFragmentManager(), Constant.PERMISSION_EXPLANATION_FRAGMENT_TAG);
	}
	
	private void startInformationDialogFragment(String message) {
		InformationDialogFragment informationDialogFragment = new InformationDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.INFORMATION_MESSAGE, message);
		informationDialogFragment.setArguments(bundle);
		informationDialogFragment.setCancelable(false);
		informationDialogFragment.show(getSupportFragmentManager(), Constant.INFORMATION_DIALOG_FRAGMENT_TAG);
	}
	
	private void startPasswordValidationDialogFragment() {
		PasswordValidationDialogFragment passwordValidationDialogFragment = new PasswordValidationDialogFragment();
		passwordValidationDialogFragment.setCancelable(false);
		passwordValidationDialogFragment.show(getSupportFragmentManager(), Constant.PASSWORD_VALIDATION_DIALOG_FRAGMENT_TAG);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.DEVICE_ADMIN_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "onActivityResult: DONE");
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	public void onOk(int requestCode) {
		startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
	
	@Override
	public void onCancel(int switchId) {
		Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	public void onValidationOk() {
		Intent intent = new Intent(ChildSignedInActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void schedualJob(PersistableBundle bundle) {
        ComponentName componentName = new ComponentName(this, UploadAppsService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .setExtras(bundle)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            //Success
        } else {
            //Failure
        }
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void cancelJob() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
        //Job cancelled
    }*/
	
	
}
