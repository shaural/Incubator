package cs408.incubator;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

class IdeaItemAdapter extends DragItemAdapter<Pair<Long, String>, IdeaItemAdapter.ViewHolder> {

    private int mLayoutId;
    private boolean mDragOnLongPress;

    IdeaItemAdapter(ArrayList<Pair<Long, String>> list, int layoutId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String text = mItemList.get(position).second;
        String[] info = text.split( "-" );
        holder.mText.setText(info[0]);
        holder.mText.setTag(info[1]);
        holder.itemView.setTag(mItemList.get(position));
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView,R.id.itemLayout, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.ideaTitle);
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), mText.getText()+" "+ mText.getTag()+ " clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}

