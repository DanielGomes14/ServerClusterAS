package ClientGUI;

import javax.swing.*;

public class ClientGUI {
    private JPanel panel1;

    private JLabel clientId;
    private JButton connectButton;
    private JPanel LBConnectPanel;
    private JPanel RequestPanel;
    private JSpinner nRequests;
    private JButton requestButton;
    private JTable pendingRequestsTable;
    private JSpinner NI;
    private JSpinner deadline;
    private JTable processedRequestsTable;
    private JButton endConnectionToLoadButton;
    private JComboBox comboBox1;

    private final EventQueue queue;
    private final ServerAux server;

    public ClientGUI(EventQueue queue, ServerAux server) {
        this.queue = queue;
        this.server = server;

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(800, 500);
        jf.add(panel1);
        jf.setVisible(true);
    }
}
