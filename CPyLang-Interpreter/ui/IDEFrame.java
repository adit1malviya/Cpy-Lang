package ui;

import main.CPyInterpreter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class IDEFrame extends JFrame {

    // ── Colour palette (Deep Navy + Purple + Gold Theme) ───────────────────────

static final Color BG_DARK         = new Color(0x0F, 0x12, 0x1A);
static final Color BG_SIDEBAR      = new Color(0x14, 0x18, 0x22);
static final Color BG_TAB_BAR      = new Color(0x18, 0x1D, 0x2A);
static final Color BG_TAB_ACTIVE   = new Color(0x1F, 0x25, 0x35);
static final Color BG_TAB_INACTIVE = new Color(0x14, 0x18, 0x22);
static final Color BG_EDITOR       = new Color(0x0F, 0x12, 0x1A);
static final Color BG_TERMINAL     = new Color(0x0B, 0x0E, 0x15);
static final Color BG_TITLE        = new Color(0x12, 0x16, 0x24);

// Accent (gold + purple mix)
static final Color ACCENT          = new Color(0xF5, 0xC5, 0x42);   // gold
static final Color ACCENT_DIM      = new Color(0xA0, 0x7C, 0x2C);
static final Color ACCENT_DARK     = new Color(0x4A, 0x3A, 0x12);

// Text colors
static final Color TEXT_PRIMARY    = new Color(0xD4, 0xD8, 0xE5);
static final Color TEXT_BRIGHT     = new Color(0xFF, 0xFF, 0xFF);
static final Color BORDER_COLOR    = new Color(0x2A, 0x30, 0x40);

// Buttons
static final Color RUN_BG = new Color(0x22, 0xC5, 0x5E);  // modern green ✔
static final Color RUN_FG = new Color(0x00, 0x00, 0x00);  // black text ✔

static final Color NEW_BG = new Color(0x3B, 0x82, 0xF6);  // modern soft blue
static final Color NEW_FG = Color.WHITE;

// Terminal
static final Color ERROR_RED       = new Color(0xFF, 0x5A, 0x5A);
static final Color TERMINAL_GREEN  = new Color(0x6C, 0xFF, 0xA3);
static final Color SEPARATOR_COLOR = new Color(0x3A, 0x40, 0x55);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    static final Font FONT_CODE  = new Font("Consolas", Font.PLAIN, 14);
    static final Font FONT_UI    = new Font("Consolas", Font.PLAIN, 13);
    static final Font FONT_UI_SM = new Font("Consolas", Font.PLAIN, 11);
    static final Font FONT_BOLD  = new Font("Consolas", Font.BOLD,  13);
    static final Font FONT_TITLE = new Font("Consolas", Font.BOLD,  15);

    // ── State ─────────────────────────────────────────────────────────────────
    private final LinkedHashMap<String, String>  fileContents = new LinkedHashMap<>();
    private final Map<String, JToggleButton>     tabButtons   = new LinkedHashMap<>();
    private String activeFile = null;

    // ── Widgets ───────────────────────────────────────────────────────────────
    private JPanel    tabBar;
    private JTextArea editorArea;
    private JTextPane terminalPane;
    private JPanel    sideFileList;
    private JLabel    statusLabel;
    private JLabel    activeFileLabel;

    private boolean updatingEditor = false;

    public IDEFrame() {
        super("CPy IDE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setIconImage(createColorIcon(ACCENT, 32));
        buildUI();
    }

    // =========================================================================
    // UI CONSTRUCTION
    // =========================================================================

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);
        root.add(buildTitleBar(),  BorderLayout.NORTH);
        root.add(buildCenter(),    BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Title bar ─────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBackground(BG_TITLE);
    bar.setPreferredSize(new Dimension(0, 44));
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_DARK));

    // LEFT PANEL (LOGO)
    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
    left.setOpaque(false);

    // SINGLE COMBINED PILL: "CPy IDE"
    JLabel logo = new JLabel("CPy IDE") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // same style as previous IDE pill
            g2.setColor(new Color(0x3B, 0x2B, 0x66));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            g2.dispose();
            super.paintComponent(g);
        }
    };

    // Slightly bigger + clean
    logo.setFont(new Font("Consolas", Font.BOLD, 14));
    logo.setForeground(new Color(0xEDE9FE)); // soft white-lavender
    logo.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
    logo.setOpaque(false);
    logo.setHorizontalAlignment(SwingConstants.CENTER);

    left.add(logo);

    // RIGHT PANEL (BUTTONS)
    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    right.setOpaque(false);

    JButton newBtn = styledButton("+ New File", NEW_BG, NEW_FG, ACCENT_DARK);
    newBtn.addActionListener(e -> promptNewFile());

    JButton runBtn = styledButton(">> Run", RUN_BG, RUN_FG, ACCENT_DIM);
    runBtn.setFont(FONT_BOLD);
    runBtn.addActionListener(e -> runActiveFile());

    right.add(newBtn);
    right.add(runBtn);

    // ADD TO BAR
    bar.add(left, BorderLayout.WEST);
    bar.add(right, BorderLayout.EAST);

    return bar;
}

    // ── Center ────────────────────────────────────────────────────────────────
    private JSplitPane buildCenter() {
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildSidebar(), buildEditorPanel());
        mainSplit.setDividerLocation(210);
        mainSplit.setDividerSize(2);
        mainSplit.setBorder(null);
        mainSplit.setBackground(ACCENT_DARK);
        return mainSplit;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(BG_SIDEBAR);
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, ACCENT_DARK));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x00, 0x18, 0x05));
        header.setBorder(new EmptyBorder(10, 14, 10, 8));
        JLabel lbl = new JLabel("-- EXPLORER --");
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(ACCENT);
        header.add(lbl, BorderLayout.WEST);

        sideFileList = new JPanel();
        sideFileList.setBackground(BG_SIDEBAR);
        sideFileList.setLayout(new BoxLayout(sideFileList, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(sideFileList);
        scroll.setBorder(null);
        scroll.setBackground(BG_SIDEBAR);
        scroll.getViewport().setBackground(BG_SIDEBAR);

        side.add(header, BorderLayout.NORTH);
        side.add(scroll, BorderLayout.CENTER);
        return side;
    }

    // ── Editor panel ──────────────────────────────────────────────────────────
    private JPanel buildEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_EDITOR);

        tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(BG_TAB_BAR);
        tabBar.setPreferredSize(new Dimension(0, 36));
        tabBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_DARK));
        panel.add(tabBar, BorderLayout.NORTH);

        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildEditor(), buildTerminal());
        vSplit.setResizeWeight(0.65);
        vSplit.setDividerSize(4);
        vSplit.setBorder(null);
        vSplit.setBackground(ACCENT_DARK);

        panel.add(vSplit, BorderLayout.CENTER);
        return panel;
    }

    // ── Code editor ───────────────────────────────────────────────────────────
    private JPanel buildEditor() {
        editorArea = new JTextArea();
        editorArea.setFont(FONT_CODE);
        editorArea.setBackground(BG_EDITOR);
        editorArea.setForeground(TEXT_PRIMARY);
        editorArea.setCaretColor(new Color(0xF5, 0xC5, 0x42));
        editorArea.setSelectionColor(new Color(0x3A, 0x4A, 0x6A));
        editorArea.setSelectedTextColor(TEXT_BRIGHT);
        editorArea.setTabSize(4);
        editorArea.setLineWrap(false);
        editorArea.setBorder(new EmptyBorder(8, 12, 8, 8));

        editorArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { syncEditorToMap(); }
            public void removeUpdate(DocumentEvent e)  { syncEditorToMap(); }
            public void changedUpdate(DocumentEvent e) { syncEditorToMap(); }
        });

        LineNumberComponent gutter = new LineNumberComponent(editorArea);

        JScrollPane scroll = new JScrollPane(editorArea);
        scroll.setBorder(null);
        scroll.setRowHeaderView(gutter);
        scroll.getViewport().setBackground(BG_EDITOR);
        scroll.setBackground(BG_EDITOR);

        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(BG_EDITOR);
        JLabel hint = new JLabel(
        "<html>" +
        "<span style='color:#6B738A;'>Press&nbsp;&nbsp;[&nbsp;</span>" +
        "<span style='color:#FFFFFF;'>+ New File</span>" +
        "<span style='color:#6B738A;'>&nbsp;]&nbsp;&nbsp;to start coding</span>" +
        "</html>"
        );
        placeholder.add(hint);
        placeholder.setName("placeholder");

        JPanel wrapper = new JPanel(new CardLayout());
        wrapper.setName("editorWrapper");
        wrapper.add(placeholder, "placeholder");
        wrapper.add(scroll,      "editor");
        return wrapper;
    }

    // ── Terminal ──────────────────────────────────────────────────────────────
    private JPanel buildTerminal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_TERMINAL);
        panel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_DARK));

        JPanel termHeader = new JPanel(new BorderLayout());
        termHeader.setBackground(new Color(0x00, 0x0E, 0x02));
        termHeader.setBorder(new EmptyBorder(5, 14, 5, 10));
        termHeader.setPreferredSize(new Dimension(0, 32));

        JLabel termLbl = new JLabel("[ TERMINAL ]");
        termLbl.setFont(FONT_BOLD);
        termLbl.setForeground(new Color(0x7D, 0xC4, 0xFF)); // light blue
        termHeader.add(termLbl, BorderLayout.WEST);

        JButton clearBtn = new JButton("Clear");

// 🎨 COLOR PALETTE (rich but controlled)
Color CLEAR_BG        = new Color(0x2A, 0x1F, 0x4A); // deep purple
Color CLEAR_HOVER     = new Color(0x3B, 0x2B, 0x66); // brighter purple
Color CLEAR_BORDER    = new Color(0x6D, 0x5B, 0xA6); // lavender glow
Color CLEAR_TEXT      = new Color(0xEDE9FE);         // soft white-lavender

clearBtn.setFont(FONT_UI_SM);
clearBtn.setForeground(CLEAR_TEXT);
clearBtn.setBackground(CLEAR_BG);

// 🌈 Stylish border + padding
clearBtn.setBorder(new CompoundBorder(
        BorderFactory.createLineBorder(CLEAR_BORDER, 1),
        new EmptyBorder(4, 14, 4, 14)
));

clearBtn.setFocusPainted(false);
clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

// 🚀 Action
clearBtn.addActionListener(e -> terminalPane.setText(""));

// ✨ Hover animation
clearBtn.addMouseListener(new MouseAdapter() {
    public void mouseEntered(MouseEvent e) {
        clearBtn.setBackground(CLEAR_HOVER);
        clearBtn.setForeground(Color.WHITE);
    }

    public void mouseExited(MouseEvent e) {
        clearBtn.setBackground(CLEAR_BG);
        clearBtn.setForeground(CLEAR_TEXT);
    }
});

// 💡 Optional glow feel (subtle shadow illusion)
clearBtn.setBorder(new CompoundBorder(
        BorderFactory.createLineBorder(CLEAR_BORDER, 1),
        new EmptyBorder(4, 14, 4, 14)
));

termHeader.add(clearBtn, BorderLayout.EAST);
        terminalPane = new JTextPane();
        terminalPane.setFont(new Font("Consolas", Font.PLAIN, 13));
        terminalPane.setBackground(BG_TERMINAL);
        terminalPane.setForeground(new Color(0xC8, 0xD0, 0xE0));
        terminalPane.setEditable(false);
        terminalPane.setBorder(new EmptyBorder(8, 14, 8, 14));

        JScrollPane tScroll = new JScrollPane(terminalPane);
        tScroll.setBorder(null);
        tScroll.getViewport().setBackground(BG_TERMINAL);

        panel.add(termHeader, BorderLayout.NORTH);
        panel.add(tScroll,    BorderLayout.CENTER);
        return panel;
    }

    // ── Status bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0x00, 0x22, 0x08));
        bar.setPreferredSize(new Dimension(0, 26));
        bar.setBorder(new EmptyBorder(0, 12, 0, 12));

        activeFileLabel = new JLabel("No file open");
        activeFileLabel.setFont(FONT_UI_SM);
        activeFileLabel.setForeground(ACCENT);
        bar.add(activeFileLabel, BorderLayout.WEST);

        statusLabel = new JLabel("CPy Language   UTF-8");
        statusLabel.setFont(FONT_UI_SM);
        statusLabel.setForeground(ACCENT_DIM);
        bar.add(statusLabel, BorderLayout.EAST);
        return bar;
    }

    // =========================================================================
    // FILE MANAGEMENT
    // =========================================================================

    private void promptNewFile() {
        JTextField nameField = new JTextField(20);
        nameField.setFont(FONT_UI);
        nameField.setBackground(new Color(0x08, 0x18, 0x08));
        nameField.setForeground(ACCENT);
        nameField.setCaretColor(ACCENT);
        nameField.setBorder(BorderFactory.createLineBorder(ACCENT_DARK, 1));

        JLabel label = new JLabel("File name (without extension):");
        label.setFont(FONT_UI);
        label.setForeground(TEXT_PRIMARY);

        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setBackground(new Color(0x08, 0x10, 0x08));
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        form.add(label,     BorderLayout.NORTH);
        form.add(nameField, BorderLayout.CENTER);

        UIManager.put("OptionPane.background",        new Color(0x08, 0x10, 0x08));
        UIManager.put("Panel.background",             new Color(0x08, 0x10, 0x08));
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);

        int result = JOptionPane.showConfirmDialog(
                this, form, "New File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String raw = nameField.getText().trim();
        if (raw.isEmpty()) return;

        String name = raw.endsWith(".cpy") ? raw : raw + ".cpy";

        if (fileContents.containsKey(name)) {
            JOptionPane.showMessageDialog(this,
                    "A file named \"" + name + "\" already exists.",
                    "Duplicate File", JOptionPane.WARNING_MESSAGE);
            return;
        }

        fileContents.put(name, "");
        addTab(name);
        addSidebarEntry(name);
        switchToFile(name);
        appendTerminal("Created: " + name + "\n", ACCENT, false);
    }

    private void addTab(String name) {
        JToggleButton tab = new JToggleButton(name);
        tab.setFont(FONT_UI_SM);
        tab.setBackground(BG_TAB_INACTIVE);
        tab.setForeground(ACCENT_DIM);
        tab.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, ACCENT_DARK),
                new EmptyBorder(6, 16, 6, 16)));
        tab.setFocusPainted(false);
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab.addActionListener(e -> switchToFile(name));

        tab.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) closeFile(name);
            }
        });

        tabButtons.put(name, tab);
        tabBar.add(tab);
        tabBar.revalidate();
        tabBar.repaint();
    }

    private void addSidebarEntry(String name) {
        JPanel entry = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        entry.setBackground(BG_SIDEBAR);
        entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        entry.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        entry.setName(name);

        JLabel icon = new JLabel("[f]");
        icon.setFont(FONT_UI_SM);
        icon.setForeground(ACCENT_DIM);

        JLabel lbl = new JLabel(name);
        lbl.setFont(FONT_UI_SM);
        lbl.setForeground(TEXT_PRIMARY);

        entry.add(icon);
        entry.add(lbl);

        entry.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { switchToFile(name); }
            public void mouseEntered(MouseEvent e) {
                entry.setBackground(new Color(0x22, 0x28, 0x38));
                lbl.setForeground(ACCENT);
                icon.setForeground(ACCENT);
            }
            public void mouseExited(MouseEvent e) {
                entry.setBackground(BG_SIDEBAR);
                lbl.setForeground(TEXT_PRIMARY);
                icon.setForeground(ACCENT_DIM);
            }
        });

        sideFileList.add(entry);
        sideFileList.revalidate();
        sideFileList.repaint();
    }

    private void switchToFile(String name) {
        if (activeFile != null && fileContents.containsKey(activeFile)) {
            fileContents.put(activeFile, editorArea.getText());
        }

        activeFile = name;

        tabButtons.forEach((n, btn) -> {
            boolean active = n.equals(name);
            btn.setSelected(active);
            btn.setBackground(active ? BG_TAB_ACTIVE  : BG_TAB_INACTIVE);
            btn.setForeground(active ? new Color(0x1E, 0x23, 0x30) : ACCENT_DIM);
            if (active) {
                btn.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(3, 0, 0, 0, ACCENT),
                        new EmptyBorder(4, 16, 6, 16)));
            } else {
                btn.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, ACCENT_DARK),
                        new EmptyBorder(6, 16, 6, 16)));
            }
        });

        updatingEditor = true;
        editorArea.setText(fileContents.getOrDefault(name, ""));
        editorArea.setCaretPosition(0);
        updatingEditor = false;

        showEditorCard("editor");
        activeFileLabel.setText("  " + name);
        editorArea.requestFocusInWindow();
    }

    private void closeFile(String name) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Close \"" + name + "\"?", "Close File", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        fileContents.remove(name);

        JToggleButton tab = tabButtons.remove(name);
        if (tab != null) { tabBar.remove(tab); tabBar.revalidate(); tabBar.repaint(); }

        for (Component c : sideFileList.getComponents()) {
            if (name.equals(c.getName())) {
                sideFileList.remove(c);
                sideFileList.revalidate();
                sideFileList.repaint();
                break;
            }
        }

        if (name.equals(activeFile)) {
            activeFile = null;
            if (!fileContents.isEmpty()) {
                switchToFile(fileContents.keySet().iterator().next());
            } else {
                editorArea.setText("");
                showEditorCard("placeholder");
                activeFileLabel.setText("No file open");
            }
        }
    }

    // =========================================================================
    // RUN
    // =========================================================================

    private void runActiveFile() {
        if (activeFile == null) {
            appendTerminal("No file is open.\n", ERROR_RED, true);
            return;
        }
        fileContents.put(activeFile, editorArea.getText());
        String code = fileContents.get(activeFile);

        appendTerminal("\n>> Running " + activeFile + " ...\n", ACCENT, true);

        SwingWorker<CPyInterpreter.RunResult, Void> worker = new SwingWorker<>() {
            protected CPyInterpreter.RunResult doInBackground() {
                return new CPyInterpreter().run(code);
            }
            protected void done() {
                try {
                    CPyInterpreter.RunResult res = get();
                    String out = res.output.isEmpty() ? "(no output)\n" : res.output;
                    appendTerminal(out, res.isError ? ERROR_RED : TEXT_PRIMARY, res.isError);
                    appendTerminal("--------------------------------------------\n", SEPARATOR_COLOR, false);
                } catch (Exception ex) {
                    appendTerminal("Internal IDE error: " + ex.getMessage() + "\n", ERROR_RED, true);
                }
            }
        };
        worker.execute();
    }

    // =========================================================================
    // TERMINAL HELPERS
    // =========================================================================

    private void appendTerminal(String text, Color color, boolean bold) {
        StyledDocument doc = terminalPane.getStyledDocument();
        Style style = terminalPane.addStyle("s" + System.nanoTime(), null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        StyleConstants.setFontFamily(style, "Consolas");
        StyleConstants.setFontSize(style, 13);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ignored) {}
        terminalPane.setCaretPosition(doc.getLength());
    }

    // =========================================================================
    // MISC HELPERS
    // =========================================================================

    private void syncEditorToMap() {
        if (!updatingEditor && activeFile != null) {
            fileContents.put(activeFile, editorArea.getText());
        }
    }

    private void showEditorCard(String card) {
        Container c = editorArea.getParent();
        while (c != null && !(c.getLayout() instanceof CardLayout)) {
            c = c.getParent();
        }
        if (c != null) {
            ((CardLayout) c.getLayout()).show(c, card);
        }
    }

    private JButton styledButton(String text, Color bg, Color fg, Color border) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_UI);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                new EmptyBorder(5, 14, 5, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hoverBg = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private Image createColorIcon(Color c, int size) {
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(c);
        g.fillRoundRect(0, 0, size, size, 8, 8);
        g.dispose();
        return img;
    }
}