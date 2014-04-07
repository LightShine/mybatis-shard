package org.lysu.shard.example.model;

/**
 * @author lysu created on 14-4-6 下午4:32
 * @version $Id$
 */
public class Test {

    private int id;

    private int a;

    private int b;

    private int c;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "Test{" + "id=" + id + ", a=" + a + ", b=" + b + ", c=" + c + '}';
    }

}
