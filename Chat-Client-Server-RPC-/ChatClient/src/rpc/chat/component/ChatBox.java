package rpc.chat.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;

public class ChatBox extends JComponent {

	private final BoxType boxType;
	private final ModelMessage message;

	public ChatBox(BoxType boxType, ModelMessage message) {
		this.boxType = boxType;
		this.message = message;
		init();
		
	}

	private void init() {
		
		initBox();
		
	}

	private void initBox() {
    	String rightToLeft = boxType == BoxType.RIGHT ? ",rtl" : "";
        setLayout(new MigLayout("inset 5" + rightToLeft, "[150!]5[]", "[center]"));
        JTextPane text = new JTextPane();
        text.setEditorKit(new AutoWrapText());
        text.setText(message.getMessage());
        text.setBackground(new Color(0, 0, 0, 0));
        if(boxType == BoxType.RIGHT)
        	text.setForeground(new Color(255, 255, 255));
        else
        	text.setForeground(new Color(55, 55, 55));
        text.setFont(new Font("Tahoma", Font.PLAIN, 14));
        text.setSelectionColor(new Color(200, 200, 200, 100));
        text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        text.setOpaque(false);
        text.setEditable(false);
        JLabel labelDate = new JLabel(message.getName() + " | " + message.getDate());
        labelDate.setForeground(new Color(127, 127, 127));
        add(text, "gapy 20, wrap");
        add(labelDate, "gapx 20,span 2");
    }

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int width = getWidth();
		int height = getHeight();
		if (boxType == BoxType.LEFT) {
			Area area = new Area(new RoundRectangle2D.Double(0, 25, width, height - 25 - 16 - 10, 15, 15));
			g2.setColor(new Color(174, 178, 182, 120));
			g2.fill(area);
		} else {
			Area area = new Area(new RoundRectangle2D.Double(0, 25, width, height - 25 - 16 - 10, 15, 15));
			g2.setColor(new Color(100, 100, 255, 150));
			g2.fill(area);
		}
		g2.dispose();
		super.paintComponent(g);
	}

	public BoxType getBoxType() {
		return boxType;
	}

	public ModelMessage getMessage() {
		return message;
	}

	public static enum BoxType {
		LEFT, RIGHT
	}
}
