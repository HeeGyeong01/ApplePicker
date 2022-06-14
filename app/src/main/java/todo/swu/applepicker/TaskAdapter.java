package todo.swu.applepicker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;
import static todo.swu.applepicker.FragmentDaily.currentDate;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<TaskItem> taskList = null;
    TaskItem itemForListener;
    FirebaseFirestore db;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    TaskAdapter(ArrayList<TaskItem> list) {
        this.taskList = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 ViewHolder 객체 생성하여 리턴.
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_task_item, parent, false);
        db = FirebaseFirestore.getInstance();
        return new TaskAdapter.ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 ViewHolder의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        holder.onBind(taskList.get(position));
    }

    // 아이템 뷰를 저장하는 ViewHolder 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_subject;
        TextView tv_part;
        ImageView iv_apple;

        public ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조.
            tv_subject = (TextView) itemView.findViewById(R.id.tv_subject);
            tv_part = (TextView) itemView.findViewById(R.id.tv_part);
            iv_apple = (ImageView) itemView.findViewById(R.id.iv_apple);

            //사과(성취도) ImageView 리스너.
            iv_apple.setOnClickListener(view -> {
                itemForListener = taskList.get(getAdapterPosition());
                String timestamp = itemForListener.getTimestamp();
                //achievement 변수 switch해서 ImageView의 이미지 리소스 교체.
                itemForListener.switchAchievement();
                boolean achievement = itemForListener.getAchievement();
                iv_apple.setImageResource(getImage(achievement));

                Log.e(itemForListener.getSubject(), "itemForListener.getSubject() 반환값");
                Log.e(itemForListener.getTimestamp(), "itemForListener.getTimestamp() 반환값");
                Log.e(currentDate, "achievement 필드 업데이트: currentDate 값");
                //DB의 achievement필드 update.
                db.collection("daily").document(currentDate)
                    .collection("task").document(timestamp)
                    .update("achievement", achievement)
                    .addOnSuccessListener(documentReference -> {
                        Log.e(TAG, "DocumentSnapshot updated with ID: ");
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating document", e);
                    });
            });
        }

        void onBind(TaskItem item) {
            tv_subject.setText(item.getSubject());
            tv_part.setText(item.getPart());
            iv_apple.setImageResource(getImage(item.getAchievement()));
        }

        int getImage(boolean achievement) {
            if (achievement == false) {
                return R.drawable.ic_green_apple;
            } else if (achievement == true) {
                return R.drawable.ic_red_apple;
            }
            return 0;
        }
    }

    public void setPostList(ArrayList<TaskItem> list) {
        this.taskList = list;
        //notifyDataSetChanged();
    }

    // getItemCount() - 전체 데이터 개수 리턴.
    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
