package gui;

import java.awt.Point;
import java.util.Observable;

public class RobotModel extends Observable
{
    private static final double DEFAULT_X = 100;
    private static final double DEFAULT_Y = 100;
    private static final double DEFAULT_DIRECTION = 0;
    private static final int DEFAULT_TARGET_X = 150;
    private static final int DEFAULT_TARGET_Y = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;
    private static final double UPDATE_DURATION = 10;
    private static final double TARGET_REACHED_DISTANCE = 0.5;

    private double robotPositionX;
    private double robotPositionY;
    private double robotDirection;

    private int targetPositionX;
    private int targetPositionY;

    public RobotModel()
    {
        this(DEFAULT_X, DEFAULT_Y, DEFAULT_DIRECTION, DEFAULT_TARGET_X, DEFAULT_TARGET_Y);
    }

    public RobotModel(double robotPositionX, double robotPositionY, double robotDirection,
                      int targetPositionX, int targetPositionY)
    {
        this.robotPositionX = robotPositionX;
        this.robotPositionY = robotPositionY;
        this.robotDirection = asNormalizedRadians(robotDirection);
        this.targetPositionX = targetPositionX;
        this.targetPositionY = targetPositionY;
    }

    public synchronized double getRobotPositionX()
    {
        return robotPositionX;
    }

    public synchronized double getRobotPositionY()
    {
        return robotPositionY;
    }

    public synchronized double getRobotDirection()
    {
        return robotDirection;
    }

    public synchronized int getTargetPositionX()
    {
        return targetPositionX;
    }

    public synchronized int getTargetPositionY()
    {
        return targetPositionY;
    }

    public synchronized void setTargetPosition(Point point)
    {
        targetPositionX = point.x;
        targetPositionY = point.y;
        notifyModelChanged();
    }

    public synchronized void update()
    {
        double distanceToTarget = distance(targetPositionX, targetPositionY, robotPositionX, robotPositionY);
        if (distanceToTarget < TARGET_REACHED_DISTANCE)
        {
            return;
        }

        double angleToTarget = angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
        double angleDiff = asNormalizedRelativeRadians(angleToTarget - robotDirection);
        double angularVelocity = 0;

        if (angleDiff > 0)
        {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        else if (angleDiff < 0)
        {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        moveRobot(MAX_VELOCITY, angularVelocity, UPDATE_DURATION);
        notifyModelChanged();
    }

    private void notifyModelChanged()
    {
        setChanged();
        notifyObservers();
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
        {
            return min;
        }
        if (value > max)
        {
            return max;
        }
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = robotPositionX + velocity / angularVelocity *
                (Math.sin(robotDirection + angularVelocity * duration) - Math.sin(robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }

        double newY = robotPositionY - velocity / angularVelocity *
                (Math.cos(robotDirection + angularVelocity * duration) - Math.cos(robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }

        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);
    }

    static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI)
        {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    static double asNormalizedRelativeRadians(double angle)
    {
        while (angle <= -Math.PI)
        {
            angle += 2 * Math.PI;
        }
        while (angle > Math.PI)
        {
            angle -= 2 * Math.PI;
        }
        return angle;
    }
}
