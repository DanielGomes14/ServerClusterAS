package Monitor;


import Communication.Message;
import Server.ServerInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;

public class MonitorGUI {
    private final EventQueue queue;
    private final Monitor monitor;

    private JTable clientsTable;
    private JTable serversTable;
    private JTable requestsTable;
    private JTable LBTable;
    private JPanel panel1;
    private DefaultTableModel tableModel0;
    private DefaultTableModel tableModel1;
    private DefaultTableModel tableModel2;
    private DefaultTableModel tableModel3;
    private HashMap<Integer, Integer> tableRows1;
    private HashMap<Integer, Integer> tableRows2;
    private HashMap<Integer, Integer> tableRows3;


    public MonitorGUI(Monitor monitor) {
        this.queue = new EventQueue();
        this.monitor = monitor;

        this.tableRows1 = new HashMap<>();
        this.tableRows2 = new HashMap<>();
        this.tableRows3 = new HashMap<>();

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(800, 500);
        jf.add(panel1);
        jf.setVisible(true);

        tableModel0 = new DefaultTableModel();
        tableModel1 = new DefaultTableModel();
        tableModel2 = new DefaultTableModel();
        tableModel3 = new DefaultTableModel();

        tableModel0.addColumn("Id");
        tableModel0.addColumn("Port");
        tableModel1.addColumn("Id");
        tableModel1.addColumn("Port");
        tableModel1.addColumn("NI");
        tableModel1.addColumn("Pending Requests");
        tableModel1.addColumn("Active Requests");
        tableModel1.addColumn("Status");
        tableModel2.addColumn("Id");
        tableModel2.addColumn("Port");
        tableModel2.addColumn("Primary");
        tableModel2.addColumn("Status");
        tableModel3.addColumn("Id");
        tableModel3.addColumn("Client Id");
        tableModel3.addColumn("NI");
        tableModel3.addColumn("Deadline");
        tableModel3.addColumn("Server Id");
        tableModel3.addColumn("Status");

        clientsTable.setModel(tableModel0);
        serversTable.setModel(tableModel1);
        LBTable.setModel(tableModel2);
        requestsTable.setModel(tableModel3);
    }

    public void registerClient(Message msg) {
        try {
            queue.invokeAndWait(() -> {

                Object[] obj = new Object[]{
                        msg.getServerId(), msg.getServerPort()
                };

                tableModel0.addRow(obj);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPendingRequest(Message msg) {
        try {
            queue.invokeAndWait(() -> {
                Object[] obj = new Object[]{msg.getRequestId(), msg.getServerId(), msg.getNI(), msg.getDeadline(), "NONE", "PENDING"};
                this.tableRows3.put(msg.getRequestId(), tableModel3.getRowCount());

                tableModel3.addRow(obj);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void registerServer(Message msg) {
        try {
            queue.invokeAndWait(() -> {
                Object[] obj = new Object[]{
                        msg.getServerId(), msg.getServerPort(), 0, 0, 0, "UP"
                };

                this.tableRows1.put(msg.getServerId(), tableModel1.getRowCount());

                tableModel1.addRow(obj);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeServer(int searchedId) {
        try {
            queue.invokeAndWait(() -> {
                if (tableRows1.containsKey(searchedId))
                    tableModel1.setValueAt("DOWN", tableRows1.get(searchedId), 5);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerLB(Message msg, int primary) {
        try {
            queue.invokeAndWait(() -> {
                String str = "FALSE";
                if (msg.getServerId() == primary)
                    str = "TRUE";

                Object[] obj = new Object[]{
                        msg.getServerId(), msg.getServerPort(), str, "UP"
                };
                this.tableRows2.put(msg.getServerId(), tableModel2.getRowCount());
                tableModel2.addRow(obj);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestProcessed(int requestId, ServerInfo server) {
        try {
            queue.invokeAndWait(() -> {
                if (tableRows3.containsKey(requestId)) {
                    tableModel3.setValueAt("PROCESSED", tableRows3.get(requestId), 5);
                }
                if (tableRows1.containsKey(server.getServerId())) {
                    tableModel1.setValueAt(server.getNI(), tableRows1.get(server.getServerId()), 2);
                    tableModel1.setValueAt(server.getPendingReq(), tableRows1.get(server.getServerId()), 3);
                    tableModel1.setValueAt(server.getActiveReq(), tableRows1.get(server.getServerId()), 4);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestInProcess(int requestId, ServerInfo server) {
        try {
            queue.invokeAndWait(() -> {
                if (tableRows3.containsKey(requestId)) {
                    tableModel3.setValueAt("IN_PROCESSING", tableRows3.get(requestId), 5);
                }
                if (tableRows1.containsKey(server.getServerId())) {
                    tableModel1.setValueAt(server.getNI(), tableRows1.get(server.getServerId()), 2);
                    tableModel1.setValueAt(server.getPendingReq(), tableRows1.get(server.getServerId()), 3);
                    tableModel1.setValueAt(server.getActiveReq(), tableRows1.get(server.getServerId()), 4);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateServerInfo(ServerInfo server, int requestId) {
        try {
            queue.invokeAndWait(() -> {
                if (tableRows3.containsKey(requestId)) {
                    tableModel3.setValueAt(server.getServerId(), tableRows3.get(requestId), 4);
                }
                if (tableRows1.containsKey(server.getServerId())) {
                    System.out.println("adsdas");
                    System.out.println(server.getNI());
                    tableModel1.setValueAt(server.getNI(), tableRows1.get(server.getServerId()), 2);
                    tableModel1.setValueAt(server.getPendingReq(), tableRows1.get(server.getServerId()), 3);
                    tableModel1.setValueAt(server.getActiveReq(), tableRows1.get(server.getServerId()), 4);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestRejected(Message msg) {
        try {
            queue.invokeAndWait(() -> {
                if (tableRows3.containsKey(msg.getRequestId())) {
                    tableModel3.setValueAt("REJECTED", tableRows3.get(msg.getRequestId()), 5);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLB(int searchedId) {
        try {
            queue.invokeAndWait(() -> {
                if (tableRows2.containsKey(searchedId)) {
                    tableModel2.setValueAt("DOWN", tableRows2.get(searchedId), 3);
                    tableModel2.setValueAt("FALSE", tableRows2.get(searchedId), 2);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnPrimaryLB(int searchedId) {
        try {
            queue.invokeAndWait(() -> {
                if (tableRows2.containsKey(searchedId))
                    tableModel2.setValueAt("TRUE", tableRows2.get(searchedId), 2);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "Monitor", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(10, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(10, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 4, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(10, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("My Server Port: 5000");
        panel2.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer9 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer9, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Load Balancers");
        panel2.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        LBTable = new JTable();
        scrollPane1.setViewportView(LBTable);
        final JLabel label3 = new JLabel();
        label3.setText("Servers");
        panel2.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel2.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        serversTable = new JTable();
        scrollPane2.setViewportView(serversTable);
        final JLabel label4 = new JLabel();
        label4.setText("Requests");
        panel2.add(label4, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel2.add(scrollPane3, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        clientsTable = new JTable();
        scrollPane3.setViewportView(clientsTable);
        final JLabel label5 = new JLabel();
        label5.setText("Clients");
        panel2.add(label5, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel2.add(scrollPane4, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        requestsTable = new JTable();
        scrollPane4.setViewportView(requestsTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
