package com.rookieandroid.rookiemusicplayer.helpers;

import java.util.ArrayList;

public class SectionIndexFixer
{
    public SectionIndexFixer(){}

    public void fixIndex(ArrayList<String> letters, ArrayList<Integer> sections)
    {
        if(!letters.contains("#"))
        {
            letters.add(0, "!");
            sections.add(0, -1);
        }
        if(!letters.contains("A"))
        {
            letters.add(1, "!");
            sections.add(1, -1);
        }
        if(!letters.contains("B"))
        {
            letters.add(2, "!");
            sections.add(2, -1);
        }
        if(!letters.contains("C"))
        {
            letters.add(3, "!");
            sections.add(3, -1);
        }
        if(!letters.contains("D"))
        {
            letters.add(4, "!");
            sections.add(4, -1);
        }
        if(!letters.contains("E"))
        {
            letters.add(5, "!");
            sections.add(5, -1);
        }
        if(!letters.contains("F"))
        {
            letters.add(6, "!");
            sections.add(6, -1);
        }
        if(!letters.contains("G"))
        {
            letters.add(7, "!");
            sections.add(7, -1);
        }
        if(!letters.contains("H"))
        {
            letters.add(8, "!");
            sections.add(8, -1);
        }
        if(!letters.contains("I"))
        {
            letters.add(9, "!");
            sections.add(9, -1);
        }
        if(!letters.contains("J"))
        {
            letters.add(10, "!");
            sections.add(10, -1);
        }
        if(!letters.contains("K"))
        {
            letters.add(11, "!");
            sections.add(11, -1);
        }
        if(!letters.contains("L"))
        {
            letters.add(12, "!");
            sections.add(12, -1);
        }
        if(!letters.contains("M"))
        {
            letters.add(13, "!");
            sections.add(13, -1);
        }
        if(!letters.contains("N"))
        {
            letters.add(14, "!");
            sections.add(14, -1);
        }
        if(!letters.contains("O"))
        {
            letters.add(15, "!");
            sections.add(15, -1);
        }
        if(!letters.contains("P"))
        {
            letters.add(16, "!");
            sections.add(16, -1);
        }
        if(!letters.contains("Q"))
        {
            letters.add(17, "!");
            sections.add(17, -1);
        }
        if(!letters.contains("R"))
        {
            letters.add(18, "!");
            sections.add(18, -1);
        }
        if(!letters.contains("S"))
        {
            letters.add(19, "!");
            sections.add(19, -1);
        }
        if(!letters.contains("T"))
        {
            letters.add(20, "!");
            sections.add(20, -1);
        }
        if(!letters.contains("U"))
        {
            letters.add(21, "!");
            sections.add(21, -1);
        }
        if(!letters.contains("V"))
        {
            letters.add(22, "!");
            sections.add(22, -1);
        }
        if(!letters.contains("W"))
        {
            letters.add(23, "!");
            sections.add(23, -1);
        }
        if(!letters.contains("X"))
        {
            letters.add(24, "!");
            sections.add(24, -1);
        }
        if(!letters.contains("Y"))
        {
            letters.add(25, "!");
            sections.add(25, -1);
        }
        if(!letters.contains("Z"))
        {
            letters.add(26, "!");
            sections.add(26, -1);
        }
    }
}
