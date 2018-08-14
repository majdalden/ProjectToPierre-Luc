package com.majdalden.al_raadforteachingsupport.ui.Fragment.ExamFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.majdalden.al_raadforteachingsupport.R;
import com.majdalden.al_raadforteachingsupport.data.ConstantsOfApp;
import com.majdalden.al_raadforteachingsupport.interfaces.AppCallback;
import com.majdalden.al_raadforteachingsupport.model.Exam;
import com.majdalden.al_raadforteachingsupport.ui.Activity.MainActivity;
import com.majdalden.al_raadforteachingsupport.ui.CustomView.ViewPager.ZoomOutPageTransformer;
import com.majdalden.al_raadforteachingsupport.ui.CustomView.WrapContentHeightViewPager;

/**
 * Created by majd_alden on 3/15/18.
 */

public class ExamFragment extends Fragment {

    private boolean isExam;
    private static ExamFragment examFragmentInstance = null;
    WrapContentHeightViewPager vp_pager_exam;
    public RowExamAdapter rowExamAdapter;
    private View examFragment;
    //    LinearLayout ll_vp_pager_exam;
    private static Exam exam;
    private TextView tv_number_remaining_time_exam;
    private static CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        examFragment = inflater.inflate(R.layout.fragment_exam, container, false);
        isExam = false;
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle(R.string.exam);
        TextView toolbar_title_post = toolbar.findViewById(R.id.toolbar_title_post);
        toolbar_title_post.setText(R.string.exam);
        countDownTimer = null;

        vp_pager_exam = examFragment.findViewById(R.id.vp_pager_exam);

//        ll_vp_pager_exam = examFragment.findViewById(R.id.ll_vp_pager_exam);

        rowExamAdapter = new RowExamAdapter(getActivity().getSupportFragmentManager());
        vp_pager_exam.setAdapter(rowExamAdapter);
        vp_pager_exam.setPageTransformer(true, new ZoomOutPageTransformer());

        vp_pager_exam.setOnPageChangeListener(rowExamAdapter);

        Button but_end_exam_exam = examFragment.findViewById(R.id.but_end_exam_exam);
        final Button but_next_question_exam = examFragment.findViewById(R.id.but_next_question_exam);
        final Button but_previous_question_exam = examFragment.findViewById(R.id.but_previous_question_exam);
        final TextView tv_text_correct_answer_rat_exam = examFragment.findViewById(R.id.tv_text_correct_answer_rat_exam);
        final TextView tv_number_correct_answer_rat_exam = examFragment.findViewById(R.id.tv_number_correct_answer_rat_exam);
        final TextView tv_result_exam = examFragment.findViewById(R.id.tv_result_exam);
        final TextView tv_text_remaining_time_exam = examFragment.findViewById(R.id.tv_text_remaining_time_exam);
        tv_number_remaining_time_exam = examFragment.findViewById(R.id.tv_number_remaining_time_exam);
        final LinearLayout ll_move_button_exam = examFragment.findViewById(R.id.ll_move_button_exam);

        but_end_exam_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        but_next_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        but_previous_question_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_text_correct_answer_rat_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_number_correct_answer_rat_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_text_remaining_time_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_number_remaining_time_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));
        tv_result_exam.setTypeface(MainActivity.SetFontToView(getContext(), "fonts/droid_arabic_kufi_regular.ttf"));

        final SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(ConstantsOfApp.AL_RAAD_FOR_TEACHING_SUPPORT_STUDENT_KEY
                        , Context.MODE_PRIVATE);

        final ProgressDialog progressGetExam = new ProgressDialog(getContext());
        progressGetExam.setCancelable(false);
        progressGetExam.setMessage(getString(R.string.get_exam));
        progressGetExam.show();

        ShowViewToExam(false);
        isExam = false;

        ConstantsOfApp.IsInternetAvailable(getContext(), new AppCallback<Boolean>() {
            @Override
            public void callback(Boolean data) {
                if (data) {
                    if (exam == null) {
                        ShowMessageNoExam(R.string
                                .there_is_currently_no_exam_when_the_exam_is_put_you_will_receive_a_notification);
                        progressGetExam.dismiss();
                        return;
                    }
                    ConstantsOfApp.examController.GetUidToLastExam(exam, new AppCallback<DataSnapshot>() {
                        @Override
                        public void callback(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null &&
                                    dataSnapshot.getKey() != null && !dataSnapshot.getKey().equalsIgnoreCase("")
                                    && dataSnapshot.getValue() != null
                                    && !String.valueOf(dataSnapshot.getValue()).equalsIgnoreCase("")) {
                                float grade = dataSnapshot.getValue(Float.class);
                                if (String.valueOf(grade).length() > 5) {
                                    grade = ((float) Math.floor(grade))
                                            + Float.parseFloat(String.valueOf(grade - Math.floor(grade)).substring(0, 4));
                                }
                                ConstantsOfApp.examController.SaveGrade(getContext(), dataSnapshot.getKey(), grade);
                                tv_number_correct_answer_rat_exam.setText(String.valueOf(grade));
                                progressGetExam.dismiss();
                            } else {
                                ConstantsOfApp.examController.LoadExam(getContext(), exam, tv_number_remaining_time_exam
                                        , progressGetExam);
                            }
                        }
                    });
                } else {
                    ShowMessageNoExam(R.string.no_internet_connection_please_connect_to_internet_and_try_again);
                    isExam = false;
                    progressGetExam.dismiss();
                }
            }
        });


        but_next_question_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vp_pager_exam.getCurrentItem() + 1 >= exam.getQuestionHash().size()) {
                    return;
                }
                SetValueToExamFragmentRow(vp_pager_exam.getCurrentItem() + 1);
            }
        });

        but_previous_question_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vp_pager_exam.getCurrentItem() - 1 < 0) {
                    return;
                }
                SetValueToExamFragmentRow(vp_pager_exam.getCurrentItem() - 1);
            }
        });

        but_end_exam_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderAlert = new AlertDialog.Builder(getContext()
                        , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builderAlert.setMessage(getString
                        (R.string.Are_you_sure_you_want_to_finish_the_exam));
                builderAlert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float grade = ConstantsOfApp.examController.GetGrade(ExamFragment.getInstance().rowExamAdapter,
                                ExamFragment.getExam());
                        ConstantsOfApp.examController.SaveGrade(getContext(), ExamFragment.getExam(), grade);
                        MainActivity.isFinishTimeExam = true;
                        MainActivity.getInstance().onBackPressed();
                    }
                });
                builderAlert.setNegativeButton(R.string.no, null);

                builderAlert.show();

            }
        });

        return examFragment;

    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public static Exam getExam() {
        return exam;
    }

    public boolean isExam() {
        return isExam;
    }

    public void setExam(boolean exam) {
        isExam = exam;
    }

    public static CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public static void setCountDownTimer(CountDownTimer countDownTimer) {
        ExamFragment.countDownTimer = countDownTimer;
    }

    public static ExamFragment getInstance() {
        if (examFragmentInstance == null) {
            examFragmentInstance = new ExamFragment();
        }

        return examFragmentInstance;
    }

    public void SetValueToExamFragmentRow(int count) {
        vp_pager_exam.setCurrentItem(count);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        examFragmentInstance = null;
        if (!MainActivity.isFinishTimeExam && countDownTimer != null) {
            countDownTimer.cancel();
        }
        exam = null;
    }

    public void ShowViewToExam(boolean isShow) {

        final Button but_next_question_exam = examFragment.findViewById(R.id.but_next_question_exam);
        final Button but_previous_question_exam = examFragment.findViewById(R.id.but_previous_question_exam);
        final TextView tv_text_correct_answer_rat_exam = examFragment.findViewById(R.id.tv_text_correct_answer_rat_exam);
        final TextView tv_number_correct_answer_rat_exam = examFragment.findViewById(R.id.tv_number_correct_answer_rat_exam);
        final TextView tv_result_exam = examFragment.findViewById(R.id.tv_result_exam);
        final TextView tv_text_remaining_time_exam = examFragment.findViewById(R.id.tv_text_remaining_time_exam);
        final LinearLayout ll_move_button_exam = examFragment.findViewById(R.id.ll_move_button_exam);
        final Button but_end_exam_exam = examFragment.findViewById(R.id.but_end_exam_exam);

        if (isShow) {
            tv_text_correct_answer_rat_exam.setVisibility(View.GONE);
            tv_number_correct_answer_rat_exam.setVisibility(View.GONE);
            tv_result_exam.setVisibility(View.GONE);

            tv_text_remaining_time_exam.setVisibility(View.VISIBLE);
            tv_number_remaining_time_exam.setVisibility(View.VISIBLE);
            but_next_question_exam.setVisibility(View.VISIBLE);
            but_previous_question_exam.setVisibility(View.VISIBLE);
            vp_pager_exam.setVisibility(View.VISIBLE);
            ll_move_button_exam.setVisibility(View.VISIBLE);
            but_end_exam_exam.setVisibility(View.VISIBLE);

        } else {
            tv_text_correct_answer_rat_exam.setVisibility(View.VISIBLE);
            tv_number_correct_answer_rat_exam.setVisibility(View.VISIBLE);
            tv_result_exam.setVisibility(View.VISIBLE);

            tv_text_remaining_time_exam.setVisibility(View.GONE);
            tv_number_remaining_time_exam.setVisibility(View.GONE);
            but_next_question_exam.setVisibility(View.GONE);
            but_previous_question_exam.setVisibility(View.GONE);
            vp_pager_exam.setVisibility(View.GONE);
            ll_move_button_exam.setVisibility(View.GONE);
            but_end_exam_exam.setVisibility(View.GONE);
        }
    }


    public void ShowMessageNoExam(int idMessage) {

        final Button but_next_question_exam = examFragment.findViewById(R.id.but_next_question_exam);
        final Button but_previous_question_exam = examFragment.findViewById(R.id.but_previous_question_exam);
        final TextView tv_text_correct_answer_rat_exam = examFragment.findViewById(R.id.tv_text_correct_answer_rat_exam);
        final TextView tv_number_correct_answer_rat_exam = examFragment.findViewById(R.id.tv_number_correct_answer_rat_exam);
        final TextView tv_result_exam = examFragment.findViewById(R.id.tv_result_exam);
        final TextView tv_text_remaining_time_exam = examFragment.findViewById(R.id.tv_text_remaining_time_exam);
        final LinearLayout ll_move_button_exam = examFragment.findViewById(R.id.ll_move_button_exam);
        final Button but_end_exam_exam = examFragment.findViewById(R.id.but_end_exam_exam);

        tv_result_exam.setVisibility(View.VISIBLE);
        tv_result_exam.setText(idMessage);

        tv_text_correct_answer_rat_exam.setVisibility(View.GONE);
        tv_number_correct_answer_rat_exam.setVisibility(View.GONE);
        tv_text_remaining_time_exam.setVisibility(View.GONE);
        tv_number_remaining_time_exam.setVisibility(View.GONE);
        but_next_question_exam.setVisibility(View.GONE);
        but_previous_question_exam.setVisibility(View.GONE);
        vp_pager_exam.setVisibility(View.GONE);
        ll_move_button_exam.setVisibility(View.GONE);
        but_end_exam_exam.setVisibility(View.GONE);

    }

}
