package com.example.sqliteapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NoteFragment extends Fragment {

    private static final String TAG = "NoteFragment";

    ImageButton btnBack, btnDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);


        // Inflate a custom layout for the fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        // Initialize the UI elements
//        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        btnBack = view.findViewById(R.id.btn_back);
        btnDelete = view.findViewById(R.id.btn_delete);

        //btnBack
        btnBack.setOnClickListener(v -> {

            closeNote();

//            // Get the Uri of the default tone
//            int rawResourceId = R.raw.soft;
//            String rawResourceString = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
//                    getResources().getResourcePackageName(rawResourceId) + '/' +
//                    getResources().getResourceTypeName(rawResourceId) + '/' +
//                    getResources().getResourceEntryName(rawResourceId);
//            Uri rawResourceUri = Uri.parse(rawResourceString);
//
//            // Create an intent to open the ringtone picker to change the alarm tone of the app
//            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);//to choose from internal (ringtones) storage
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, rawResourceUri);
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
//
////                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI); //to choose from external storage
//
//            // Open the ringtone picker
//            mGetContent.launch(intent);
        });

        //btnDelete
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Direct to NotesFragment
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                // Animations. this has to be before fragmentTransaction.replace()
//                fragmentTransaction.setCustomAnimations(
//                        androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
//                        androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
//                        androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
//                        androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
//                );
//
//                fragmentTransaction.replace(R.id.fragment_container_notes, new NotesFragment());
//                fragmentTransaction.addToBackStack(null); // Optional, for back navigation
//
//                fragmentTransaction.commit();
//            }
//        });

        return view;
    }

    private void closeNote() {
        // Get the FragmentManager
//        assert getFragmentManager() != null; // Ensure that getFragmentManager() is not null
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Begin a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Animations. this has to be before fragmentTransaction.replace()
        fragmentTransaction.setCustomAnimations(
                androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
                androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
                androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
                androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
        );

        // Remove the current fragment from the container
        fragmentTransaction.remove(this);

        // Optionally, add the transaction to the back stack
        // This allows the user to navigate back to the previous fragment
        // fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }
}
