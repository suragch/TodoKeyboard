package net.studymongolian.todochimee;

public class Word {

    private long id;
    private String word;
    private int frequency;
    private String following;

    Word(String word) {
        this.word = word;
        this.frequency = 1;
        this.following = "";
    }

    public void incrementFrequency() {
        frequency++;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public long getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getWord() {
        return word;
    }

    public String getFollowing() {
        return following;
    }
}