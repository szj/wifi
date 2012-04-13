package com.redinput.wifi;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DetailsActivity extends FragmentActivity implements
		DetailsFragment.OnDbChanged {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

		if (savedInstanceState == null) {
			// During initial setup, plug in the details fragment.
			DetailsFragment details = new DetailsFragment();
			details.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, details).commit();
		}
	}

	@Override
	public void onAdd() {

	}

	@Override
	public void onEdit() {

	}

	@Override
	public void onDelete() {
		finish();
	}
	
	

}
