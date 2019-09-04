package com.kadme.test;

import java.util.*;

public class OutlineBuilderImpl implements OutlineBuilder {

    @Override
    public Polygon buildOutline(Set<Line> lines) {
        Set<Point> pointHashSet = new HashSet<Point>();
        Point p = null;
        Line[] lineArr = lines.stream().toArray(Line[]::new);


        for(int i = 0; i<lineArr.length; i++){
            for(int j = i+1; j < lineArr.length; j++){
                p = getIntersectPoint(lineArr[i], lineArr[j]);
                if(p!=null) pointHashSet.add(p);
            }
        }
        List<Point> pointList= new ArrayList<Point>();
        for(Line line : lines){
            pointList.add( line.getP1());
            pointList.add( line.getP2());
        }
        Point[] point = pointHashSet.stream().toArray(Point[]::new);
        Set<Point> intHullPoint= convexHull(point,point.length);
        Point[] outPolyPoint = pointList.stream().toArray(Point[]::new);
        Set<Point> outHullPoint= convexHull(outPolyPoint,outPolyPoint.length);
        intHullPoint.addAll(outHullPoint);
        return new Polygon(new ArrayList<Point>(intHullPoint));
    }

    private Point getIntersectPoint(Line line1, Line line2) {
        double line1x1 = line1.getP1().getX();
        double line1x2 = line1.getP2().getX();
        double line1y1 = line1.getP1().getY();
        double line1y2 = line1.getP2().getY();
        double line2x3 = line2.getP1().getX();
        double line2x4 = line2.getP2().getX();
        double line2y3 = line2.getP1().getY();
        double line2y4 = line2.getP2().getY();
        double din = ((line1x1 - line1x2) * (line2y3 - line2y4) - (line1y1 - line1y2) * (line2x3 - line2x4));
        //if Denominator is Zero line never intersect and thus are parallel
        if(din == 0)
            return null;
        //x, y are Intersection point
        double x = ((line1x2 - line1x1) * (line2x3 * line2y4 - line2x4 * line2y3) - (line2x4 - line2x3) * (line1x1 * line1y2 - line1x2 * line1y1)) / din;
        double y = ((line2y3 - line2y4) * (line1x1 * line1y2 - line1x2 * line1y1) - (line1y1 - line1y2) * (line2x3 * line2y4 - line2x4 * line2y3)) / din;

        // Code to check if intersection point are not on line and thus potentially not intersect the Line
        double[] a = {line1x1, line1x2, line2x3, line2x4};
        double[] b = {line1y1, line1y2, line2y3, line2y4};
        double xMax = Arrays.stream(a).max().getAsDouble();
        double xMin = Arrays.stream(a).min().getAsDouble();
        double yMax = Arrays.stream(b).max().getAsDouble();
        double yMin = Arrays.stream(b).min().getAsDouble();
        if ((x <= xMin || x >= xMax) || (y <= yMin || y >= yMax))
            return null;
        else
            return new Point(x,y);
    }

    private double orientation(Point p, Point q, Point r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (val == 0) return 0;
        return (val > 0)? 1: 2;
    }

    private Set<Point> convexHull(Point[] points, int n) {
        if (n < 3) return new HashSet<>();
        Set<Point> hull = new HashSet<>();
        int l = 0;
        for (int i = 1; i < n; i++)
            if (points[i].getX() < points[l].getX())
                l = i;
        int p = l, q;
        do
        {
            hull.add(points[p]);
            q = (p + 1) % n;
            for (int i = 0; i < n; i++)
            {
                if (orientation(points[p], points[i], points[q]) == 2)
                    q = i;
            }
            p = q;
        } while (p != l);
        return hull;
    }

    public static void main(String[] args) {
        Line l1 = new Line(new Point(0, 0), new Point(8, 3));
        Line l2 = new Line(new Point(1, 11), new Point(8, 4));
        Line l3 = new Line(new Point(2, 10), new Point(8, 3));
        Line l4 = new Line(new Point(2, 9), new Point(8, 4));
        Line l5 = new Line(new Point(3, 8), new Point(8, 3));
        Line l6 = new Line(new Point(4, 7), new Point(20, 4));
        Line l7 = new Line(new Point(5, 6), new Point(1, 3));
        Line l8 = new Line(new Point(6, 5), new Point(22, 4));
        Line l9 = new Line(new Point(7, 4), new Point(23, 3));
        Line l10 = new Line(new Point(8, 3), new Point(23, 4));
        Line l11 = new Line(new Point(9, 2), new Point(2, 3));
        Line l12 = new Line(new Point(10, 1), new Point(12, 4));
        List<Line> lineList = Arrays.asList(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12);
        List<Point> ploygonPoint = new OutlineBuilderImpl().buildOutline(new HashSet<>(lineList)).getPoints();
        ploygonPoint.forEach(data -> System.out.println("( x : " + data.getX() + " y : " + data.getY() + " )"));

    }
}
