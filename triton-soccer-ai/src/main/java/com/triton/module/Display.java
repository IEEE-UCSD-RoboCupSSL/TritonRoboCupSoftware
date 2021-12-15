package com.triton.module;

import com.triton.config.DisplayConfig;
import com.triton.config.ObjectConfig;
import proto.vision.MessagesRobocupSslDetection;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import proto.vision.MessagesRobocupSslGeometry.SSL_FieldCircularArc;
import proto.vision.MessagesRobocupSslGeometry.SSL_FieldLineSegment;
import proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;
import proto.vision.MessagesRobocupSslGeometry.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.DISPLAY_CONFIG;
import static com.triton.config.Config.OBJECT_CONFIG;
import static com.triton.config.EasyYamlReader.readYaml;
import static com.triton.publisher_consumer.Exchange.RAW_DETECTION;
import static com.triton.publisher_consumer.Exchange.RAW_GEOMETRY;
import static java.awt.BorderLayout.*;
import static java.awt.Color.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData;

public class Display extends Module {
    private static final String MAIN_FRAME_TITLE = "Triton Display";
    private ObjectConfig objectConfig;
    private DisplayConfig displayConfig;
    private JFrame frame;
    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel eastPanel;
    private JPanel westPanel;
    private FieldPanel fieldPanel;

    public Display() throws IOException, TimeoutException {
        super();
        prepareGUI();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        objectConfig = (ObjectConfig) readYaml(OBJECT_CONFIG);
        displayConfig = (DisplayConfig) readYaml(DISPLAY_CONFIG);
    }

    private void prepareGUI() {
        frame = new JFrame(MAIN_FRAME_TITLE);
        frame.setSize(400, 400);
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

        fieldPanel = new FieldPanel();
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.setBackground(BLACK);

        frame.add(northPanel, NORTH);
        frame.add(southPanel, SOUTH);
        frame.add(eastPanel, EAST);
        frame.add(westPanel, WEST);
        frame.add(fieldPanel, CENTER);
        frame.setVisible(true);
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(RAW_GEOMETRY, this::consumeRawGeometryData);
        declareConsume(RAW_DETECTION, this::consumeRawDetectionFrame);
    }

    private void consumeRawGeometryData(Object object) {
        if (object == null) return;
        fieldPanel.setSslGeometryData((SSL_GeometryData) object);
        frame.repaint();
    }

    private void consumeRawDetectionFrame(Object object) {
        if (object == null) return;
        fieldPanel.setSslDetectionFrame((SSL_DetectionFrame) object);
        frame.repaint();
    }

    private class FieldPanel extends JPanel {
        private SSL_GeometryData sslGeometryData;
        private SSL_DetectionFrame sslDetectionFrame;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D graphics2D = (Graphics2D) g;

            if (sslGeometryData != null) {
                try {
                    paintField(graphics2D);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void paintField(Graphics2D graphics2D) throws IOException {
            SSL_GeometryFieldSize sslGeometryFieldSize = sslGeometryData.getField();
            int totalFieldLength = sslGeometryFieldSize.getFieldLength() + sslGeometryFieldSize.getGoalDepth() * 2;
            int fieldWidth = sslGeometryFieldSize.getFieldWidth();

            float xScale = (float) getWidth() / totalFieldLength;
            float yScale = (float) getHeight() / fieldWidth;
            float scale = Math.min(xScale, yScale);

            graphics2D.scale(scale, -scale);
            graphics2D.translate(totalFieldLength / 2, -fieldWidth / 2);

            graphics2D.setColor(DARK_GRAY);
            graphics2D.fillRect(-totalFieldLength / 2, -fieldWidth / 2, totalFieldLength, fieldWidth);

            for (SSL_FieldLineSegment sslFieldLineSegment : sslGeometryFieldSize.getFieldLinesList()) {
                Vector2f p1 = sslFieldLineSegment.getP1();
                Vector2f p2 = sslFieldLineSegment.getP2();

                graphics2D.setColor(WHITE);
                graphics2D.drawLine((int) p1.getX(),
                        (int) p1.getY(),
                        (int) p2.getX(),
                        (int) p2.getY());
            }

            for (SSL_FieldCircularArc sslFieldCircularArc : sslGeometryFieldSize.getFieldArcsList()) {
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

            if (sslDetectionFrame == null) return;

            for (SSL_DetectionBall sslDetectionBall : sslDetectionFrame.getBallsList()) {
                float x = sslDetectionBall.getX();
                float y = sslDetectionBall.getY();
                float radius = objectConfig.getBallRadius();

                graphics2D.setColor(GREEN);
                graphics2D.fillArc((int) (x - radius / 2),
                        (int) (y - radius / 2),
                        (int) radius,
                        (int) radius,
                        0,
                        360);
            }

            for (MessagesRobocupSslDetection.SSL_DetectionRobot sslDetectionRobotYellow : sslDetectionFrame.getRobotsYellowList()) {
                float x = sslDetectionRobotYellow.getX();
                float y = sslDetectionRobotYellow.getY();
                float radius = objectConfig.getYellowRobotRadius();

                graphics2D.setColor(YELLOW);
                graphics2D.fillArc((int) (x - radius / 2),
                        (int) (y - radius / 2),
                        (int) radius,
                        (int) radius,
                        0,
                        360);

                graphics2D.setColor(RED);
                setFont(new Font(displayConfig.getRobotIdFontName(), Font.BOLD, displayConfig.getRobotIdFontSize()));
                AffineTransform orgi = graphics2D.getTransform();
                graphics2D.translate(x, y);
                graphics2D.scale(1, -1);
                graphics2D.drawString(String.valueOf(sslDetectionRobotYellow.getRobotId()), 0, 0);
                graphics2D.setTransform(orgi);
            }

            graphics2D.setColor(BLUE);
            for (SSL_DetectionRobot sslDetectionRobotBlue : sslDetectionFrame.getRobotsBlueList()) {
                float x = sslDetectionRobotBlue.getX();
                float y = sslDetectionRobotBlue.getY();
                float radius = objectConfig.getBlueRobotRadius();

                graphics2D.setColor(BLUE);
                graphics2D.fillArc((int) (x - radius / 2),
                        (int) (y - radius / 2),
                        (int) radius,
                        (int) radius,
                        0,
                        360);

                graphics2D.setColor(CYAN);
                setFont(new Font(displayConfig.getRobotIdFontName(), Font.BOLD, displayConfig.getRobotIdFontSize()));
                AffineTransform orgi = graphics2D.getTransform();
                graphics2D.translate(x, y);
                graphics2D.scale(1, -1);
                graphics2D.drawString(String.valueOf(sslDetectionRobotBlue.getRobotId()), 0, 0);
                graphics2D.setTransform(orgi);
            }
        }

        public void setSslGeometryData(SSL_GeometryData sslGeometryData) {
            this.sslGeometryData = sslGeometryData;
        }

        public void setSslDetectionFrame(SSL_DetectionFrame sslDetectionFrame) {
            this.sslDetectionFrame = sslDetectionFrame;
        }
    }
}
