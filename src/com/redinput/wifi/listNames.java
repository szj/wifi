package com.redinput.wifi;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class listNames extends ListFragment implements OnClickListener {
	boolean mDualPane;
	long mCurCheckPosition = 0;
	Button btnNew;
	List<Entity> listStr;
	SharedPreferences prefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle icicle) {
		View view = inflater.inflate(R.layout.list_names, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle icicle) {
		super.onActivityCreated(icicle);

		if (Build.VERSION.SDK_INT > 10) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.HC_or_ICS)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
									getActivity().finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}

		View detailsFrame = getActivity().findViewById(R.id.details);
		mDualPane = detailsFrame != null
				&& detailsFrame.getVisibility() == View.VISIBLE;

		if (icicle != null) {
			// Restore last state for checked position.
			mCurCheckPosition = icicle.getLong("curChoice", 0);
		}

		if (mDualPane) {
			// In dual-pane mode, the list view highlights the selected item.
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}

		btnNew = (Button) getActivity().findViewById(R.id.btnNew);
		btnNew.setOnClickListener(this);

		prefs = getActivity().getSharedPreferences("Wifi IP Chjanger",
				Context.MODE_PRIVATE);

		Boolean firstLaunch = prefs.getBoolean("firstLaunch", true);

		if (firstLaunch) {
			Toast toast = Toast.makeText(getActivity(), getActivity()
					.getResources().getString(R.string.remember),
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstLaunch", false);
			editor.commit();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		updateData();
	}

	public void updateData() {
		listStr = DataFramework.getInstance().getEntityList("profiles");

		setListAdapter(new ArrayAdapter<Entity>(getActivity(),
				R.layout.item_list, listStr));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("curChoice", mCurCheckPosition);
	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		showDetails(listStr.get(pos).getId(), pos);
	}

	/**
	 * Helper function to show the details of a selected item, either by
	 * displaying a fragment in-place in the current UI, or starting a whole new
	 * activity in which it is displayed.
	 */
	void showDetails(long id, int pos) {
		mCurCheckPosition = id;

		if (mDualPane) {
			// We can display everything in-place with fragments.
			// Have the list highlight this item and show the data.
			getListView().setItemChecked(pos, true);

			// Check what fragment is shown, replace if needed.
			DetailsFragment details = (DetailsFragment) getFragmentManager()
					.findFragmentById(R.id.details);
			if (details == null || details.getID() != id) {
				// Make new fragment to show this selection.
				details = DetailsFragment.newInstance(id);

				// Execute a transaction, replacing any existing
				// fragment with this one inside the frame.
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.details, details);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}

		} else {
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			Intent intent = new Intent();
			intent.setClass(getActivity(), DetailsActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNew:
			showDetails(-1, -1);

			break;
		}
	}
}
