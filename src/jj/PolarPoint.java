package jj;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.InputStreamReader;
import java.util.Scanner;

public class PolarPoint implements UserType {
    private static final double ANGLE_LIMIT = 2 * Math.PI;
    private double distance;
    private double angle;

    public PolarPoint() {
        this(0.0, 0.0);
    }

    public PolarPoint(double distance, double angle) {
        this.distance = distance;
        this.angle = normalizeAngle(angle);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public String typeName() {
        return "Polar point";
    }

    @Override
    public Object create() {
        return new PolarPoint(0.0, 0.0);
    }

    @Override
    public Object clone() {
        return new PolarPoint(distance, angle);
    }

    @Override
    public Object readValue(InputStreamReader in) {
        Scanner scanner = new Scanner(in);
        distance = scanner.nextDouble();
        angle = normalizeAngle(scanner.nextDouble());
        return this;
    }

    @Override
    public Object parseValue(String ss) {
        String[] parts = ss.split(",");
        double angle = Double.parseDouble(parts[1]);
        return new PolarPoint(Double.parseDouble(parts[0].trim()), normalizeAngle(angle));
    }

    @JsonIgnore
    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> {
            PolarPoint p1 = (PolarPoint) o1;
            PolarPoint p2 = (PolarPoint) o2;
            int diff = Double.compare(p1.distance, p2.distance);
            if (diff != 0) {
                return diff;
            }
            return Double.compare(p1.angle, p2.angle);
        };
    }

    @Override
    public String toString() {
        return "(" + String.format("%.2f", distance) + "; " + String.format("%.2f", angle) + ")";
    }

    private double normalizeAngle(double angle) {
        if (angle < 0) {
            double c = Math.floor(-angle / ANGLE_LIMIT);
            angle = (c+1) * ANGLE_LIMIT;
        }
        double c = Math.floor(angle / ANGLE_LIMIT);
        return angle - c * ANGLE_LIMIT;
    }
}
