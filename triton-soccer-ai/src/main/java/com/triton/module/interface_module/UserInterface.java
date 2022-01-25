package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.search.node2d.Node2d;
import proto.triton.ObjectWithMetadata;
import proto.vision.MessagesRobocupSslGeometry.SSL_FieldCircularArc;
import proto.vision.MessagesRobocupSslGeometry.SSL_FieldLineSegment;
import proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;
import proto.vision.MessagesRobocupSslGeometry.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

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
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
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
        frame.repaint();
    }

    private class FieldPanel extends JPanel {
        private SSL_GeometryFieldSize field;
        private Ball ball;
        private HashMap<Integer, ObjectWithMetadata.Robot> allies;
        private HashMap<Integer, ObjectWithMetadata.Robot> foes;
        private final ArrayList<Debug> debug;
        private final HashMap<Integer, DebugPath> alliesPaths;

        public FieldPanel() {
            debug = new ArrayList<>();
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

            transformGraphics(graphics2D);
            paintGeometry(graphics2D);
            paintBall(graphics2D);
            paintBots(graphics2D);
            paintDebug(graphics2D);
        }

        private void transformGraphics(Graphics2D graphics2D) {
            int totalFieldWidth = field.getFieldWidth() + 2 * field.getBoundaryWidth();
            int totalFieldLength = field.getFieldLength() + field.getGoalDepth() * 2 + 2 * field.getBoundaryWidth();

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
            int totalFieldWidth = field.getFieldWidth() + 2 * field.getBoundaryWidth();
            int totalFieldLength = field.getFieldLength() + field.getGoalDepth() * 2 + 2 * field.getBoundaryWidth();

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
                float radius = RuntimeConstants.objectConfig.ballRadius * 1000;

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
                    switch (RuntimeConstants.team) {
                        case YELLOW -> fillColor = ORANGE;
                        case BLUE -> fillColor = BLUE;
                        default -> throw new IllegalStateException("Unexpected value: " + RuntimeConstants.team);
                    }
                    paintBot(graphics2D, ally, fillColor, GREEN);
                }
            }

            if (foes != null) {
                for (ObjectWithMetadata.Robot foe : foes.values()) {
                    Color fillColor;
                    switch (RuntimeConstants.team) {
                        case YELLOW -> fillColor = BLUE;
                        case BLUE -> fillColor = ORANGE;
                        default -> throw new IllegalStateException("Unexpected value: " + RuntimeConstants.team);
                    }
                    paintBot(graphics2D, foe, fillColor, RED);
                }
            }
        }

        private void paintBot(Graphics2D graphics2D, ObjectWithMetadata.Robot robot, Color fillColor, Color outlineColor) {
            float x = robot.getX();
            float y = robot.getY();
            float radius = RuntimeConstants.objectConfig.robotRadius * 1000;

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
            setFont(new Font(RuntimeConstants.displayConfig.botIdFontName, Font.BOLD, RuntimeConstants.displayConfig.botIdFontSize));
            AffineTransform orgi = graphics2D.getTransform();
            graphics2D.translate(x, y);
            graphics2D.scale(1, -1);
            graphics2D.drawString(String.valueOf(robot.getId()), 0, 0);
            graphics2D.setTransform(orgi);
        }

        private void paintDebug(Graphics2D graphics2D) {
//            PathfindField pathfindField = new PathfindField(field);

//            Map<Vector2d, Node2d> nodeMap = pathfindField.getNodeMap();
//            nodeMap.forEach((pos, node) -> {
//                if (node.isObstacle())
//                    paintNode(graphics2D, node, RED);
//                else
//                    paintNode(graphics2D, node, BLACK);
//            });

            alliesPaths.forEach((id, path) -> {
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

                DebugVector fromPos = path.getFromPos();
                DebugVector toPos = path.getToPos();
                DebugVector nextPos = path.getNextPos();
                graphics2D.setColor(GREEN);
                graphics2D.drawLine((int) fromPos.getX(),
                        (int) fromPos.getY(),
                        (int) nextPos.getX(),
                        (int) nextPos.getY());

                graphics2D.setColor(RED);
                graphics2D.drawLine((int) nextPos.getX(),
                        (int) nextPos.getY(),
                        (int) toPos.getX(),
                        (int) toPos.getY());
            });
        }

        private void paintNode(Graphics2D graphics2D, Node2d node, Color color) {
            graphics2D.setColor(color);

            float x = node.getPos().x;
            float y = node.getPos().y;
            float radius = RuntimeConstants.aiConfig.nodeRadius;
            graphics2D.drawArc((int) (x - radius),
                    (int) (y - radius),
                    (int) radius * 2,
                    (int) radius * 2,
                    0,
                    360);
        }

        public void setField(SSL_GeometryFieldSize field) {
            this.field = field;
        }

        public void setBall(Ball ball) {
            this.ball = ball;
        }

        public void setAllies(HashMap<Integer, ObjectWithMetadata.Robot> allies) {
            this.allies = allies;
        }

        public void setFoes(HashMap<Integer, ObjectWithMetadata.Robot> foes) {
            this.foes = foes;
        }

        public synchronized void addDebug(Debug debug) {
            this.debug.add(debug);
            if (debug.hasPath()) {
                DebugPath path = debug.getPath();
                alliesPaths.put(path.getId(), path);
            }
        }
    }
}
