package ca.huynhat.itsasteal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ca.huynhat.itsasteal.R;

public class SelectPhotoDialog extends BottomSheetDialogFragment {

    private static final int PICKFILE_REQUEST_CODE = 1234;
    private static final int CAMERA_REQUEST_CODE = 4321;

    public interface OnPhotoSelectedListner{
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }

    OnPhotoSelectedListner onPhotoSelectedListner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout,container,false);

        //Take photo
        LinearLayout takePic = (LinearLayout) view.findViewById(R.id.fragment_history_bottom_sheet_takePic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST_CODE);
            }
        });


        //Choose photo
        LinearLayout choosePic = (LinearLayout) view.findViewById(R.id.fragment_history_bottom_sheet_choosePic);
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,PICKFILE_REQUEST_CODE);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICKFILE_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri selectedImageURI = data.getData();

            //Send to Sell Fragment
            onPhotoSelectedListner.getImagePath(selectedImageURI);
            getDialog().dismiss();

        }else if (requestCode == CAMERA_REQUEST_CODE && resultCode== Activity.RESULT_OK){

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            //Send this bitmap to Sell Fragment
            onPhotoSelectedListner.getImageBitmap(bitmap);
            getDialog().dismiss();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onPhotoSelectedListner = (OnPhotoSelectedListner) getActivity();
        }catch (ClassCastException e){
            e.getMessage();
        }


    }
}
