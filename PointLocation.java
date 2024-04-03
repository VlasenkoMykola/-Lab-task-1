import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Edge {
    Point start, end;

    public Edge(Point start, Point end) {
        this.start = start;
        this.end = end;
    }
}

class Face {
    List<Point> vertices;

    public Face() {
        vertices = new ArrayList<>();
    }
}

public class PointLocation extends JPanel {

    List<Face> faces;
    boolean hackyworkaround_prevent_repeating = false;

    public PointLocation() {
        faces = new ArrayList<>();
    }

    public void readInput(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        Face currentFace = null;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                currentFace = new Face();
                faces.add(currentFace);
            } else {
                String[] coordinates = line.split(" ");
                double x = Double.parseDouble(coordinates[0]);
                double y = Double.parseDouble(coordinates[1]);
                currentFace.vertices.add(new Point(x, y));
            }
        }
        reader.close();
    }

    public boolean isInsideFace(Face face, Point point) {
        int intersectCount = 0;
        List<Edge> edges = getEdges(face);
        for (Edge edge : edges) {
            if (edge.start.y <= point.y && edge.end.y > point.y
                    || edge.start.y > point.y && edge.end.y <= point.y) {
                double intersectX = (point.y - edge.start.y) / (edge.end.y - edge.start.y)
                        * (edge.end.x - edge.start.x) + edge.start.x;
                if (point.x < intersectX) {
                    intersectCount++;
                }
            }
        }
        return intersectCount % 2 == 1;
    }

    private List<Edge> getEdges(Face face) {
        List<Edge> edges = new ArrayList<>();
        List<Point> vertices = face.vertices;
        for (int i = 0; i < vertices.size(); i++) {
            Point start = vertices.get(i);
            Point end = vertices.get((i + 1) % vertices.size());
            edges.add(new Edge(start, end));
        }
        return edges;
    }

    public void add_point_to_window(int test_x, int test_y,Graphics g, Color color_value) {
        Graphics2D g2d = (Graphics2D) g;
        // Draw test point
        g2d.setColor(color_value);
        Point testPoint = new Point(test_x, test_y); // Test point
        g2d.fillOval((int) testPoint.x - 3, (int) testPoint.y - 3, 6, 6);

        if (hackyworkaround_prevent_repeating != true) {
            //test the point
            System.out.println("------------------------------------------");
            checkPointInAllFaces(testPoint);
        }
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Draw faces
        for (Face face : faces) {
            List<Point> vertices = face.vertices;
            int[] xPoints = new int[vertices.size()];
            int[] yPoints = new int[vertices.size()];
            for (int i = 0; i < vertices.size(); i++) {
                xPoints[i] = (int) vertices.get(i).x;
                yPoints[i] = (int) vertices.get(i).y;
            }
            g2d.drawPolygon(xPoints, yPoints, vertices.size());
        }

        add_point_to_window(50,50,g, Color.RED);
        add_point_to_window(300,200,g, Color.BLUE);
        add_point_to_window(200,400,g, Color.getHSBColor(50,255,50) );

        hackyworkaround_prevent_repeating = true;

    }

    public void checkPointInAllFaces (Point test_point) {
        for (Face face : faces) {
            System.out.println("point " + test_point.x + " " + test_point.y + " is in " + face + "? " + isInsideFace(face, test_point));
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Point Location Visualization");
        PointLocation pointLocation = new PointLocation();
        try {
            pointLocation.readInput("faces.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.add(pointLocation);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}