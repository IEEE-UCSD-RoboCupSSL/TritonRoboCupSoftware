package com.triton.module;

import com.triton.Module;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_GEOMETRY_DATA_EXCHANGE;
import static java.awt.BorderLayout.*;
import static java.awt.Color.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData;

public class Display extends Module {
    private static final String MAIN_FRAME_TITLE = "Triton Display";

    private JFrame frame;

    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel eastPanel;
    private JPanel westPanel;
    private JPanel centerPanel;

    private JTextArea visionText;

    private SSL_GeometryData sslGeometryData;

    public Display() throws IOException, TimeoutException {
        super();
        prepareGUI();
        declareExchanges();
    }

    public static void main(String[] args) {
        try {
            new Display();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
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

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(GREEN);

        visionText = new JTextArea("Vision Here");
        visionText.setEditable(false);
        visionText.setLineWrap(true);
        visionText.setWrapStyleWord(true);

        centerPanel.add(visionText, CENTER);

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
        declareConsume(SSL_GEOMETRY_DATA_EXCHANGE, this::consume_SSL_GeometryData);
    }

    private void consume_SSL_GeometryData(Object object) {
        sslGeometryData = (SSL_GeometryData) object;
        visionText.setText(sslGeometryData.toString());
    }
}
