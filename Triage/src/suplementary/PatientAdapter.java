package suplementary;

import java.util.ArrayList;
import com.example.triage.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Customized ArrayAdapter for the listview of AllPatients
 * @author arturomenacruz
 *
 */
public class PatientAdapter extends ArrayAdapter<Patient> {
	// View lookup cache
	private static class ViewHolder {
		TextView name;
		TextView healthCard;
	}

	/**
	 * Constructor
	 * @param context Context from the activity it was initialized on
	 * @param patient Patients to be displayed
	 */
	public PatientAdapter(Context context, ArrayList<Patient> patients) {
		super(context, R.layout.activity_all_patients_layout, patients);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		Patient patient = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(
					R.layout.activity_all_patients_layout, parent, false);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.name_display);
			viewHolder.healthCard = (TextView) convertView
					.findViewById(R.id.health_card);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// Populate the data into the template view using the data object
		viewHolder.name.setText(patient.getName());
		viewHolder.healthCard.setText(patient.getHealthCard());
		// Return the completed view to render on screen
		return convertView;

	}

	@Override
	public Patient getItem(int position) {
		return super.getItem(getCount() - 1 - position);
	}
}
