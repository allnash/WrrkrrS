// Copyright 2018 OmegaTrace Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License

package models;

import play.Logger;

/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *
 *  Implementation of 2D point using rectangular coordinates.
 *
 *  Copyright © 2000–2017, Robert Sedgewick and Kevin Wayne.
 *  Last updated: Fri Oct 20 12:50:46 EDT 2017.
 *
 ******************************************************************************/

public class Point {
    public final int x;
    public final int y;

    // create and initialize a point with given (x, y)
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // return Euclidean distance between this point and that point
    public double distanceTo(Point that) {
        if (that == null) return Double.POSITIVE_INFINITY;
        double dx = this.x - that.x;
        double dy = this.y - that.y;
        return Math.hypot(dx, dy);
    }

    // draw point
    //public void draw() {
    //    StdDraw.point(x, y);
    //}

    // draw line segment between this point and that point
    //public void drawTo(Point that) {
    //    StdDraw.line(this.x, this.y, that.x, that.y);
    //}

    // is a->b->c a counter-clockwise turn?
    // +1 if counter-clockwise, -1 if clockwise, 0 if collinear
    public static int ccw(Point a, Point b, Point c) {
        // return a.x*b.y - a.y*b.x + a.y*c.x - a.x*c.y + b.x*c.y - b.y*c.x;
        double area2 = (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
        if      (area2 < 0) return -1;
        else if (area2 > 0) return +1;
        else                return  0;
    }

    // is a-b-c collinear?
    public static boolean collinear(Point a, Point b, Point c) {
        return ccw(a, b, c) == 0;
    }

    // is c between a and b?
    // Reference: O' Rourke p. 32
    public static boolean between(Point a, Point b, Point c) {
        if (ccw(a, b, c) != 0) return false;
        if (a.x == b.x && a.y == b.y) {
            return a.x == c.x && a.y == c.y;
        }
        else if (a.x != b.x) {
            // ab not vertical
            return (a.x <= c.x && c.x <= b.x) || (a.x >= c.x && c.x >= b.x);
        }
        else {
            // ab not horizontal
            return (a.y <= c.y && c.y <= b.y) || (a.y >= c.y && c.y >= b.y);
        }
    }


    // return string representation of this point
    public String toString() {
        return "(" + x + ", " + y + ")";
    }


    // test client
    public static void main(String[] args) {
        Point p = new Point(5, 6);
        Logger.info("p  = " + p);
        Point q = new Point(2, 2);
        Logger.info("q  = " + q);
        Logger.info("dist(p, q) = " + p.distanceTo(q) + " = " + q.distanceTo(p));
    }
}