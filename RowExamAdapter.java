package com.majdalden.al_raadforteachingsupport.ui.Fragment.ExamFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.majdalden.al_raadforteachingsupport.model.Exam;
import com.majdalden.al_raadforteachingsupport.model.Question;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by majd_alden on 3/20/18.
 */

public class RowExamAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

    private ArrayList<RowExamFragment> rowExamFragments;
    private Exam exam;


    public RowExamAdapter(FragmentManager fm) {
        super(fm);
        rowExamFragments = new ArrayList<>();
        exam = null;
    }

    @Override
    public Fragment getItem(int position) {
        return rowExamFragments.get(position);
    }

    @Override
    public int getCount() {
        return rowExamFragments.size();
    }

    public void CreateRowExamFragments() {
        for (Map.Entry<String, Question> entry : exam.getQuestionHash().entrySet()) {
            RowExamFragment rowExamFragment = new RowExamFragment();
            rowExamFragments.add(rowExamFragment);
        }
        this.notifyDataSetChanged();
    }
    public void SetDateToFragment(int position) {
        if (position < exam.getQuestionHash().size()
                && position > -1) {
            if (rowExamFragments.get(position) == null) {
                return;
            }

            rowExamFragments.get(position).SetData(exam, exam.getQuestionHash()
                    .keySet().toArray()[position].toString());
        }
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public ArrayList<RowExamFragment> getRowAddExamFragments() {
        return rowExamFragments;
    }

    public void setRowAddExamFragments(ArrayList<RowExamFragment> rowExamFragments) {
        this.rowExamFragments = rowExamFragments;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
//        ExamFragment.getInstance().SetHeightLayout(LinearLayout.LayoutParams.MATCH_PARENT);
        SetDateToFragment(position);
//        ExamFragment.getInstance().SetHeightLayout();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}