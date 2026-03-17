package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class RobotCoordinatesWindow extends JInternalFrame implements Observer
{
    private final RobotModel model;
    private final TextArea coordinatesContent;

    public RobotCoordinatesWindow(RobotModel model)
    {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        this.model.addObserver(this);

        coordinatesContent = new TextArea("");
        coordinatesContent.setEditable(false);
        coordinatesContent.setSize(250, 140);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateCoordinates();
    }

    private void updateCoordinates()
    {
        String content = String.format(
                "x = %.2f%ny = %.2f%nangle = %.4f rad%ntarget = (%d, %d)",
                model.getRobotPositionX(),
                model.getRobotPositionY(),
                model.getRobotDirection(),
                model.getTargetPositionX(),
                model.getTargetPositionY());
        coordinatesContent.setText(content);
        coordinatesContent.invalidate();
    }

    @Override
    public void update(Observable observable, Object arg)
    {
        EventQueue.invokeLater(this::updateCoordinates);
    }
}
