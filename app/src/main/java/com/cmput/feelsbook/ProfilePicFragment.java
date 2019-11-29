package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ProfilePicFragment extends DialogFragment {
    private OnPictureSelectedListener listener;
    private Bitmap picSelected;

    public interface OnPictureSelectedListener{
        public void onPictureSelect(Bitmap picture);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_pic_fragment, null);

        picSelected = null;
        ImageButton profilePic1  = view.findViewById(R.id.profile_pic_1);
        profilePic1.setOnClickListener(v -> {
            picSelected = ((BitmapDrawable)profilePic1.getDrawable()).getBitmap();
            listener.onPictureSelect(picSelected);
        });

        ImageButton profilePic2  = view.findViewById(R.id.profile_pic_2);
        profilePic2.setOnClickListener(v -> {
            picSelected = ((BitmapDrawable)profilePic2.getDrawable()).getBitmap();
            listener.onPictureSelect(picSelected);
        });

        ImageButton profilePic3  = view.findViewById(R.id.profile_pic_3);
        profilePic3.setOnClickListener(v -> {
            picSelected = ((BitmapDrawable)profilePic3.getDrawable()).getBitmap();
            listener.onPictureSelect(picSelected);
        });

        ImageButton profilePic4  = view.findViewById(R.id.profile_pic_4);
        profilePic4.setOnClickListener(v -> {
            picSelected = ((BitmapDrawable)profilePic4.getDrawable()).getBitmap();
            listener.onPictureSelect(picSelected);
        });

        ImageButton profilePic5  = view.findViewById(R.id.profile_pic_5);
        profilePic5.setOnClickListener(v -> {
            picSelected = ((BitmapDrawable)profilePic5.getDrawable()).getBitmap();
            listener.onPictureSelect(picSelected);
        });

        ImageButton profilePic6  = view.findViewById(R.id.profile_pic_6);
        profilePic6.setOnClickListener(v -> {
            picSelected = ((BitmapDrawable)profilePic6.getDrawable()).getBitmap();
            listener.onPictureSelect(picSelected);
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
