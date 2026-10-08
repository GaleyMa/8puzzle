package puzzle;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Ventana Swing del Agente GUI: botón Iniciar, tabla de respuestas y bitácora. */
public class VentanaGUI extends JFrame {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final DefaultTableModel modelo;
    private final JTextArea bitacora;
    private int ronda = 0;

    public VentanaGUI(AgenteGUI agente) {
        super("8-Puzzle · Agente GUI");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(640, 460);
        setLocationRelativeTo(null);

        JButton btnIniciar = new JButton("Iniciar competencia");
        btnIniciar.setFont(btnIniciar.getFont().deriveFont(Font.BOLD, 14f));
        btnIniciar.addActionListener(e -> agente.iniciarCompetencia());

        JButton btnLimpiar = new JButton("Limpiar");

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        arriba.add(btnIniciar);
        arriba.add(btnLimpiar);

        modelo = new DefaultTableModel(new String[]{"Ronda", "Jugador", "Performativa", "Respuesta"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(2).setCellRenderer(new ColorPerformativa());

        bitacora = new JTextArea(6, 40);
        bitacora.setEditable(false);
        bitacora.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        btnLimpiar.addActionListener(e -> {
            modelo.setRowCount(0);
            bitacora.setText("");
        });

        JSplitPane centro = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tabla), new JScrollPane(bitacora));
        centro.setResizeWeight(0.65);

        add(arriba, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    public void mostrarEnvio(int ronda) {
        this.ronda = ronda;
        log("GUI -> jugador1, jugador2, jugador3 : INFORM \"" + Protocolo.COMPETENCIA_INICIADA
                + "\" (ronda " + ronda + ")");
    }

    public void mostrarRespuesta(String jugador, String performativa, String texto) {
        modelo.addRow(new Object[]{ronda, jugador, performativa, texto});
        log(jugador + " -> GUI : " + performativa + " \"" + texto + "\"");
    }

    private void log(String linea) {
        bitacora.append("[" + LocalTime.now().format(HORA) + "] " + linea + "\n");
        bitacora.setCaretPosition(bitacora.getDocument().getLength());
    }

    /** Colorea la columna de performativa para que se lea rápido en el demo. */
    private static class ColorPerformativa extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean foco, int fila, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foco, fila, col);
            if (!sel) {
                switch (String.valueOf(v)) {
                    case "AGREE":          c.setBackground(new Color(0xD4EDDA)); break;
                    case "REFUSE":         c.setBackground(new Color(0xF8D7DA)); break;
                    case "NOT-UNDERSTOOD": c.setBackground(new Color(0xFFF3CD)); break;
                    default:               c.setBackground(Color.WHITE);
                }
            }
            return c;
        }
    }
}
