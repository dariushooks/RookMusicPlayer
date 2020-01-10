package com.example.android.rookmusicplayer.helpers;

import com.example.android.rookmusicplayer.Albums;
import com.example.android.rookmusicplayer.AlbumsSections;

import java.util.ArrayList;

public class SectionContent
{
    private Albums album;
    private ArrayList<Albums> albums;
    private ArrayList<AlbumsSections> albumsSections;

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
        boolean letter;
        for(int i = 0; i < 27; i++)
        {
            ArrayList<Albums> sections = new ArrayList<>();
            for(int j = 0; j < albums.size(); j++)
            {
                String album = albums.get(j).getAlbum();
                char sectionLetter = album.charAt(0);
                letter = Character.isLetter(sectionLetter);
                addAlbumToSection(sections, i, j, sectionLetter, letter);
            }

            albumsSections.add(new AlbumsSections(sections, null));
        }
    }

    private void addAlbumToSection(ArrayList<Albums> sections, int index, int position, Character sectionLetter, boolean letter)
    {
        switch (index)
        {
            case 0:
                if(!letter)
                    sections.add(albums.get(position));
                break;
            case 1:
                if(sectionLetter.equals('A') || sectionLetter.equals('a'))
                    sections.add(albums.get(position));
                break;
            case 2:
                if(sectionLetter.equals('B') || sectionLetter.equals('b'))
                    sections.add(albums.get(position));
                break;
            case 3:
                if(sectionLetter.equals('C') || sectionLetter.equals('c'))
                    sections.add(albums.get(position));
                break;
            case 4:
                if(sectionLetter.equals('D') || sectionLetter.equals('d'))
                    sections.add(albums.get(position));
                break;
            case 5:
                if(sectionLetter.equals('E') || sectionLetter.equals('e'))
                    sections.add(albums.get(position));
                break;
            case 6:
                if(sectionLetter.equals('F') || sectionLetter.equals('f'))
                    sections.add(albums.get(position));
                break;
            case 7:
                if(sectionLetter.equals('G') || sectionLetter.equals('g'))
                    sections.add(albums.get(position));
                break;
            case 8:
                if(sectionLetter.equals('H') || sectionLetter.equals('h'))
                    sections.add(albums.get(position));
                break;
            case 9:
                if(sectionLetter.equals('I') || sectionLetter.equals('i'))
                    sections.add(albums.get(position));
                break;
            case 10:
                if(sectionLetter.equals('J') || sectionLetter.equals('j'))
                    sections.add(albums.get(position));
                break;
            case 11:
                if(sectionLetter.equals('K') || sectionLetter.equals('k'))
                    sections.add(albums.get(position));
                break;
            case 12:
                if(sectionLetter.equals('L') || sectionLetter.equals('l'))
                    sections.add(albums.get(position));
                break;
            case 13:
                if(sectionLetter.equals('M') || sectionLetter.equals('m'))
                    sections.add(albums.get(position));
                break;
            case 14:
                if(sectionLetter.equals('N') || sectionLetter.equals('n'))
                    sections.add(albums.get(position));
                break;
            case 15:
                if(sectionLetter.equals('O') || sectionLetter.equals('o'))
                    sections.add(albums.get(position));
                break;
            case 16:
                if(sectionLetter.equals('P') || sectionLetter.equals('p'))
                    sections.add(albums.get(position));
                break;
            case 17:
                if(sectionLetter.equals('Q') || sectionLetter.equals('q'))
                    sections.add(albums.get(position));
                break;
            case 18:
                if(sectionLetter.equals('R') || sectionLetter.equals('r'))
                    sections.add(albums.get(position));
                break;
            case 19:
                if(sectionLetter.equals('S') || sectionLetter.equals('s'))
                    sections.add(albums.get(position));
                break;
            case 20:
                if(sectionLetter.equals('T') || sectionLetter.equals('t'))
                    sections.add(albums.get(position));
                break;
            case 21:
                if(sectionLetter.equals('U') || sectionLetter.equals('u'))
                    sections.add(albums.get(position));
                break;
            case 22:
                if(sectionLetter.equals('V') || sectionLetter.equals('v'))
                    sections.add(albums.get(position));
                break;
            case 23:
                if(sectionLetter.equals('W') || sectionLetter.equals('w'))
                    sections.add(albums.get(position));
                break;
            case 24:
                if(sectionLetter.equals('X') || sectionLetter.equals('x'))
                    sections.add(albums.get(position));
                break;
            case 25:
                if(sectionLetter.equals('Y') || sectionLetter.equals('y'))
                    sections.add(albums.get(position));
                break;
            case 26:
                if(sectionLetter.equals('Z') || sectionLetter.equals('z'))
                    sections.add(albums.get(position));
                break;
        }
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
