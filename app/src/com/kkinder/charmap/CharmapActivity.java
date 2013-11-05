package com.kkinder.charmap;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CharmapActivity extends Activity {
	
	private class ButtonMapAdapter extends BaseAdapter {

		private int begin, end;
	    
	    public ButtonMapAdapter(int begin, int end) {
	        this.begin = begin;
	        this.end = end;
	    }

	    public int getCount() {
	        return end >= begin ? end - begin : 0;
	    }

	    public Character getItem(int position) {
	        return (char) (begin + position);
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	if (convertView == null) {
	            convertView = new Button(CharmapActivity.this);
	        }
	    	Button button = (Button) convertView;;
	    	
	    	button.setText(String.valueOf(getItem(position)));
	    	
	    	button.setOnClickListener(new OnClickListener() {
	    		@Override
	    	    public void onClick(View v) {
	    			CharSequence c = ((TextView) v).getText();
	    			if (editArea.getVisibility() == View.VISIBLE) {
	    			    editor.append(c);
	    			} else {
	        			finish(c.toString());
	    			}
	    	    }
	    	});
	        return convertView;
	    }
	}

	// End of inner classes
	
	private static final int DIALOG_ABOUT = 1;

	private Boolean batchMode;
	private int pageIndex;
	
	private Spinner sectionSpinner;
	private AutoCompleteTextView sectionSearchText;
	private View copyButton;
	private EditText editor;
	private GridView gridview;
	private LinearLayout editArea;
	private View searchToggleButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Restore preferences

		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		batchMode = settings.getBoolean("batchMode", false);
		pageIndex = settings.getInt("page", 0);

		updateBatchMode(batchMode);

		// Do the GUI stuff

		sectionSpinner = (Spinner) findViewById(R.id.sectionSpinner);
		sectionSearchText = (AutoCompleteTextView) findViewById(R.id.sectionSearchText);
		copyButton = findViewById(R.id.copyButton);
		editor = (EditText) findViewById(R.id.editor);
		gridview = (GridView) findViewById(R.id.gridView);
		editArea = (LinearLayout) findViewById(R.id.editArea);
		searchToggleButton = findViewById(R.id.searchToggleButton);

		// Copy button
		
		copyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish(editor.getText().toString());
			}
		});
		
		// Section choice spinner

		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, CharMap.charmapNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sectionSpinner.setAdapter(adapter);
		sectionSpinner.setSelection(pageIndex);
		sectionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> a, View v, int position, long id) {
				onSectionChoiceSelected(position);
			}
			public void onNothingSelected(AdapterView<?> a) {
			}
		});
		
		// Section search

		ArrayList<IndexEntry> charmapNameEntries = new ArrayList<IndexEntry>(CharMap.charmapNames.length);
		for (int i = 0; i < CharMap.charmapNames.length; ++i)
			charmapNameEntries.add(new IndexEntry(i, CharMap.charmapNames[i]));
		ArrayAdapter<IndexEntry> searchAdapter = new ArrayAdapter<IndexEntry>(
				this, android.R.layout.simple_dropdown_item_1line, charmapNameEntries);
		sectionSearchText.setAdapter(searchAdapter);
		sectionSearchText.setVisibility(View.GONE);
		sectionSearchText.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View view, int pos, long id) {
				sectionSpinner.setSelection(((IndexEntry) a.getItemAtPosition(pos)).index);
				setSectionSearchVisible(false);
			}
		});
		
		searchToggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleSectionSearch();
				sectionSearchText.requestFocus();
			}
		});
	}
	
	private void toggleSectionSearch() {
		setSectionSearchVisible(sectionSearchText.getVisibility() != View.VISIBLE);
	}
	
	private void setSectionSearchVisible(boolean visible) {
		if (visible) {
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
			sectionSearchText.setText(null);
			sectionSearchText.setVisibility(View.VISIBLE);
			sectionSearchText.startAnimation(anim);
			sectionSpinner.setVisibility(View.GONE);
		} else {
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
			sectionSearchText.setVisibility(View.GONE);
			sectionSearchText.startAnimation(anim);
			sectionSpinner.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		savePreferences();
	}

	public void savePreferences() {
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("batchMode", batchMode);
		editor.putInt("page", pageIndex);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (batchMode) {
			menu.findItem(R.id.menu_show_editor).setVisible(false);
			menu.findItem(R.id.menu_hide_editor).setVisible(true);
		} else {
			menu.findItem(R.id.menu_show_editor).setVisible(true);
			menu.findItem(R.id.menu_hide_editor).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			showDialogCompat(DIALOG_ABOUT);
			return true;
		case R.id.menu_show_editor:
			batchMode = true;
			updateBatchMode(true);
			return true;
		case R.id.menu_hide_editor:
			batchMode = false;
			updateBatchMode(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void invalidateOptionsMenuCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			super.invalidateOptionsMenu();
	}

	public void updateBatchMode(boolean batchMode) {
		if (batchMode) {
			editArea.setVisibility(View.VISIBLE);
			editArea.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up));
		} else {
			editArea.setVisibility(View.GONE);
			editArea.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down));
		}
		
		invalidateOptionsMenuCompat();
	}

	private void onSectionChoiceSelected(int index) {
		pageIndex = index;

		int begin = CharMap.charmaps[index][0];
		int end = CharMap.charmaps[index][1] + 1;
		gridview.setAdapter(new ButtonMapAdapter(begin, end));
	}

	@SuppressWarnings("deprecation")
	private void finish(String text) {
		((android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).
		setText(text);

		Toast.makeText(this, "\"" + editor.getText() + "\" copied to clipboard.",
				Toast.LENGTH_SHORT).show();
		finish();
	}
	
	@SuppressWarnings("deprecation")
	public void showDialogCompat(int id) {
		super.showDialog(id);
	}
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ABOUT) {
			final Dialog dialog = new Dialog(CharmapActivity.this);
			dialog.setContentView(R.layout.about);
			dialog.setTitle(R.string.about_title);
			dialog.setCancelable(true);
			
			TextView aboutText = (TextView) dialog.findViewById(R.id.AboutTextView);
			Button closeButton = (Button) dialog.findViewById(R.id.CloseButton);
			
			aboutText.setText(Html.fromHtml(getString(R.string.about)));
			aboutText.setMovementMethod(LinkMovementMethod.getInstance());
	
			closeButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					try {
						dismissDialog(DIALOG_ABOUT);
					} catch (RuntimeException e) {
						Log.e(CharmapActivity.class.getSimpleName(), e.toString());
					}
				}
			});
	
			return dialog;
			
		} else {
			return super.onCreateDialog(id);
		}
	}
}