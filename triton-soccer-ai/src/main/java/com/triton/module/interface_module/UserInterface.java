package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.config.DisplayConfig;
import com.triton.config.ObjectConfig;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
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

import static com.triton.config.ConfigPath.DISPLAY_CONFIG;
import static com.triton.config.ConfigPath.OBJECT_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static java.awt.BorderLayout.*;
import static java.awt.Color.*;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class UserInterface extends Module {
    private static final String MAIN_FRAME_TITLE = "Triton Display";
    private ObjectConfig objectConfig;
    private DisplayConfig displayConfig;
    private JFrame frame;
    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel eastPanel;
    private JPanel westPanel;
    private JPanel centerPanel;
    private FieldPanel fieldPanel;

    public UserInterface() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        objectConfig = (ObjectConfig) readConfig(OBJECT_CONFIG);
        displayConfig = (DisplayConfig) readConfig(DISPLAY_CONFIG);
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
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_FIELD, this::callbackBiasedField);
        declareConsume(AI_BIASED_BALLS, this::callbackBiasedBalls);
        declareConsume(AI_BIASED_ALLIES, this::callbackBiasedAllies);
        declareConsume(AI_BIASED_FOES, this::callbackBiasedFoes);
    }

    private void callbackBiasedField(String s, Delivery delivery) {
        SSL_GeometryFieldSize field;
        try {
            field = (SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        fieldPanel.setField(field);
        frame.repaint();
    }

    private void callbackBiasedBalls(String s, Delivery delivery) {
        ArrayList<SSL_DetectionBall> balls;
        try {
            balls = (ArrayList<SSL_DetectionBall>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        fieldPanel.setBalls(balls);
        frame.repaint();
    }

    private void callbackBiasedAllies(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> allies;
        try {
            allies = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        fieldPanel.setAllies(allies);
        frame.repaint();
    }

    private void callbackBiasedFoes(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> foes;
        try {
            foes = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        fieldPanel.setFoes(foes);
        frame.repaint();
    }

    private class FieldPanel extends JPanel {
        private SSL_GeometryFieldSize field;
        private List<SSL_DetectionBall> balls;
        private HashMap<Integer, SSL_DetectionRobot> allies;
        private HashMap<Integer, SSL_DetectionRobot> foes;

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

        private void paintField(Graphics2D graphics2D) throws IOException {
            if (field == null) return;

            transformGraphics(graphics2D, field);
            paintGeometry(graphics2D, field);
            paintBalls(graphics2D, balls);
            paintBots(graphics2D, allies, foes);
        }

        private void transformGraphics(Graphics2D graphics2D, SSL_GeometryFieldSize field) {
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

        private void paintGeometry(Graphics2D graphics2D, SSL_GeometryFieldSize field) {
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

        private void paintBalls(Graphics2D graphics2D, List<SSL_DetectionBall> balls) {
            if (balls != null) {
                for (SSL_DetectionBall sslDetectionBall : balls) {
                    float x = sslDetectionBall.getX();
                    float y = sslDetectionBall.getY();
                    float radius = objectConfig.ballRadius;

                    graphics2D.setColor(MAGENTA);
                    graphics2D.fillArc((int) (x - radius / 2),
                            (int) (y - radius / 2),
                            (int) radius,
                            (int) radius,
                            0,
                            360);

                    graphics2D.setColor(BLACK);
                    graphics2D.drawArc((int) (x - radius / 2),
                            (int) (y - radius / 2),
                            (int) radius,
                            (int) radius,
                            0,
                            360);
                }
            }
        }

        private void paintBots(Graphics2D graphics2D, HashMap<Integer, SSL_DetectionRobot> allies, HashMap<Integer, SSL_DetectionRobot> foes) {
            if (allies != null) {
                for (SSL_DetectionRobot ally : allies.values()) {

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
                for (SSL_DetectionRobot foe : foes.values()) {
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

        private void paintBot(Graphics2D graphics2D, SSL_DetectionRobot bot, Color fillColor, Color outlineColor) {
            float x = bot.getX();
            float y = bot.getY();
            float radius = objectConfig.robotRadius;

            graphics2D.setColor(fillColor);
            graphics2D.fillArc((int) (x - radius / 2),
                    (int) (y - radius / 2),
                    (int) radius,
                    (int) radius,
                    0,
                    360);

            graphics2D.setColor(outlineColor);
            graphics2D.drawArc((int) (x - radius / 2),
                    (int) (y - radius / 2),
                    (int) radius,
                    (int) radius,
                    0,
                    360);

            graphics2D.setColor(BLACK);
            float orientation = bot.getOrientation();
            graphics2D.drawLine((int) x, (int) y, (int) (x + radius * Math.cos(orientation)), (int) (y + radius * Math.sin(orientation)));

            graphics2D.setColor(WHITE);
            setFont(new Font(displayConfig.botIdFontName, Font.BOLD, displayConfig.botIdFontSize));
            AffineTransform orgi = graphics2D.getTransform();
            graphics2D.translate(x, y);
            graphics2D.scale(1, -1);
            graphics2D.drawString(String.valueOf(bot.getRobotId()), 0, 0);
            graphics2D.setTransform(orgi);
        }

        public void setField(SSL_GeometryFieldSize field) {
            this.field = field;
        }

        public void setBalls(ArrayList<SSL_DetectionBall> balls) {
            this.balls = balls;
        }

        public void setAllies(HashMap<Integer, SSL_DetectionRobot> allies) {
            this.allies = allies;
        }

        public void setFoes(HashMap<Integer, SSL_DetectionRobot> foes) {
            this.foes = foes;
        }
    }
}
