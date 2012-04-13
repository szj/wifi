package com.redinput.wifi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.android.dataframework.DataFramework;

public class Main extends FragmentActivity implements
		DetailsFragment.OnDbChanged {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.main);

		try {
			DataFramework.getInstance().open(this, getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}

	@Override
	public void onAdd() {
		listNames fragment = (listNames) getSupportFragmentManager()
				.findFragmentById(R.id.names);
		fragment.updateData();
	}

	@Override
	public void onEdit() {
		listNames fragment = (listNames) getSupportFragmentManager()
				.findFragmentById(R.id.names);
		fragment.updateData();
	}

	@Override
	public void onDelete() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.remove(getSupportFragmentManager().findFragmentById(
				R.id.details));
		transaction.commit();

		listNames fragment = (listNames) getSupportFragmentManager()
				.findFragmentById(R.id.names);
		fragment.updateData();
	}
}
