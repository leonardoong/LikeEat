package com.example.android.likeeatapplication;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.support.v7.widget.SearchView;
import android.widget.ImageView;
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


public class activity_news extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String API_KEY = "0842a69b4b0446b79043c23ec61e603f";
    public static final String Category = "health";
//    public static final String source = "bbc-food";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private NewsAdapter adapter;
    private String TAG = activity_news.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

            swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent);


            recyclerView = findViewById(R.id.recyclerView1);
            layoutManager = new LinearLayoutManager(activity_news.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);

            onloadingSwipeRefrehs("");
            }

            public void LoadJson(final String keyword){

                swipeRefreshLayout.setRefreshing(true);
                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                String country  = Utils.getCountry();


                Call<News> call;

                if(keyword.length()>0){
                    call = apiInterface.getNewsSearch(keyword,country,"publishedAt",API_KEY);
                }else{
                    call = apiInterface.getNews(country, API_KEY,Category);
                }

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

                            swipeRefreshLayout.setRefreshing(false);

                            initListener();

                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(activity_news.this,"No Result",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<News> call, Throwable t) {

                    }
                });
            }

           private void initListener(){


        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ImageView imageView = view.findViewById(R.id.img_news);
                Intent intent = new Intent(activity_news.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url",article.getUrl());
                intent.putExtra("title",article.getTitle());
                intent.putExtra("img_news",article.getUrlToImage());
                intent.putExtra("date",article.getPublishedAt());
                intent.putExtra("source",article.getSource().getName());
                intent.putExtra("author",article.getAuthor());

                Pair<View, String> pair = Pair.create((View)imageView,ViewCompat.getTransitionName(imageView));

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity_news.this,
                        pair
                );

                startActivity(intent, optionsCompat.toBundle());
            }
        });
           }


            @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_news, menu);
        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView=(SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest ");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {

                    onloadingSwipeRefrehs(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false,false);

        return true;
            }

    @Override
    public void onRefresh() {
        LoadJson("");
    }

    private void onloadingSwipeRefrehs(final String keyword){
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadJson(keyword);
                    }
                }
        );
    }
}
