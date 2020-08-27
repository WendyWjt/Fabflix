package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListViewActivity extends Activity {

    private Button nextPageButton;
    private Button previousButton;
    public TextView page;
    private String searchString;
    public JSONArray moviesarray;
    public int pagenumber;
    private String url;
    public ArrayList<Movie> movies;
    public MovieListViewAdapter adapter;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        nextPageButton = findViewById(R.id.nextPage);
        previousButton = findViewById(R.id.previousPage);
        page = findViewById(R.id.pagenumber);
        pagenumber = 1;
//        url = "https://ec2-50-18-137-69.us-west-1.compute.amazonaws.com:8443/cs122b-spring20-project2-login-cart-example/api/";
        url = "https://ec2-50-18-137-69.us-west-1.compute.amazonaws.com:8443/cs122b-spring20-project2-login-cart-example/api/";
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String movies1 = bundle.getString("movies");
        searchString = bundle.getString("searchString");
        movies = new ArrayList<>();
        try {
            moviesarray = new JSONArray(movies1);
            int len = moviesarray.length();
            for (int i = (pagenumber-1)*20; i< Math.min(pagenumber*20,moviesarray.length()-(pagenumber-1)*20); i++) {
                //循环遍历，依次取出JSONObject对象
                //用getInt和getString方法取出对应键值
                JSONObject jsonObject = moviesarray.getJSONObject(i);
                movies.add(new Movie(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        page.setText(pagenumber+"");

        //this should be retrieved from the database and the backend server
        adapter = new MovieListViewAdapter(movies, this);

        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                Intent singleMovie = new Intent(ListViewActivity.this, SingleMovieActivity.class);

                //用Bundle携带数据
                Bundle bundle=new Bundle();
                bundle.putString("movie",movie.getJson());
                singleMovie.putExtras(bundle);
                //without starting the activity/page, nothing would happen
                startActivity(singleMovie);
            }
        });



        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getPagenumber()!=1){
                    int nowpage = getPagenumber();
                    nowpage--;
                    setPagenumber(nowpage);
                    final RequestQueue queue = NetworkManager.sharedManager(getParent()).queue;
                    final StringRequest nextPageRequest = new StringRequest(Request.Method.POST, url + "movieList", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<Movie> newmovies = new ArrayList<Movie>();
                            try {
                                JSONArray newmoviesarray = new JSONArray(response);
                                int len = newmoviesarray.length();
                                for (int i = 0; i< newmoviesarray.length(); i++) {
                                    //循环遍历，依次取出JSONObject对象
                                    //用getInt和getString方法取出对应键值
                                    JSONObject jsonObject = newmoviesarray.getJSONObject(i);
                                    newmovies.add(new Movie(jsonObject));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            setMovies(newmovies);
                            refreshAdapter();
                            refreshPage();
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("login.error", error.toString());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // Post request form data
                            final Map<String, String> params = new HashMap<>();
                            params.put("search","true");
                            params.put("fullTextSearchTitle", searchString);
                            params.put("offset",getPagenumber()-1+"");
                            params.put("itemNum",20+"");
                            return params;
                        }
                    };
                    queue.add(nextPageRequest);
                }
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSize()==20){
                    int nowpage = getPagenumber();
                    nowpage++;
                    setPagenumber(nowpage);
                    final RequestQueue queue = NetworkManager.sharedManager(getParent()).queue;
                    final StringRequest nextPageRequest = new StringRequest(Request.Method.POST, url + "movieList", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<Movie> newmovies = new ArrayList<Movie>();
                            try {
                                JSONArray newmoviesarray = new JSONArray(response);
                                int len = newmoviesarray.length();
                                for (int i = 0; i< newmoviesarray.length(); i++) {
                                    //循环遍历，依次取出JSONObject对象
                                    //用getInt和getString方法取出对应键值
                                    JSONObject jsonObject = newmoviesarray.getJSONObject(i);
                                    newmovies.add(new Movie(jsonObject));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            setMovies(newmovies);
                            refreshAdapter();
                            refreshPage();
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("login.error", error.toString());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // Post request form data
                            final Map<String, String> params = new HashMap<>();
                            params.put("search","true");
                            params.put("fullTextSearchTitle", searchString);
                            params.put("offset",getPagenumber()-1+"");
                            params.put("itemNum",20+"");
                            return params;
                        }
                    };
                    queue.add(nextPageRequest);
                }
            }
        });

    }

    public ArrayList getMovies(){
        return this.movies;
    }

    public void setMovies(ArrayList<Movie> movies){
        this.movies.clear();
        for(int i=0;i<movies.size();i++){
            this.movies.add(movies.get(i));
        }
    }

    public int getPagenumber(){
        return this.pagenumber;
    }

    public void setPagenumber(int pagenumber){
        this.pagenumber=pagenumber;
    }

    public void refreshAdapter(){
        adapter.notifyDataSetChanged();
    }

    public void refreshPage(){
        page.setText(getPagenumber()+"");
    }

    public int getSize(){
        return movies.size();
    }
}