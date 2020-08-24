package com.example.project_toilet;

public class TotiInfo {

    private String name;        //ex 1층 남자화장실
    private int num;            //칸 갯수
    private int sex;        //성별
    private float remain[];       //남은 휴지의 %배열

    public TotiInfo(String name, int num, int sex, float[] remain)
    {
        this.name = name;
        this.num = num;
        this.remain = new float[num];
        this.sex = sex;
        deepCopy(this.remain, remain);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float[] getRemain() {
        return remain;
    }

    public void setRemain(float[] remain) {
        this.remain = remain;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    private void deepCopy(float[] target, float[] source)
    {
        for(int i=0; i<target.length; i++)
        {
            target[i] = source[i];
        }
    }
}
