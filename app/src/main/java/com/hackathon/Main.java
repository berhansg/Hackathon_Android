package com.hackathon;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Main extends Activity implements View.OnClickListener
{
    private EditText etEmail, etName, etPhone;
    private DrawShapes drawShapes;
    private TextView tvStatus;
    private RelativeLayout progressBar;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (RelativeLayout) findViewById(R.id.progressbar);
        send = (Button) findViewById(R.id.send);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        drawShapes = (DrawShapes) findViewById(R.id.drawShapes);

        send.setOnClickListener(this);

        progressBar.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);

    }

    private void hideSoftKeyboard()
    {
        View view = getCurrentFocus();
        if(view != null)
        {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.send:
                progressBar.setVisibility(View.VISIBLE);
                drawShapes.setElements(null);
                new SendForm().execute(
                        "https://getir-bitaksi-hackathon.herokuapp.com/getElements",
                        etEmail.getText().toString().trim(),
                        etName.getText().toString().trim(),
                        etPhone.getText().toString().trim()
                );
                break;
        }
    }

    private class SendForm extends AsyncTask<String, Void, Boolean>
    {
        JSONObject result;
        @Override
        protected Boolean doInBackground(String... strings)
        {
            if(strings.length != 4) return false;
            HashMap<String, String> params = new HashMap<>();
            params.put("email", strings[1]);
            params.put("name",  strings[2]);
            params.put("gsm",   strings[3]);
            result = getJSONFromUrl(strings[0], params);
            return result != null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            try
            {
                if(!isFinishing())
                {
                    progressBar.setVisibility(View.GONE);
                    if(aBoolean)
                    {
                        Log.e("HACK", result.toString());
                        Hackathon hackathon = new Gson().fromJson(result.toString(), Hackathon.class);
                        if(hackathon == null)
                        {
                            hackathon = new Hackathon();
                        }
                        if(hackathon.code != -1)
                        {
                            tvStatus.setText(hackathon.msg);
                            tvStatus.append(String.format(Locale.US, "\nInvite Code: %s", hackathon.inviteCode));
                            drawShapes.setElements(hackathon.elements);
                        }
                        else
                        {
                            tvStatus.setText(hackathon.msg);
                        }
                    }
                    else
                    {
                        tvStatus.setText("No Result");
                    }
                }
            }
            catch (Exception exp)
            {
                exp.printStackTrace();
            }
        }



        @SuppressWarnings("deprecation")
        public JSONObject getJSONFromUrl(String url, HashMap<String, String> params)
        {
            InputStream is = null;
            JSONObject jsonObject = null;
            String json = "";

            try
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                if(params != null && params.size() > 0)
                {
                    List<NameValuePair> nameValuePairs = new ArrayList<>(params.size());
                    for(String key: params.keySet())
                    {
                        nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
                    }
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                }
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            try
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    String s = line +  "\n";
                    sb.append(s);
                }
                is.close();
                json = sb.toString();
            }
            catch (Exception exp)
            {
                exp.printStackTrace();
            }

            try
            {
                jsonObject = new JSONObject(json);
            }
            catch (Exception exp)
            {
                exp.printStackTrace();
            }
            return jsonObject;
        }
    }
}
