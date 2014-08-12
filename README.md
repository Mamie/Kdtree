Kdtree
======
This is the week 5 assignment of [Coursera Algorithm, part I] (https://www.coursera.org/course/algs4partI) class.

Summary
-------
This program implements 2d-tree data type to represent a set of points in the unit square, which speeds up the range search method compared with a brute force algorithm (shown in PointSET.java).

PointSET
--------
Use a regular binary search tree to store the point and support only brute force range search method. The API is shown below:

       public class PointSET {
          public PointSET()                               // construct an empty set of points
          public boolean isEmpty()                        // is the set empty?
          public int size()                               // number of points in the set
          public void insert(Point2D p)                   // add the point p to the set (if it is not already in the set)
          public boolean contains(Point2D p)              // does the set contain the point p?
          public void draw()                              // draw all of the points to standard draw
          public Iterable<Point2D> range(RectHV rect)     // all points in the set that are inside the rectangle
          public Point2D nearest(Point2D p)               // a nearest neighbor in the set to p; null if set is empty
       }

KdTree
------
KdTree uses a red-black BST that inserts 2d points alternately based on their x and y corrdinate. This 2d-tree representation allows for effienct range search method. Its API is same as pointSET above.

Details of the assignment requirements can be found [here](http://coursera.cs.princeton.edu/algs4/assignments/kdtree.html). The checklist can be found [here](http://coursera.cs.princeton.edu/algs4/checklists/kdtree.html).
