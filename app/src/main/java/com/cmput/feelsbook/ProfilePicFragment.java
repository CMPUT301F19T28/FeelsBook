package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

/**
 * Creates a new window where the user can choose between 6 pre-determined profile pictures.
 * OnPictureSelectedListener listener - used to relay information about which
 * profile picture was selected
 * long ONE_MEGABYTE - max number of bytes able to be rendered by the app
 * Bitmap profilePic1 - 6 - default profile pictures used by the app
 */
public class ProfilePicFragment extends DialogFragment {
    private OnPictureSelectedListener listener;
    private Bitmap picSelected;
    private final long ONE_MEGABYTE = 1024 * 1024;

    private Bitmap profilePic1;
    private Bitmap profilePic2;
    private Bitmap profilePic3;
    private Bitmap profilePic4;
    private Bitmap profilePic5;
    private Bitmap profilePic6;

    public interface OnPictureSelectedListener{
        void onPictureSelect(Bitmap picture);
    }

    /**
     * Checks to see if OnPictureSelectedListener is implemented
     * in the activity/fragment ProfilePicFragment is used in
     * @param context - the activity/fragment context where ProfilePicFragment is called from
     * @throws RuntimeException - throws exception when OnPictureSelectedListener is not
     * implemented.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfilePicFragment.OnPictureSelectedListener) {
            listener = (ProfilePicFragment.OnPictureSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnPictureSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_pic_fragment, null);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        picSelected = null;

        ImageButton profileBtn1  = view.findViewById(R.id.profile_pic_1);
        StorageReference defaultPic1 = storageRef.child("default_profile_pictures/app_default_profile_pic.png");
        defaultPic1.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data retrieval success, convert to a BitMap and set as user's profile pic
                profilePic1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileBtn1.setImageBitmap(profilePic1);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SignUp","Exception: "+exception);
                profileBtn1.setImageBitmap(null);
            }
        });
        profileBtn1.setOnClickListener(v -> {
            picSelected = profilePic1;
            listener.onPictureSelect(picSelected);
            dismiss();
        });

        ImageButton profileBtn2  = view.findViewById(R.id.profile_pic_2);
        StorageReference defaultPic2 = storageRef.child("default_profile_pictures/default_profile_pic2.png");
        defaultPic2.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data retrieval success, convert to a BitMap and set as user's profile pic
                profilePic2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileBtn2.setImageBitmap(profilePic2);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SignUp","Exception: "+exception);
                profileBtn2.setImageBitmap(null);
            }
        });
        profileBtn2.setOnClickListener(v -> {
            picSelected = profilePic2;
            listener.onPictureSelect(picSelected);
            dismiss();
        });

        ImageButton profileBtn3  = view.findViewById(R.id.profile_pic_3);
        StorageReference defaultPic3 = storageRef.child("default_profile_pictures/default_profile_pic3.png");
        defaultPic3.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data retrieval success, convert to a BitMap and set as user's profile pic
                profilePic3 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileBtn3.setImageBitmap(profilePic3);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SignUp","Exception: "+exception);
                profileBtn3.setImageBitmap(null);
            }
        });
        profileBtn3.setOnClickListener(v -> {
            picSelected = profilePic3;
            listener.onPictureSelect(picSelected);
            dismiss();
        });

        ImageButton profileBtn4  = view.findViewById(R.id.profile_pic_4);
        StorageReference defaultPic4 = storageRef.child("default_profile_pictures/default_profile_pic4.png");
        defaultPic4.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data retrieval success, convert to a BitMap and set as user's profile pic
                profilePic4 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileBtn4.setImageBitmap(profilePic4);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SignUp","Exception: "+exception);
                profileBtn4.setImageBitmap(null);
            }
        });
        profileBtn4.setOnClickListener(v -> {
            picSelected = profilePic4;
            listener.onPictureSelect(picSelected);
            dismiss();
        });

        ImageButton profileBtn5  = view.findViewById(R.id.profile_pic_5);
        StorageReference defaultPic5 = storageRef.child("default_profile_pictures/default_profile_pic5.png");
        defaultPic5.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data retrieval success, convert to a BitMap and set as user's profile pic
                profilePic5 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileBtn5.setImageBitmap(profilePic5);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SignUp","Exception: "+exception);
                profileBtn5.setImageBitmap(null);
            }
        });
        profileBtn5.setOnClickListener(v -> {
            picSelected = profilePic5;
            listener.onPictureSelect(picSelected);
            dismiss();
        });

        ImageButton profileBtn6  = view.findViewById(R.id.profile_pic_6);
        StorageReference defaultPic6 = storageRef.child("default_profile_pictures/default_profile_pic6.png");
        defaultPic6.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data retrieval success, convert to a BitMap and set as user's profile pic
                profilePic6 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileBtn6.setImageBitmap(profilePic6);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SignUp","Exception: "+exception);
                profileBtn6.setImageBitmap(null);
            }
        });
        profileBtn6.setOnClickListener(v -> {
            picSelected = profilePic6;
            listener.onPictureSelect(picSelected);
            dismiss();
        });

        // create dialog using FilterDialog theme
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.getContext().getTheme().applyStyle(R.style.FilterDialog, true);
        builder.setView(view);

        AlertDialog picChooseWindow = builder.create();
        picChooseWindow.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        picChooseWindow.show();

        return picChooseWindow;
    }
}
