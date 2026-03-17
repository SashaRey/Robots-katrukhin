package gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class RobotModelTest
{
    private static final double EPS = 1e-6;

    @Test
    public void robotMovesTowardTargetAfterUpdate()
    {
        RobotModel model = new RobotModel(100, 100, 0, 150, 100);

        model.update();

        assertTrue(model.getRobotPositionX() > 100.0);
        assertEquals(100.0, model.getRobotPositionY(), 1e-3);
    }

    @Test
    public void robotDoesNotMoveWhenAlreadyAtTarget()
    {
        RobotModel model = new RobotModel(100, 100, 0, 100, 100);

        model.update();

        assertEquals(100.0, model.getRobotPositionX(), EPS);
        assertEquals(100.0, model.getRobotPositionY(), EPS);
        assertEquals(0.0, model.getRobotDirection(), EPS);
    }

    @Test
    public void robotTurnsShortestWayAcrossZeroAngle()
    {
        RobotModel model = new RobotModel(100, 100, 2 * Math.PI - 0.001, 200, 100);

        model.update();

        assertTrue(model.getRobotDirection() < 0.1);
    }

    @Test
    public void setTargetPositionNotifiesObservers()
    {
        RobotModel model = new RobotModel();
        AtomicInteger notifications = new AtomicInteger(0);
        model.addObserver(new Observer()
        {
            @Override
            public void update(Observable observable, Object arg)
            {
                notifications.incrementAndGet();
            }
        });

        model.setTargetPosition(new Point(300, 200));

        assertEquals(1, notifications.get());
    }

    @Test
    public void updateNotifiesObserversWhenRobotMoves()
    {
        RobotModel model = new RobotModel(100, 100, 0, 150, 100);
        AtomicInteger notifications = new AtomicInteger(0);
        model.addObserver((observable, arg) -> notifications.incrementAndGet());

        model.update();

        assertEquals(1, notifications.get());
    }

    @Test
    public void relativeAngleNormalizationKeepsAngleInMinusPiToPi()
    {
        double normalized = RobotModel.asNormalizedRelativeRadians(-1.5 * Math.PI);

        assertTrue(normalized > -Math.PI);
        assertTrue(normalized <= Math.PI);
        assertEquals(Math.PI / 2, normalized, EPS);
    }
}
