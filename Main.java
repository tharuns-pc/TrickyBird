import javax.swing.*;
public class Main {
    public static void main(String[] args)
    {
        // int fWidth=360;
        // int fheight=640;

        TrickyBird trickybird = new TrickyBird();
        ImageIcon icon = new ImageIcon(Main.class.getResource("./tb_logo.png"));
        JFrame frame = new JFrame("Tricky Bird");
        
        // frame.setSize(fWidth,fheight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon.getImage());
        frame.add(trickybird);


        frame.pack();
        frame.setLocationRelativeTo(null);      
        trickybird.requestFocus();
        frame.setVisible(true);
    }
}