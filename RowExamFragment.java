package com.majdalden.al_raadforteachingsupport.ui.Fragment.ExamFragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.majdalden.al_raadforteachingsupport.R;
import com.majdalden.al_raadforteachingsupport.data.ConstantsOfApp;
import com.majdalden.al_raadforteachingsupport.interfaces.AppCallback;
import com.majdalden.al_raadforteachingsupport.model.Exam;
import com.majdalden.al_raadforteachingsupport.ui.Activity.MainActivity;

/**
 * Created by majd_alden on 3/20/18.
 */

public class RowExamFragment extends Fragment implements View.OnClickListener {

    ViewGroup rowExamFragment;
    LinearLayout ll_view_row_exam;

    RadioButton rb_first_question_exam, rb_second_question_exam, rb_third_question_exam, rb_fourth_question_exam;
    TextView tv_question_title_exam, tv_question_details_exam, tv_first_question_exam, tv_second_question_exam, tv_third_question_exam, tv_fourth_question_exam;
    ImageView iv_question_exam;

    String question_details_exam, first_question_exam = "", second_question_exam = "", third_question_exam = "", fourth_question_exam = "", im_question_exam = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rowExamFragment = (ViewGroup) inflater.inflate(
                R.layout.row_exam_layout, container, false);

        tv_question_title_exam = rowExamFragment.findViewById(R.id.tv_question_title_exam);
        tv_question_details_exam = rowExamFragment.findViewById(R.id.tv_question_details_exam);
        rb_first_question_exam = rowExamFragment.findViewById(R.id.rb_first_question_exam);
        tv_first_question_exam = rowExamFragment.findViewById(R.id.tv_first_question_exam);
        rb_second_question_exam = rowExamFragment.findViewById(R.id.rb_second_question_exam);
        tv_second_question_exam = rowExamFragment.findViewById(R.id.tv_second_question_exam);
        rb_third_question_exam = rowExamFragment.findViewById(R.id.rb_third_question_exam);
        tv_third_question_exam = rowExamFragment.findViewById(R.id.tv_third_question_exam);
        rb_fourth_question_exam = rowExamFragment.findViewById(R.id.rb_fourth_question_exam);
        tv_fourth_question_exam = rowExamFragment.findViewById(R.id.tv_fourth_question_exam);
        iv_question_exam = rowExamFragment.findViewById(R.id.iv_question__exam);

        tv_question_title_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_question_details_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        rb_first_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_first_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        rb_second_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_second_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        rb_third_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_third_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        rb_fourth_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_fourth_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));

        tv_question_details_exam.setText(question_details_exam);
        tv_first_question_exam.setText(first_question_exam);
        tv_second_question_exam.setText(second_question_exam);
        tv_third_question_exam.setText(third_question_exam);
        tv_fourth_question_exam.setText(fourth_question_exam);
        if (im_question_exam != null && !im_question_exam.isEmpty()) {

            ConstantsOfApp.constantsOfApp.GetImageBitmap(getContext(), im_question_exam,
                    new AppCallback<Bitmap>() {
                        @Override
                        public void callback(Bitmap bitmap) {
                            if (bitmap != null) {
                                iv_question_exam.setImageBitmap(bitmap);
                                iv_question_exam.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {

            iv_question_exam.setVisibility(View.GONE);
        }
        rb_first_question_exam.setChecked(false);
        rb_second_question_exam.setChecked(false);
        rb_third_question_exam.setChecked(false);
        rb_fourth_question_exam.setChecked(false);


        rb_first_question_exam.setOnClickListener(this);
        rb_second_question_exam.setOnClickListener(this);
        rb_third_question_exam.setOnClickListener(this);
        rb_fourth_question_exam.setOnClickListener(this);

        return rowExamFragment;
    }

    private void SetAllRadioFalse() {
        rb_first_question_exam.setChecked(false);
        rb_second_question_exam.setChecked(false);
        rb_third_question_exam.setChecked(false);
        rb_fourth_question_exam.setChecked(false);
    }

    public void SetData(final Exam exam, final String keyQuestion) {
        if (exam == null || exam.equals(null) || keyQuestion == null
                || keyQuestion.equalsIgnoreCase(null)
                || keyQuestion.equalsIgnoreCase("")
                || exam.getQuestionHash().get(keyQuestion) == null) {
            return;
        }

        if (tv_question_details_exam != null) {
            tv_question_details_exam.setText(exam.getQuestionHash().get(keyQuestion).getQuestion());
            tv_first_question_exam.setText(exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("1"));
            tv_second_question_exam.setText(exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("2"));
            tv_third_question_exam.setText(exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("3"));
            tv_fourth_question_exam.setText(exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("4"));
            if (exam.getQuestionHash().get(keyQuestion).getImageQuestionUri() != null
                    && !exam.getQuestionHash().get(keyQuestion).getImageQuestionUri().isEmpty()) {
                iv_question_exam.setImageURI(Uri.parse(exam.getQuestionHash().get(keyQuestion).getImageQuestionUri()));
                iv_question_exam.setVisibility(View.VISIBLE);
            } else if (exam.getQuestionHash().get(keyQuestion).getImageQuestionUrl() != null
                    && !exam.getQuestionHash().get(keyQuestion).getImageQuestionUrl().isEmpty()) {

                ConstantsOfApp.constantsOfApp.GetImageBitmap(getContext(),
                        exam.getQuestionHash().get(keyQuestion).getImageQuestionUrl(),
                        new AppCallback<Bitmap>() {
                            @Override
                            public void callback(Bitmap bitmap) {
                                if (bitmap != null) {
                                    iv_question_exam.setImageBitmap(bitmap);
//                                    exam.getQuestionHash().get(keyQuestion).setImageQuestionUri(uriImage.getPath());
                                    iv_question_exam.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            } else {
                iv_question_exam.setVisibility(View.GONE);
            }
        } else {
            question_details_exam = exam.getQuestionHash().get(keyQuestion).getQuestion();
            first_question_exam = exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("1");
            second_question_exam = exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("2");
            third_question_exam = exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("3");
            fourth_question_exam = exam.getQuestionHash().get(keyQuestion).getAnswerHash().get("4");
            im_question_exam = exam.getQuestionHash().get(keyQuestion).getImageQuestionUrl();
        }
//        ExamFragment.getInstance().rowExamAdapter.notifyDataSetChanged();
//        ExamFragment.getInstance().SetHeightLayout();
    }

    @Override
    public void onClick(View v) {
        SetAllRadioFalse();
        switch (v.getId()) {
            case R.id.rb_first_question_exam:
                rb_first_question_exam.setChecked(true);
                break;
            case R.id.rb_second_question_exam:
                rb_second_question_exam.setChecked(true);
                break;
            case R.id.rb_third_question_exam:
                rb_third_question_exam.setChecked(true);
                break;
            case R.id.rb_fourth_question_exam:
                rb_fourth_question_exam.setChecked(true);
                break;
        }
    }

    public int GetSelectedAnswer() {
        if (rb_first_question_exam != null && rb_first_question_exam.isChecked()) {
            return 1;
        } else if (rb_second_question_exam != null && rb_second_question_exam.isChecked()) {
            return 2;
        } else if (rb_third_question_exam != null && rb_third_question_exam.isChecked()) {
            return 3;
        } else if (rb_fourth_question_exam != null && rb_fourth_question_exam.isChecked()) {
            return 4;
        }
        return 0;
    }
}

