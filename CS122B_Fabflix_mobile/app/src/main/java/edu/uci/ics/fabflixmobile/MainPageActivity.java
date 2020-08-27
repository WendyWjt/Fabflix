package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class MainPageActivity extends Activity {

    private TextView title;
    private Button Search;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.singlepage);
        title = findViewById(R.id.title);
        Search = findViewById(R.id.search_button);
        /**
         * In Android, localhost is the address of the device or the emulator.
         * To connect to your machine, you need to use the below IP address
         * **/
//        http://localhost:8080/cs122b_spring20_project2_fabflix_war/login.html
//        url = "https://ec2-50-18-137-69.us-west-1.compute.amazonaws.com:8443/cs122b-spring20-project2-login-cart-example/api/";
        url = "https://ec2-50-18-137-69.us-west-1.compute.amazonaws.com:8443/cs122b-spring20-project2-login-cart-example/api/";

        //assign a listener to call a function to handle the user request when clicking a button
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Searchfortitle();
            }
        });

        title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey (View v, int keyCode, KeyEvent event){
                if (keyCode == event.KEYCODE_ENTER) {
                    Searchfortitle();
                    return false;
                }
                return true;
            }
        });
    }

    public void Searchfortitle() {

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, url + "movieList", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("login.success", response);
                //initialize the activity(page)/destination
                Intent listPage = new Intent(MainPageActivity.this, ListViewActivity.class);

                //用Bundle携带数据
                Bundle bundle=new Bundle();
                bundle.putString("movies", response);
                bundle.putString("searchString",title.getText().toString());
                listPage.putExtras(bundle);
                //without starting the activity/page, nothing would happen
                startActivity(listPage);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("search","true");
                params.put("fullTextSearchTitle", title.getText().toString());

                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(loginRequest);

    }
}
