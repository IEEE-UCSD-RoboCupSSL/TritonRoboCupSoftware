package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;
import com.triton.search.node2d.Node2d;
import com.triton.search.node2d.PathfindGrid;
import com.triton.util.Vector2d;
import org.apache.commons.math3.analysis.function.Sigmoid;
import proto.triton.ObjectWithMetadata;
import proto.vision.MessagesRobocupSslGeometry.SSL_FieldCircularArc;
import proto.vision.MessagesRobocupSslGeometry.SSL_FieldLineSegment;
import proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;
import proto.vision.MessagesRobocupSslGeometry.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.triton.constant.RuntimeConstants.*;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static java.awt.BorderLayout.*;
import static java.awt.Color.*;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static proto.triton.AiDebugInfo.*;
import static proto.triton.ObjectWithMetadata.Ball;

public class UserInterface extends Module {
    private static final String MAIN_FRAME_TITLE = "Triton Display";
    private JFrame frame;
    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel eastPanel;
    private JPanel westPanel;
    private JPanel centerPanel;
    private FieldPanel fieldPanel;

    public UserInterface() {
        super();
    }

    @Override
    protected void prepare() {
        super.prepare();
        prepareGUI();
    }

    private void prepareGUI() {
        frame = new JFrame(MAIN_FRAME_TITLE);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        northPanel = new JPanel();
        northPanel.setBackground(YELLOW);

        southPanel = new JPanel();
        southPanel.setBackground(MAGENTA);

        eastPanel = new JPanel();
        eastPanel.setBackground(BLUE);

        westPanel = new JPanel();
        westPanel.setBackground(RED);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, Y_AXIS));
        centerPanel.setBackground(BLACK);

        fieldPanel = new FieldPanel();
        fieldPanel.setBackground(WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(fieldPanel, CENTER);
        centerPanel.add(Box.createVerticalGlue());

        frame.add(northPanel, NORTH);
        frame.add(southPanel, SOUTH);
        frame.add(eastPanel, EAST);
        frame.add(westPanel, WEST);
        frame.add(centerPanel, CENTER);
        frame.setVisible(true);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_FILTERED_BALL, this::callbackBall);
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declareConsume(AI_FILTERED_FOES, this::callbackFoes);
        declareConsume(AI_DEBUG, this::callbackDebug);
    }

    private void callbackField(String s, Delivery delivery) {
        SSL_GeometryFieldSize field = (SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
        fieldPanel.setField(field);
        fieldPanel.repaint();
    }

    private void callbackBall(String s, Delivery delivery) {
        Ball ball = (Ball) simpleDeserialize(delivery.getBody());
        fieldPanel.setBall(ball);
        fieldPanel.repaint();
    }

    private void callbackAllies(String s, Delivery delivery) {
        HashMap<Integer, ObjectWithMetadata.Robot> allies = (HashMap<Integer, ObjectWithMetadata.Robot>) simpleDeserialize(delivery.getBody());
        fieldPanel.setAllies(allies);
        fieldPanel.repaint();
    }

    private void callbackFoes(String s, Delivery delivery) {
        HashMap<Integer, ObjectWithMetadata.Robot> foes = (HashMap<Integer, ObjectWithMetadata.Robot>) simpleDeserialize(delivery.getBody());
        fieldPanel.setFoes(foes);
        fieldPanel.repaint();
    }

    private void callbackDebug(String s, Delivery delivery) {
        Debug debug = (Debug) simpleDeserialize(delivery.getBody());
        fieldPanel.addDebug(debug);
    }

    private class FieldPanel extends JPanel {
        private final ArrayList<Debug> debug;
        private final HashMap<Integer, DebugPath> alliesPaths;
        private final ReadWriteLock fieldLock;
        private final ReadWriteLock ballLock;
        private final ReadWriteLock alliesLock;
        private final ReadWriteLock foesLock;
        private final ReadWriteLock debugLock;
        private SSL_GeometryFieldSize field;
        private Ball ball;
        private HashMap<Integer, ObjectWithMetadata.Robot> allies;
        private HashMap<Integer, ObjectWithMetadata.Robot> foes;

        private PathfindGrid pathfindGrid;

        public FieldPanel() {
            debug = new ArrayList<>();

            fieldLock = new ReentrantReadWriteLock();
            ballLock = new ReentrantReadWriteLock();
            alliesLock = new ReentrantReadWriteLock();
            foesLock = new ReentrantReadWriteLock();
            debugLock = new ReentrantReadWriteLock();

            alliesPaths = new HashMap<>();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D graphics2D = (Graphics2D) g;
            try {
                paintField(graphics2D);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized void paintField(Graphics2D graphics2D) throws IOException {
            if (field == null) return;

            fieldLock.readLock().lock();
            try {
                transformGraphics(graphics2D);
                paintGeometry(graphics2D);
            } finally {
                fieldLock.readLock().unlock();
            }

            ballLock.readLock().lock();
            try {
                paintBall(graphics2D);
            } finally {
                ballLock.readLock().unlock();
            }

            alliesLock.readLock().lock();
            foesLock.readLock().lock();
            try {
                paintBots(graphics2D);
            } finally {
                alliesLock.readLock().unlock();
                foesLock.readLock().unlock();
            }

            debugLock.readLock().lock();
            try {
                paintDebug(graphics2D);
            } finally {
                debugLock.readLock().unlock();
            }
        }

        private void transformGraphics(Graphics2D graphics2D) {
            int totalFieldWidth;
            int totalFieldLength;

            totalFieldWidth = field.getFieldWidth()
                    + 2 * field.getBoundaryWidth()
                    + 2 * displayConfig.fieldExtend;
            totalFieldLength = field.getFieldLength()
                    + 2 * field.getGoalDepth()
                    + 2 * field.getBoundaryWidth()
                    + 2 * displayConfig.fieldExtend;

            float xScale = (float) getParent().getWidth() / totalFieldWidth;
            float yScale = (float) getParent().getHeight() / totalFieldLength;
            float minScale = Math.min(xScale, yScale);

            Dimension dimension = new Dimension((int) (totalFieldWidth * minScale), (int) (totalFieldLength * minScale));
            setMinimumSize(dimension);
            setMaximumSize(dimension);
            setPreferredSize(dimension);

            graphics2D.scale(minScale, -minScale);
            graphics2D.translate(totalFieldWidth / 2, -totalFieldLength / 2);
        }

        private void paintGeometry(Graphics2D graphics2D) {
            int totalFieldWidth = field.getFieldWidth()
                    + 2 * field.getBoundaryWidth()
                    + 2 * displayConfig.fieldExtend;
            int totalFieldLength = field.getFieldLength()
                    + 2 * field.getGoalDepth()
                    + 2 * field.getBoundaryWidth()
                    + 2 * displayConfig.fieldExtend;


            graphics2D.setColor(DARK_GRAY);
            graphics2D.fillRect(-totalFieldWidth / 2, -totalFieldLength / 2, totalFieldWidth, totalFieldLength);

            for (SSL_FieldLineSegment sslFieldLineSegment : field.getFieldLinesList()) {
                Vector2f p1 = sslFieldLineSegment.getP1();
                Vector2f p2 = sslFieldLineSegment.getP2();

                graphics2D.setColor(WHITE);
                graphics2D.drawLine((int) p1.getX(),
                        (int) p1.getY(),
                        (int) p2.getX(),
                        (int) p2.getY());
            }

            for (SSL_FieldCircularArc sslFieldCircularArc : field.getFieldArcsList()) {
                Vector2f center = sslFieldCircularArc.getCenter();
                float radius = sslFieldCircularArc.getRadius();
                float a1 = sslFieldCircularArc.getA1();
                float a2 = sslFieldCircularArc.getA2();

                graphics2D.setColor(WHITE);
                graphics2D.drawArc((int) (center.getX() - radius / 2),
                        (int) (center.getY() - radius / 2),
                        (int) radius,
                        (int) radius,
                        (int) Math.toDegrees(a1),
                        (int) Math.toDegrees(a2));
            }
        }

        private void paintBall(Graphics2D graphics2D) {
            if (ball != null) {
                float x = ball.getX();
                float y = ball.getY();
                float radius = objectConfig.ballRadius * 1000;

                graphics2D.setColor(MAGENTA);
                graphics2D.fillArc((int) (x - radius),
                        (int) (y - radius),
                        (int) radius * 2,
                        (int) radius * 2,
                        0,
                        360);

                graphics2D.setColor(BLACK);
                graphics2D.drawArc((int) (x - radius),
                        (int) (y - radius),
                        (int) radius * 2,
                        (int) radius * 2,
                        0,
                        360);
            }
        }

        private void paintBots(Graphics2D graphics2D) {
            if (allies != null) {
                for (ObjectWithMetadata.Robot ally : allies.values()) {
                    Color fillColor;
                    switch (team) {
                        case YELLOW -> fillColor = ORANGE;
                        case BLUE -> fillColor = BLUE;
                        default -> throw new IllegalStateException("Unexpected value: " + team);
                    }
                    paintBot(graphics2D, ally, fillColor, GREEN);
                }
            }

            if (foes != null) {
                for (ObjectWithMetadata.Robot foe : foes.values()) {
                    Color fillColor;
                    switch (team) {
                        case YELLOW -> fillColor = BLUE;
                        case BLUE -> fillColor = ORANGE;
                        default -> throw new IllegalStateException("Unexpected value: " + team);
                    }
                    paintBot(graphics2D, foe, fillColor, RED);
                }
            }
        }

        private void paintBot(Graphics2D graphics2D, ObjectWithMetadata.Robot robot, Color fillColor, Color outlineColor) {
            float x = robot.getX();
            float y = robot.getY();
            float radius = objectConfig.robotRadius * 1000;

            graphics2D.setColor(fillColor);
            graphics2D.fillArc((int) (x - radius),
                    (int) (y - radius),
                    (int) radius * 2,
                    (int) radius * 2,
                    0,
                    360);

            graphics2D.setColor(outlineColor);
            graphics2D.drawArc((int) (x - radius),
                    (int) (y - radius),
                    (int) radius * 2,
                    (int) radius * 2,
                    0,
                    360);

            graphics2D.setColor(BLACK);
            float orientation = robot.getOrientation();
            graphics2D.drawLine((int) x, (int) y, (int) (x + radius * Math.cos(orientation)), (int) (y + radius * Math.sin(orientation)));

            graphics2D.setColor(WHITE);
            setFont(new Font(displayConfig.robotIdFontName, Font.BOLD, displayConfig.robotIdFontSize));
            AffineTransform orgi = graphics2D.getTransform();
            graphics2D.translate(x, y);
            graphics2D.scale(1, -1);
            graphics2D.drawString(String.valueOf(robot.getId()), 0, 0);
            graphics2D.setTransform(orgi);
        }

        private void paintDebug(Graphics2D graphics2D) {
            if (pathfindGrid == null)
                pathfindGrid = new PathfindGrid(field);

            if (displayConfig.showNodeGrid) {
                alliesLock.readLock().lock();
                foesLock.readLock().lock();
                try {
                    pathfindGrid.updateObstacles(allies, foes, null);
                } finally {
                    alliesLock.readLock().unlock();
                    foesLock.readLock().unlock();
                }

                if (displayConfig.showOnlyObstacles) {
                    Set<Node2d> obstacles = pathfindGrid.getObstacles();
                    obstacles.forEach(node -> {
                        paintNode(graphics2D, node);
                    });
                } else {
                    Map<Vector2d, Node2d> nodeMap = pathfindGrid.getNodeMap();
                    nodeMap.forEach((pos, node) -> {
                        paintNode(graphics2D, node);
                    });
                }
            }

            alliesPaths.forEach((id, path) -> {
                if (displayConfig.showRoute) {
                    List<DebugVector> nodes = path.getNodesList();
                    graphics2D.setColor(YELLOW);
                    for (int i = 1; i < nodes.size(); i++) {
                        DebugVector prevNode = nodes.get(i - 1);
                        DebugVector currentNode = nodes.get(i);
                        graphics2D.drawLine((int) prevNode.getX(),
                                (int) prevNode.getY(),
                                (int) currentNode.getX(),
                                (int) currentNode.getY());
                    }
                }

                DebugVector fromPos = path.getFromPos();
                DebugVector toPos = path.getToPos();
                DebugVector nextPos = path.getNextPos();

                if (displayConfig.showNext) {
                    graphics2D.setColor(GREEN);
                    graphics2D.drawLine((int) fromPos.getX(),
                            (int) fromPos.getY(),
                            (int) nextPos.getX(),
                            (int) nextPos.getY());
                }

                if (displayConfig.showTo) {
                    graphics2D.setColor(RED);
                    graphics2D.drawLine((int) nextPos.getX(),
                            (int) nextPos.getY(),
                            (int) toPos.getX(),
                            (int) toPos.getY());
                }
            });
        }

        private void paintNode(Graphics2D graphics2D, Node2d node) {
            Color color;
            if (node.getPenalty() == 0)
                color = BLACK;
            else {
                Sigmoid sigmoid = new Sigmoid();
                float scaled = (float) sigmoid.value(node.getPenalty() / aiConfig.obstacleScale);
                color = Color.getHSBColor(scaled, scaled, scaled);
            }
            graphics2D.setColor(color);

            float x = node.getPos().x;
            float y = node.getPos().y;
            float radius = aiConfig.nodeRadius;
            graphics2D.drawArc((int) (x - radius),
                    (int) (y - radius),
                    (int) radius * 2,
                    (int) radius * 2,
                    0,
                    360);
        }

        public void setField(SSL_GeometryFieldSize field) {
            fieldLock.writeLock().lock();
            try {
                this.field = field;
            } finally {
                fieldLock.writeLock().unlock();
            }
        }

        public void setBall(Ball ball) {
            ballLock.writeLock().lock();
            try {
                this.ball = ball;
            } finally {
                ballLock.writeLock().unlock();
            }
        }

        public void setAllies(HashMap<Integer, ObjectWithMetadata.Robot> allies) {
            foesLock.writeLock().lock();
            try {
                this.allies = allies;
            } finally {
                foesLock.writeLock().unlock();
            }
        }

        public void setFoes(HashMap<Integer, ObjectWithMetadata.Robot> foes) {
            foesLock.writeLock().lock();
            try {
                this.foes = foes;
            } finally {
                foesLock.writeLock().unlock();
            }
        }

        public void addDebug(Debug debug) {
            debugLock.writeLock().lock();
            try {
                this.debug.add(debug);
                if (debug.hasPath()) {
                    DebugPath path = debug.getPath();
                    alliesPaths.put(path.getId(), path);
                }
            } finally {
                debugLock.writeLock().unlock();
            }
        }
    }
}
