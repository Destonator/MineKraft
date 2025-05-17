import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Launch implements ActionListener {
    JFrame frame = new JFrame();
    JLabel title = new JLabel("MineKraft");
    JButton button = new JButton("New Game");
    Launch(){
        title.setBounds(100, 10, 300, 50);
        title.setFont(new Font(null, Font.PLAIN, 25));
        frame.add(title);

        button.setBounds(100,100,100,50);
        button.setFocusable(false);
        button.addActionListener(this);
        frame.add(button);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == button){
            frame.dispose();
            NewWindow window = new NewWindow();
        }
    }
}
