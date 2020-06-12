package com.rookieandroid.rookiemusicplayer.helpers;

import com.rookieandroid.rookiemusicplayer.Albums;
import com.rookieandroid.rookiemusicplayer.AlbumsSections;

import java.util.ArrayList;

public class SectionContent
{
    private Albums album;
    private ArrayList<Albums> albums;
    private ArrayList<AlbumsSections> albumsSections;
    private ArrayList<Albums> section0 = new ArrayList<>();
    private ArrayList<Albums> sectionA = new ArrayList<>();
    private ArrayList<Albums> sectionB = new ArrayList<>();
    private ArrayList<Albums> sectionC = new ArrayList<>();
    private ArrayList<Albums> sectionD = new ArrayList<>();
    private ArrayList<Albums> sectionE = new ArrayList<>();
    private ArrayList<Albums> sectionF = new ArrayList<>();
    private ArrayList<Albums> sectionG = new ArrayList<>();
    private ArrayList<Albums> sectionH = new ArrayList<>();
    private ArrayList<Albums> sectionI = new ArrayList<>();
    private ArrayList<Albums> sectionJ = new ArrayList<>();
    private ArrayList<Albums> sectionK = new ArrayList<>();
    private ArrayList<Albums> sectionL = new ArrayList<>();
    private ArrayList<Albums> sectionM = new ArrayList<>();
    private ArrayList<Albums> sectionN = new ArrayList<>();
    private ArrayList<Albums> sectionO = new ArrayList<>();
    private ArrayList<Albums> sectionP = new ArrayList<>();
    private ArrayList<Albums> sectionQ = new ArrayList<>();
    private ArrayList<Albums> sectionR = new ArrayList<>();
    private ArrayList<Albums> sectionS = new ArrayList<>();
    private ArrayList<Albums> sectionT = new ArrayList<>();
    private ArrayList<Albums> sectionU = new ArrayList<>();
    private ArrayList<Albums> sectionV = new ArrayList<>();
    private ArrayList<Albums> sectionW = new ArrayList<>();
    private ArrayList<Albums> sectionX = new ArrayList<>();
    private ArrayList<Albums> sectionY = new ArrayList<>();
    private ArrayList<Albums> sectionZ = new ArrayList<>();

    public SectionContent(ArrayList<Albums> albums, ArrayList<AlbumsSections> albumsSections)
    {
        this.albums = albums;
        this.albumsSections = albumsSections;
    }

    public SectionContent(Albums album, ArrayList<AlbumsSections> albumsSections)
    {
        this.album = album;
        this.albumsSections = albumsSections;
    }

    public void sectionAlbums()
    {
        for(int i = 0; i < albums.size(); i++)
        {
            String album = albums.get(i).getAlbum();
            char sectionLetter = album.charAt(0);
            boolean letter = Character.isLetter(sectionLetter);
            if(!letter)
                section0.add(albums.get(i));
            else
                addToSection(albums.get(i), sectionLetter);
        }
        addAllSections();
    }

    private void addToSection(Albums album, Character letter)
    {
        switch(Character.toUpperCase(letter))
        {
            case 'A': sectionA.add(album); break;
            case 'B': sectionB.add(album); break;
            case 'C': sectionC.add(album); break;
            case 'D': sectionD.add(album); break;
            case 'E': sectionE.add(album); break;
            case 'F': sectionF.add(album); break;
            case 'G': sectionG.add(album); break;
            case 'H': sectionH.add(album); break;
            case 'I': sectionI.add(album); break;
            case 'J': sectionJ.add(album); break;
            case 'K': sectionK.add(album); break;
            case 'L': sectionL.add(album); break;
            case 'M': sectionM.add(album); break;
            case 'N': sectionN.add(album); break;
            case 'O': sectionO.add(album); break;
            case 'P': sectionP.add(album); break;
            case 'Q': sectionQ.add(album); break;
            case 'R': sectionR.add(album); break;
            case 'S': sectionS.add(album); break;
            case 'T': sectionT.add(album); break;
            case 'U': sectionU.add(album); break;
            case 'V': sectionV.add(album); break;
            case 'W': sectionW.add(album); break;
            case 'X': sectionX.add(album); break;
            case 'Y': sectionY.add(album); break;
            case 'Z': sectionZ.add(album); break;
        }
    }

    private void addAllSections()
    {
        albumsSections.add(new AlbumsSections(section0, null));
        albumsSections.add(new AlbumsSections(sectionA, null));
        albumsSections.add(new AlbumsSections(sectionB, null));
        albumsSections.add(new AlbumsSections(sectionC, null));
        albumsSections.add(new AlbumsSections(sectionD, null));
        albumsSections.add(new AlbumsSections(sectionE, null));
        albumsSections.add(new AlbumsSections(sectionF, null));
        albumsSections.add(new AlbumsSections(sectionG, null));
        albumsSections.add(new AlbumsSections(sectionH, null));
        albumsSections.add(new AlbumsSections(sectionI, null));
        albumsSections.add(new AlbumsSections(sectionJ, null));
        albumsSections.add(new AlbumsSections(sectionK, null));
        albumsSections.add(new AlbumsSections(sectionL, null));
        albumsSections.add(new AlbumsSections(sectionM, null));
        albumsSections.add(new AlbumsSections(sectionN, null));
        albumsSections.add(new AlbumsSections(sectionO, null));
        albumsSections.add(new AlbumsSections(sectionP, null));
        albumsSections.add(new AlbumsSections(sectionQ, null));
        albumsSections.add(new AlbumsSections(sectionR, null));
        albumsSections.add(new AlbumsSections(sectionS, null));
        albumsSections.add(new AlbumsSections(sectionT, null));
        albumsSections.add(new AlbumsSections(sectionU, null));
        albumsSections.add(new AlbumsSections(sectionV, null));
        albumsSections.add(new AlbumsSections(sectionW, null));
        albumsSections.add(new AlbumsSections(sectionX, null));
        albumsSections.add(new AlbumsSections(sectionY, null));
        albumsSections.add(new AlbumsSections(sectionZ, null));
    }

    public AlbumsSections getSection()
    {
        AlbumsSections section = null;
        char sectionLetter = album.getAlbum().charAt(0);
        if(!Character.isLetter(sectionLetter))
            section = albumsSections.get(0);
        else
        {
            switch (Character.toString(sectionLetter).toUpperCase())
            {
                case "A":
                    section = albumsSections.get(1);
                    break;

                case "B":
                    section = albumsSections.get(2);
                    break;

                case "C":
                    section = albumsSections.get(3);
                    break;

                case "D":
                    section = albumsSections.get(4);
                    break;

                case "E":
                    section = albumsSections.get(5);
                    break;

                case "F":
                    section = albumsSections.get(6);
                    break;

                case "G":
                    section = albumsSections.get(7);
                    break;

                case "H":
                    section = albumsSections.get(8);
                    break;

                case "I":
                    section = albumsSections.get(9);
                    break;

                case "J":
                    section = albumsSections.get(10);
                    break;

                case "K":
                    section = albumsSections.get(11);
                    break;

                case "L":
                    section = albumsSections.get(12);
                    break;

                case "M":
                    section = albumsSections.get(13);
                    break;

                case "N":
                    section = albumsSections.get(14);
                    break;

                case "O":
                    section = albumsSections.get(15);
                    break;

                case "P":
                    section = albumsSections.get(16);
                    break;

                case "Q":
                    section = albumsSections.get(17);
                    break;

                case "R":
                    section = albumsSections.get(18);
                    break;

                case "S":
                    section = albumsSections.get(19);
                    break;

                case "T":
                    section = albumsSections.get(20);
                    break;

                case "U":
                    section = albumsSections.get(21);
                    break;

                case "V":
                    section = albumsSections.get(22);
                    break;

                case "W":
                    section = albumsSections.get(23);
                    break;

                case "X":
                    section = albumsSections.get(24);
                    break;

                case "Y":
                    section = albumsSections.get(25);
                    break;

                case "Z":
                    section = albumsSections.get(26);
                    break;
            }
        }

        return section;
    }
}
