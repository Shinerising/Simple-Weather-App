package us.wayshine.apollo.myweather;

/**
 * Created by Apollo on 9/26/15.
 */
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView
        .Adapter<MyAdapter
        .DataObjectHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;
    private Context mContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        CardView card;
        TextView label_city;
        TextView label_temp;
        TextView label_weather;
        TextView image_weather;
        ImageView image_cover;

        public DataObjectHolder(View itemView, int ID) {
            super(itemView);
            card = (CardView)itemView.findViewById(R.id.card);
            label_city = (TextView) itemView.findViewById(R.id.card_city);
            label_temp = (TextView) itemView.findViewById(R.id.card_temper);
            label_weather = (TextView) itemView.findViewById(R.id.card_weather);
            image_weather = (TextView) itemView.findViewById(R.id.card_image);
            image_cover = (ImageView) itemView.findViewById(R.id.card_cover);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        MyAdapter.myClickListener = myClickListener;
    }

    public MyAdapter(ArrayList<DataObject> myDataset, Context context) {
        mContext = context;
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);

        return new DataObjectHolder(view, getItemCount());
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label_city.setText(mDataset.get(position).getCity());
        holder.label_temp.setText(mDataset.get(position).getTemp());
        holder.label_weather.setText(mDataset.get(position).getWeather());
        holder.image_weather.setText(mDataset.get(position).getAlterImage());
        holder.image_cover.setImageResource(mDataset.get(position).getCover());
        MyAnimator.deflateFadeIn(holder.card, 0);
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void updateItem(DataObject dataObj, int index) {
        mDataset.set(index, dataObj);
        notifyItemChanged(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public DataObject getDataObject(int index) {
        return mDataset.get(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }


}