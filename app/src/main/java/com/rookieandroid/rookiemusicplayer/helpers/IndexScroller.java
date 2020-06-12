package com.rookieandroid.rookiemusicplayer.helpers;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rookieandroid.rookiemusicplayer.R;

import java.util.ArrayList;

public class IndexScroller implements ViewGroup.OnTouchListener
{
    private final String TAG = IndexScroller.class.getSimpleName();
    private Context context;
    private View rootView;
    private ArrayList<Integer> sections;
    private ArrayList<String> letters;
    private ArrayList<Boolean> flags = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ViewGroup indexContainer;
    private RelativeLayout indexDivider;
    private TextView indexLetter;
    private TextView index;
    private TextView indexA;
    private TextView indexB;
    private TextView indexC;
    private TextView indexD;
    private TextView indexE;
    private TextView indexF;
    private TextView indexG;
    private TextView indexH;
    private TextView indexI;
    private TextView indexJ;
    private TextView indexK;
    private TextView indexL;
    private TextView indexM;
    private TextView indexN;
    private TextView indexO;
    private TextView indexP;
    private TextView indexQ;
    private TextView indexR;
    private TextView indexS;
    private TextView indexT;
    private TextView indexU;
    private TextView indexV;
    private TextView indexW;
    private TextView indexX;
    private TextView indexY;
    private TextView indexZ;

    public IndexScroller(Context context, View rootView, RecyclerView recyclerView, LinearLayoutManager layoutManager, ArrayList<Integer> sections, ArrayList<String> letters)
    {
        this.context = context;
        this.rootView = rootView;
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
        this.sections = sections;
        this.letters = letters;
        setIndexing();
        addFlags();
        indexContainer.setOnTouchListener(this);
    }

    private void setIndexing()
    {
        indexContainer = rootView.findViewById(R.id.indexScroller);
        indexContainer.setHapticFeedbackEnabled(true);
        indexDivider = rootView.findViewById(R.id.indexDivider);
        indexLetter = rootView.findViewById(R.id.indexLetter);
        index = indexContainer.findViewById(R.id.index);
        indexA = indexContainer.findViewById(R.id.indexA);
        indexB = indexContainer.findViewById(R.id.indexB);
        indexC = indexContainer.findViewById(R.id.indexC);
        indexD = indexContainer.findViewById(R.id.indexD);
        indexE = indexContainer.findViewById(R.id.indexE);
        indexF = indexContainer.findViewById(R.id.indexF);
        indexG = indexContainer.findViewById(R.id.indexG);
        indexH = indexContainer.findViewById(R.id.indexH);
        indexI = indexContainer.findViewById(R.id.indexI);
        indexJ = indexContainer.findViewById(R.id.indexJ);
        indexK = indexContainer.findViewById(R.id.indexK);
        indexL = indexContainer.findViewById(R.id.indexL);
        indexM = indexContainer.findViewById(R.id.indexM);
        indexN = indexContainer.findViewById(R.id.indexN);
        indexO = indexContainer.findViewById(R.id.indexO);
        indexP = indexContainer.findViewById(R.id.indexP);
        indexQ = indexContainer.findViewById(R.id.indexQ);
        indexR = indexContainer.findViewById(R.id.indexR);
        indexS = indexContainer.findViewById(R.id.indexS);
        indexT = indexContainer.findViewById(R.id.indexT);
        indexU = indexContainer.findViewById(R.id.indexU);
        indexV = indexContainer.findViewById(R.id.indexV);
        indexW = indexContainer.findViewById(R.id.indexW);
        indexX = indexContainer.findViewById(R.id.indexX);
        indexY = indexContainer.findViewById(R.id.indexY);
        indexZ = indexContainer.findViewById(R.id.indexZ);
    }

    private void addFlags()
    {
        flags.add(false);flags.add(false);flags.add(false);flags.add(false);flags.add(false);
        flags.add(false);flags.add(false);flags.add(false);flags.add(false);flags.add(false);
        flags.add(false);flags.add(false);flags.add(false);flags.add(false);flags.add(false);
        flags.add(false);flags.add(false);flags.add(false);flags.add(false);flags.add(false);
        flags.add(false);flags.add(false);flags.add(false);flags.add(false);flags.add(false);
        flags.add(false);flags.add(false);
    }

    //KEEPS TRACK OF WHICH LETTER IS CURRENTLY INDEXED
    private void setFlags(int position)
    {
        switch (position)
        {
            case 0: flags.set(0, true); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 1: flags.set(0, false); flags.set(1, true);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 2: flags.set(0, false); flags.set(1, false);
                flags.set(2, true); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 3: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, true); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 4: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, true); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 5: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, true); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 6: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, true);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 7: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, true); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 8: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, true); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 9: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, true); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 10: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, true); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 11: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, true);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 12: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, true); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 13: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, true); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 14: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, true); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 15: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, true); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 16: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, true);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 17: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, true); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 18: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, true); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 19: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, true); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 20: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, true); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 21: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, true);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 22: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, true); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 23: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, true); flags.set(24, false); flags.set(25, false); flags.set(26, false);
                break;
            case 24: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, true); flags.set(25, false); flags.set(26, false);
                break;
            case 25: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, true); flags.set(26, false);
                break;
            case 26: flags.set(0, false); flags.set(1, false);
                flags.set(2, false); flags.set(3, false); flags.set(4, false); flags.set(5, false); flags.set(6, false);
                flags.set(7, false); flags.set(8, false); flags.set(9, false); flags.set(10, false); flags.set(11, false);
                flags.set(12, false); flags.set(13, false); flags.set(14, false); flags.set(15, false); flags.set(16, false);
                flags.set(17, false); flags.set(18, false); flags.set(19, false); flags.set(20, false); flags.set(21, false);
                flags.set(22, false); flags.set(23, false); flags.set(24, false); flags.set(25, false); flags.set(26, true);
                break;
        }
    }

    public void setScrolling()
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(0) || layoutManager.findFirstVisibleItemPosition() == sections.get(1) - 1 && letters.contains("#"))
                    indexLetter.setText("#");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(1) || layoutManager.findFirstVisibleItemPosition() == sections.get(2) - 1 && letters.contains("A"))
                    indexLetter.setText("A");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(2) || layoutManager.findFirstVisibleItemPosition() == sections.get(3) - 1 && letters.contains("B"))
                    indexLetter.setText("B");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(3) || layoutManager.findFirstVisibleItemPosition() == sections.get(4) - 1 && letters.contains("C"))
                    indexLetter.setText("C");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(4) || layoutManager.findFirstVisibleItemPosition() == sections.get(5) - 1 && letters.contains("D"))
                    indexLetter.setText("D");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(5) || layoutManager.findFirstVisibleItemPosition() == sections.get(6) - 1 && letters.contains("E"))
                    indexLetter.setText("E");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(6) || layoutManager.findFirstVisibleItemPosition() == sections.get(7) - 1 && letters.contains("F"))
                    indexLetter.setText("F");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(7) || layoutManager.findFirstVisibleItemPosition() == sections.get(8) - 1 && letters.contains("G"))
                    indexLetter.setText("G");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(8) || layoutManager.findFirstVisibleItemPosition() == sections.get(9) - 1 && letters.contains("H"))
                    indexLetter.setText("H");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(9) || layoutManager.findFirstVisibleItemPosition() == sections.get(10) - 1 && letters.contains("I"))
                    indexLetter.setText("I");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(10) || layoutManager.findFirstVisibleItemPosition() == sections.get(11) - 1 && letters.contains("J"))
                    indexLetter.setText("J");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(11) || layoutManager.findFirstVisibleItemPosition() == sections.get(12) - 1 && letters.contains("K"))
                    indexLetter.setText("K");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(12) || layoutManager.findFirstVisibleItemPosition() == sections.get(13) - 1 && letters.contains("L"))
                    indexLetter.setText("L");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(13) || layoutManager.findFirstVisibleItemPosition() == sections.get(14) - 1 && letters.contains("M"))
                    indexLetter.setText("M");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(14) || layoutManager.findFirstVisibleItemPosition() == sections.get(15) - 1 && letters.contains("N"))
                    indexLetter.setText("N");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(15) || layoutManager.findFirstVisibleItemPosition() == sections.get(16) - 1 && letters.contains("O"))
                    indexLetter.setText("O");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(16) || layoutManager.findFirstVisibleItemPosition() == sections.get(17) - 1 && letters.contains("P"))
                    indexLetter.setText("P");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(17) || layoutManager.findFirstVisibleItemPosition() == sections.get(18) - 1 && letters.contains("Q"))
                    indexLetter.setText("Q");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(18) || layoutManager.findFirstVisibleItemPosition() == sections.get(19) - 1 && letters.contains("R"))
                    indexLetter.setText("R");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(19) || layoutManager.findFirstVisibleItemPosition() == sections.get(20) - 1 && letters.contains("S"))
                    indexLetter.setText("S");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(20) || layoutManager.findFirstVisibleItemPosition() == sections.get(21) - 1 && letters.contains("T"))
                    indexLetter.setText("T");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(21) || layoutManager.findFirstVisibleItemPosition() == sections.get(22) - 1 && letters.contains("U"))
                    indexLetter.setText("U");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(22) || layoutManager.findFirstVisibleItemPosition() == sections.get(23) - 1 && letters.contains("V"))
                    indexLetter.setText("V");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(23) || layoutManager.findFirstVisibleItemPosition() == sections.get(24) - 1 && letters.contains("W"))
                    indexLetter.setText("W");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(24) || layoutManager.findFirstVisibleItemPosition() == sections.get(25) - 1 && letters.contains("X"))
                    indexLetter.setText("X");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(25) || layoutManager.findFirstVisibleItemPosition() == sections.get(26) - 1 && letters.contains("Y"))
                    indexLetter.setText("Y");
                if(layoutManager.findFirstVisibleItemPosition() == sections.get(26) && letters.contains("Z"))
                    indexLetter.setText("Z");
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        float x, y;
        //indexDivider.setVisibility(View.VISIBLE);
        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                x = motionEvent.getX(); y = motionEvent.getY();
                if(y > index.getY() && y < indexA.getY() && !flags.get(0) && letters.contains("#"))
                {
                    //Log.i(TAG, "Index # at " + y);
                    indexLetter.setText("#");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(0)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(0);
                }

                if(y > indexA.getY() && y < indexB.getY() && !flags.get(1) && letters.contains("A"))
                {
                    //Log.i(TAG, "Index A at " + y);
                    indexLetter.setText("A");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(1)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(1);
                }

                if(y > indexB.getY() && y < indexC.getY() && !flags.get(2) && letters.contains("B"))
                {
                    //Log.i(TAG, "Index B at " + y);
                    indexLetter.setText("B");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(2)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(2);
                }

                if(y > indexC.getY() && y < indexD.getY() && !flags.get(3) && letters.contains("C"))
                {
                    //Log.i(TAG, "Index C at " + y);
                    indexLetter.setText("C");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(3)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(3);
                }

                if(y > indexD.getY() && y < indexE.getY() && !flags.get(4) && letters.contains("D"))
                {
                    //Log.i(TAG, "Index D at " + y);
                    indexLetter.setText("D");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(4)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(4);
                }

                if(y > indexE.getY() && y < indexF.getY() && !flags.get(5) && letters.contains("E"))
                {
                    //Log.i(TAG, "Index E at " + y);
                    indexLetter.setText("E");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(5)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(5);
                }

                if(y > indexF.getY() && y < indexG.getY() && !flags.get(6) && letters.contains("F"))
                {
                    //Log.i(TAG, "Index F at " + y);
                    indexLetter.setText("F");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(6)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(6);
                }

                if(y > indexG.getY() && y < indexH.getY() && !flags.get(7) && letters.contains("G"))
                {
                    //Log.i(TAG, "Index G at " + y);
                    indexLetter.setText("G");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(7)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(7);
                }

                if(y > indexH.getY() && y < indexI.getY() && !flags.get(8) && letters.contains("H"))
                {
                    //Log.i(TAG, "Index H at " + y);
                    indexLetter.setText("H");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(8)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(8);
                }

                if(y > indexI.getY() && y < indexJ.getY() && !flags.get(9) && letters.contains("I"))
                {
                    //Log.i(TAG, "Index I at " + y);
                    indexLetter.setText("I");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(9)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(9);
                }

                if(y > indexJ.getY() && y < indexK.getY() && !flags.get(10) && letters.contains("J"))
                {
                    //Log.i(TAG, "Index J at " + y);
                    indexLetter.setText("J");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(10)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(10);
                }

                if(y > indexK.getY() && y < indexL.getY() && !flags.get(11) && letters.contains("K"))
                {
                    //Log.i(TAG, "Index K at " + y);
                    indexLetter.setText("K");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(11)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(11);
                }

                if(y > indexL.getY() && y < indexM.getY() && !flags.get(12) && letters.contains("L"))
                {
                    //Log.i(TAG, "Index L at " + y);
                    indexLetter.setText("L");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(12)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(12);
                }

                if(y > indexM.getY() && y < indexN.getY() && !flags.get(13) && letters.contains("M"))
                {
                    //Log.i(TAG, "Index M at " + y);
                    indexLetter.setText("M");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(13)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(13);
                }

                if(y > indexN.getY() && y < indexO.getY() && !flags.get(14) && letters.contains("N"))
                {
                    //Log.i(TAG, "Index N at " + y);
                    indexLetter.setText("N");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(14)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(14);
                }

                if(y > indexO.getY() && y < indexP.getY() && !flags.get(15) && letters.contains("O"))
                {
                    //Log.i(TAG, "Index O at " + y);
                    indexLetter.setText("O");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(15)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(15);
                }

                if(y > indexP.getY() && y < indexQ.getY() && !flags.get(16) && letters.contains("P"))
                {
                    //Log.i(TAG, "Index P at " + y);
                    indexLetter.setText("P");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(16)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(16);
                }

                if(y > indexQ.getY() && y < indexR.getY() && !flags.get(17) && letters.contains("Q"))
                {
                    //Log.i(TAG, "Index Q at " + y);
                    indexLetter.setText("Q");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(17)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(17);
                }

                if(y > indexR.getY() && y < indexS.getY() && !flags.get(18) && letters.contains("R"))
                {
                    //Log.i(TAG, "Index R at " + y);
                    indexLetter.setText("R");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(18)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(18);
                }

                if(y > indexS.getY() && y < indexT.getY() && !flags.get(19) && letters.contains("S"))
                {
                    //Log.i(TAG, "Index S at " + y);
                    indexLetter.setText("S");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(19)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(19);
                }

                if(y > indexT.getY() && y < indexU.getY() && !flags.get(20) && letters.contains("T"))
                {
                    //Log.i(TAG, "Index T at " + y);
                    indexLetter.setText("T");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(20)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(20);
                }

                if(y > indexU.getY() && y < indexV.getY() && !flags.get(21) && letters.contains("U"))
                {
                    //Log.i(TAG, "Index U at " + y);
                    indexLetter.setText("U");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(21)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(21);
                }

                if(y > indexV.getY() && y < indexW.getY() && !flags.get(22) && letters.contains("V"))
                {
                    //Log.i(TAG, "Index V at " + y);
                    indexLetter.setText("V");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(22)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(22);
                }

                if(y > indexW.getY() && y < indexX.getY() && !flags.get(23) && letters.contains("W"))
                {
                    //Log.i(TAG, "Index W at " + y);
                    indexLetter.setText("W");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(23)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(23);
                }

                if(y > indexX.getY() && y < indexY.getY() && !flags.get(24) && letters.contains("X"))
                {
                    //Log.i(TAG, "Index X at " + y);
                    indexLetter.setText("X");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(24)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(24);
                }

                if(y > indexY.getY() && y < indexZ.getY() && !flags.get(25) && letters.contains("Y"))
                {
                    //Log.i(TAG, "Index Y at " + y);
                    indexLetter.setText("Y");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(25)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(25);
                }

                if(y > indexZ.getY() && y < indexContainer.getHeight() && !flags.get(26) && letters.contains("Z"))
                {
                    //Log.i(TAG, "Index Z at " + y);
                    indexLetter.setText("Z");
                    recyclerView.post(()->layoutManager.scrollToPosition(sections.get(26)));
                    indexContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    setFlags(26);
                }
                break;
        }
        return true;
    }
}