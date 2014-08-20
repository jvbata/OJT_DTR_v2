package list_adapters;

import java.util.List;
import java.sql.Timestamp;

import list_objects.Standup;

import com.example.gztrackz.R;
import com.example.gztrackz.R.id;
import com.example.gztrackz.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StandUpDateAdapter extends BaseAdapter {
	private List<Standup> resultList;
	private Context context;
	
	public StandUpDateAdapter(Context context, List<Standup> resultList) {
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
			convertView = inflater.inflate(R.layout.standups_list_item, parent,false);
		}
		
		TextView standupDate = (TextView) convertView.findViewById(R.id.standupsdate);
		TextView standupPreviewMessage = (TextView) convertView.findViewById(R.id.standupspreviewmessage);
		TextView standupTime = (TextView) convertView.findViewById(R.id.standupstime);
		
		standupDate.setText(resultList.get(position).getDate().substring(0,11));
		standupTime.setText(resultList.get(position).getDate().substring(11));
		int hour = Integer.parseInt(resultList.get(position).getDate().substring(11,13));
		if(hour<12){
			standupTime.setText(resultList.get(position).getDate().substring(11,resultList.get(position).getDate().length()) + " AM");
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
			standupTime.setText(timeTemp+resultList.get(position).getDate().substring(13,resultList.get(position).getDate().length()) + " PM");
		}
		if(resultList.get(position).getStandup_todo().length()<=37){
			standupPreviewMessage.setText(resultList.get(position).getStandup_todo());
		}else{
			standupPreviewMessage.setText(resultList.get(position).getStandup_todo().substring(0,37)+ "...");
		}
		
		
		return convertView;
	}

}
