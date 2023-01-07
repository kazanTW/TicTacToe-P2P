import javax.swing.*;
import java.awt.*;

public class Button extends JButton{
    int index;

    Button(int index){
        super();
        this.index=index;
        this.setFont(new Font(null, Font.BOLD, 300 / 16 * 5));
    }
}
