package jj;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.InputStreamReader;
import java.util.Scanner;

public class Integer implements UserType {
    private int value;

    public Integer() {
        this(0);
    }
    
    public Integer(int value) {
        this.value = value;
    }

    @Override
    public String typeName() {
        return "java.Integer";
    }

    @Override
    public Object create() {
        return new Integer(0);
    }

    @Override
    public Object clone() {
        return new Integer(value);
    }

    @Override
    public Object readValue(InputStreamReader in) {
        Scanner scanner = new Scanner(in);
        value = scanner.nextInt();
        return this;
    }

    @Override
    public Object parseValue(String ss) {
        return new Integer(java.lang.Integer.parseInt(ss));
    }

    @JsonIgnore
    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> java.lang.Integer.compare(((Integer)o1).value, ((Integer)o2).value);
    }

    @Override
    public String toString() {
        return java.lang.Integer.toString(value);
    }
}
