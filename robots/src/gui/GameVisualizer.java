package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements Observer
{
    private final Timer timer = initTimer();
    private final RobotModel model;

    private static Timer initTimer()
    {
        return new Timer("events generator", true);
    }

    public GameVisualizer(RobotModel model)
    {
        this.model = model;
        this.model.addObserver(this);

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setTargetPosition(e.getPoint());
            }
        });
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point point)
    {
        model.setTargetPosition(point);
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent()
    {
        model.update();
    }

    private static int round(double value)
    {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics graphics)
    {
        super.paint(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        drawRobot(g2d, round(model.getRobotPositionX()), round(model.getRobotPositionY()), model.getRobotDirection());
        drawTarget(g2d, model.getTargetPositionX(), model.getTargetPositionY());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        AffineTransform oldTransform = g.getTransform();
        AffineTransform rotateTransform = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(rotateTransform);
        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
        g.setTransform(oldTransform);
    }

    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform oldTransform = g.getTransform();
        g.setTransform(AffineTransform.getRotateInstance(0, 0, 0));
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
        g.setTransform(oldTransform);
    }

    @Override
    public void update(Observable observable, Object arg)
    {
        onRedrawEvent();
    }
}
