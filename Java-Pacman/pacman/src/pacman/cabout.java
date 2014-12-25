package pacman;

import java.awt.*;
import java.awt.event.*;

class cabout extends Window
        implements MouseListener {
    private static final long serialVersionUID = -6444989674095739037L;

    final String[] about = {
            "",
            "Pacman, copyright 2014, CSF, group 3.2"

    };

    cabout(Frame parent) {
        super(parent);

        setSize(420, 280);
        setLocation(100, 100);
        show();

        addMouseListener(this);
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.setFont(new Font("Helvetica", Font.BOLD, 12));
        for (int i = 0; i < about.length; i++)
            g.drawString(about[i], 6, (i + 1) * 18);
    }

    public void mouseClicked(MouseEvent e) {
        dispose();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}



