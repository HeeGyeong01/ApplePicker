package todo.swu.applepicker;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * Use the {@link DndFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class DndFragment extends Fragment {

    Button Ocr_Next;

    public DndFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("jsonResponse", this,
            new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                    // We use a String here, but any type that can be put in a Bundle is supported
                    String result = bundle.getString("jsonResponse");
                    Log.e("TAG", "DndFragment 결과 받음.");
                    Log.e("result: ", result);
                    // Do something with the result...
                }
            });
        return inflater.inflate(R.layout.fragment_dnd, container, false);
    }
}