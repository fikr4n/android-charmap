package com.kkinder.charmap;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ButtonMapAdapter extends BaseAdapter {
	
    private Activity mContext;
    private String[] characters = {};
    private View appEditArea;
    private EditText appEditor;
    
    public ButtonMapAdapter(Activity context, View editArea, EditText editor, String[] chars) {
        mContext = context;
        characters = chars;
        appEditArea = editArea;
        appEditor = editor;
    }

    public int getCount() {
        return characters.length;
    }

    public Object getItem(int position) {
        return characters[position];
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ButtonView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Button button;
        
    	if (convertView == null) {
            button = new Button(mContext);
        } else {
        	button = (Button) convertView;
        }
    	button.setText(characters[position]);
    	
    	button.setOnClickListener(new OnClickListener() {
    		@Override
    	    public void onClick(View v) {
    			if (appEditArea.getVisibility() == View.VISIBLE) {
    			    appEditor.append(((Button)v).getText());
    			} else {
        			ClipboardManager ClipMan = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        			ClipMan.setText(((Button)v).getText());
        			
        			CharSequence text = "\"" + ((Button)v).getText() + "\" copied to clipboard.";
        			int duration = Toast.LENGTH_SHORT;

        			Toast toast = Toast.makeText(mContext, text, duration);
        			toast.show();
        			mContext.finish();
    			}
    	    }
    	});
        return button;
    }
}
