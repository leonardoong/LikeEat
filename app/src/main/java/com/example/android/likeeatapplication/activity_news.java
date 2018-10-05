package com.example.android.likeeatapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.android.likeeatapplication.API.ApiClient;
import com.example.android.likeeatapplication.API.ApiInterface;
import com.example.android.likeeatapplication.Adapter.NewsAdapter;
import com.example.android.likeeatapplication.Model.Article;
import com.example.android.likeeatapplication.Model.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class activity_news extends AppCompatActivity {

    public static final String API_KEY = "0842a69b4b0446b79043c23ec61e603f";
    public static final String Category = "health";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private NewsAdapter adapter;
    private String TAG = activity_news.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


            recyclerView = findViewById(R.id.recyclerView1);
            layoutManager = new LinearLayoutManager(activity_news.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);

            LoadJson();
            }

            public void LoadJson(){
                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                String country  = Utils.getCountry();

                Call<News> call;
                call = apiInterface.getNews(country, API_KEY,Category);

                call.enqueue(new Callback<News>() {
                    @Override
                    public void onResponse(Call<News> call, Response<News> response) {
                        if (response.isSuccessful() && response.body().getArticle() != null){
                            if (!articles.isEmpty()){
                                articles.clear();
                            }
                            articles = response.body().getArticle();
                            adapter = new NewsAdapter(articles, activity_news.this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }else{
                            Toast.makeText(activity_news.this,"No Result",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<News> call, Throwable t) {

                    }
                });
            }
}
