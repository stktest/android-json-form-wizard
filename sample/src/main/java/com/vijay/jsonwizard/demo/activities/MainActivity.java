package com.vijay.jsonwizard.demo.activities;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.demo.R;
import com.vijay.jsonwizard.demo.utils.CommonUtils;
import com.vijay.jsonwizard.utils.JsonFormUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by vijay on 5/16/15.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GET_JSON = 1;

    private static final String TAG = "MainActivity";
    private static final String DATA_JSON_PATH = "data.json";
    private static final String COMPLETE_JSON_PATH = "complete.json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JsonFormActivity.class);
                String json = CommonUtils
                        .loadJSONFromAsset(getApplicationContext(), DATA_JSON_PATH);
                intent.putExtra("json", json);
                intent.putExtra("resolver",
                        "com.vijay.jsonwizard.demo.expressions.AssetsContentResolver");
                intent.putExtra(JsonFormConstants.ORIENTATION_EXTRA, JsonFormConstants.ORIENTATION_LANDSCAPE);
                intent.putExtra(JsonFormConstants.CURRENT_ORIENTATION_EXTRA, JsonFormConstants.ORIENTATION_LANDSCAPE);
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                intent.putExtra(JsonFormConstants.CURRENT_ORIENTATION_EXTRA, rotation);

                //intent.putExtra(JsonFormConstants.INPUT_METHOD_EXTRA, JsonFormConstants.INPUT_METHOD_HIDDEN);
                startActivityForResult(intent, REQUEST_CODE_GET_JSON);
            }
        });
        findViewById(R.id.button_start_ro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JsonFormActivity.class);
                String json = CommonUtils
                        .loadJSONFromAsset(getApplicationContext(), COMPLETE_JSON_PATH);
                intent.putExtra("json", json);
                intent.putExtra(JsonFormConstants.VISUALIZATION_MODE_EXTRA,
                        JsonFormConstants.VISUALIZATION_MODE_READ_ONLY);
                startActivityForResult(intent, REQUEST_CODE_GET_JSON);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            String json = data.getStringExtra("json");
            Log.d(TAG, json);
            JSONObject result = extractDataFromForm(json);
            Log.d(TAG, result.toString());
        } else if (requestCode == REQUEST_CODE_GET_JSON
                && resultCode == JsonFormConstants.RESULT_JSON_PARSE_ERROR) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage((CharSequence) data.getData().toString());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private static JSONObject extractDataFromForm(String form) {
        try {
            return JsonFormUtils
                    .extractDataFromForm((JSONObject) new JSONTokener(form).nextValue());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON document", e);
        }
        return null;
    }
}
