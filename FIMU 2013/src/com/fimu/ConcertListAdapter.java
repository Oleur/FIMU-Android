package com.fimu;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fimu.database.MusicGroup;

/**
 * List adapter in order to show the basic information about the concert into the listview.
 * A list element is composed of a thumbnail, the group name, the scene, and the concert time.
 * A check box has been added in order to view the concert in a google map representation.
 * @author Julien Salvi
 */
public class ConcertListAdapter extends ArrayAdapter<MusicGroup> implements OnTouchListener {
	
	private List<MusicGroup> groupItems = null;
	private Activity context;
	private ViewHolder viewHolder;
	
	//Variables to detect the swipe gesture.
	private int padding = 0;
    private int initX = 0;
    private int currentX = 0;

	public ConcertListAdapter(Activity _context, List<MusicGroup> objects) {
		super(_context, R.layout.music_group_item_list, objects);
		this.context = _context;
		this.groupItems = objects;
	}
	
	static class ViewHolder {
        
		/** Position in the list view */
		protected int positionInList;
		
        /** The com text. */
        protected TextView nameGroupText, sceneText, concertTimeText;
        
        /** The img view. */
        protected ImageView imgView;
        
        /** The checkbox. */
        protected CheckBox checkbox;

		public void swipingItem(boolean swipe) {
			if (swipe) {
				imgView.setImageResource(R.drawable.ic_smenu_terms);
			} else {
				imgView.setImageResource(R.drawable.icon_fimu);
			}
			
		}
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.music_group_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.nameGroupText = (TextView) view.findViewById(R.id.textGroupName);
            viewHolder.nameGroupText.setTextColor(Color.BLACK);
            viewHolder.sceneText = (TextView) view.findViewById(R.id.textScene);
            viewHolder.sceneText.setTextColor(Color.BLACK);
            viewHolder.concertTimeText = (TextView) view.findViewById(R.id.textDateHour);
            viewHolder.concertTimeText.setTextColor(Color.BLACK);
            viewHolder.imgView = (ImageView) view.findViewById(R.id.imageViewGroup);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					MusicGroup elem = (MusicGroup) viewHolder.checkbox.getTag();
					elem.setChecked(buttonView.isChecked());
					if (elem.isChecked() == true) {
						System.out.println("Plop");
					} else {
						System.out.println("Elem unchecked");
					}
				}
			});
            viewHolder.positionInList = position;
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(groupItems.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(groupItems.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
        holder.nameGroupText.setText(groupItems.get(position).getGroupName());
        holder.sceneText.setText(context.getString(R.string.scene)+groupItems.get(position).getScene());
        //Let us check if is today or tomorrow.
        holder.concertTimeText.setText(isToday(groupItems.get(position).getDate())+" "+groupItems.get(position).getHour());
        holder.imgView.setImageResource(R.drawable.icon_fimu);
        holder.checkbox.setChecked(groupItems.get(position).isChecked());
        holder.positionInList = position;
        
        //Set the rounded shape of the view
        if (position == 0 && groupItems.size() == 1) {
        	view.setBackgroundResource(R.drawable.list_item_shape_one);
        } else if (position == 0) {
        	view.setBackgroundResource(R.drawable.list_item_shape_first);
        } else if (position == groupItems.size()-1) {
        	view.setBackgroundResource(R.drawable.list_item_shape_last);
        } else {
        	view.setBackgroundResource(R.drawable.list_item_shape_middle);
        }
        
        view.setOnTouchListener(this);
		return view;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<MusicGroup> getGroupItems() {
		return groupItems;
	}
	
	/**
	 * Check if the give date is today.
	 * @param date The given date.
	 * @return Return "today" if it is.
	 */
	public String isToday(String date) {
		String today = context.getString(R.string.today);
		Date date_now = new Date(System.currentTimeMillis());
		DateFormat.format("dd/MM/yyyy", date_now);
		
		if (date.equals(DateFormat.format("dd/MM/yyyy", date_now))) {
			return today;
		} else {
			return date;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN) {
            padding = 0;
            initX = (int) event.getX();
            currentX = (int) event.getX();
            viewHolder = ((ViewHolder) v.getTag());
        }
        if ( event.getAction() == MotionEvent.ACTION_MOVE) {
            currentX = (int) event.getX();
            padding = currentX - initX;
        }
        if ( (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
            padding = 0;
            initX = 0;
            currentX = 0;
            Toast.makeText(getContext(), "press", Toast.LENGTH_SHORT).show();
        }
        
        if(viewHolder != null) {
            if(padding > 200) {
                viewHolder.swipingItem(true);
            }
            if(padding < -75) {
                viewHolder.swipingItem(false);
            }
              
            viewHolder.nameGroupText = (TextView) v.findViewById(R.id.textGroupName);
            viewHolder.nameGroupText.setTextColor(Color.BLACK);
            viewHolder.sceneText = (TextView) v.findViewById(R.id.textScene);
            viewHolder.sceneText.setTextColor(Color.BLACK);
            viewHolder.concertTimeText = (TextView) v.findViewById(R.id.textDateHour);
            viewHolder.concertTimeText.setTextColor(Color.BLACK);
            
            //Set the rounded shape of the view
            if (viewHolder.positionInList == 0 && groupItems.size() == 1) {
            	v.setBackgroundResource(R.drawable.list_item_shape_one);
            } else if (viewHolder.positionInList == 0) {
            	v.setBackgroundResource(R.drawable.list_item_shape_first);
            } else if (viewHolder.positionInList == groupItems.size()-1) {
            	v.setBackgroundResource(R.drawable.list_item_shape_last);
            } else {
            	v.setBackgroundResource(R.drawable.list_item_shape_middle);
            }
            v.setPadding(padding+8, 8, 8, 8);
        }
        
		return true;
	}
	

}
