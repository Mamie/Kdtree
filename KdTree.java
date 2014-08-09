public class KdTree {

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node left;
        private Node right;

        public Node(Point2D p, RectHV rect) {
            RectHV r = rect;
            if (r == null)
                r = new RectHV(0, 0, 1, 1);
            this.rect = r;
            this.p = p;
        }
    }

    private Node root;
    private int size;

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() { return root == null; }

    // number of points in the set
    public int size() { return size; }

    // larger or equal keys go right
    private Node insertH(Node x, Point2D p, RectHV rect) {
        if (x == null) {
            size++;
            return new Node(p, rect);
        }
        if (x.p.equals(p)) return x;

        RectHV r;
        int cmp = Point2D.Y_ORDER.compare(x.p, p);
        if (cmp > 0) {
            if (x.left == null)
                r = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), x.p.y());
            else
                r = x.left.rect;
            x.left = insertV(x.left, p, r);
        } else {
            if (x.right == null)
                r = new RectHV(rect.xmin(), x.p.y(), rect.xmax(), rect.ymax());
            else
                r = x.right.rect;
            x.right = insertV(x.right, p, r);
        }

        return x;
    }

    // larger or equal keys go right
    private Node insertV(Node x, Point2D p, RectHV rect) {
        if (x == null) {
            size++;
            return new Node(p, rect);
        }
        if (x.p.equals(p)) return x;

        RectHV r;
        int cmp = Point2D.X_ORDER.compare(x.p, p);
        if (cmp > 0) {
            if (x.left == null)
                r = new RectHV(rect.xmin(), rect.ymin(), x.p.x(), rect.ymax());
            else
                r = x.left.rect;
            x.left = insertH(x.left, p, r);
        } else {
            if (x.right == null)
                r = new RectHV(x.p.x(), rect.ymin(), rect.xmax(), rect.ymax());
            else
                r = x.right.rect;
            x.right = insertH(x.right, p, r);
        }

        return x;
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (isEmpty())
            root = insertV(root, p, null);
        else
            root = insertV(root, p, root.rect);
    }

    // larger or equal keys go right
    private boolean contains(Node x, Point2D p, boolean vert) {
        if (x == null) return false;
        if (x.p.equals(p)) return true;
        int cmp;
        if (vert) cmp = Point2D.X_ORDER.compare(x.p, p);
        else cmp = Point2D.Y_ORDER.compare(x.p, p);
        if (cmp > 0) return contains(x.left, p, !vert);
        else return contains(x.right, p, !vert);
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return contains(root, p, true);
    }

    private void draw(Node x, boolean vert) {
        if (x.left != null) draw(x.left, !vert);
        if (x.right != null) draw(x.right, !vert);

        // draw the point first
        StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(.01);
            StdDraw.point(x.p.x(), x.p.y());

            // draw the line
            double xmin, ymin, xmax, ymax;
            if (vert) {
                StdDraw.setPenColor(StdDraw.RED);
                xmin = x.p.x();
                xmax = x.p.x();
                ymin = x.rect.ymin();
                ymax = x.rect.ymax();
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                ymin = x.p.y();
                ymax = x.p.y();
                xmin = x.rect.xmin();
                xmax = x.rect.xmax();
            }
            StdDraw.setPenRadius();
            StdDraw.line(xmin, ymin, xmax, ymax);
    }

    // draw all of the points to standard draw
    public void draw() {
        // draw the rectangle
        StdDraw.rectangle(0.5, 0.5, 0.5, 0.5);
        if (isEmpty()) return;
        draw(root, true);
    }

    private void range(Node x, RectHV rect, Queue<Point2D> q) {
        if (x == null)
            return;
        if (rect.contains(x.p))
            q.enqueue(x.p);
        if (x.left != null && rect.intersects(x.left.rect))
            range(x.left, rect, q);
        if (x.right != null && rect.intersects(x.right.rect))
            range(x.right, rect, q);
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> q = new Queue<Point2D>();
        range(root, rect, q);
        return q;
    }

    private Point2D nearest(Node x, Point2D p, Point2D mp, boolean vert) {
        Point2D min = mp;

        if (x == null) return min;
        if (p.distanceSquaredTo(x.p) < p.distanceSquaredTo(min))
            min = x.p;

        // choose the side that contains the query point first
        if (vert) {
            if (x.p.x() < p.x()) {
                min = nearest(x.right, p, min, !vert);
                if (x.left != null
                        && (min.distanceSquaredTo(p)
                            > x.left.rect.distanceSquaredTo(p)))
                    min = nearest(x.left, p, min, !vert);
            } else {
                min = nearest(x.left, p, min, !vert);
                if (x.right != null
                        && (min.distanceSquaredTo(p)
                         > x.right.rect.distanceSquaredTo(p)))
                    min = nearest(x.right, p, min, !vert);
            }
        } else {
            if (x.p.y() < p.y()) {
                min = nearest(x.right, p, min, !vert);
                if (x.left != null
                        && (min.distanceSquaredTo(p)
                            > x.left.rect.distanceSquaredTo(p)))
                    min = nearest(x.left, p, min, !vert);
            } else {
                min = nearest(x.left, p, min, !vert);
                if (x.right != null
                        && (min.distanceSquaredTo(p)
                            > x.right.rect.distanceSquaredTo(p)))
                    min = nearest(x.right, p, min, !vert);
            }
        }
        return min;
    }

    // a nearest neighbor in the set to p: null if set is empty
    public Point2D nearest(Point2D p) {
        if (isEmpty()) return null;
        return nearest(root, p, root.p, true);
    }
}