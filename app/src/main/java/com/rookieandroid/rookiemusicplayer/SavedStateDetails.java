package com.rookieandroid.rookiemusicplayer;

public class SavedStateDetails
{
    private int id;
    private int position;
    private int shuffle;
    private int repeat;
    private int state;
    private int elapsed;
    private int duration;
    private String now_playing_from;
    private int from;

    public SavedStateDetails(int id, int position, int shuffle, int repeat, int state, int elapsed, int duration, String now_playing_from, int from)
    {
        this.id = id;
        this.position = position;
        this.shuffle = shuffle;
        this.repeat = repeat;
        this.state = state;
        this.elapsed = elapsed;
        this.duration = duration;
        this.now_playing_from = now_playing_from;
        this.from = from;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getPosition() { return position; }

    public void setPosition(int position) { this.position = position; }

    public int getShuffle() { return shuffle; }

    public void setShuffle(int shuffle) { this.shuffle = shuffle; }

    public int getRepeat() { return repeat; }

    public void setRepeat(int repeat) { this.repeat = repeat; }

    public int getState() { return state; }

    public void setState(int state) { this.state = state; }

    public int getElapsed() { return elapsed; }

    public void setElapsed(int elapsed) { this.elapsed = elapsed; }

    public int getDuration() { return duration; }

    public void setDuration(int duration) { this.duration = duration; }

    public String getNow_playing_from() { return now_playing_from; }

    public void setNow_playing_from(String now_playing_from) { this.now_playing_from = now_playing_from; }

    public int getFrom() { return from; }

    public void setFrom(int from) { this.from = from; }
}
