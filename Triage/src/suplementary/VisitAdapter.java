package suplementary;

import java.util.ArrayList;
import com.example.triage.R;
import com.example.triage.ViewVitalsActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ViewAdapter is the custom adapter for a ListView, specifically made for ViewPatientInfo.
 * @author Brandon Pon
 *
 */
public class VisitAdapter extends ArrayAdapter<Visit> {
	
	/**
	 * 
	 *	Create TextView element.
	 */
	private static class ViewHolder {
		TextView visit_name;
	}
	
	/**
	 * VisitAdapter constructor method.
	 * @param context Context
	 * @param values ArrayList value
	 */
	public VisitAdapter(Context context, ArrayList<Visit> values) {
		super(context, R.layout.activity_view_patient_info_layout, values);
	}
	
	/**
	 * 
	 * Builds the user interface for ViewPatientInfo ListView.
	 * @param position 
	 * @param convertView 
	 * @param parent 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		Visit visit = getItem(position);    
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.activity_view_patient_info_layout, parent, false);
			viewHolder.visit_name = (TextView) convertView.findViewById(R.id.visit_name);
			convertView.setTag(viewHolder);
	       } else {
	           viewHolder = (ViewHolder) convertView.getTag();
	       }
	       // Populate the data into the template view using the data object
	//       viewHolder.visit_name.setText(visit.getArrivalTime());
		
		int[] time = visit.getArrivalTime();
		viewHolder.visit_name.setText("Visit on " + time[0] + "/" + time[1]
						+ "/" + time[2] + " @ " + ViewVitalsActivity.timeToString(time[3], time[4]));

	       // Return the completed view to render on screen
	       return convertView;

	    }
} 

