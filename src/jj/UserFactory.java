package jj;

import java.util.Arrays;
import java.util.List;

public class UserFactory{
    public List<String> getTypeNameList() {
           return Arrays.asList("java.PolarPoint", "java.Integer");
    }

    public UserType getBuilderByName(String name) {
        switch (name) {
            case "java.PolarPoint":
                return new PolarPoint();
            case "java.Integer":
                return new Integer();
            default:
                throw new IllegalArgumentException();
        }
    }
}