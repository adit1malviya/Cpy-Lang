package ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class LineNumberComponent extends JPanel {

    private final JTextArea textArea;

    // 🎨 THEME COLORS (match your new navy + gold UI)
    private static final Color BG  = new Color(0x0F, 0x12, 0x1A); // same as editor bg
    private static final Color FG  = new Color(0x6B, 0x73, 0x8A); // soft grey-blue
    private static final Color SEP = new Color(0x2A, 0x30, 0x40); // subtle divider

    private static final Font FONT = new Font("Consolas", Font.PLAIN, 13);
    private static final int W = 48;

    public LineNumberComponent(JTextArea textArea) {
        this.textArea = textArea;

        setPreferredSize(new Dimension(W, 0));
        setBackground(BG);

        // repaint when text changes
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { repaint(); }
            public void removeUpdate(DocumentEvent e)  { repaint(); }
            public void changedUpdate(DocumentEvent e) { repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // right-side separator line
        g2.setColor(SEP);
        g2.fillRect(W - 1, 0, 1, getHeight());

        g2.setFont(FONT);
        g2.setColor(FG);

        FontMetrics fm = g2.getFontMetrics();

        int lineHeight = textArea.getFontMetrics(FONT).getHeight();
        int lineCount  = textArea.getLineCount();
        int topInset   = textArea.getInsets().top;

        for (int i = 1; i <= lineCount; i++) {
            String num = String.valueOf(i);

            int x = W - fm.stringWidth(num) - 6;
            int y = topInset + (i - 1) * lineHeight + fm.getAscent();

            g2.drawString(num, x, y);
        }
    }
}