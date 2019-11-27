package com.nontivi.nonton.features.genre;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nontivi.nonton.R;
import com.nontivi.nonton.data.model.Genre;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.common.ErrorView;
import com.nontivi.nonton.injection.component.ActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

import static com.nontivi.nonton.app.ConstantGroup.KEY_GENRE;
import static com.nontivi.nonton.app.ConstantGroup.KEY_SEARCH_STRING;

public class GenreActivity extends BaseActivity implements GenreMvpView, ErrorView.ErrorListener {

    @Inject
    GenrePresenter genrePresenter;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.ib_back)
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        Genre genre = (Genre)bundle.getSerializable(KEY_GENRE);
        mTvTitle.setText(genre.getName());

    }

    @Override
    public int getLayout() {
        return R.layout.activity_genre;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void attachView() {
        genrePresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        genrePresenter.detachView();
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
