package com.nontivi.nonton.features.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nontivi.nonton.R;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.common.ErrorView;
import com.nontivi.nonton.features.detail.DetailActivity;
import com.nontivi.nonton.features.main.MainMvpView;
import com.nontivi.nonton.features.main.MainPresenter;
import com.nontivi.nonton.features.main.PokemonAdapter;
import com.nontivi.nonton.injection.component.ActivityComponent;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static com.nontivi.nonton.app.ConstantGroup.KEY_SEARCH_STRING;

public class SearchActivity extends BaseActivity implements SearchMvpView, ErrorView.ErrorListener {

    @Inject
    SearchPresenter searchPresenter;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.tv_search_title)
    TextView mTvSearchTitle;

    @BindView(R.id.ib_back)
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTvTitle.setText(getString(R.string.title_search));
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        String searchTitle = "Search result for \""+bundle.getString(KEY_SEARCH_STRING,"")+"\"";
        mTvSearchTitle.setText(searchTitle);

    }

    @Override
    public int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void attachView() {
        searchPresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        searchPresenter.detachView();
    }


    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showError(Throwable error) {

    }

    @Override
    public void onReloadData() {

    }
}
