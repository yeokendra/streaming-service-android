package com.nontivi.nonton.features.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.nontivi.nonton.R;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.injection.component.ActivityComponent;
import com.nontivi.nonton.util.ClickUtil;
import com.nontivi.nonton.util.LocaleUtil;

import butterknife.BindView;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class FaqActivity extends BaseActivity {


    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.ib_back)
    ImageButton btnBack;

    @BindView(R.id.expand_text_view1)
    TextView mTvFaq1;

    @BindView(R.id.expand_text_view2)
    TextView mTvFaq2;

    @BindView(R.id.expand_text_view3)
    TextView mTvFaq3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTvTitle.setText(getString(R.string.setting_faq));
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int textSize1 = getResources().getDimensionPixelSize(R.dimen.text_medium);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen.text_small);

        String faq1Header = getString(R.string.faq_1_header);
        String faq2Header = getString(R.string.faq_2_header);
        String faq3Header = getString(R.string.faq_3_header);

        String faq1Body = getString(R.string.faq_1_body);
        String faq2Body = getString(R.string.faq_2_body);
        String faq3Body = getString(R.string.faq_3_body);



        mTvFaq1.setText(combineString(faq1Header,faq1Body,textSize1,textSize2));
        mTvFaq2.setText(combineString(faq2Header,faq2Body,textSize1,textSize2));
        mTvFaq3.setText(combineString(faq3Header,faq3Body,textSize1,textSize2));

    }

    private CharSequence combineString(String text1, String text2, int textSize1, int textSize2){
        SpannableString span1 = new SpannableString(text1);
        span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, text1.length(), SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(text2);
        span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, text2.length(), SPAN_INCLUSIVE_INCLUSIVE);
        CharSequence finalText = TextUtils.concat(span1, "\n\n", span2);
        return finalText;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_setting_faq;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {

    }

    @Override
    protected void attachView() {
    }

    @Override
    protected void detachPresenter() {

    }


}
