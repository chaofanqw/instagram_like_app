//package net.dunrou.mobile.activity;//package net.dunrou.mobile.activity;
//
//import android.app.Activity;
//import android.app.SearchManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.widget.SearchView;
//
//import net.dunrou.mobile.R;
//
//
///**
// * Created by yvette on 2018/9/18.
// */
//
//public class SearchableActivity extends Activity implements SearchView.OnQueryTextListener {
//    private transient final static String TAG = SearchableActivity.class.getSimpleName();
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_discover);
//
//        Intent intent = getIntent();
//        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            searchUsers(query);
//            Log.d(TAG, "receive query: " + query);
//        }
//    }
//
//    public void searchUsers(String query) {
////        TODO search user query
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.discover_search_menu, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.layout.discover_search_item).getActionView();
//
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false);
//
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setOnQueryTextListener(this);
//
//        return true;
//
//    }
//
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        return false;
//    }
//}
