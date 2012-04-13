package com.redinput.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dataframework.Entity;

public class DetailsFragment extends Fragment implements OnClickListener {
	static final int DIALOG_NAME = 1;
	OnDbChanged mListener;

	TextView txtTitle;
	EditText editIP, editGateway, editNetmask, editDNS1, editDNS2;
	Button btnSave, btnCancel, btnDelete, btnApply;
	Entity rowDB;
	ImageButton btnTitle;

	public static DetailsFragment newInstance(long index) {
		DetailsFragment f = new DetailsFragment();
		Bundle args = new Bundle();
		args.putLong("id", index);
		args.putBoolean("fragment", true);
		f.setArguments(args);

		return f;
	}

	public long getID() {
		return getArguments().getLong("id", 0);
	}

	public Boolean isFragment() {
		return getArguments().getBoolean("fragment", false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle icicle) {
		if (container == null) {
			return null;
		}
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		View view = inflater.inflate(R.layout.detail_profile, null);
		InputFilter[] filters = new InputFilter[] { filter };

		txtTitle = (TextView) view.findViewById(R.id.txtTitle);

		editIP = (EditText) view.findViewById(R.id.editIP);
		editIP.setFilters(filters);

		editGateway = (EditText) view.findViewById(R.id.editGateway);
		editGateway.setFilters(filters);

		editNetmask = (EditText) view.findViewById(R.id.editNetmask);
		editNetmask.setFilters(filters);

		editDNS1 = (EditText) view.findViewById(R.id.editDNS1);
		editDNS1.setFilters(filters);

		editDNS2 = (EditText) view.findViewById(R.id.editDNS2);
		editDNS2.setFilters(filters);

		btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

		btnDelete = (Button) view.findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(this);

		btnApply = (Button) view.findViewById(R.id.btnApply);
		btnApply.setOnClickListener(this);

		btnTitle = (ImageButton) view.findViewById(R.id.btnTitle);
		btnTitle.setOnClickListener(this);

		if (getID() == -1) {
			rowDB = new Entity("profiles");
			pedirNombre();
			btnDelete.setVisibility(View.GONE);
			btnApply.setVisibility(View.GONE);
		} else {
			rowDB = new Entity("profiles", getID());
		}

		txtTitle.setText(rowDB.getString("name"));
		editIP.setText(rowDB.getString("ip"));
		editGateway.setText(rowDB.getString("gateway"));
		editNetmask.setText(rowDB.getString("netmask"));
		editDNS1.setText(rowDB.getString("dns1"));
		editDNS2.setText(rowDB.getString("dns2"));

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDbChanged) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnDbChanged");
		}
	}

	private void pedirNombre() {
		final EditText input = new EditText(getActivity());
		new AlertDialog.Builder(getActivity())
				.setMessage(R.string.dialog_name)
				.setView(input)
				.setNeutralButton(R.string.save,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString();

								rowDB.setValue("name", value);
								txtTitle.setText(value);
							}
						}).show();
	}

	InputFilter filter = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			if (end > start) {
				String destTxt = dest.toString();
				String resultingTxt = destTxt.substring(0, dstart)
						+ source.subSequence(start, end)
						+ destTxt.substring(dend);
				if (!resultingTxt
						.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
					return "";
				} else {
					String[] splits = resultingTxt.split("\\.");
					for (int i = 0; i < splits.length; i++) {
						if (Integer.valueOf(splits[i]) > 255) {
							return "";
						}
					}
				}
			}
			return null;
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			rowDB.setValue("name", txtTitle.getText().toString());
			rowDB.setValue("ip", editIP.getText().toString());
			rowDB.setValue("gateway", editGateway.getText().toString());
			rowDB.setValue("netmask", editNetmask.getText().toString());
			rowDB.setValue("dns1", editDNS1.getText().toString());
			rowDB.setValue("dns2", editDNS2.getText().toString());

			rowDB.save();

			Toast.makeText(getActivity(),
					getResources().getString(R.string.saved),
					Toast.LENGTH_SHORT).show();

			if (getID() == -1) {
				btnDelete.setVisibility(View.VISIBLE);
				btnApply.setVisibility(View.VISIBLE);
				mListener.onAdd();
			} else {
				mListener.onEdit();
			}

			break;

		case R.id.btnCancel:

			txtTitle.setText(rowDB.getString("name"));
			editIP.setText(rowDB.getString("ip"));
			editGateway.setText(rowDB.getString("gateway"));
			editNetmask.setText(rowDB.getString("netmask"));
			editDNS1.setText(rowDB.getString("dns1"));
			editDNS2.setText(rowDB.getString("dns2"));

			Toast.makeText(getActivity(),
					getResources().getString(R.string.restored),
					Toast.LENGTH_SHORT).show();

			break;

		case R.id.btnDelete:
			rowDB.delete();

			Toast.makeText(getActivity(),
					getResources().getString(R.string.deleted),
					Toast.LENGTH_SHORT).show();
			mListener.onDelete();

			break;

		case R.id.btnApply:
			btnSave.performClick();

			android.provider.Settings.System.putString(getActivity()
					.getContentResolver(),
					android.provider.Settings.System.WIFI_USE_STATIC_IP, "1");
			android.provider.Settings.System.putString(getActivity()
					.getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_IP, rowDB
							.getString("ip"));
			android.provider.Settings.System.putString(getActivity()
					.getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_GATEWAY, rowDB
							.getString("gateway"));
			android.provider.Settings.System.putString(getActivity()
					.getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_NETMASK, rowDB
							.getString("netmask"));

			android.provider.Settings.System.putString(getActivity()
					.getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_DNS1, rowDB
							.getString("dns1"));
			android.provider.Settings.System.putString(getActivity()
					.getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_DNS2, rowDB
							.getString("dns2"));

			Toast.makeText(getActivity(),
					"Aplicado perfil " + rowDB.getString("name"),
					Toast.LENGTH_SHORT).show();

			break;

		case R.id.btnTitle:
			pedirNombre();

			break;
		}
	}

	public interface OnDbChanged {
		public void onAdd();

		public void onEdit();

		public void onDelete();
	}
}
