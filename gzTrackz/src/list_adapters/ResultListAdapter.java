package list_adapters;

import java.util.List;
import java.sql.Timestamp;

import list_objects.TimeLog;

import com.example.gztrackz.R;
import com.example.gztrackz.R.id;
import com.example.gztrackz.R.layout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ResultListAdapter extends BaseAdapter {
	private List<TimeLog> resultList;
	private Context context;
	
	public ResultListAdapter(Context context, List<TimeLog> resultList) {
		this.resultList = resultList;
		this.context = context;
	}
	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return resultList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.history_timein_timeout_item, parent,false);
		}
		
		TextView date = (TextView) convertView.findViewById(R.id.historydateitem);
		TextView timein = (TextView) convertView.findViewById(R.id.historytimeinitem);
		TextView timeout = (TextView) convertView.findViewById(R.id.historytimeoutitem);
		
		
		date.setText(resultList.get(position).getTimeIn().substring(0,11));
		int hour = Integer.parseInt(resultList.get(position).getTimeIn().substring(11,13));
		if(hour<12){
			timein.setText(resultList.get(position).getTimeIn().substring(11,resultList.get(position).getTimeIn().length()) + " AM");
		}else{
			if(hour!=12){
				hour-=12;
			}
			String timeTemp;
			if(hour<10){
				timeTemp = "0" + Integer.toString(hour);
			}else{
				timeTemp =Integer.toString(hour);
			}
			timein.setText(timeTemp+resultList.get(position).getTimeIn().substring(13,resultList.get(position).getTimeIn().length()) + " PM");
		}
		
		
		if(resultList.get(position).getTimeOut().length()>4){
			hour = Integer.parseInt(resultList.get(position).getTimeOut().substring(11,13));
			if(hour<12){
				timeout.setText(resultList.get(position).getTimeOut().substring(11,resultList.get(position).getTimeOut().length()) + " AM");
			}else{
				if(hour!=12){
					hour-=12;
				}
				String timeTemp;
				if(hour<10){
					timeTemp = "0" + Integer.toString(hour);
				}else{
					timeTemp =Integer.toString(hour);
				}
				timeout.setText(timeTemp+resultList.get(position).getTimeOut().substring(13,resultList.get(position).getTimeOut().length()) + " PM");
			}
		}else{
			timeout.setText("--:--:-- --");
		}
		
		
		return convertView;
	}

}
