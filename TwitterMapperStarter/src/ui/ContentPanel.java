package ui;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import query.Query;

import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {
    @SuppressWarnings("FieldCanBeLocal")
    private final JSplitPane topLevelSplitPane;
    @SuppressWarnings("FieldCanBeLocal")
    private final JSplitPane querySplitPane;
    @SuppressWarnings("FieldCanBeLocal")
    private final JPanel newQueryPanel;
    private final JPanel existingQueryList;
    private final JMapViewer map;

    private final Application app;

    public ContentPanel(Application app) {
        this.app = app;

        map = new JMapViewer();
        map.setMinimumSize(new Dimension(100, 50));
        setLayout(new BorderLayout());
        newQueryPanel = new NewQueryPanel(app);

        // NOTE: We wrap existingQueryList in a container so it gets a pretty border.
        JPanel layerPanelContainer = new JPanel();
        existingQueryList = new JPanel();
        existingQueryList.setLayout(new javax.swing.BoxLayout(existingQueryList, javax.swing.BoxLayout.Y_AXIS));
        layerPanelContainer.setLayout(new BorderLayout());
        layerPanelContainer.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Current Queries"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
        layerPanelContainer.add(existingQueryList, BorderLayout.NORTH);

        querySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        querySplitPane.setDividerLocation(150);
        querySplitPane.setTopComponent(newQueryPanel);
        querySplitPane.setBottomComponent(layerPanelContainer);

        topLevelSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topLevelSplitPane.setDividerLocation(150);
        topLevelSplitPane.setLeftComponent(querySplitPane);
        topLevelSplitPane.setRightComponent(map);

        add(topLevelSplitPane, "Center");
        revalidate();

        repaint();
    }

    // Add a new query to the set of queries and update the UI to reflect the new query.
    public void addQuery(Query query) {
        JPanel newQueryPanel = new JPanel();
        newQueryPanel.setLayout(new GridBagLayout());
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(query.getColor());
        colorPanel.setPreferredSize(new Dimension(30, 30));
        JButton removeButton = new JButton("X");
        removeButton.setPreferredSize(new Dimension(30, 20));
        removeButton.addActionListener(e -> {
            app.terminateQuery(query);
            query.terminate();
            existingQueryList.remove(newQueryPanel);
            revalidate();
        });

        GridBagConstraints c = new GridBagConstraints();
        newQueryPanel.add(colorPanel, c);

        c = new GridBagConstraints();
        JCheckBox checkbox = new JCheckBox(query.getQueryString());
        checkbox.setSelected(true);
        checkbox.addActionListener(e -> app.updateVisibility());
        query.setCheckBox(checkbox);
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        newQueryPanel.add(checkbox, c);
        newQueryPanel.add(removeButton);

        existingQueryList.add(newQueryPanel);
        validate();
    }

    public JMapViewer getViewer() {
        return map;
    }
}
