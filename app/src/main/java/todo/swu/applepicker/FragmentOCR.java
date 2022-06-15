package todo.swu.applepicker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentOCR extends Fragment {
    Button galleryBtn;
    Button cameraBtn;
    ImageView ocrImageView;

    private Fragment fa, fb, fc;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View myView = inflater.inflate(R.layout.fragment_ocr, container, false);
        galleryBtn = (Button) myView.findViewById(R.id.galleryBtn);
        cameraBtn = (Button) myView.findViewById(R.id.cameraBtn);
        ocrImageView = (ImageView) myView.findViewById(R.id.ocrImageView);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //앨범에서 이미지 가져오기
                startActivityResult.launch("image/*");
            }
        });

        cameraBtn.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "카메라 버튼 누름", Toast.LENGTH_SHORT).show();
        });

        return myView; // Inflate the layout for this fragment
    }

    ActivityResultLauncher<String> startActivityResult = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    //ocrImageView.setImageURI(result);
                    String imagePath = getPathFromUri(getActivity(), result);
                    Log.e(imagePath, "URI에서 변환한 이미지의 절대경로");
                    Log.e(result.toString(), "선택한 이미지 파일의 URI 출력");

                    NetworkTask networkTask = new NetworkTask(imagePath);
                    networkTask.execute();
                }
                if (result == null) {
                    Log.d(this.getClass().getName(), "사진의 URI값이 null입니다.");
                }
            }
        }
    );

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        String imagePath;

        NetworkTask(String path) {
            imagePath = path;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            String response = requestHttpURLConnection.SendImage(imagePath);
            return response;
        }

        @Override
        //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어온다.
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            //DndFragment로 화면 전환
            DndFragment dndFragment = new DndFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, dndFragment);
            transaction.commit();

            // DndFramgment로 response 전달
            Bundle bundle = new Bundle();
            bundle.putString("jsonResponse", response);
            Log.e("Bundle is null?", String.valueOf(bundle));

            getParentFragmentManager().setFragmentResult("jsonResponse", bundle);
        }
    }

    //URI값을 절대 경로(Real path)로 바꿔주는 메소드
    public static String getPathFromUri(Activity ctx, Uri fileUri) {
        String path = null;
        final String column = "_data";
        Cursor cursor = ctx.getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            if (document_id == null) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (column.equalsIgnoreCase(cursor.getColumnName(i))) {
                        path = cursor.getString(i);
                        break;
                    }
                }
            } else {
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                final String[] projection = {column};
                try {
                    cursor = ctx.getContentResolver().query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        path = cursor.getString(cursor.getColumnIndexOrThrow(column));
                    }
                } finally {
                    if (cursor != null) cursor.close();
                }
            }
        }
        return path;
    }
}


